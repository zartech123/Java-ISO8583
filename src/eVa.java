import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.flexiprovider.common.util.ByteUtils;
import de.flexiprovider.core.FlexiCoreProvider;

public class eVa {

	/**
	 * @param args
	 */
	private String loginXML = "";
	private String transactionXML = "";
	private String getID = "";
	private String getIDISO = "";
    private int fail = 0;
    private HttpImpl http = new HttpImpl();
	private Logger logger = Logger.getLogger(eVa.class);

	private Properties propXML = new Properties();
	private XMLRead xml;
	private String loginResponse[] = new String[5];
	private String loginRequest[] = new String[3];
	private String transactionResponse[] = new String[7];
	private String transactionRequest[] = new String[12];
	private String loginResponseTAG[] = new String[5];
	private String transactionResponseTAG[] = new String[7];
    
    private String evaNumber = "";
    private String sessionId = "";
    private String success = "";
    private String response = "";
    
    private long number;
        
	/*Load Configuration File XML.txt*/
    public eVa()
	{
		try
		{
			PropertyConfigurator.configure("conf/log4j.properties");    	
			propXML.load(new FileInputStream("conf/XML.txt"));
			loginResponseTAG=propXML.getProperty("evaloginresTAG").split(",");
			transactionResponseTAG=propXML.getProperty("evainputtransactionresTAG").split(",");
		}
		catch(IOException e)
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		} 
		
	}
	
	/*Extract eVA Login Response*/ 
	public String[] LoginResponse(String input)
	{
		xml = new XMLRead();
		
		return xml.execute(input,"outputLogin",loginResponseTAG);
		
	}
	
	/*Create eVA Login Input Request*/ 
	public int Login(String[] input, String traxId)
	{
		fail=0;

		loginXML=propXML.getProperty("evaloginreqXML");

		loginXML = StringUtils.replaceOnce(loginXML,"_desc_", input[0]);
		loginXML = StringUtils.replaceOnce(loginXML,"_phoneNo_", input[1]);
		loginXML = StringUtils.replaceOnce(loginXML,"_sessionId_", input[2]);

    	http.sendHTTPPOST(propXML.getProperty("evaurl"), loginXML,new Integer(propXML.getProperty("evatimeout")),"http://www.webserviceX.NET/ConversionRate");
    	
    	if(http.getFail()==0)	
    	{	
    		loginResponse=LoginResponse(http.getPOSTResponse());
    		if(loginResponse[3].compareTo("0")!=0)
    		{
    			fail=1;
    		}
		}	
		else
		{	
			fail=1;
		}	
    	
   	
    	return fail;
	
	}

	/*Extract eVA Transaction Input Response*/ 
	public String[] TransactionResonspe(String input)
	{
		xml = new XMLRead();
		
		return xml.execute(input,"outputTransaction",transactionResponseTAG);
		
	}

	/*Create eVA Transaction Input Request*/ 
	public int Transaction(String[] input, String traxId)
	{
		
		fail=0;
		
		transactionXML=propXML.getProperty("evainputtransactionreqXML");

		transactionXML = StringUtils.replaceOnce(transactionXML,"_description_", input[0]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_notiDesc_", input[1]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_sessionId_", input[2]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_source1Acc_", input[3]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_source1Amount_", input[4]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_transactionType_", input[5]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_traxId_", input[6]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_dest1Amount_", input[7]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_dest2Amount_", input[8]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_dest1Acc_", input[9]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_phoneNo_", input[10]);
		transactionXML = StringUtils.replaceOnce(transactionXML,"_notiPhone_", input[11]);

			
    	http.sendHTTPPOST(propXML.getProperty("evaurl"), transactionXML,new Integer(propXML.getProperty("evatimeout")),"urn:maia#maia");

    	if(http.getFail()==0)	
    	{	
    		transactionResponse=TransactionResonspe(http.getPOSTResponse());
    		if(transactionResponse[3].compareTo("0")!=0)
    		{
    			fail=1;
    		}
		}	
		else
		{	
			fail=1;
		}	
    	
    	return fail;
	}
	
	public String ripemd320(String input)
	{
		String result="";
		
    	try
    	{
    		Security.addProvider(new FlexiCoreProvider());
		
			MessageDigest md = MessageDigest.getInstance("RIPEMD320", "FlexiCore");
			
			md.update(input.getBytes());
			byte[] digest = md.digest();

			result=ByteUtils.toHexString(digest);
			
    	}	
    	catch(NoSuchProviderException e)
    	{
			logger.error(this.getClass().getName()+" "+e.getMessage());    		    		
    	}
    	catch(NoSuchAlgorithmException e)
    	{
			logger.error(this.getClass().getName()+" "+e.getMessage());    		    		    		
    	}
    	
    	return result;
	}
	
    public int getID(String dest1Acc, String senderPhone, String desc, String descAmount, String transactionType, String traxId, String notiDesc, String feeAmount)
	{	

   		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
   		String date = sdf.format(new Date()); 

   		number = (long) Math.floor(Math.random() * 9000000L) + 1000000L;  
   		sessionId = new Long(number).toString();
   		
   		senderPhone = senderPhone.trim().substring(1);
   		
		getID=propXML.getProperty("getid");		

		getID = StringUtils.replaceOnce(getID,"_nh_", senderPhone);
		getID = StringUtils.replaceOnce(getID,"_ts_", date);
		getID = StringUtils.replaceOnce(getID,"_cr_", ripemd320(senderPhone)+ripemd320(date));

		http.sendHTTPGET(propXML.getProperty("evaurls")+getID,new Integer(propXML.getProperty("evatimeout")));

		if(http.getFail()==0)	
		{	
			response=http.getGETResponse();
			success=response.substring(0,2);
			if(success.compareTo("00")==0)
			{					
				evaNumber=response.substring(2,26).trim();
				senderPhone=response.substring(26,50).trim();
				loginRequest[0]=desc;
				loginRequest[1]="+"+evaNumber;
				loginRequest[2]=sessionId;
				if(Login(loginRequest,traxId)==0)
				{
					transactionRequest[0]=desc;
					transactionRequest[1]=notiDesc;
					transactionRequest[2]=sessionId;
					transactionRequest[3]="+"+senderPhone;
					transactionRequest[5]=transactionType;
					transactionRequest[6]=traxId;
					transactionRequest[7]=descAmount;
					transactionRequest[8]=feeAmount;
					transactionRequest[9]=dest1Acc;
					transactionRequest[4]=new Integer(new Integer(descAmount).intValue()+new Integer(feeAmount).intValue()).toString();
					transactionRequest[10]="+"+evaNumber;
					transactionRequest[11]="+"+senderPhone;
					if(Transaction(transactionRequest,traxId)!=0)
					{
						fail=1;
					}
				}
				else
				{
					fail=1;
				}
			}
			else
			{
				fail=1;
			}
		}	
		else
		{	
			fail = 1;
		}
		
		return fail;
	
	}	
    
    public int getIDISO(String dest1Acc, String senderPhone, String desc, String descAmount, String transactionType, String traxId, String notiDesc, String feeAmount)
	{	

   		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
   		String date = sdf.format(new Date()); 

   		number = (long) Math.floor(Math.random() * 9000000L) + 1000000L;  
   		sessionId = new Long(number).toString();
   		
   		senderPhone = senderPhone.trim().substring(1);
   		
		getIDISO=propXML.getProperty("getidiso");		

		getIDISO = StringUtils.replaceOnce(getIDISO,"_ne_", senderPhone);
		getIDISO = StringUtils.replaceOnce(getIDISO,"_ts_", date);
		getIDISO = StringUtils.replaceOnce(getIDISO,"_cr_", ripemd320(senderPhone)+ripemd320(date));

		http.sendHTTPGET(propXML.getProperty("evaurls")+getIDISO,new Integer(propXML.getProperty("evatimeout")));

		if(http.getFail()==0)	
		{	
			response=http.getGETResponse();
			success=response.substring(0,2);
			if(success.compareTo("00")==0)
			{					
				evaNumber=response.substring(2,26).trim();
				loginRequest[0]=desc;
				loginRequest[1]="+"+evaNumber;
				loginRequest[2]=sessionId;
				if(Login(loginRequest,traxId)==0)
				{
					transactionRequest[0]=desc;
					transactionRequest[1]=notiDesc;
					transactionRequest[2]=sessionId;
					transactionRequest[3]="+"+evaNumber;
					transactionRequest[5]=transactionType;
					transactionRequest[6]=traxId;
					transactionRequest[7]=descAmount;
					transactionRequest[8]=feeAmount;
					transactionRequest[9]=dest1Acc;
					transactionRequest[4]=new Integer(new Integer(descAmount).intValue()+new Integer(feeAmount).intValue()).toString();
					transactionRequest[10]="+"+evaNumber;
					transactionRequest[11]="+"+evaNumber;
					if(Transaction(transactionRequest,traxId)!=0)
					{
						fail=1;
					}
				}
				else
				{
					fail=1;
				}
			}
			else
			{
				fail=1;
			}
		}	
		else
		{	
			fail = 1;
		}
		
		return fail;
	
	}	

    
}
