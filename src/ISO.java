import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class ISO
{
	private Properties propXML = new Properties();
	private static Logger logger = Logger.getLogger(ISO.class);
	private ConnectionImpl connDb = new ConnectionImpl();
	private HttpImpl http = new HttpImpl();
	private eVa eva = new eVa();
	private Https https = new Https();
	
	private int portServer = 0;
	private static ServerSocket server;
	
	private String cashInResponseBank[] = new String[25];
	private String cashInConfResponseBank[] = new String[25];
	private String cashInCheckResponseBank[] = new String[9];

	private String bankurl = "";
	private String banktimeout = "";
	private String traxId = "";
	
	private String mainTAG[] = new String[21];
	private String main0800TAG[] = new String[7];
	private String inputHangleon = "";
	private int maxHangleon = 0;
	
	private String mainTAGres[] = new String[23];
	private XMLBank xmlRemittanceBank = new XMLBank();

	private String cashInBankRequest[] = new String[23];
	private String cashInConfBankRequest[] = new String[25];
	private String cashInCheckBankRequest[] = new String[9];
	
	private String reFundRequestTAG[] = new String[4];
	private String reFundRequest[] = new String[4];	
	private String reFundConfRequestTAG[] = new String[6];
	private String reFundConfRequest[] = new String[6];
	private String reFundCheckRequestTAG[] = new String[6];
	private String reFundCheckRequest[] = new String[6];

	private String cashInRequestTAG[] = new String[28];
	private String cashInRequest[] = new String[28];	
	private String cashInConfRequestTAG[] = new String[6];
	private String cashInConfRequest[] = new String[6];
	private String cashInCheckRequestTAG[] = new String[6];
	private String cashInCheckRequest[] = new String[6];
	
	private String cashInBankRequestTAG[] = new String[29];
	private String cashInBankRequestISO[] = new String[28];	
	private String cashInConfBankRequestTAG[] = new String[7];
	private String cashInConfBankRequestISO[] = new String[7];
	private String cashInCheckBankRequestTAG[] = new String[7];
	private String cashInCheckBankRequestISO[] = new String[7];

	private String cashOutCheckRequestTAG[] = new String[6];
	private String cashOutCheckRequest[] = new String[6];
	private String cashOutRequestTAG[] = new String[4];
	private String cashOutRequest[] = new String[4];
	private String cashOutConfRequestTAG[] = new String[17];
	private String cashOutConfRequest[] = new String[17];

	
	private String decimal[] = new String[2];	
	private String decimal2[] = new String[4];	
	private String decimal3[] = new String[3];	
	private String httpsurl = "";
	private String httpstimeout = "";

	private String inquiryResponse[] = new String[16];
	private String submissionResponse[] = new String[4];
	private String checkstatusResponse[] = new String[2];

	private Object[][] results_inquiry = new Object[1][1];
	private Map <Integer, String> field_inquiry = new HashMap<Integer, String>();
	private Object[][] results_inquiry2 = new Object[1][2];
	private Map <Integer, String> field_inquiry2 = new HashMap<Integer, String>();
	private Object[][] results_inquiry3 = new Object[1][23];
	private Map <Integer, String> field_inquiry3 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry4 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry5 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry6 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry7 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry8 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry9 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry10 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry11 = new HashMap<Integer, String>();
	private Object[][] results_inquiry4 = new Object[1][1];
	private Object[][] results_inquiry5 = new Object[1][1];
	private Object[][] results_inquiry6 = new Object[1][1];
	private Object[][] results_inquiry7 = new Object[1][1];
	private Object[][] results_inquiry8 = new Object[1][1];
	private Object[][] results_inquiry9 = new Object[1][1];
	private Object[][] results_inquiry10 = new Object[1][3];
	private Object[][] results_inquiry11 = new Object[1][1];
	private Object[][] results_inquiry12 = new Object[1][1];
	private Object[][] results_inquiry13 = new Object[1][1];
	private Object[][] results_inquiry14 = new Object[1][13];
	
	private long number = 0;
	private String refCode = "";
	private String bit1 = "";
	private String bit2 = "";
	private String bit3 = "";
	private String bit4 = "";
	private String bit7 = "";
	private String bit11 = "";
	private String bit12 = "";
	private String bit13 = "";
	private String bit14 = "";
	private String bit15 = "";
	private String bit18 = "";
	private String bit32 = "";
	private String bit33 = "";
	private String bit37 = "";
	private String bit38 = "000000";
	private String bit39 = "";
	private String bit41 = "";
	private String bit42 = "";
	private String bit43 = "";
	private String bit49 = "";
	private String bit61 = "";
	private String bit70 = "";
	private String bit103 = "";
	private String mti = "";
	private int len2 = 0;
	private int len32 = 0;
	private int len33 = 0;
	private boolean stan = false;

	private String bit38res = "000000";
	private String bit39res = "";
	
	private String dest1Acc = "";
	private String responseISO = "";
	
	private ISOClient isoclient;
	
	/*Constructor*/ 
	public ISO()
	{
        try 
        {
			propXML.load(new FileInputStream("conf/ISO.txt"));
			portServer=new Integer(propXML.getProperty("port")).intValue();
	        mainTAG=propXML.getProperty("main").split(",");
	        main0800TAG=propXML.getProperty("main0800").split(",");
	        mainTAGres=propXML.getProperty("mainres").split(",");

	        cashInRequestTAG=propXML.getProperty("cashinreq").split(",");
	        cashInConfRequestTAG=propXML.getProperty("cashinconfreq").split(",");
	        cashInCheckRequestTAG=propXML.getProperty("cashincheckreq").split(",");
	        cashOutRequestTAG=propXML.getProperty("cashoutreq").split(",");
	        cashOutConfRequestTAG=propXML.getProperty("cashoutconfreq").split(",");
	        cashOutCheckRequestTAG=propXML.getProperty("cashoutcheckreq").split(",");

	        cashInBankRequestTAG=propXML.getProperty("cashinbankreq").split(",");
	        cashInConfBankRequestTAG=propXML.getProperty("cashinconfbankreq").split(",");
	        cashInCheckBankRequestTAG=propXML.getProperty("cashincheckbankreq").split(",");
	        
	        reFundRequestTAG=propXML.getProperty("refundreq").split(",");
	        reFundConfRequestTAG=propXML.getProperty("refundconfreq").split(",");
	        reFundCheckRequestTAG=propXML.getProperty("refundcheckreq").split(",");
	        
	        isoclient = new ISOClient();
	        
			/*Create Database Connection*/
			while(connDb.isConnected()==false)
			{	
				connDb.setProperties(propXML);
				connDb.setUrl();
				connDb.setConnection();
			}

			
			field_inquiry7 = new TreeMap<Integer, String>();
			field_inquiry7.put(0, "bit38");

			field_inquiry8 = new TreeMap<Integer, String>();
			field_inquiry8.put(0, "erroriso");

			field_inquiry5 = new TreeMap<Integer, String>();
			field_inquiry5.put(0, "httpsurl");
			field_inquiry5.put(1, "httpstimeout");
			field_inquiry5.put(2, "dest1Acc");				
	        
	        field_inquiry = new TreeMap<Integer, String>();
			field_inquiry.put(0, "kodetransfer");

	        field_inquiry9 = new TreeMap<Integer, String>();
			field_inquiry9.put(0, "refCode");

			field_inquiry2 = new TreeMap<Integer, String>();
			field_inquiry2.put(0, "amount");
			field_inquiry2.put(1, "fee");

			field_inquiry10 = new TreeMap<Integer, String>();
			field_inquiry10.put(0, "destAmount");
			field_inquiry10.put(1, "feeAmount");
			field_inquiry10.put(2, "senderName");
			field_inquiry10.put(3, "senderAddress");
			field_inquiry10.put(4, "senderCity");
			field_inquiry10.put(5, "senderCountry");
			field_inquiry10.put(6, "senderID");
			field_inquiry10.put(7, "senderPhone");
			field_inquiry10.put(8, "recipientName");
			field_inquiry10.put(9, "recipientAddress");
			field_inquiry10.put(10, "recipientCity");
			field_inquiry10.put(11, "recipientCountry");
			field_inquiry10.put(12, "recipientPhone");
			
			field_inquiry3 = new TreeMap<Integer, String>();
			field_inquiry3.put(0, "nama1");
			field_inquiry3.put(1, "kelamin1");
			field_inquiry3.put(2, "alamat1");
			field_inquiry3.put(3, "kota1");
			field_inquiry3.put(4, "kodepos1");
			field_inquiry3.put(5, "negara1");
			field_inquiry3.put(6, "tipe1");
			field_inquiry3.put(7, "idcard1");
			field_inquiry3.put(8, "tempat1");
			field_inquiry3.put(9, "tanggal1");
			field_inquiry3.put(10, "telp1");
			field_inquiry3.put(11, "nama2");
			field_inquiry3.put(12, "kelamin2");
			field_inquiry3.put(13, "alamat2");
			field_inquiry3.put(14, "kota2");
			field_inquiry3.put(15, "kodepos2");
			field_inquiry3.put(16, "negara2");
			field_inquiry3.put(17, "tipe2");
			field_inquiry3.put(18, "idcard2");
			field_inquiry3.put(19, "tempat2");
			field_inquiry3.put(20, "tanggal2");
			field_inquiry3.put(21, "telp2");

			field_inquiry11 = new TreeMap<Integer, String>();
			field_inquiry11.put(0, "nama1");
			field_inquiry11.put(1, "kelamin1");
			field_inquiry11.put(2, "alamat1");
			field_inquiry11.put(3, "kota1");
			field_inquiry11.put(4, "kodepos1");
			field_inquiry11.put(5, "negara1");
			field_inquiry11.put(6, "tipe1");
			field_inquiry11.put(7, "idcard1");
			field_inquiry11.put(8, "tempat1");
			field_inquiry11.put(9, "tanggal1");
			field_inquiry11.put(10, "telp1");
			field_inquiry11.put(11, "nama2");
			field_inquiry11.put(12, "kelamin2");
			field_inquiry11.put(13, "alamat2");
			field_inquiry11.put(14, "kota2");
			field_inquiry11.put(15, "kodepos2");
			field_inquiry11.put(16, "negara2");
			field_inquiry11.put(17, "tipe2");
			field_inquiry11.put(18, "idcard2");
			field_inquiry11.put(19, "tempat2");
			field_inquiry11.put(20, "tanggal2");
			field_inquiry11.put(21, "telp2");
			field_inquiry11.put(22, "destbankacc");

			field_inquiry4 = new TreeMap<Integer, String>();
			field_inquiry4.put(0, "dest1Acc");
        
			field_inquiry6 = new TreeMap<Integer, String>();
			field_inquiry6.put(0, "fee");

			server = new ServerSocket(portServer);
			
			while (!Thread.currentThread().isInterrupted()) 
			{
                Socket socket = server.accept();
                new Thread(new SocketThread(socket)).start();				
			}
			
        } 
        catch (FileNotFoundException e) 
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		} 
        catch (IOException e) 
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		}
		
	}
	
	/*Extract 0200 ISO Request*/
	public void extractMain(String[] input, String text)
	{
		String[] output = new String[input.length];
		
		int[] index = new int[28];
		for(int i=0;i<input.length;i++)
		{
			if(i==3)
			{		
				input[i]=new Integer(new Integer(input[i]).intValue()-len2).toString();
			}
			if(i==13)
			{
				input[i]=new Integer(new Integer(input[i]).intValue()-len32).toString();
			}
			if(i==14)
			{
				input[i]=new Integer(new Integer(input[i]).intValue()-len33).toString();
			}
			if(i==21)	
			{	
				input[21]=output[20];
			}	
			index[i]=0;
			for(int j=0;j<=i;j++)
			{
				index[i]=index[i]+new Integer(input[j]).intValue();
			}			
			if(i>0 && i<=input.length-1)
			{
				output[i]=text.substring(index[i-1], index[i]);
				if(i==1)
				{
					mti = output[i]; 
					logger.info("[Request | MTI ] : "+mti);
				}
				else if(i==2)
				{
					bit1 = output[i];
					logger.info("[Request | Field (1) ] : "+bit1);
				}
				else if(i==3)
				{
					bit2 = output[i];
					bit2 = bit2.substring(2);
					logger.info("[Request | Field (2) ] : "+bit2);
				}
				else if(i==4)
				{
					bit3 = output[i];
					logger.info("[Request | Field (3) ] : "+bit3);
				}
				else if(i==5)
				{
					bit4 = output[i];
					logger.info("[Request | Field (4) ] : "+bit4);
				}
				else if(i==6)
				{
					bit7 = output[i];
					logger.info("[Request | Field (7) ] : "+bit7);
				}
				else if(i==7)
				{
					bit11 = output[i];
					logger.info("[Request | Field (11) ] : "+bit11);
				}
				else if(i==8)
				{
					bit12 = output[i];
					logger.info("[Request | Field (12) ] : "+bit12);
				}
				else if(i==9)
				{
					bit13 = output[i];
					logger.info("[Request | Field (13) ] : "+bit13);
				}
				else if(i==10)
				{
					bit14 = output[i];
					logger.info("[Request | Field (14) ] : "+bit14);
				}
				else if(i==11)
				{
					bit15 = output[i];					
					logger.info("[Request | Field (15) ] : "+bit15);
				}
				else if(i==12)
				{
					bit18 = output[i];					
					logger.info("[Request | Field (18) ] : "+bit18);
				}
				else if(i==13)
				{
					bit32 = output[i];
					bit32 = bit32.substring(2);
					logger.info("[Request | Field (32) ] : "+bit32);
				}
				else if(i==14)
				{
					bit33 = output[i];
					bit33 = bit33.substring(2);					
					logger.info("[Request | Field (33) ] : "+bit33);
				}
				else if(i==15)
				{
					bit37 = output[i];					
					logger.info("[Request | Field (37) ] : "+bit37);
				}
				else if(i==16)
				{
					bit41 = output[i];					
					logger.info("[Request | Field (41) ] : "+bit41);
				}
				else if(i==17)
				{
					bit42 = output[i];					
					logger.info("[Request | Field (42) ] : "+bit42);
				}
				else if(i==18)
				{
					bit43 = output[i];					
					logger.info("[Request | Field (43) ] : "+bit43);
				}
				else if(i==19)
				{
					bit49 = output[i];					
					logger.info("[Request | Field (49) ] : "+bit49);
				}
				else if(i==21)
				{
					bit61 = output[i];					
					logger.info("[Request | Field (61) ] : "+bit61);
				}
				else if(i==22)
				{
					bit103 = output[i];
					bit103 = bit103.substring(2);
					logger.info("[Request | Field (103) ] : "+bit103);
				}
			}
		}
	}
	
	/*Extract 0800 ISO Request*/
	public void extractMain0800(String[] input, String text)
	{
		String[] output = new String[input.length];
		
		int[] index = new int[28];
		for(int i=0;i<input.length;i++)
		{
			index[i]=0;
			for(int j=0;j<=i;j++)
			{
				index[i]=index[i]+new Integer(input[j]).intValue();
			}			
			if(i>0 && i<=input.length-1)
			{
				output[i]=text.substring(index[i-1], index[i]);
				if(i==1)
				{
					mti = output[i]; 
					logger.info("[Request | MTI ] : "+mti);
				}
				else if(i==2)
				{
					bit1 = output[i];
					logger.info("[Request | Field (1) ] : "+bit1);
				}
				else if(i==3)
				{
					bit7 = output[i];
					logger.info("[Request | Field (7) ] : "+bit7);
				}
				else if(i==4)
				{
					bit11 = output[i];
					logger.info("[Request | Field (11) ] : "+bit11);
				}
				else if(i==5)
				{
					bit33 = output[i];
					bit33 = bit33.substring(2);					
					logger.info("[Request | Field (33) ] : "+bit33);
				}
				else if(i==6)
				{
					bit70 = output[i];
					logger.info("[Request | Field (70) ] : "+bit70);
				}
			}
		}
	}

	/*Extract 0210 ISO Response*/
	public void extractMainResponse(String[] input, String text)
	{
		String[] output = new String[input.length];
		
		int[] index = new int[30];
		for(int i=0;i<input.length;i++)
		{
			if(i==23)	
			{	
				input[23]=output[22];
			}	
			index[i]=0;
			for(int j=0;j<=i;j++)
			{
				index[i]=index[i]+new Integer(input[j]).intValue();
			}			
			if(i>0 && i<=input.length-1)
			{
				output[i]=text.substring(index[i-1], index[i]);
				if(i==16)
				{
					bit38res = output[i];					
				}
				else if(i==17)
				{
					bit39res = output[i];					
				}
				else if(i==23)
				{
					bit61 = output[i];					
				}
			}
		}
	}

	/*Extract BIT 61*/
	public String[] extract(String[] input, String text)
	{
		String[] output = new String[input.length];
		
		int[] index = new int[28];
		for(int i=0;i<input.length;i++)
		{
			index[i]=0;
			for(int j=0;j<=i;j++)
			{
				index[i]=index[i]+new Integer(input[j]).intValue();
			}			
			if(i>0 && i<=input.length-1)
			{
				output[i]=text.substring(index[i-1], index[i]).trim();
			}
		}
		return output;
		
	}
	

	public static void main(String[] args)
	{
		PropertyConfigurator.configure("conf/log4j.properties");
		logger.info("ISO Listener is running");    		    		
		try
        {

			ISO iso = new ISO();
			
	      //String message = "02000000000002000000000000000000000019000000019510000126138009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360447327804630620008                          00000030000000000000000077012606    AHMAD JUNAEDHY                MJL SINDOMULYO 48 A PURWODADI-BMALANG              65125     INDONESIA       KTP       3573012907860004         MALANG              2907198608778073257    SITI MASRODIAH                FJLN  LEUWIDAHU KALER NO 107   BANDUNG             46151     INDONESIA       KTP       327804630620008          TASIKMALAYA         23061982087826831788   06002013";
	      //String message = "02000000000002000000000000000000000019000000019510000126150009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360077327804630620008                          00000030000000000000000077012606    06002013";
	      //String message = "02000000000002000000000000000000000019000000019510000126150009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360077327804630620008                          00000030000000000000000077012606    06002015";
		  //String message = "02000000000002000000000000000000000019000000019510000126138009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360053327804630620008                          77012606    06002014";
		  //String message = "02000000000002000000000000000000000019000000019510000126150009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360262327804630620008                          00000030000000000000000077012606    AHMAD JUNAEDHY                MJL SINDOMULYO 48 A PURWODADI-BMALANG              65125     INDONESIA       KTP       3573012907860004         MALANG              2907198608778073257    06002014";
		  //String message = "02000000000002000000000000000000000019000000019510000126150009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360077327804630620008                          00000030000000000000000077012606    06002016";
		    
		
		  //String message = "02000000000002000000000000000000000019000000019510000126138009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360053327804630620008                          77012606    06002023";
		  //String message = "02000000000002000000000000000000000019000000019510000126150009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360262327804630620008                          00000030000000000000000077012606    AHMAD JUNAEDHY                MJL SINDOMULYO 48 A PURWODADI-BMALANG              65125     INDONESIA       KTP       3573012907860004         MALANG              2907198608778073257    06002023";
		  //String message = "02000000000002000000000000000000000019000000019510000126150009900000030000006060736090126121436090606000006076010037700677093400000001261312606   Q04261         UPC PLAZA CIBUBUR                       360077327804630620008                          00000030000000000000000077012606    06002025";
        }
        catch (Exception e)
        {
			logger.error(e.getMessage());
        }
	}

	/*Recieve ISO Request*/
	public class SocketThread implements Runnable 
	{

		Socket sock;
		
        public SocketThread(Socket sock) 
        {
            this.sock = sock;
        }
				
		public void run() 
        {
			int len = 0;
			int len1 = 0;
			int len3 = 0;
			int len4 = 0;
			String chs = "";
			try 
			{
				logger.info("[ISO Connection from ] : "+sock.getInetAddress()+" Port : "+sock.getPort());
								
				BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(sock.getOutputStream())));
		    	StringBuffer buffer = new StringBuffer("");
		    	int i=0;
		    	int max = 216;
		    	String mtid = "";
		    	String length = "";
		    	String header = "";
		    	int y = 0;
	            while (true) 
	            {
	            	if(br.ready())
	            	{	
		                int ch = br.read();
		                buffer.append((char) ch);
	
		                if(i>1 && i<=5)
		                {
		                	chs = "0"+(char) ch;	              
		                	mtid=mtid+new Integer(chs.trim()).intValue();
		                }
		                else if(i==6)
		                {
		                	if(mtid.compareTo("0800")==0)
		                	{
		                		max=max-151;
		                	}
		                	else if(mtid.compareTo("0200")==0)
		                	{
		                		y=1;
		                	}
		                	else
		                	{
		                		parseISOMessage(mtid, mtid);

			    				length=String.format("%4s",Integer.toHexString(responseISO.length())).replace(" ","0");
			    				header=hexToAscii(length.substring(2));
			    				header=header+hexToAscii(length.substring(0,2));
			    				responseISO=header+responseISO;

			    				wr.write(responseISO);
			    		    	wr.flush();
			    	            logger.info("[ISO Send Response] : "+responseISO);
			    	            max=216;
			    	            mtid = "";
			    	            i=-1;
			    	            length = "";
			    	            header = "";  
			    	            buffer = new StringBuffer("");
		                		br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		                		y = 0;
		                	}
		                }
		                if(i==38 && y==1)
		                {
		                	chs = "0"+(char) ch;	              		                	
		                	len1=new Integer(chs.trim()).intValue()*10;
		                }
		                else if(i==39 && y==1)
		                {
		                	chs = "0"+(char) ch;	              		                			                	
		                	len1=len1+new Integer(chs.trim()).intValue();
		                }
		                else if(i==40 && y==1)
		                {
		                	max=max-(19-len1);
		                	len2 = 19-len1;
		                }
		                
		                if(i==(115-len2) && y==1)
		                {
		                	chs = "0"+(char) ch;	              		                	
		                	len3=new Integer(chs.trim()).intValue()*10;
		                }
		                else if(i==(116-len2) && y==1)
		                {
		                	chs = "0"+(char) ch;	              		                			                	
		                	len3=len3+new Integer(chs.trim()).intValue();
		                }
		                else if(i==(117-len2) && y==1)
		                {
		                	max=max-(3-len3);
		                	len32 = 3-len3;
		                }

		                if(i==(120-len2-len32) && y==1)
		                {
		                	chs = "0"+(char) ch;	              		                	
		                	len4=new Integer(chs.trim()).intValue()*10;
		                }
		                else if(i==(121-len2-len32) && y==1)
		                {
		                	chs = "0"+(char) ch;	              		                			                	
		                	len4=len4+new Integer(chs.trim()).intValue();
		                }
		                else if(i==(122-len2-len32) && y==1)
		                {
		                	max=max-(6-len4);
		                	len33 = 6-len4;
		                }
		                
		                if(i==(206-len2-len32-len33))
		                {
		                	chs = "0"+(char) ch;
		                	len=new Integer(chs.trim()).intValue()*100;
		                }
		                else if(i==(207-len2-len32-len33))
		                {
		                	chs = "0"+(char) ch;
		                	len=len+new Integer(chs.trim()).intValue()*10;
		                }
		                else if(i==(208-len2-len32-len33))
		                {
		                	chs = "0"+(char) ch;
		                	len=len+new Integer(chs.trim()).intValue();
		                }
		                else if(i==(209-len2-len32-len33))
		                {
		                	max=max+len+1;
		                }
		                if(i==max-1)
		                {
		                	String clientRequest = buffer.toString();
		                	clientRequest = clientRequest.substring(2);
		    	            logger.info("[ISO Recieved Request] : "+clientRequest);
		    				parseISOMessage(clientRequest, mtid);
		    				
		    				length=String.format("%4s",Integer.toHexString(responseISO.length())).replace(" ","0");
		    				header=hexToAscii(length.substring(2));
		    				header=header+hexToAscii(length.substring(0,2));
		    				responseISO=header+responseISO;
		    				wr.write(responseISO);
		    		    	wr.flush();
		    	            logger.info("[ISO Send Response] : "+responseISO);
		    	            max=216;
		    	            mtid = "";
		    	            i=-1;	                	
		    	            length = "";
		    	            header = "";  
		    	            buffer = new StringBuffer("");
	                		br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	                		y = 0;
		                }
		                i++;
	            	}
	            }
			}
			catch(IOException e)
			{
				logger.error(getClass().getName()+" "+e.getMessage());				
			}
        	
        }
		
	}

	/*Convert Hexadecimal to ASCII*/
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

	/*Convert Hexadecimal to Integer*/
	private static int hexToInt(char ch) 
	{
	  if ('a' <= ch && ch <= 'f') { return ch - 'a' + 10; }
	  if ('A' <= ch && ch <= 'F') { return ch - 'A' + 10; }
	  if ('0' <= ch && ch <= '9') { return ch - '0'; }
	  throw new IllegalArgumentException(String.valueOf(ch));
	}
	
	/*Create 0210 ISO Response*/
	public String buildISOResponseMessage() 
	{
		byte[] result = new byte[30];
		
        try 
        {
        	GenericPackager packager = new GenericPackager("conf/basic.xml");
        	
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
    		isoMsg.setMTI(mti);
    		isoMsg.set(2, bit2);
    		isoMsg.set(3, bit3);
    		isoMsg.set(4, bit4);
    		isoMsg.set(7, bit7);
    		isoMsg.set(11, bit11);
    		isoMsg.set(12, bit12);
    		isoMsg.set(13, bit13);
    		isoMsg.set(14, bit14);
    		isoMsg.set(15, bit15);
    		isoMsg.set(18, bit18);
    		isoMsg.set(32, bit32);
    		isoMsg.set(33, bit33);
    		isoMsg.set(37, bit37);
    		isoMsg.set(38, bit38);
    		isoMsg.set(39, bit39);
    		isoMsg.set(41, String.format("%-8s",bit41));
    		isoMsg.set(42, String.format("%-15s",bit42));
    		isoMsg.set(43, String.format("%-40s",bit43));
    		isoMsg.set(49, bit49);
    		isoMsg.set(61, bit61);
    		isoMsg.set(103,bit103);
    		
            printISOMessage("Response",isoMsg);
 
            result = isoMsg.pack();
            
        } 
        catch (ISOException e) 
        {
        	logger.error(this.getClass().getName()+" "+e.getMessage());
        }
        
        return new String(result);
    }	

	
	/*Create 0810 ISO Response*/
	public String buildISO810Message() 
	{
		byte[] result = new byte[30];
		
        try 
        {
        	GenericPackager packager = new GenericPackager("conf/basic0810.xml");
        	
			ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
    		isoMsg.setMTI("0810");
    		isoMsg.set(7, bit7);
    		isoMsg.set(11, bit11);
    		isoMsg.set(39, "00");
    		isoMsg.set(70, "001");
    		
            printISOMessage("Response",isoMsg);
 
            result = isoMsg.pack();
            
        } 
        catch (ISOException e) 
        {
        	logger.error(this.getClass().getName()+" "+e.getMessage());
        }
        
        return new String(result);
    }	
	
	/*Create BIT 61*/
	public String setBit61(Object[][] input, int length)
	{
		String result = "";
		
		for(int i=0;i<length;i++)
		{
			result=result+input[0][i].toString();
		}
		
		return result;
	}
	
	/*Parse 0200 & 0800 ISO Request*/
	public void parseISOMessage(String message, String mtid)
	{
        String response = "";
        int e=0;
        
        //try
        //{
        	if(mtid.compareTo("0800")==0)	// Process 0800 Request
        	{
				extractMain0800(main0800TAG,message);	// Extract ISO Request       		
        	}
        	else if(mtid.compareTo("0200")==0)	// Process 0200 Request
        	{
		        extractMain(mainTAG,message);	// Extract ISO Request         		
        	}
        	
			if(mti.compareTo("0800")==0)	// Process 0800 Request
			{
				
	        	bit39="00";
	        	
	        	responseISO=buildISO810Message();	//Create 0810 ISO Response
				
			}
			else if(bit3.compareTo("380099")==0 && mti.compareTo("0200")==0)	// Process 0200 & BIT3=380099 Request
	        {				

		        bit39="";
	        	
		        /*Create BIT 38*/
				results_inquiry12=connDb.getQuery("select bit38.nextval as bit38 from dual",new Object[]{0}, field_inquiry7, new Object[]{},0);
				if(connDb.getRowCount(0)>0)
				{	
					bit38 = results_inquiry12[0][0].toString();
					bit38 = String.format("%06d",new Integer(bit38).intValue());
				}	
				
				
		        /*Internal ISO Cash In Inquiry*/
				if(getCategory(bit3+bit103.substring(2),"internal1","iso"))
	        	{
					//2013
					//Extract BIT 61
	        		cashInRequest = extract(cashInRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashInRequest[4]=getFee(cashInRequest[3],bit103);
	        		stan=checkSTAN(bit11,bit103,bit3);
	        		
	        		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi,nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashInRequest[1],cashInRequest[2],cashInRequest[3],cashInRequest[4],cashInRequest[5],cashInRequest[6],cashInRequest[7],cashInRequest[8],cashInRequest[9],cashInRequest[10],cashInRequest[11],cashInRequest[12],cashInRequest[13],cashInRequest[14],cashInRequest[15],cashInRequest[16],cashInRequest[17],cashInRequest[18],cashInRequest[19],cashInRequest[20],cashInRequest[21],cashInRequest[22],cashInRequest[23],cashInRequest[24],cashInRequest[25],cashInRequest[26],cashInRequest[27]});
	        		mti="0210";

	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashInRequest[4]).intValue())+bit61.substring(65);
	        		if(!checkBlank(cashInRequest))
	        		{
	        			decimal2[0] = cashInRequest[3];
	        			decimal2[1] = cashInRequest[4];
	        			decimal2[2] = cashInRequest[16];
	        			decimal2[3] = cashInRequest[27];
	        			if(checkDecimal(decimal2))	/*Check if Destination Amount, feeAmount, senderPhone, recipientPhone value is decimal*/
	        			{
	        				if(stan)
	        				{	
								number = (long) Math.floor(Math.random() * 90000000L) + 10000000L;  
		        				refCode = "00"+new Long(number).toString();
		    	        		bit61=bit61.substring(0,25)+String.format("%16s",refCode)+bit61.substring(41);
		        				bit39="00";
	        				}
	        				else
	        				{
	        					bit39="94";
	        				}
	        			}
	        			else
	        			{
	        				bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set fee='"+cashInRequest[4]+"',kodetransfer='"+refCode.trim()+"', bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL", new Object[]{});
		       		
		       		responseISO=buildISOResponseMessage();	//Create cash In ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"bank1","iso"))	/*ISO to Bank Cash In Inquiry*/
	        	{
					//2019
					//Extract BIT 61
					cashInBankRequestISO = extract(cashInBankRequestTAG,bit61);
	        		//Get Fee Amount
					cashInBankRequestISO[4]=getFee(cashInBankRequestISO[3],bit103);
	        		
					//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi,nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2,destbankacc) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashInBankRequestISO[1],cashInBankRequestISO[2],cashInBankRequestISO[3],cashInBankRequestISO[4],cashInBankRequestISO[5],cashInBankRequestISO[6],cashInBankRequestISO[7],cashInBankRequestISO[8],cashInBankRequestISO[9],cashInBankRequestISO[10],cashInBankRequestISO[11],cashInBankRequestISO[12],cashInBankRequestISO[13],cashInBankRequestISO[14],cashInBankRequestISO[15],cashInBankRequestISO[16],cashInBankRequestISO[17],cashInBankRequestISO[18],cashInBankRequestISO[19],cashInBankRequestISO[20],cashInBankRequestISO[21],cashInBankRequestISO[22],cashInBankRequestISO[23],cashInBankRequestISO[24],cashInBankRequestISO[25],cashInBankRequestISO[26],cashInBankRequestISO[27],cashInBankRequestISO[28]});

	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashInBankRequestISO[4]).intValue())+bit61.substring(65);
	        		mti="0210";
	        		if(!checkBlank(cashInBankRequestISO))
	        		{
	        			decimal2[0] = cashInBankRequestISO[3];
	        			decimal2[1] = cashInBankRequestISO[4];
	        			decimal2[2] = cashInBankRequestISO[16];
	        			decimal2[3] = cashInBankRequestISO[27];
	        			if(checkDecimal(decimal2))	/*Check if Destination Amount, feeAmount, senderPhone, recipientPhone value is decimal*/
	        			{
	        				//Kirim ke Bank
	        				cashInBankRequest[0]="Description";
	        				cashInBankRequest[1]="userName";
	        				cashInBankRequest[2]="signature";
	        				cashInBankRequest[3]="007004";
	        				cashInBankRequest[4]=cashInBankRequestISO[28];
	        				cashInBankRequest[5]=cashInBankRequestISO[3];
	        				cashInBankRequest[6]="11";
	        				cashInBankRequest[7]=bit41;
	        				cashInBankRequest[8]="sourceID";
	        				cashInBankRequest[9]="sourceName";
	        				cashInBankRequest[10]=cashInBankRequestISO[6];
	        				cashInBankRequest[11]=cashInBankRequestISO[8];
	        				cashInBankRequest[12]=cashInBankRequestISO[13];
	        				cashInBankRequest[13]=cashInBankRequestISO[16];
	        				cashInBankRequest[14]=cashInBankRequestISO[9];
	        				cashInBankRequest[15]=cashInBankRequestISO[11];
	        				cashInBankRequest[16]=cashInBankRequestISO[17];
	        				cashInBankRequest[17]=cashInBankRequestISO[27];
	        				cashInBankRequest[18]=cashInBankRequestISO[19];
	        				cashInBankRequest[19]=cashInBankRequestISO[20];
	        				cashInBankRequest[20]=cashInBankRequestISO[22];
	        				cashInBankRequest[21]="notiDesc";
	        				cashInBankRequest[22]=bit11;
	        				
	        				http.sendHTTPPOST(bankurl, xmlRemittanceBank.cashInRequest(cashInBankRequest),new Integer(banktimeout),"urn:routeDx#route");	// Send XML Request to Bank

							if(http.getFail()==0)	
							{	
								cashInResponseBank=xmlRemittanceBank.cashInResponse(http.getPOSTResponse());	//Extract XML Response from Bank
								if(cashInResponseBank[2].compareTo("00")==0)
								{	
									bit39="00";
								}
								else
								{
									bit39=errorMapping(cashInResponseBank[2]);
								}
							}
							else
							{
								bit39="96";
							}
	        			}
	        			else
	        			{
	        				bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set fee='"+cashInBankRequestISO[4]+"',kodetransfer='"+cashInResponseBank[1]+"', bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL", new Object[]{});

		       		responseISO=buildISOResponseMessage();	//Create cash In ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"internal2","iso"))	/*Internal ISO Cash Out Inquiry*/
	        	{
					//2014
					int jml2=0;
					int jml3=0;
					//Extract BIT 61
	        		cashOutRequest = extract(cashOutRequestTAG,bit61);
	        		stan=checkSTAN(bit11,bit103,bit3);
	        		
					//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutRequest[1],cashOutRequest[2],cashOutRequest[3]});
	        		mti="0210";
	        		
	        		//Query Data from Cash In ISO Request
	        		results_inquiry2=connDb.getQuery("select amount,fee from transactioniso where bit39='00' and kodetransfer=? and bit3=? and bit103=?", new Object[]{"",""}, field_inquiry2, new Object[]{cashOutRequest[2],bit3,"002013"},0);

	        		if(connDb.getRowCount(0)>0)
	        		{
	        			jml2=1;
	        			results_inquiry2[0][1]=getFee(results_inquiry2[0][0].toString(),bit103);
	        			results_inquiry2[0][0]=String.format("%012d",new Integer(results_inquiry2[0][0].toString()).intValue());
	        			results_inquiry2[0][1]=String.format("%012d",new Integer(results_inquiry2[0][1].toString()).intValue());
	        			
	            		results_inquiry3=connDb.getQuery("select nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2 from transactioniso where bit39='00' and kodetransfer=? and bit3=? and bit103=?", new Object[]{"","","","","","","","","","","","","","","","","","","","","",""}, field_inquiry3, new Object[]{cashOutRequest[2],bit3,"002013"},0);
	            		if(connDb.getRowCount(0)>0)
	            		{
		        			jml3=1;
		        			results_inquiry3[0][0]=String.format("%-30s",results_inquiry3[0][0].toString());
		        			results_inquiry3[0][1]=String.format("%-1s",results_inquiry3[0][1].toString());
		        			results_inquiry3[0][2]=String.format("%-30s",results_inquiry3[0][2].toString());
		        			results_inquiry3[0][3]=String.format("%-20s",results_inquiry3[0][3].toString());
		        			results_inquiry3[0][4]=String.format("%-10s",results_inquiry3[0][4].toString());
		        			results_inquiry3[0][5]=String.format("%-16s",results_inquiry3[0][5].toString());
		        			results_inquiry3[0][6]=String.format("%-10s",results_inquiry3[0][6].toString());
		        			results_inquiry3[0][7]=String.format("%-25s",results_inquiry3[0][7].toString());
		        			results_inquiry3[0][8]=String.format("%-20s",results_inquiry3[0][8].toString());
		        			results_inquiry3[0][9]=String.format("%-8s",results_inquiry3[0][9].toString());
		        			results_inquiry3[0][10]=String.format("%-15s",results_inquiry3[0][10].toString());

		        			results_inquiry3[0][11]=String.format("%-30s",results_inquiry3[0][11].toString());
		        			results_inquiry3[0][12]=String.format("%-1s",results_inquiry3[0][12].toString());
		        			results_inquiry3[0][13]=String.format("%-30s",results_inquiry3[0][13].toString());
		        			results_inquiry3[0][14]=String.format("%-20s",results_inquiry3[0][14].toString());
		        			results_inquiry3[0][15]=String.format("%-10s",results_inquiry3[0][15].toString());
		        			results_inquiry3[0][16]=String.format("%-16s",results_inquiry3[0][16].toString());
		        			results_inquiry3[0][17]=String.format("%-10s",results_inquiry3[0][17].toString());
		        			results_inquiry3[0][18]=String.format("%-25s",results_inquiry3[0][18].toString());
		        			results_inquiry3[0][19]=String.format("%-20s",results_inquiry3[0][19].toString());
		        			results_inquiry3[0][20]=String.format("%-8s",results_inquiry3[0][20].toString());
		        			results_inquiry3[0][21]=String.format("%-15s",results_inquiry3[0][21].toString());
		        			bit61=String.format("%-25s",cashOutRequest[1])+String.format("%-16s",cashOutRequest[2])+setBit61(results_inquiry2,2)+String.format("%-12s",cashOutRequest[3])+setBit61(results_inquiry3,22);
	            		}
	            		else
	            		{
	            			bit61=String.format("%-25s",cashOutRequest[1])+String.format("%-16s",cashOutRequest[2])+setBit61(results_inquiry2,2)+String.format("%-12s",cashOutRequest[3])+padding(" ",185*2);
	            		}
	        		}
	        		else
	        		{
		        		//Query Data from Cash In XML Request
		        		results_inquiry14=connDb.getQuery("select destAmount, feeAmount, senderName,senderAddress,senderCity,senderCountry,senderID,senderPhone,recipientName,recipientAddress,recipientCity,recipientCountry,recipientPhone from transaction where resultCode='00' and refCode=? and transactionType='11'",new Object[]{"","","","","","","","","","","","",""}, field_inquiry10, new Object[]{cashOutRequest[2]},0);	        		
		        		if(connDb.getRowCount(0)>0)
		        		{
		        			jml2=2;
		        			jml3=2;
		        			results_inquiry14[0][1]=getFee(results_inquiry14[0][0].toString(),bit103);
		        			results_inquiry14[0][0]=String.format("%012d",new Integer(results_inquiry14[0][0].toString()).intValue());
		        			results_inquiry14[0][1]=String.format("%012d",new Integer(results_inquiry14[0][1].toString()).intValue());
		        			results_inquiry14[0][2]=String.format("%-30s",results_inquiry14[0][2].toString());
		        			results_inquiry14[0][3]=String.format("%-30s",results_inquiry14[0][3].toString());
		        			results_inquiry14[0][4]=String.format("%-20s",results_inquiry14[0][4].toString());
		        			results_inquiry14[0][5]=String.format("%-16s",results_inquiry14[0][5].toString());
		        			results_inquiry14[0][6]=String.format("%-25s",results_inquiry14[0][6].toString());
		        			results_inquiry14[0][7]=String.format("%-15s",results_inquiry14[0][7].toString());
		        			results_inquiry14[0][8]=String.format("%-30s",results_inquiry14[0][8].toString());
		        			results_inquiry14[0][9]=String.format("%-30s",results_inquiry14[0][9].toString());
		        			results_inquiry14[0][10]=String.format("%-20s",results_inquiry14[0][10].toString());
		        			results_inquiry14[0][11]=String.format("%-16s",results_inquiry14[0][11].toString());
		        			results_inquiry14[0][12]=String.format("%-15s",results_inquiry14[0][12].toString());
		        					        			
		        			bit61=results_inquiry14[0][0].toString()+results_inquiry14[0][1].toString();
		        			bit61=bit61+results_inquiry14[0][2].toString()+"M"+results_inquiry14[0][3].toString()+results_inquiry14[0][4].toString()+String.format("%-10s","Kode Pos")+results_inquiry14[0][5].toString()+String.format("%-10s","Tipe ID")+results_inquiry14[0][6].toString()+String.format("%-25s","Tempat Lahir")+"DDMMYYYY"+results_inquiry14[0][7].toString();
		        			bit61=bit61+results_inquiry14[0][8].toString()+"M"+results_inquiry14[0][9].toString()+results_inquiry14[0][10].toString()+String.format("%-10s","Kode Pos")+results_inquiry14[0][11].toString()+String.format("%-10s","Tipe ID")+String.format("%-25s","ID Card")+String.format("%-20s","Tempat Lahir")+"DDMMYYYY"+results_inquiry14[0][12].toString();
		        		}	
		        		else
		        		{	
		        			bit61=String.format("%-25s",cashOutRequest[1])+String.format("%-16s",cashOutRequest[2])+padding("0",24)+String.format("%-12s",cashOutRequest[3])+padding(" ",185*2);
		        		}	
	        		}
	        		if(checkBlank(cashOutRequest))
	        		{
	        			if(checkRefCode(cashOutRequest[2],"002013") || checkRefCodeXML(cashOutRequest[2]))	/*Check if Kode Transfer is available for XML / ISO*/
	        			{
	        				if(cashInCheck(cashOutRequest[2]) || cashInCheckXML(cashOutRequest[2]))	/*Check if Cash In & Cash In Confirmation are Success*/
	        				{
		        				if(checkRefCodeDouble(cashOutRequest[2],bit103,"500099")==true)	/*Check if there is a Success Cash Out Confirmation with same Kode Transfer*/
		        				{
			        				if(stan)
			        				{
			        					bit39="00";
			        				}
			        				else
			        				{
			        					bit39="94";
			        				}
		        				}
		        				else
		        				{
		        					bit39="87";
		        				}
	        				}
	        				else
	        				{
		        				bit39="96";	        					
	        				}
	        			}
	        			else
	        			{
	        				bit39="86";
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}        		

					//Update Response to Transactioniso Table
	        		if(jml2==2 && jml3==2)
	        		{	
	        			connDb.updateQuery("update transactioniso set amount='"+results_inquiry14[0][0].toString()+"',fee='"+results_inquiry14[0][1].toString()+"',nama1='"+results_inquiry14[0][2].toString()+"',alamat1='"+results_inquiry14[0][3].toString()+"',kota1='"+results_inquiry14[0][4].toString()+"',negara1='"+results_inquiry14[0][5].toString()+"',idcard1='"+results_inquiry14[0][6].toString()+"',telp1='"+results_inquiry14[0][7].toString()+"',nama2='"+results_inquiry14[0][8].toString()+"',alamat2='"+results_inquiry14[0][9].toString()+"',kota2='"+results_inquiry14[0][10].toString()+"',negara2='"+results_inquiry14[0][11].toString()+"',telp2='"+results_inquiry14[0][12].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutRequest[2]+"'", new Object[]{});
	        		}
	        		else if(jml2==1 && jml3==1)
	        		{	
	        			connDb.updateQuery("update transactioniso set amount='"+results_inquiry2[0][0].toString()+"',fee='"+results_inquiry2[0][1].toString()+"',nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutRequest[2]+"'", new Object[]{});
	        		}
	        		else
	        		{
	        			connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutRequest[2]+"'", new Object[]{});
	        		}
	        			 
		       		responseISO=buildISOResponseMessage();	//Create cash Out ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"https2","iso"))	/*ISO to Sing Cash Cash Out Inquiry*/
		       	{
					//2026
					//Extract BIT 61
		       		cashOutRequest = extract(cashOutRequestTAG,bit61);

		       		//Insert Request to Transactioniso Table
		       		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutRequest[1],cashOutRequest[2],cashOutRequest[3]});
		       		mti="0210";

	        		//Create BIT 61 for ISO Response
	        		bit61=cashOutRequest[1]+cashOutRequest[2]+padding(" ",24)+cashOutRequest[3]+padding(" ",185*2);
		       		if(checkBlank(cashOutRequest))
		       		{
						http.sendHTTPPOST(httpsurl+https.inquiry(bit41, "", "", cashOutRequest[2]),"",new Integer(httpstimeout),"");	//Send Request to Sing Cash
							
						if(http.getFail()==0)	
						{	
							inquiryResponse = https.inquiryResponse(http.getPOSTResponse());	//Extract Response from Sing Cash
							if(inquiryResponse[0].compareTo("00")==0)
							{
								bit39="00";
							}
							else
							{
	        					bit39="96";		        													
							}
						}
						else
						{
        					bit39="96";		        												
						}
		       		}
		       		else
		       		{
		       			bit39="30";
		       		}        		
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set amount='"+results_inquiry2[0][0].toString()+"',fee='"+results_inquiry2[0][1].toString()+"',nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutRequest[2]+"'", new Object[]{});

		       		responseISO=buildISOResponseMessage();	//Create cash Out ISO Response
		       	}
				else if(getCategory(bit3+bit103.substring(2),"hongleong2","iso"))	/*ISO to Hangleong Cash Out Inquiry*/
		       	{					
					e=0;
					//2023
					//Extract BIT 61
		       		cashOutRequest = extract(cashOutRequestTAG,bit61);

		       		//Insert Request to Transactioniso Table
		       		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutRequest[1],cashOutRequest[2],cashOutRequest[3]});
		       		mti="0210";

		       		if(checkBlank(cashOutRequest))
		       		{
		       			message=message.substring(0, message.length()-8)+"06002014";
	       				inputHangleon = message;
	       				maxHangleon = 671;

	       				response=isoclient.sendISOPacket(httpsurl, httpstimeout, inputHangleon, maxHangleon);	//Send ISO Request to Hangleong
	       				if(response.length()>=672)
	       				{
	       					response=response.substring(0,271)+String.format("%012d",new Integer(cashOutRequest[4]).intValue())+response.substring(283);
	       					responseISO=response;
	       					extractMainResponse(mainTAGres,response);	//Extract Response from Hangleong
	       					bit39 = bit39res;
	       					bit38 = bit38res;
	       					e=1;
	       				}
	       				else
	       				{
	       					logger.error("ISO Response Message Length less than 672 characters");
	       					bit39="96";
	       				}
		       		}
		       		else
		       		{
		       			bit39="30";
		       		}        		
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set amount='"+results_inquiry2[0][0].toString()+"',fee='"+results_inquiry2[0][1].toString()+"',nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutRequest[2]+"'", new Object[]{});

		       		if(e==0)	responseISO=buildISOResponseMessage();	//Create cash Out ISO Response
		       	}
				else if(getCategory(bit3+bit103.substring(2),"internal5","iso"))	/*Internal ISO Refund Inquiry*/
	        	{
					//2018
					int jml2=0;
					int jml3=0;
					//Extract BIT 61
	        		reFundRequest = extract(reFundRequestTAG,bit61);
	        		stan=checkSTAN(bit11,bit103,bit3);
	        		
		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,reFundRequest[1],reFundRequest[2],reFundRequest[3]});
	        		mti="0210";

	        		//Query Data from Cash In ISO Request
	        		results_inquiry2=connDb.getQuery("select amount,fee from transactioniso where bit39='00' and kodetransfer=? and bit3=? and bit103=?", new Object[]{"",""}, field_inquiry2, new Object[]{reFundRequest[2],bit3,"002013"},0);
	        		
	        		if(connDb.getRowCount(0)>0)
	        		{
	        			jml2=1;
	        			results_inquiry2[0][1]=getFee(results_inquiry2[0][0].toString(),bit103);
	        			results_inquiry2[0][0]=String.format("%012d",new Integer(results_inquiry2[0][0].toString()).intValue());
	        			results_inquiry2[0][1]=String.format("%012d",new Integer(results_inquiry2[0][1].toString()).intValue());
	        			
	            		results_inquiry3=connDb.getQuery("select nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2 from transactioniso where bit39='00' and kodetransfer=? and bit3=? and bit103=?", new Object[]{"","","","","","","","","","","","","","","","","","","","","",""}, field_inquiry3, new Object[]{reFundRequest[2],bit3,"002013"},0);
	            		if(connDb.getRowCount(0)>0)
	            		{
		        			jml3=1;
		        			results_inquiry3[0][0]=String.format("%-30s",results_inquiry3[0][0].toString());
		        			results_inquiry3[0][1]=String.format("%-1s",results_inquiry3[0][1].toString());
		        			results_inquiry3[0][2]=String.format("%-30s",results_inquiry3[0][2].toString());
		        			results_inquiry3[0][3]=String.format("%-20s",results_inquiry3[0][3].toString());
		        			results_inquiry3[0][4]=String.format("%-10s",results_inquiry3[0][4].toString());
		        			results_inquiry3[0][5]=String.format("%-16s",results_inquiry3[0][5].toString());
		        			results_inquiry3[0][6]=String.format("%-10s",results_inquiry3[0][6].toString());
		        			results_inquiry3[0][7]=String.format("%-25s",results_inquiry3[0][7].toString());
		        			results_inquiry3[0][8]=String.format("%-20s",results_inquiry3[0][8].toString());
		        			results_inquiry3[0][9]=String.format("%-8s",results_inquiry3[0][9].toString());
		        			results_inquiry3[0][10]=String.format("%-15s",results_inquiry3[0][10].toString());

		        			results_inquiry3[0][11]=String.format("%-30s",results_inquiry3[0][11].toString());
		        			results_inquiry3[0][12]=String.format("%-1s",results_inquiry3[0][12].toString());
		        			results_inquiry3[0][13]=String.format("%-30s",results_inquiry3[0][13].toString());
		        			results_inquiry3[0][14]=String.format("%-20s",results_inquiry3[0][14].toString());
		        			results_inquiry3[0][15]=String.format("%-10s",results_inquiry3[0][15].toString());
		        			results_inquiry3[0][16]=String.format("%-16s",results_inquiry3[0][16].toString());
		        			results_inquiry3[0][17]=String.format("%-10s",results_inquiry3[0][17].toString());
		        			results_inquiry3[0][18]=String.format("%-25s",results_inquiry3[0][18].toString());
		        			results_inquiry3[0][19]=String.format("%-20s",results_inquiry3[0][19].toString());
		        			results_inquiry3[0][20]=String.format("%-8s",results_inquiry3[0][20].toString());
		        			results_inquiry3[0][21]=String.format("%-15s",results_inquiry3[0][21].toString());
		        			bit61=String.format("%-25s",reFundRequest[1])+String.format("%-16s",reFundRequest[2])+setBit61(results_inquiry2,2)+String.format("%-12s",reFundRequest[3])+setBit61(results_inquiry3,22);
	            		}
	            		else
	            		{
	            			bit61=String.format("%-25s",reFundRequest[1])+String.format("%-16s",reFundRequest[2])+setBit61(results_inquiry2,2)+String.format("%-12s",reFundRequest[3])+padding(" ",185*2);
	            		}
	        		}
	        		else
	        		{
		        		//Query Data from Cash In XML Request
		        		results_inquiry14=connDb.getQuery("select destAmount, feeAmount, senderName,senderAddress,senderCity,senderCountry,senderID,senderPhone,recipientName,recipientAddress,recipientCity,recipientCountry,recipientPhone from transaction where resultCode='00' and refCode=? and transactionType='11'",new Object[]{"","","","","","","","","","","","",""}, field_inquiry10, new Object[]{reFundRequest[2]},0);	        		
		        		if(connDb.getRowCount(0)>0)
		        		{
		        			jml2=2;
		        			jml3=2;
		        			results_inquiry14[0][1]=getFee(results_inquiry14[0][0].toString(),bit103);
		        			results_inquiry14[0][0]=String.format("%012d",new Integer(results_inquiry14[0][0].toString()).intValue());
		        			results_inquiry14[0][1]=String.format("%012d",new Integer(results_inquiry14[0][1].toString()).intValue());
		        			results_inquiry14[0][2]=String.format("%-30s",results_inquiry14[0][2].toString());
		        			results_inquiry14[0][3]=String.format("%-30s",results_inquiry14[0][3].toString());
		        			results_inquiry14[0][4]=String.format("%-20s",results_inquiry14[0][4].toString());
		        			results_inquiry14[0][5]=String.format("%-16s",results_inquiry14[0][5].toString());
		        			results_inquiry14[0][6]=String.format("%-25s",results_inquiry14[0][6].toString());
		        			results_inquiry14[0][7]=String.format("%-15s",results_inquiry14[0][7].toString());
		        			results_inquiry14[0][8]=String.format("%-30s",results_inquiry14[0][8].toString());
		        			results_inquiry14[0][9]=String.format("%-30s",results_inquiry14[0][9].toString());
		        			results_inquiry14[0][10]=String.format("%-20s",results_inquiry14[0][10].toString());
		        			results_inquiry14[0][11]=String.format("%-16s",results_inquiry14[0][11].toString());
		        			results_inquiry14[0][12]=String.format("%-15s",results_inquiry14[0][12].toString());
		        					        			
		        			bit61=results_inquiry14[0][0].toString()+results_inquiry14[0][1].toString();
		        			bit61=bit61+results_inquiry14[0][2].toString()+"M"+results_inquiry14[0][3].toString()+results_inquiry14[0][4].toString()+String.format("%-10s","Kode Pos")+results_inquiry14[0][5].toString()+String.format("%-10s","Tipe ID")+results_inquiry14[0][6].toString()+String.format("%-25s","Tempat Lahir")+"DDMMYYYY"+results_inquiry14[0][7].toString();
		        			bit61=bit61+results_inquiry14[0][8].toString()+"M"+results_inquiry14[0][9].toString()+results_inquiry14[0][10].toString()+String.format("%-10s","Kode Pos")+results_inquiry14[0][11].toString()+String.format("%-10s","Tipe ID")+String.format("%-25s","ID Card")+String.format("%-20s","Tempat Lahir")+"DDMMYYYY"+results_inquiry14[0][12].toString();
		        		}	
		        		else
		        		{	
		        			bit61=String.format("%-25s",reFundRequest[1])+String.format("%-16s",reFundRequest[2])+padding("0",24)+String.format("%-12s",reFundRequest[3])+padding(" ",185*2);
		        		}	
	        		}

	        		if(checkBlank(reFundRequest))
	        		{
        				if(stan)
        				{	
        					if(checkRefCode(reFundRequest[2],"002013") || checkRefCodeXML(cashOutRequest[2]))	/*Check if Kode Transfer is available for XML / ISO*/
        					{	
    	        				if(cashInCheck(reFundRequest[2]) || cashInCheckXML(reFundRequest[2]))	/*Check if Cash In & Cash In Confirmation are Success*/
    	        				{	
	        						if(checkRefCodeDouble(reFundRequest[2],bit103,"500099")==true)	/*Check if there is a Success Refund Confirmation with same Kode Transfer*/
	        						{
	        							bit39="00";
	        						}
	        						else
	        						{
	        							bit39="87";
	        						}
    	        				}
    	        				else
    	        				{
        							bit39="96";
    	        				}
        					}	
        					else
        					{
		        				bit39="86";        						
        					}
        				}
        				else
        				{
        					bit39="94";
        				}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
					//Update Response to Transactioniso Table
	        		if(jml2==2 && jml3==2)
	        		{	
	        			connDb.updateQuery("update transactioniso set amount='"+results_inquiry14[0][0].toString()+"',fee='"+results_inquiry14[0][1].toString()+"',nama1='"+results_inquiry14[0][2].toString()+"',alamat1='"+results_inquiry14[0][3].toString()+"',kota1='"+results_inquiry14[0][4].toString()+"',negara1='"+results_inquiry14[0][5].toString()+"',idcard1='"+results_inquiry14[0][6].toString()+"',telp1='"+results_inquiry14[0][7].toString()+"',nama2='"+results_inquiry14[0][8].toString()+"',alamat2='"+results_inquiry14[0][9].toString()+"',kota2='"+results_inquiry14[0][10].toString()+"',negara2='"+results_inquiry14[0][11].toString()+"',telp2='"+results_inquiry14[0][12].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+reFundRequest[2]+"'", new Object[]{});
	        		}
	        		else if(jml2==1 && jml3==1)
	        		{	
	        			connDb.updateQuery("update transactioniso set amount='"+results_inquiry2[0][0].toString()+"',fee='"+results_inquiry2[0][1].toString()+"',nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+reFundRequest[2]+"'", new Object[]{});
	        		}
	        		else
	        		{
	        			connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+reFundRequest[2]+"'", new Object[]{});
	        		}
		       		//Edit Fee di dlm ISO
		       		responseISO=buildISOResponseMessage();	//Create Refund ISO Response
	        	}	
				else
				{
					bit39="30";
					responseISO=buildISOResponseMessage();	//Create Error ISO Response
				}
	        }
	        else if(bit3.compareTo("500099")==0 && mti.compareTo("0200")==0)	// Process 0200 & BIT3=500099 Request
	        {
	            bit39="";

		        /*Create BIT 38*/
	        	results_inquiry12=connDb.getQuery("select bit38.nextval as bit38 from dual",new Object[]{0}, field_inquiry7, new Object[]{},0);
				if(connDb.getRowCount(0)>0)
				{	
					bit38 = results_inquiry12[0][0].toString();
					bit38 = String.format("%06d",new Integer(bit38).intValue());
				}	
				
				if(getCategory(bit3+bit103.substring(2),"internal1","iso"))
	        	{
					//2013
					int jml3=0;
					//Extract BIT 61
	        		cashInConfRequest = extract(cashInConfRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashInConfRequest[4]=getFee(cashInConfRequest[3],bit103);
	        		stan=checkSTAN(bit11,bit103,bit3);

		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashInConfRequest[1],cashInConfRequest[2],cashInConfRequest[3],cashInConfRequest[4],cashInConfRequest[5]});
	        		mti="0210";
	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashInConfRequest[4]).intValue())+bit61.substring(65);

	        		//Query Data from Cash In ISO Request
	        		results_inquiry3=connDb.getQuery("select nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2 from transactioniso where kodetransfer='"+cashInConfRequest[2]+"' and bit3='380099' and bit103='"+bit103+"'", new Object[]{"","","","","","","","","","","","","","","","","","","","","",""}, field_inquiry3, new Object[]{},0);
	        		if(connDb.getRowCount(0)>0)
	        		{
	        			jml3=1;
	        			results_inquiry3[0][0]=String.format("%-30s",results_inquiry3[0][0].toString());
	        			results_inquiry3[0][1]=String.format("%-1s",results_inquiry3[0][1].toString());
	        			results_inquiry3[0][2]=String.format("%-30s",results_inquiry3[0][2].toString());
	        			results_inquiry3[0][3]=String.format("%-20s",results_inquiry3[0][3].toString());
	        			results_inquiry3[0][4]=String.format("%-10s",results_inquiry3[0][4].toString());
	        			results_inquiry3[0][5]=String.format("%-16s",results_inquiry3[0][5].toString());
	        			results_inquiry3[0][6]=String.format("%-10s",results_inquiry3[0][6].toString());
	        			results_inquiry3[0][7]=String.format("%-25s",results_inquiry3[0][7].toString());
	        			results_inquiry3[0][8]=String.format("%-20s",results_inquiry3[0][8].toString());
	        			results_inquiry3[0][9]=String.format("%-8s",results_inquiry3[0][9].toString());
	        			results_inquiry3[0][10]=String.format("%-15s",results_inquiry3[0][10].toString());

	        			results_inquiry3[0][11]=String.format("%-30s",results_inquiry3[0][11].toString());
	        			results_inquiry3[0][12]=String.format("%-1s",results_inquiry3[0][12].toString());
	        			results_inquiry3[0][13]=String.format("%-30s",results_inquiry3[0][13].toString());
	        			results_inquiry3[0][14]=String.format("%-20s",results_inquiry3[0][14].toString());
	        			results_inquiry3[0][15]=String.format("%-10s",results_inquiry3[0][15].toString());
	        			results_inquiry3[0][16]=String.format("%-16s",results_inquiry3[0][16].toString());
	        			results_inquiry3[0][17]=String.format("%-10s",results_inquiry3[0][17].toString());
	        			results_inquiry3[0][18]=String.format("%-25s",results_inquiry3[0][18].toString());
	        			results_inquiry3[0][19]=String.format("%-20s",results_inquiry3[0][19].toString());
	        			results_inquiry3[0][20]=String.format("%-8s",results_inquiry3[0][20].toString());
	        			results_inquiry3[0][21]=String.format("%-15s",results_inquiry3[0][21].toString());
	        			bit61=bit61+setBit61(results_inquiry3,22);  
	        		}
	        		else
	        		{
	           			bit61=bit61+padding(" ",185*2);
	        		}
	        		if(checkBlank(cashInConfRequest))
	        		{
	        			decimal[0] = cashInConfRequest[3];
	        			decimal[1] = cashInConfRequest[4];
	        			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
	        			{
		        			if(checkRefCode(cashInConfRequest[2],bit103))	/*Check if Kode Transfer is available*/
		        			{
		        				if(checkRefCodeDouble(cashInConfRequest[2],bit103,bit3)==true)	/*Check if there is a cash In Confirmation with same Kode Transfer*/
		        				{	
		        					if(stan)
		        					{	
				        				if(eva.getIDISO(dest1Acc,"+"+bit2.substring(7), "Cashin Remittance", cashInConfRequest[3], "CASHIN", bit11, "notiDesc", cashInConfRequest[4])==0)	//Send eVa Request
				        				{
				        					bit39="00";
				        				}
				        				else
				        				{
				        					bit39="96";	        					
				        				}
		        					}
		        					else
		        					{
			        					bit39="94";	        							        						
		        					}
		        				}
		        				else
		        				{
		        					bit39="95";
		        				}
		        			}
		        			else
		        			{
		        				bit39="86";
		        			}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}        			
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}

					//Update Response to Transactioniso Table
	        		if(jml3>0)
	        		{	
	        			connDb.updateQuery("update transactioniso set nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInConfRequest[2]+"'", new Object[]{});        		
	        		}
	        		else
	        		{
	        			connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInConfRequest[2]+"'", new Object[]{});        			        			
	        		}
	        		responseISO=buildISOResponseMessage();	//Create cash In Confirmation ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"bank1","iso"))
	        	{
					//2019
					int jml3=0;
					//Extract BIT 61
					cashInConfBankRequestISO = extract(cashInConfBankRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashInConfBankRequestISO[4]=getFee(cashInConfBankRequestISO[3],bit103);

		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi,destbankacc) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashInConfBankRequestISO[1],cashInConfBankRequestISO[2],cashInConfBankRequestISO[3],cashInConfBankRequestISO[4],cashInConfBankRequestISO[5],cashInConfBankRequestISO[6]});
	        		mti="0210";
	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashInConfBankRequestISO[4]).intValue())+bit61.substring(65);

	        		//Query Data from Cash In ISO Request
	        		results_inquiry3=connDb.getQuery("select nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2,destbankacc from transactioniso where kodetransfer='"+cashInConfBankRequestISO[2]+"' and bit3='380099' and bit103='"+bit103+"'", new Object[]{"","","","","","","","","","","","","","","","","","","","","",""}, field_inquiry11, new Object[]{},0);
	        		if(connDb.getRowCount(0)>0)
	        		{
	        			jml3=1;
	        			results_inquiry3[0][0]=String.format("%-30s",results_inquiry3[0][0].toString());
	        			results_inquiry3[0][1]=String.format("%-1s",results_inquiry3[0][1].toString());
	        			results_inquiry3[0][2]=String.format("%-30s",results_inquiry3[0][2].toString());
	        			results_inquiry3[0][3]=String.format("%-20s",results_inquiry3[0][3].toString());
	        			results_inquiry3[0][4]=String.format("%-10s",results_inquiry3[0][4].toString());
	        			results_inquiry3[0][5]=String.format("%-16s",results_inquiry3[0][5].toString());
	        			results_inquiry3[0][6]=String.format("%-10s",results_inquiry3[0][6].toString());
	        			results_inquiry3[0][7]=String.format("%-25s",results_inquiry3[0][7].toString());
	        			results_inquiry3[0][8]=String.format("%-20s",results_inquiry3[0][8].toString());
	        			results_inquiry3[0][9]=String.format("%-8s",results_inquiry3[0][9].toString());
	        			results_inquiry3[0][10]=String.format("%-15s",results_inquiry3[0][10].toString());

	        			results_inquiry3[0][11]=String.format("%-30s",results_inquiry3[0][11].toString());
	        			results_inquiry3[0][12]=String.format("%-1s",results_inquiry3[0][12].toString());
	        			results_inquiry3[0][13]=String.format("%-30s",results_inquiry3[0][13].toString());
	        			results_inquiry3[0][14]=String.format("%-20s",results_inquiry3[0][14].toString());
	        			results_inquiry3[0][15]=String.format("%-10s",results_inquiry3[0][15].toString());
	        			results_inquiry3[0][16]=String.format("%-16s",results_inquiry3[0][16].toString());
	        			results_inquiry3[0][17]=String.format("%-10s",results_inquiry3[0][17].toString());
	        			results_inquiry3[0][18]=String.format("%-25s",results_inquiry3[0][18].toString());
	        			results_inquiry3[0][19]=String.format("%-20s",results_inquiry3[0][19].toString());
	        			results_inquiry3[0][20]=String.format("%-8s",results_inquiry3[0][20].toString());
	        			results_inquiry3[0][21]=String.format("%-15s",results_inquiry3[0][21].toString());
	        			results_inquiry3[0][22]=String.format("%-25s",results_inquiry3[0][22].toString());
	        			bit61=bit61+setBit61(results_inquiry3,23);  
	        		}
	        		else
	        		{
	           			bit61=bit61+padding(" ",185*2+25);
	        		}
	        		if(checkBlank(cashInConfRequest))
	        		{
	        			decimal[0] = cashInConfBankRequestISO[3];
	        			decimal[1] = cashInConfBankRequestISO[4];
	        			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
	        			{
	        				cashInConfBankRequest[0]="Description";
	        				cashInConfBankRequest[1]="userName";
	        				cashInConfBankRequest[2]="signature";
	        				cashInConfBankRequest[3]="007004";
	        				cashInConfBankRequest[4]=cashInConfBankRequestISO[6];
	        				cashInConfBankRequest[5]=cashInConfBankRequestISO[3];
	        				cashInConfBankRequest[6]=cashInConfBankRequestISO[4];
	        				cashInConfBankRequest[7]="12";
	        				cashInConfBankRequest[8]=bit41;
	        				cashInConfBankRequest[9]="sourceID";
	        				cashInConfBankRequest[10]="sourceName";
	        				cashInConfBankRequest[11]="senderName";
	        				cashInConfBankRequest[12]="senderAddress";
	        				cashInConfBankRequest[13]="senderID";
	        				cashInConfBankRequest[14]="senderPhone";
	        				cashInConfBankRequest[15]="senderCity";
	        				cashInConfBankRequest[16]="senderCountry";
	        				cashInConfBankRequest[17]="recipientName";
	        				cashInConfBankRequest[18]="recipientPhone";
	        				cashInConfBankRequest[19]="recipientAddress";
	        				cashInConfBankRequest[20]="recipientCity";
	        				cashInConfBankRequest[21]="recipientCountry";
	        				cashInConfBankRequest[22]="notiDesc";
	        				cashInConfBankRequest[23]=bit11;
	        				cashInConfBankRequest[24]="refCode";
		        				
							http.sendHTTPPOST(bankurl, xmlRemittanceBank.cashInConfRequest(cashInConfBankRequest),new Integer(banktimeout),"urn:routeDx#route");	//Send XML Request to Bank

							if(http.getFail()==0)	
							{	
								cashInConfResponseBank=xmlRemittanceBank.cashInConfResponse(http.getPOSTResponse());	//Extract XML Response fom Bank
								if(cashInConfResponseBank[2].compareTo("00")==0)
								{
									bit39="00";
								}
								else
								{
									bit39=errorMapping(cashInConfResponseBank[2]);
								}
							}
							else
							{
								bit39="96";
							}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}        			
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
	        		
					//Update Response to Transactioniso Table
	        		if(jml3>0)
	        		{	
	        			connDb.updateQuery("update transactioniso set destbankacc='"+results_inquiry3[0][22].toString()+"', nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInConfBankRequestISO[2]+"'", new Object[]{});        		
	        		}
	        		else
	        		{
	        			connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInConfBankRequestISO[2]+"'", new Object[]{});        		
	        		}
		       		responseISO=buildISOResponseMessage();	//Create cash In Confirmation ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"internal2","iso"))
	        	{
					//2014
					//Extract BIT 61
	        		cashOutConfRequest = extract(cashOutConfRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashOutConfRequest[4]=getFee(cashOutConfRequest[3],bit103);
	        		stan=checkSTAN(bit11,bit103,bit3);
	        		
		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi,nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutConfRequest[1],cashOutConfRequest[2],cashOutConfRequest[3],cashOutConfRequest[4],cashOutConfRequest[5],cashOutConfRequest[6],cashOutConfRequest[7],cashOutConfRequest[8],cashOutConfRequest[9],cashOutConfRequest[10],cashOutConfRequest[11],cashOutConfRequest[12],cashOutConfRequest[13],cashOutConfRequest[14],cashOutConfRequest[15],cashOutConfRequest[16]});
	        		mti="0210";
	        		
	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashOutConfRequest[4]).intValue())+bit61.substring(65);
	        		bit61=bit61.substring(0, 77);
	        		if(checkBlank(cashOutConfRequest))
	        		{
	        			decimal3[0] = cashOutConfRequest[3];
	        			decimal3[1] = cashOutConfRequest[4];
	        			decimal3[2] = cashOutConfRequest[16];
	        			if(checkDecimal(decimal3))	/*Check if Destination Amount, feeAmount, senderPhone value is decimal*/
	        			{
		        			if(checkRefCode(cashOutConfRequest[2],"002013") || checkRefCodeXML(cashOutConfRequest[2]))	/*Check if Kode Transfer is available for XML / ISO*/
		        			{
		        				if(checkRefCodeDouble(cashOutConfRequest[2],bit103,bit3)==true)	/*Check if there is a cash Out Confirmation with same Kode Transfer*/
		        				{		
			        				if(stan)
			        				{	
				        				if(eva.getIDISO(dest1Acc,"+"+bit2.substring(7), "Cashout Remittance", cashOutConfRequest[3], "CASHOUT", bit11, "notiDesc", cashOutConfRequest[4])==0)	// Send eVa Request
				        				{	
				        					bit39="00";
				        				}
				        				else
				        				{
				        					bit39="96";		        					
				        				}
			        				}
			        				else
			        				{
			        					bit39="94";
			        				}
		        				}
		        				else
		        				{
		        					bit39="87";
		        				}
		        			}
		        			else
		        			{
		        				bit39="86";
		        			}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutConfRequest[2]+"'", new Object[]{});        		
		       		responseISO=buildISOResponseMessage();	//Create cash Out Confirmation ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"https2","iso"))
		       	{
					//2026
					//Extract BIT 61
		       		cashOutConfRequest = extract(cashOutConfRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashOutConfRequest[4]=getFee(cashOutConfRequest[3],bit103);
	        		
		       		//Insert Request to Transactioniso Table
		       		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi,nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutConfRequest[1],cashOutConfRequest[2],cashOutConfRequest[3],cashOutConfRequest[4],cashOutConfRequest[5],cashOutConfRequest[6],cashOutConfRequest[7],cashOutConfRequest[8],cashOutConfRequest[9],cashOutConfRequest[10],cashOutConfRequest[11],cashOutConfRequest[12],cashOutConfRequest[13],cashOutConfRequest[14],cashOutConfRequest[15],cashOutConfRequest[16]});
		       		mti="0210";

	        		//Create BIT 61 for ISO Response
		       		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashOutConfRequest[4]).intValue())+bit61.substring(65);
		       		bit61=bit61.substring(0, 77);
		       		if(checkBlank(cashOutConfRequest))
		       		{
		       			decimal3[0] = cashOutConfRequest[3];
		       			decimal3[1] = cashOutConfRequest[4];
		       			decimal3[2] = cashOutConfRequest[16];
		       			if(checkDecimal(decimal3))	/*Check if Destination Amount, feeAmount, senderPhone value is decimal*/
		       			{
	        				if(eva.getIDISO(dest1Acc,"+"+bit2.substring(7), "Cashout Remittance", cashOutConfRequest[3], "CASHOUT", bit11, "notiDesc", cashOutConfRequest[4])==0)	// Send eVa Request
	        				{	
								http.sendHTTPPOST(httpsurl+https.submission(bit41, cashOutConfRequest[9], cashOutConfRequest[16], cashOutConfRequest[2], cashOutConfRequest[3]),"",new Integer(httpstimeout),"");	// Send Request to Sing Cash
								
								if(http.getFail()==0)	
								{	
									submissionResponse = https.submissionResponse(http.getPOSTResponse());	// Extract Response from Sing Cash
									if(submissionResponse[0].compareTo("00")==0)
									{
										bit39="00";
									}
									else
									{
			        					bit39="96";		        					
									}
		        				}
		        				else
		        				{
		        					bit39="96";		        					
		        				}
	        				}
	        				else
	        				{
	        					bit39="96";
	        				}
		       			}
		       			else
		       			{
		           			bit39="30";        				
		       			}
		       		}
		       		else
		       		{
		       			bit39="30";
		       		}
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutConfRequest[2]+"'", new Object[]{});
		       		responseISO=buildISOResponseMessage();	//Create cash Out Confirmation ISO Response
		       	}
				else if(getCategory(bit3+bit103.substring(2),"hongleong2","iso"))
		       	{
					e=0;
					//2023
					//Extract BIT 61
		       		cashOutConfRequest = extract(cashOutConfRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashOutConfRequest[4]=getFee(cashOutConfRequest[3],bit103);

		       		//Insert Request to Transactioniso Table
		       		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi,nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutConfRequest[1],cashOutConfRequest[2],cashOutConfRequest[3],cashOutConfRequest[4],cashOutConfRequest[5],cashOutConfRequest[6],cashOutConfRequest[7],cashOutConfRequest[8],cashOutConfRequest[9],cashOutConfRequest[10],cashOutConfRequest[11],cashOutConfRequest[12],cashOutConfRequest[13],cashOutConfRequest[14],cashOutConfRequest[15],cashOutConfRequest[16]});
		       		mti="0210";

	        		//Create BIT 61 for ISO Response
		       		bit61=bit61.substring(0, 77);
		       		if(checkBlank(cashOutConfRequest))
		       		{
		       			decimal3[0] = cashOutConfRequest[3];
		       			decimal3[1] = cashOutConfRequest[4];
		       			decimal3[2] = cashOutConfRequest[16];
		       			if(checkDecimal(decimal3))	/*Check if Destination Amount, feeAmount, senderPhone value is decimal*/
		       			{
	        				if(eva.getIDISO(dest1Acc,"+"+bit2.substring(7), "Cashout Remittance", cashOutConfRequest[3], "CASHOUT", bit11, "notiDesc", cashOutConfRequest[4])==0)	// Send eVa Request
	        				{	
	    		       			message=message.substring(0, message.length()-8)+"06002014";
			       				inputHangleon = message;
			       				maxHangleon = 301;
			       				response=isoclient.sendISOPacket(httpsurl, httpstimeout, inputHangleon, maxHangleon);	// Send Request to Hangleong
			       				if(response.length()>=302)
			       				{
			       					e=1;
			       					response=response.substring(0,271)+String.format("%012d",new Integer(cashOutConfRequest[4]).intValue())+response.substring(283);
			       					responseISO=response;
			       					extractMainResponse(mainTAGres,response);	// Extract Response from Hangleong
			       					bit39 = bit39res;
			       					bit38 = bit38res;
			       				}
			       				else
			       				{
			       					bit39="96";
			       					logger.error("ISO Response Message Length less than 302 characters");
			       				}
	        				}
	        				else
	        				{
	        					bit39="87";
	        				}
		       			}
		       			else
		       			{
		           			bit39="30";        				
		       			}
		       		}
		       		else
		       		{
		       			bit39="30";
		       		}
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashOutConfRequest[4]).intValue())+bit61.substring(65);

					//Update Response to Transactioniso Table
	        		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutConfRequest[2]+"'", new Object[]{});
		    		if(e==0)	responseISO=buildISOResponseMessage();	//Create cash Out Confirmation ISO Response
		       	}
				else if(getCategory(bit3+bit103.substring(2),"internal3","iso"))
	        	{
					//2015
					int jml3=0;
					//Extract BIT 61
	        		cashInCheckRequest = extract(cashInCheckRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashInCheckRequest[4]=getFee(cashInCheckRequest[3],bit103);
	        		stan=checkSTAN(bit11,bit103,bit3);

		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashInCheckRequest[1],cashInCheckRequest[2],cashInCheckRequest[3],cashInCheckRequest[4],cashInCheckRequest[5]});
	        		mti="0210";

	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashInCheckRequest[4]).intValue())+bit61.substring(65);

	        		//Query Data from Cash In ISO Request
	        		results_inquiry3=connDb.getQuery("select nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2 from transactioniso where kodetransfer=? and bit3=? and bit103=?", new Object[]{"","","","","","","","","","","","","","","","","","","","","",""}, field_inquiry3, new Object[]{cashInCheckRequest[2],"380099","002013"},0);
	        		if(connDb.getRowCount(0)>0)
	        		{
	        			jml3=1;
	        			results_inquiry3[0][0]=String.format("%-30s",results_inquiry3[0][0].toString());
	        			results_inquiry3[0][1]=String.format("%-1s",results_inquiry3[0][1].toString());
	        			results_inquiry3[0][2]=String.format("%-30s",results_inquiry3[0][2].toString());
	        			results_inquiry3[0][3]=String.format("%-20s",results_inquiry3[0][3].toString());
	        			results_inquiry3[0][4]=String.format("%-10s",results_inquiry3[0][4].toString());
	        			results_inquiry3[0][5]=String.format("%-16s",results_inquiry3[0][5].toString());
	        			results_inquiry3[0][6]=String.format("%-10s",results_inquiry3[0][6].toString());
	        			results_inquiry3[0][7]=String.format("%-25s",results_inquiry3[0][7].toString());
	        			results_inquiry3[0][8]=String.format("%-20s",results_inquiry3[0][8].toString());
	        			results_inquiry3[0][9]=String.format("%-8s",results_inquiry3[0][9].toString());
	        			results_inquiry3[0][10]=String.format("%-15s",results_inquiry3[0][10].toString());

	        			results_inquiry3[0][11]=String.format("%-30s",results_inquiry3[0][11].toString());
	        			results_inquiry3[0][12]=String.format("%-1s",results_inquiry3[0][12].toString());
	        			results_inquiry3[0][13]=String.format("%-30s",results_inquiry3[0][13].toString());
	        			results_inquiry3[0][14]=String.format("%-20s",results_inquiry3[0][14].toString());
	        			results_inquiry3[0][15]=String.format("%-10s",results_inquiry3[0][15].toString());
	        			results_inquiry3[0][16]=String.format("%-16s",results_inquiry3[0][16].toString());
	        			results_inquiry3[0][17]=String.format("%-10s",results_inquiry3[0][17].toString());
	        			results_inquiry3[0][18]=String.format("%-25s",results_inquiry3[0][18].toString());
	        			results_inquiry3[0][19]=String.format("%-20s",results_inquiry3[0][19].toString());
	        			results_inquiry3[0][20]=String.format("%-8s",results_inquiry3[0][20].toString());
	        			results_inquiry3[0][21]=String.format("%-15s",results_inquiry3[0][21].toString());
	        			bit61=bit61+setBit61(results_inquiry3,22);        			
	        		}
	        		else
	        		{
	           			bit61=bit61+padding(" ",185*2);
	        		}
	        		if(checkBlank(cashInCheckRequest))
	        		{
	        			decimal[0] = cashInCheckRequest[3];
	        			decimal[1] = cashInCheckRequest[4];
	        			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
	        			{
		        			if(checkRefCode(cashInCheckRequest[2],"002013"))	/*Check if Kode Transfer is available*/
		        			{
		        				if(cashInCheck(cashInCheckRequest[2]))	/*Check if Cash In & Cash In Confirmation are Success*/
		        				{
			        				if(stan)
			        				{	
			        					bit39="00";
			        				}
			        				else
			        				{
			        					bit39="94";
			        				}
		        				}
		        				else
		        				{
		        					bit39="70";	        					
		        				}
		        			}
		        			else
		        			{
		        				bit39="86";
		        			}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
	        		
					//Update Response to Transactioniso Table
	        		if(jml3>1)
	        		{	
	        			connDb.updateQuery("update transactioniso set nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInCheckRequest[2]+"'", new Object[]{});
	        		}
	        		else
	        		{
	        			connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInCheckRequest[2]+"'", new Object[]{});	        			
	        		}
		       		responseISO=buildISOResponseMessage();	//Create cash In Check Status ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"bank3","iso"))
	        	{
					//2021
					int jml3=0;
					//Extract BIT 61
					cashInCheckBankRequestISO = extract(cashInCheckBankRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashInCheckBankRequestISO[4]=getFee(cashInCheckBankRequestISO[3],bit103);
	        		
		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi,destbankacc) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashInCheckBankRequestISO[1],cashInCheckBankRequestISO[2],cashInCheckBankRequestISO[3],cashInCheckBankRequestISO[4],cashInCheckBankRequestISO[5],cashInCheckBankRequestISO[6]});
	        		mti="0210";

	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashInCheckBankRequestISO[4]).intValue())+bit61.substring(65);

	        		//Query Data from Cash In ISO Request
	        		results_inquiry3=connDb.getQuery("select nama1,kelamin1,alamat1,kota1,kodepos1,negara1,tipe1,idcard1,tempat1,tanggal1,telp1,nama2,kelamin2,alamat2,kota2,kodepos2,negara2,tipe2,idcard2,tempat2,tanggal2,telp2,destbankacc from transactioniso where kodetransfer=? and bit3=? and bit103=?", new Object[]{"","","","","","","","","","","","","","","","","","","","","",""}, field_inquiry11, new Object[]{cashInCheckBankRequestISO[2],"380099","00"+new Integer(new Integer(bit103).intValue()-2).toString()},0);
	        		if(connDb.getRowCount(0)>0)
	        		{
	        			jml3=1;
	        			results_inquiry3[0][0]=String.format("%-30s",results_inquiry3[0][0].toString());
	        			results_inquiry3[0][1]=String.format("%-1s",results_inquiry3[0][1].toString());
	        			results_inquiry3[0][2]=String.format("%-30s",results_inquiry3[0][2].toString());
	        			results_inquiry3[0][3]=String.format("%-20s",results_inquiry3[0][3].toString());
	        			results_inquiry3[0][4]=String.format("%-10s",results_inquiry3[0][4].toString());
	        			results_inquiry3[0][5]=String.format("%-16s",results_inquiry3[0][5].toString());
	        			results_inquiry3[0][6]=String.format("%-10s",results_inquiry3[0][6].toString());
	        			results_inquiry3[0][7]=String.format("%-25s",results_inquiry3[0][7].toString());
	        			results_inquiry3[0][8]=String.format("%-20s",results_inquiry3[0][8].toString());
	        			results_inquiry3[0][9]=String.format("%-8s",results_inquiry3[0][9].toString());
	        			results_inquiry3[0][10]=String.format("%-15s",results_inquiry3[0][10].toString());

	        			results_inquiry3[0][11]=String.format("%-30s",results_inquiry3[0][11].toString());
	        			results_inquiry3[0][12]=String.format("%-1s",results_inquiry3[0][12].toString());
	        			results_inquiry3[0][13]=String.format("%-30s",results_inquiry3[0][13].toString());
	        			results_inquiry3[0][14]=String.format("%-20s",results_inquiry3[0][14].toString());
	        			results_inquiry3[0][15]=String.format("%-10s",results_inquiry3[0][15].toString());
	        			results_inquiry3[0][16]=String.format("%-16s",results_inquiry3[0][16].toString());
	        			results_inquiry3[0][17]=String.format("%-10s",results_inquiry3[0][17].toString());
	        			results_inquiry3[0][18]=String.format("%-25s",results_inquiry3[0][18].toString());
	        			results_inquiry3[0][19]=String.format("%-20s",results_inquiry3[0][19].toString());
	        			results_inquiry3[0][20]=String.format("%-8s",results_inquiry3[0][20].toString());
	        			results_inquiry3[0][21]=String.format("%-15s",results_inquiry3[0][21].toString());
	        			results_inquiry3[0][22]=String.format("%-25s",results_inquiry3[0][22].toString());
	        			bit61=bit61+setBit61(results_inquiry3,23);        			
	        		}
	        		else
	        		{
	           			bit61=bit61+padding(" ",185*2+25);
	        		}
	        		if(checkBlank(cashInCheckBankRequestISO))
	        		{
	        			decimal[0] = cashInCheckBankRequestISO[3];
	        			decimal[1] = cashInCheckBankRequestISO[4];
	        			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
	        			{
		        			cashInCheckBankRequest[0]="userName";
			       			cashInCheckBankRequest[1]="signature";
			       			cashInCheckBankRequest[2]="007004";
			       			cashInCheckBankRequest[3]=cashInCheckBankRequestISO[3];
			       			cashInCheckBankRequest[4]="13";
			       			cashInCheckBankRequest[5]=bit41;
			       			cashInCheckBankRequest[6]="sourceID";
			       			cashInCheckBankRequest[7]=bit11;
			       			cashInCheckBankRequest[8]=cashInCheckBankRequestISO[2];

			       			http.sendHTTPPOST(bankurl, xmlRemittanceBank.cashInCheckRequest(cashInCheckBankRequest),new Integer(banktimeout),"urn:routeDx#route");	// Send XML Request to Bank

							if(http.getFail()==0)	
							{	
								cashInCheckResponseBank=xmlRemittanceBank.cashInCheckResponse(http.getPOSTResponse());	// Extract XML Response from Bank
								if(cashInCheckResponseBank[2].compareTo("00")==0)
								{	
									bit39="00";
								}
								else
								{
									bit39=errorMapping(cashInCheckResponseBank[2]);
								}
							}
							else
							{
								bit39="96";
							}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}

					//Update Response to Transactioniso Table
	        		if(jml3>0)
	        		{	
	        			connDb.updateQuery("update transactioniso set destbankacc='"+results_inquiry3[0][22].toString()+"',nama1='"+results_inquiry3[0][0].toString()+"',kelamin1='"+results_inquiry3[0][1].toString()+"',alamat1='"+results_inquiry3[0][2].toString()+"',kota1='"+results_inquiry3[0][3].toString()+"',kodepos1='"+results_inquiry3[0][4].toString()+"',negara1='"+results_inquiry3[0][5].toString()+"',tipe1='"+results_inquiry3[0][6].toString()+"',idcard1='"+results_inquiry3[0][7].toString()+"',tempat1='"+results_inquiry3[0][8].toString()+"',tanggal1='"+results_inquiry3[0][9].toString()+"',telp1='"+results_inquiry3[0][10].toString()+"',nama2='"+results_inquiry3[0][11].toString()+"',kelamin2='"+results_inquiry3[0][12].toString()+"',alamat2='"+results_inquiry3[0][13].toString()+"',kota2='"+results_inquiry3[0][14].toString()+"',kodepos2='"+results_inquiry3[0][15].toString()+"',negara2='"+results_inquiry3[0][16].toString()+"',tipe2='"+results_inquiry3[0][17].toString()+"',idcard2='"+results_inquiry3[0][18].toString()+"',tempat2='"+results_inquiry3[0][19].toString()+"',tanggal2='"+results_inquiry3[0][20].toString()+"',telp2='"+results_inquiry3[0][21].toString()+"',bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInCheckBankRequestISO[2]+"'", new Object[]{});
	        		}
	        		else
	        		{
	        			connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashInCheckBankRequestISO[2]+"'", new Object[]{});
	        		}
		       		responseISO=buildISOResponseMessage();	//Create cash In Check Status ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"internal4","iso"))
	        	{
					//2016
					//Extract BIT 61
	        		cashOutCheckRequest = extract(cashOutCheckRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashOutCheckRequest[4]=getFee(cashOutCheckRequest[3],bit103);
	        		stan=checkSTAN(bit11,bit103,bit3);
	        		
		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutCheckRequest[1],cashOutCheckRequest[2],cashOutCheckRequest[3],cashOutCheckRequest[4],cashOutCheckRequest[5]});
	        		mti="0210";

	        		//Create BIT 61 for ISO Response
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashOutCheckRequest[4]).intValue())+bit61.substring(65);
	        		if(checkBlank(cashOutCheckRequest))
	        		{
	        			decimal[0] = cashOutCheckRequest[3];
	        			decimal[1] = cashOutCheckRequest[4];
	        			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
	        			{
		        			if(checkRefCode(cashOutCheckRequest[2],"002013") || checkRefCodeXML(cashOutCheckRequest[2]))	/*Check if Kode Transfer is available for XML / ISO*/
		        			{
		        				if(cashOutCheck(cashOutCheckRequest[2]))	/*Check if Cash Out & Cash Out Confirmation are Success*/
		        				{
			        				if(stan)
			        				{	
			        					bit39="00";
			        				}
			        				else
			        				{
			        					bit39="94";
			        				}
		        				}
		        				else
		        				{
		        					bit39="71";
		        				}
		        			}
		        			else
		        			{
		        				bit39="86";
		        			}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}

					//Update Response to Transactioniso Table
	        		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutCheckRequest[2]+"'", new Object[]{});
		       		responseISO=buildISOResponseMessage();	//Create cash Out Check Status ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"https4","iso"))
		       	{
					//2028
					//Extract BIT 61
		       		cashOutCheckRequest = extract(cashOutCheckRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashOutCheckRequest[4]=getFee(cashOutCheckRequest[3],bit103);

		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutCheckRequest[1],cashOutCheckRequest[2],cashOutCheckRequest[3],cashOutCheckRequest[4],cashOutCheckRequest[5]});
		       		mti="0210";

	        		//Create BIT 61 for ISO Response
		       		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashOutCheckRequest[4]).intValue())+bit61.substring(65);
		       		if(checkBlank(cashOutCheckRequest))
		       		{
		       			decimal[0] = cashOutCheckRequest[3];
		       			decimal[1] = cashOutCheckRequest[4];
		       			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
		       			{
							http.sendHTTPPOST(httpsurl+https.checkstatus(bit41, "", "", cashOutCheckRequest[2]),"",new Integer(httpstimeout),"");	// Send Request to Sing Cash
							if(http.getFail()==0)	
							{	
								checkstatusResponse = https.checkstatusResponse(http.getPOSTResponse());	// Extract Response from Sing Cash
								if(checkstatusResponse[0].compareTo("00")==0)
								{
					       			bit39="00";								
								}
								else
								{
		        					bit39="96";		        														
								}
							}
							else
							{
	        					bit39="96";		        													
							}
		       			}
		       			else
		       			{
		           			bit39="30";        				
		       			}
		       		}
		       		else
		       		{
		       			bit39="30";
		       		}

					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutCheckRequest[2]+"'", new Object[]{});
		       		responseISO=buildISOResponseMessage();	//Create cash Out Check Status ISO Response
		       	}
				else if(getCategory(bit3+bit103.substring(2),"hongleong4","iso"))
		       	{
					e=0;
					//2025
					//Extract BIT 61
		       		cashOutCheckRequest = extract(cashOutCheckRequestTAG,bit61);
	        		//Get Fee Amount
	        		cashOutCheckRequest[4]=getFee(cashOutCheckRequest[3],bit103);

		       		//Insert Request to Transactioniso Table
		       		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,cashOutCheckRequest[1],cashOutCheckRequest[2],cashOutCheckRequest[3],cashOutCheckRequest[4],cashOutCheckRequest[5]});
		       		mti="0210";
		       		if(checkBlank(cashOutCheckRequest))
		       		{
		       			decimal[0] = cashOutCheckRequest[3];
		       			decimal[1] = cashOutCheckRequest[4];
		       			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
		       			{
			       			message=message.substring(0, message.length()-8)+"06002016";
		       				inputHangleon = message;
		       				maxHangleon = 301;
		       				response=isoclient.sendISOPacket(httpsurl, httpstimeout, inputHangleon, maxHangleon);	// Send ISO Request to Hangleong
		       				if(response.length()>=302)
		       				{
		       					e=1;
		       					response=response.substring(0,271)+String.format("%012d",new Integer(cashOutCheckRequest[4]).intValue())+response.substring(283);
		       					responseISO=response;
		       					extractMainResponse(mainTAGres,response);	// Extract Response from Hangleong
		       					bit39 = bit39res;
		       					bit38 = bit38res;
		       				}
		       				else
		       				{
		       					bit39="96";
		       					logger.error("ISO Response Message Length less than 302 characters");
		       				}
		       			}
		       			else
		       			{
		           			bit39="30";        				
		       			}
		       		}
		       		else
		       		{
		       			bit39="30";
		       		}
	        		bit61=bit61.substring(0,53)+String.format("%012d",new Integer(cashOutCheckRequest[4]).intValue())+bit61.substring(65);

					//Update Response to Transactioniso Table
	        		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+cashOutCheckRequest[2]+"'", new Object[]{});
		       		if(e==0)	responseISO=buildISOResponseMessage();	//Create cash Out Check Status ISO Response
		       	}
				else if(getCategory(bit3+bit103.substring(2),"internal5","iso"))
	        	{
					//2018
					//Extract BIT 61
					reFundConfRequest = extract(reFundConfRequestTAG,bit61);
	        		stan=checkSTAN(bit11,bit103,bit3);
	        		
		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,reFundConfRequest[1],reFundConfRequest[2],reFundConfRequest[3],reFundConfRequest[4],reFundConfRequest[5]});
	        		mti="0210";
	        		if(checkBlank(reFundConfRequest))
	        		{
	        			decimal[0] = reFundConfRequest[3];
	        			decimal[1] = reFundConfRequest[4];
	        			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
	        			{
		        			if(checkRefCode(reFundConfRequest[2],"002013") || checkRefCodeXML(reFundConfRequest[2]))	/*Check if Kode Transfer is available for XML / ISO*/
		        			{
		        				if(checkRefCodeDouble(reFundConfRequest[2],bit103,bit3)==true)	/*Check if there is a Refund Confirmation with same Kode Transfer*/
		        				{		
			        				if(stan)
			        				{	
				        				if(eva.getIDISO(dest1Acc,"+"+bit2.substring(7), "Cashout Remittance", reFundConfRequest[3], "CASHOUT", bit11, "notiDesc", "0")==0)	// Send eVa Request
				        				{	
				        					bit39="00";
				        				}
				        				else
				        				{
				        					bit39="96";		        					
				        				}
			        				}
			        				else
			        				{
			        					bit39="94";
			        				}
		        				}
		        				else
		        				{
		        					bit39="87";
		        				}
		        			}
		        			else
		        			{
		        				bit39="86";
		        			}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+reFundConfRequest[2]+"'", new Object[]{});        		
		       		responseISO=buildISOResponseMessage();	//Create Refund Confirmation ISO Response
	        	}
				else if(getCategory(bit3+bit103.substring(2),"internal6","iso"))
	        	{
					//2017
					//Extract BIT 61
	        		reFundCheckRequest = extract(reFundCheckRequestTAG,bit61);
	        		stan=checkSTAN(bit11,bit103,bit3);
	        		
		       		//Insert Request to Transactioniso Table
	        		connDb.updateQuery("insert into transactioniso (mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,identitas,kodetransfer,amount,fee,referensi) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{mti,bit1,bit2,bit3,bit4,bit7,bit11,bit12,bit13,bit14,bit15,bit18,bit32,bit33,bit37,bit38,bit39,bit41,bit42,bit43,bit49,bit103,reFundCheckRequest[1],reFundCheckRequest[2],reFundCheckRequest[3],reFundCheckRequest[4],reFundCheckRequest[5]});
	        		mti="0210";
	        		if(checkBlank(reFundCheckRequest))
	        		{
	        			decimal[0] = reFundCheckRequest[3];
	        			decimal[1] = reFundCheckRequest[4];
	        			if(checkDecimal(decimal))	/*Check if Destination Amount, feeAmount value is decimal*/
	        			{
		        			if(checkRefCode(reFundCheckRequest[2],"002013") || checkRefCodeXML(reFundCheckRequest[2]))	/*Check if Kode Transfer is available for XML / ISO*/
		        			{
		        				if(cashOutCheck(reFundCheckRequest[2]))	/*Check if Cash Out & Cash Out Confirmation are Success*/
		        				{
			        				if(stan)
			        				{	
			        					bit39="00";
			        				}
			        				else
			        				{
			        					bit39="94";
			        				}
		        				}
		        				else
		        				{
		        					bit39="71";
		        				}
		        			}
		        			else
		        			{
		        				bit39="86";
		        			}
	        			}
	        			else
	        			{
	            			bit39="30";        				
	        			}
	        		}
	        		else
	        		{
	        			bit39="30";
	        		}
					//Update Response to Transactioniso Table
		       		connDb.updateQuery("update transactioniso set bit38='"+bit38+"', bit39='"+bit39+"' where bit3='"+bit3+"' and bit103='"+bit103+"' and bit39 is NULL and kodetransfer='"+reFundCheckRequest[2]+"'", new Object[]{});
		       		responseISO=buildISOResponseMessage();	//Create Refund Check Status ISO Response
	        	}
				else
				{
					bit39="30";
					responseISO=buildISOResponseMessage();	//Create Error ISO Response
				}
	        }
			else
			{
				mti=mtid;
				bit39="30";
				responseISO=buildISOResponseMessage();	//Create Error ISO Response
			}
//        }
    }	

	public boolean checkBlank(String[] input)
	{
		boolean result = true;

		for(int i=1;i<input.length;i++)
		{
			if(input[i].trim().compareTo("")==0)
			{
				result = false;
				break;
			}
		}
		
		return result;
	}

	public boolean checkDecimal(String[] input)
	{

		boolean result = true;

		Pattern PATTERN = Pattern.compile( "^[+]?([0-9]\\d*)" );

		for(int i=0;i<input.length;i++)
		{
			if(!PATTERN.matcher( input[i] ).matches())        		
			{
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	public boolean cashOutCheck(String kodetransfer)
	{
		boolean result = false;

		results_inquiry4=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='380099' and (bit103='002014' or bit103='002018') and kodetransfer='"+kodetransfer+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    			
			results_inquiry5=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='500099' and (bit103='002014' or bit103='002018') and kodetransfer='"+kodetransfer+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{
				result=true;
			}	
		}	

		if(result==false)
		{	
			results_inquiry4=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='14' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry9, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    			
				results_inquiry5=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='15' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry9, new Object[]{},0);
				if(connDb.getRowCount(0)>0)
				{
					result=true;
				}	
			}	
		}
		
		return result;

	}

	public boolean cashInCheck(String kodetransfer)
	{
		boolean result = false;

		results_inquiry6=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='380099' and bit103='002013' and kodetransfer='"+kodetransfer+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    			
			results_inquiry7=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='500099' and bit103='002013' and kodetransfer='"+kodetransfer+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{
				result=true;
			}	
		}	

		return result;

	}
	
	public boolean cashInCheckXML(String refCode)
	{
		boolean result = false;

		results_inquiry=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='11' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry9, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    			
			results_inquiry=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='12' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry9, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{
				result=true;
			}	
		}    		

		return result;

	}
	
	public boolean checkRefCodeXML(String refCode)
	{
		boolean result = false;

		results_inquiry=connDb.getQuery("SELECT refCode FROM transaction WHERE transactionType='11' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry9, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{
			result=true;
		}	

		return result;

	}
	
	public String errorMapping(String errorCode)
	{
		String result = "96";
		
		results_inquiry13=connDb.getQuery("SELECT erroriso FROM errormapping WHERE errorxml='"+errorCode+"'", new Object[]{""}, field_inquiry8, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{
			result=results_inquiry13[0][0].toString();
		}	

		return result;
	}

	public String getFee(String destAmount, String productCode)
	{
		String feeAmount = "";

		results_inquiry11=connDb.getQuery("SELECT fee FROM fee WHERE namapengguna='"+productCode+"' and mintrx<="+destAmount+" and maxtrx>="+destAmount, new Object[]{""}, field_inquiry6, new Object[]{},0);
		feeAmount = results_inquiry11[0][0].toString();

		return feeAmount;

	}
	
	
	public boolean checkRefCodeDouble(String kodetransfer, String bit103, String bit3)
	{
		boolean result = true;

		results_inquiry8=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit103='"+bit103+"' and bit3='"+bit3+"' and kodetransfer='"+kodetransfer+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{
			result=false;
		}	
		
		if(result==true && bit103.compareTo("002014")==0)
		{
			if(bit3.compareTo("380099")==0)
			{	
				results_inquiry=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='14' and refCode='"+kodetransfer+"'", new Object[]{""}, field_inquiry9, new Object[]{},0);
			}
			else if(bit3.compareTo("500099")==0)
			{
				results_inquiry=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='15' and refCode='"+kodetransfer+"'", new Object[]{""}, field_inquiry9, new Object[]{},0);				
			}
			if(connDb.getRowCount(0)>0)
			{
				result=false;
			}	
		}

		return result;

	}
	
	public boolean getCategory(String productCode, String category, String type)
	{
		boolean result=false;
		
		if(category.startsWith("internal")  && type.compareTo("iso")==0)
		{	
			results_inquiry9=connDb.getQuery("SELECT dest1Acc FROM routing WHERE productCode='"+productCode+"' and category like '"+category+"%' and types='"+type+"'", new Object[]{""}, field_inquiry4, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    		
				dest1Acc = results_inquiry9[0][0].toString();
				result=true;
			}        		
		}	
		else if(category.startsWith("https")  && type.compareTo("iso")==0)
		{	
			results_inquiry10=connDb.getQuery("SELECT httpsurl, httpstimeout, dest1Acc FROM routing WHERE productCode='"+productCode+"' and category='"+category+"' and types='"+type+"'", new Object[]{"","",""}, field_inquiry5, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    		
				httpsurl = results_inquiry10[0][0].toString();
				httpstimeout = results_inquiry10[0][1].toString();
				dest1Acc = results_inquiry10[0][2].toString();
				result=true;
			}        		
		}	
		else if(category.startsWith("hongleong")  && type.compareTo("iso")==0)
		{	
			results_inquiry10=connDb.getQuery("SELECT httpsurl, httpstimeout, dest1Acc FROM routing WHERE productCode='"+productCode+"' and category='"+category+"' and types='"+type+"'", new Object[]{"","",""}, field_inquiry5, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    		
				httpsurl = results_inquiry10[0][0].toString();
				httpstimeout = results_inquiry10[0][1].toString();
				dest1Acc = results_inquiry10[0][2].toString();
				result=true;
			}        		
		}	
		else if(category.startsWith("bank")  && type.compareTo("iso")==0)
		{	
			results_inquiry10=connDb.getQuery("SELECT httpsurl, httpstimeout, dest1Acc FROM routing WHERE productCode='"+productCode+"' and category='"+category+"' and types='"+type+"'", new Object[]{"","",""}, field_inquiry5, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    		
				bankurl = results_inquiry10[0][0].toString();
				banktimeout = results_inquiry10[0][1].toString();
				dest1Acc = results_inquiry10[0][2].toString();
				result=true;
			}        		
		}	

		return result;

	}	
	
	public String padding(String input, int count)
	{
		String result = "";
		
		for(int i=0;i<count;i++)
		{
			result=result+input;
		}
		
		return result;
	}
		
	public boolean checkRefCode(String kodetransfer, String bit103)
	{
		boolean result = false;

		results_inquiry=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit3='380099' and kodetransfer='"+kodetransfer+"' and bit103='"+bit103+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{
			result=true;
		}	
		
		return result;

	}

	public boolean checkSTAN(String stan, String bit103, String bit3)
	{
		boolean result = true;

		results_inquiry=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit3='"+bit3+"' and bit11='"+stan+"' and bit103='"+bit103+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{
			result=false;
		}	
		
		return result;

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

	public String readFile(File file)
			throws IOException 
			{
		int len;
		char[] chr = new char[4096];
		final StringBuffer buffer = new StringBuffer();
		final FileReader reader = new FileReader(file);
		try {
			while ((len = reader.read(chr)) > 0) {
				buffer.append(chr, 0, len);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
			}        
	
	
}
