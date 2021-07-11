import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;


public class ISOClient 
{

	private SocketAddress address;
	private Socket client;
	private BufferedReader in;
	private BufferedWriter out;
	private String timestamp = "";
	private String traxId = "";
	private String Server = "";
	private static Logger logger = Logger.getLogger(ISOClient.class);
	private String responseLogin = "";
	private String responseHangleon = "";
	private int port = 0;
	private long number = 0;

	public ISOClient()
	{
		PropertyConfigurator.configure("conf/log4j.properties");
		if(client==null)	
		{
			client = new Socket();
		}	
	}

	String hexToAscii(String s) 
	{
	  int n = s.length();
	  StringBuilder sb = new StringBuilder(n / 2);
	  for (int i = 0; i < n; i += 2) {
	    char a = s.charAt(i);
	    char b = s.charAt(i + 1);
	    sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
	  }
	  return sb.toString();
	}

	private static int hexToInt(char ch) 
	{
	  if ('a' <= ch && ch <= 'f') { return ch - 'a' + 10; }
	  if ('A' <= ch && ch <= 'F') { return ch - 'A' + 10; }
	  if ('0' <= ch && ch <= '9') { return ch - '0'; }
	  throw new IllegalArgumentException(String.valueOf(ch));
	}
	
	
	public String buildISO800Message() 
	{
		byte[] result = new byte[30];
		
        try 
        {
        	GenericPackager packager = new GenericPackager("conf/basic0800.xml");
        	
			SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
			timestamp = sdf.format(new Date()); 

	   		number = (long) Math.floor(Math.random() * 900000L) + 100000L;  
	   		traxId = new Long(number).toString();
			
			ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
    		isoMsg.setMTI("0800");
    		isoMsg.set(7, timestamp);
    		isoMsg.set(11, traxId);
    		isoMsg.set(33, "770924");
    		isoMsg.set(70, "001");
    		
            printISOMessage("Request",isoMsg);
 
            result = isoMsg.pack();
            
        } 
        catch (ISOException e) 
        {
        	logger.error(this.getClass().getName()+" "+e.getMessage());
        }
        
        return new String(result);
    }	
	
	
	public String sendISOPacket(String httpsurl, String httpstimeout, String inputHangleon, int maxHangleon)
	{
		responseHangleon = "";
		String request = "";
		responseLogin  = "";
    	String length = "";
    	String header = "";
		
    	
		try
		{

			int x=0;
			if(!client.isConnected())
			{	
				//httpsurl="127.0.0.1:9099";
				//httpstimeout = "60000";
				
				Server = httpsurl.split(":")[0];
				port = new Integer(httpsurl.split(":")[1]).intValue();
				
				address = new InetSocketAddress(Server, port);
	
		        client.connect(address, new Integer(httpstimeout).intValue());
		        
		        x=1;

		    	in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	    		out = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(client.getOutputStream())));
			}
			
			if(client.isConnected())
			{
				logger.info("[ISO Connected To ] : "+client.getRemoteSocketAddress()+" from Local Port : "+client.getLocalPort());
			
				if(x==1)
				{
					request=buildISO800Message();
					length=String.format("%4s",Integer.toHexString(request.length())).replace(" ","0");
    				header=hexToAscii(length.substring(2));
    				header=header+hexToAscii(length.substring(0,2));
					request=header+request;
					
					out.write(request);
					out.flush();

					logger.info("[ISO Send Login to "+httpsurl+" ] : "+request);					
					int i=0;
					int j=0;
					while(true)
					{
						if(in.ready())
						{	
							responseLogin=responseLogin+(char) in.read();
							if(i==58)
							{
								break;
							}
							i++;
						}	
						if(!in.ready())
						{
							if(j==1)	break;
							Thread.sleep(10000);
						}						
						j++;
					}
					logger.info("[ISO Receive Login from "+httpsurl+" ] : "+responseLogin);
				}				
				
				if(responseLogin.trim().compareTo("")!=0)
				{	
					length=String.format("%4s",Integer.toHexString(inputHangleon.length())).replace(" ","0");
					header=hexToAscii(length.substring(2));
					header=header+hexToAscii(length.substring(0,2));
					inputHangleon=header+inputHangleon;
					
					out.write(inputHangleon);	
					out.flush();
	
					logger.info("[ISO Send Request to "+httpsurl+" ] : "+inputHangleon);				
					int i=0;
					int j=0;
					while(true)
					{
						if(in.ready())
						{	
							responseHangleon=responseHangleon+(char) in.read();
							if(i==maxHangleon)
							{
								break;
							}
							i++;
						}	
						if(!in.ready())
						{
							if(j==1)	break;
							Thread.sleep(10000);
						}						
						j++;
					}
				}
				else
				{
					responseHangleon="";
				}
				
				logger.info("[ISO Receive Request from "+httpsurl+" ] : "+responseHangleon);										
			}
			
		}		
		catch(IOException e)
		{
        	logger.error(this.getClass().getName()+" "+e.getMessage());				
			client = new Socket();
			sendISOPacket(httpsurl,httpstimeout,inputHangleon,maxHangleon);
		}
		catch(InterruptedException e)
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		}
		
		return responseHangleon;
	}
	
	public void printISOMessage(String text, ISOMsg isoMsg) 
	{
        try 
        {
            logger.info("["+text+" | MTI ] : "+isoMsg.getMTI());
            for (int i = 1; i <= isoMsg.getMaxField(); i++) 
            {
                if (isoMsg.hasField(i)) 
                {
                    logger.info("["+text+" | Field "+i+" ] : "+isoMsg.getString(i));
                }
            }
        }
        catch (ISOException e)
        {
			logger.error(this.getClass().getName()+" "+e.getMessage());
        }
    }
	
	
}
