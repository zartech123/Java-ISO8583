import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class XML 
{
	private String cashOutCheckResponseXML = "";
	private String errorResponseXML = "";
	private String cashOutConfResponseXML = "";
	private String cashOutResponseXML = "";
	private String cashInResponseXML = "";
	private String cashInConfResponseXML = "";
	private String cashInCheckResponseXML = "";
	private String cashInRequestTAG[] = new String[24];
	private String cashInConfRequestTAG[] = new String[26];
	private String cashInCheckRequestTAG[] = new String[9];
	private String cashOutCheckRequestTAG[] = new String[9];
	private String cashOutRequestTAG[] = new String[10];
	private String cashOutConfRequestTAG[] = new String[23];
	private Logger logger = Logger.getLogger(XML.class);
	private Properties propXML = new Properties();
	private XMLRead xml;
	
	/*Load Configuration File XML.txt*/
	public XML()
	{
		try
		{
			PropertyConfigurator.configure("conf/log4j.properties");    	
	        propXML.load(new FileInputStream("conf/XML.txt"));
	        cashInRequestTAG=propXML.getProperty("cashinreqTAG").split(",");
	        cashInConfRequestTAG=propXML.getProperty("cashinconfreqTAG").split(",");
	        cashInCheckRequestTAG=propXML.getProperty("cashincheckreqTAG").split(",");
	        cashOutRequestTAG=propXML.getProperty("cashoutreqTAG").split(",");
	        cashOutConfRequestTAG=propXML.getProperty("cashoutconfreqTAG").split(",");
	        cashOutCheckRequestTAG=propXML.getProperty("cashincheckreqTAG").split(",");
		}
		catch(IOException e)
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		} 
	}
	
	
	/*Extract Internal XML cash In Request*/ 
	public String[] cashInRequest(String input)
	{
		xml = new XMLRead();

		return xml.execute(input,"inputTransaction",cashInRequestTAG);

	}
	
	/*Create Internal XML cash In Response*/ 
	public String cashInResponse(String[] input)
	{
        cashInResponseXML=propXML.getProperty("cashinresXML");

        cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_description_", input[0]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_userName_", input[1]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_signature_", input[2]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_sysCode_", input[3]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_refCode_", input[4]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_resultCode_", input[5]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_resultDesc_", input[6]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_productCode_", input[7]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_destBankAcc_", input[8]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_destAmount_", input[9]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_feeAmount_", input[10]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_sourceID_", input[11]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_sourceName_", input[12]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_terminal_", input[13]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_senderName_", input[14]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_senderAddress_", input[15]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_senderID_", input[16]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_senderPhone_", input[17]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_senderCity_", input[18]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_senderCountry_", input[19]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_recipientName_", input[20]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_recipientPhone_", input[21]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_recipientAddress_", input[22]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_recipientCity_", input[23]);		
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_recipientCountry_", input[24]);		
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_notiDesc_", input[25]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_transactionType_", input[26]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_traxId_", input[27]);
		cashInResponseXML = StringUtils.replaceOnce(cashInResponseXML,"_recipientID_", input[28]);        

        return cashInResponseXML;
	
	}
	
	/*Extract Internal XML cash In Confirmation Request*/ 
	public String[] cashInConfRequest(String input)
	{
		xml = new XMLRead();

		return xml.execute(input,"inputTransaction",cashInConfRequestTAG);

	}

	
	/*Create Internal XML cash In Confirmation Response*/ 
	public String cashInConfResponse(String[] input)
	{
        cashInConfResponseXML=propXML.getProperty("cashinconfresXML");

        cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_description_", input[0]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_userName_", input[1]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_signature_", input[2]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_sysCode_", input[3]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_refCode_", input[4]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_resultCode_", input[5]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_resultDesc_", input[6]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_productCode_", input[7]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_destBankAcc_", input[8]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_destAmount_", input[9]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_feeAmount_", input[10]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_sourceID_", input[11]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_sourceName_", input[12]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_terminal_", input[13]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_senderName_", input[14]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_senderAddress_", input[15]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_senderID_", input[16]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_senderPhone_", input[17]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_senderCity_", input[18]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_senderCountry_", input[19]);		
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_recipientName_", input[20]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_recipientPhone_", input[21]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_recipientAddress_", input[22]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_recipientCity_", input[23]);		
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_recipientCountry_", input[24]);		
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_notiDesc_", input[25]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_transactionType_", input[26]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_traxId_", input[27]);
		cashInConfResponseXML = StringUtils.replaceOnce(cashInConfResponseXML,"_recipientID_", input[28]);
		
		return cashInConfResponseXML;
		
	}
	
	/*Extract Internal XML cash In Check Status Request*/ 
	public String[] cashInCheckRequest(String input)
	{
		xml = new XMLRead();
		
		return xml.execute(input,"inputTransaction",cashInCheckRequestTAG);
		
	}

	/*Create Internal XML cash In Check Status Response*/ 
	public String cashInCheckResponse(String[] input)
	{
		
        cashInCheckResponseXML=propXML.getProperty("cashincheckresXML");

        cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_sysCode_", input[0]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_refCode_", input[1]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_resultCode_", input[2]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_resultDesc_", input[3]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_productCode_", input[4]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_destAmount_", input[5]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_sourceID_", input[6]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_transactionType_", input[7]);
		cashInCheckResponseXML = StringUtils.replaceOnce(cashInCheckResponseXML,"_traxId_", input[8]);
		
		return cashInCheckResponseXML;
	}

	/*Extract Internal XML cash Out Request*/ 
	public String[] cashOutRequest(String input)
	{
		xml = new XMLRead();
		
		return xml.execute(input,"inputTransaction",cashOutRequestTAG);

	}

	/*Create Internal XML cash Out Response*/ 
	public String cashOutResponse(String[] input)
	{
		
        cashOutResponseXML=propXML.getProperty("cashoutresXML");

        cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_userName_", input[0]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_signature_", input[1]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_sysCode_", input[2]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_refCode_", input[3]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_resultCode_", input[4]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_resultDesc_", input[5]);		
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_productCode_", input[6]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_destAmount_", input[7]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_feeAmount_", input[8]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_sourceID_", input[9]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_sourceName_", input[10]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_terminal_", input[11]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_senderName_", input[12]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_senderAddress_", input[13]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_senderID_", input[14]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_senderPhone_", input[15]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_senderCity_", input[16]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_senderCountry_", input[17]);		
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_recipientName_", input[18]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_recipientPhone_", input[19]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_recipientAddress_", input[20]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_recipientCity_", input[21]);		
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_recipientCountry_", input[22]);		
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_transactionType_", input[23]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_traxId_", input[24]);
		cashOutResponseXML = StringUtils.replaceOnce(cashOutResponseXML,"_recipientID_", input[25]);
		
		return cashOutResponseXML;
	}
	
	/*Extract Internal XML cash Out Confirmation Request*/ 
	public String[] cashOutConfRequest(String input)
	{
		xml = new XMLRead();
		
		return xml.execute(input,"inputTransaction",cashOutConfRequestTAG);

	}

	/*Create Internal XML cash Out Confirmation Response*/ 
	public String cashOutConfResponse(String[] input)
	{
		
        cashOutConfResponseXML=propXML.getProperty("cashoutconfresXML");

        
        
        cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_userName_", input[0]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_signature_", input[1]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_sysCode_", input[2]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_refCode_", input[3]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_resultCode_", input[4]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_resultDesc_", input[5]);		
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_productCode_", input[6]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_destAmount_", input[7]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_feeAmount_", input[8]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_sourceID_", input[9]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_sourceName_", input[10]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_terminal_", input[11]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_senderName_", input[12]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_senderAddress_", input[13]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_senderID_", input[14]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_senderPhone_", input[15]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_senderCity_", input[16]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_senderCountry_", input[17]);		
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_recipientName_", input[18]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_recipientPhone_", input[19]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_recipientAddress_", input[20]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_recipientCity_", input[21]);		
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_recipientCountry_", input[22]);		
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_transactionType_", input[23]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_traxId_", input[24]);
		cashOutConfResponseXML = StringUtils.replaceOnce(cashOutConfResponseXML,"_recipientID_", input[25]);

		return cashOutConfResponseXML;

	}

	/*Extract Internal XML cash Out Check Status Request*/ 
	public String[] cashOutCheckRequest(String input)
	{
		xml = new XMLRead();
		
		return xml.execute(input,"inputTransaction",cashOutCheckRequestTAG);

	}
	
	
	/*Create Internal XML cash Out Check Status Response*/ 
	public String cashOutCheckResponse(String[] input)
	{
        cashOutCheckResponseXML=propXML.getProperty("cashincheckresXML");

        cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_sysCode_", input[0]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_refCode_", input[1]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_resultCode_", input[2]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_resultDesc_", input[3]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_productCode_", input[4]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_destAmount_", input[5]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_sourceID_", input[6]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_transactionType_", input[7]);
		cashOutCheckResponseXML = StringUtils.replaceOnce(cashOutCheckResponseXML,"_traxId_", input[8]);
		
		return cashOutCheckResponseXML;
	}
	
	/*Create Internal XML Error Response*/ 
	public String errorResponse(String resultCode, String resultDesc, String productCode)
	{
		errorResponseXML=propXML.getProperty("errorresXML");

        errorResponseXML = StringUtils.replaceOnce(errorResponseXML,"_resultCode_", resultCode);
		errorResponseXML = StringUtils.replaceOnce(errorResponseXML,"_resultDesc_", resultDesc);
		errorResponseXML = StringUtils.replaceOnce(errorResponseXML,"_productCode_", productCode);
		
		return errorResponseXML;
		
	}

}
