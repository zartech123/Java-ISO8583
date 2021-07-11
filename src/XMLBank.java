import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class XMLBank 
{
	private String cashInRequestXML = "";
	private String cashInConfRequestXML = "";
	private String cashInCheckRequestXML = "";
	private String cashInResponseTAG[] = new String[25];
	private String cashInConfResponseTAG[] = new String[25];
	private String cashInCheckResponseTAG[] = new String[9];
	private Logger logger = Logger.getLogger(XMLBank.class);
	private Properties propXML = new Properties();
	private XMLRead xml;
	
	/*Load Configuration File XML.txt*/
	public XMLBank()
	{
		try
		{
			PropertyConfigurator.configure("conf/log4j.properties");    	
	        propXML.load(new FileInputStream("conf/XML.txt"));
	        cashInResponseTAG=propXML.getProperty("cashinbankresTAG").split(",");
	        cashInConfResponseTAG=propXML.getProperty("cashinconfbankresTAG").split(",");
	        cashInCheckResponseTAG=propXML.getProperty("cashincheckbankresTAG").split(",");
		}
		catch(IOException e)
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		} 
	}
	
	/*Extract Bank XML cash In Response*/ 
	public String[] cashInResponse(String input)
	{
		xml = new XMLRead();

		return xml.execute(input,"outputTransaction",cashInResponseTAG);
		
	}
	
	/*Create Bank XML cash In Request*/ 
	public String cashInRequest(String[] input)
	{
				
        cashInRequestXML=propXML.getProperty("cashinbankreqXML");

        cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_description_", input[0]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_userName_", input[1]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_signature_", input[2]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_productCode_", input[3]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_destBankAcc_", input[4]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_destAmount_", input[5]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_transactionType_", input[6]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_terminal_", input[7]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_sourceID_", input[8]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_sourceName_", input[9]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_senderName_", input[10]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_senderAddress_", input[11]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_senderID_", input[12]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_senderPhone_", input[13]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_senderCity_", input[14]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_senderCountry_", input[15]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_recipientName_", input[16]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_recipientPhone_", input[17]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_recipientAddress_", input[18]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_recipientCity_", input[19]);		
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_recipientCountry_", input[20]);		
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_notiDesc_", input[21]);
		cashInRequestXML = StringUtils.replaceOnce(cashInRequestXML,"_traxId_", input[22]);

		return cashInRequestXML;
	
	}
	
	/*Extract Bank XML cash In Confirmation Response*/ 
	public String[] cashInConfResponse(String input)
	{
		xml = new XMLRead();

		return xml.execute(input,"outputTransaction",cashInConfResponseTAG);

	}

	/*Create Bank XML cash In Check Confirmation Request*/ 
	public String cashInConfRequest(String[] input)
	{
		
        cashInConfRequestXML=propXML.getProperty("cashinconfbankreqXML");

        cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_description_", input[0]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_userName_", input[1]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_signature_", input[2]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_productCode_", input[3]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_destBankAcc_", input[4]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_destAmount_", input[5]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_feeAmount_", input[6]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_transactionType_", input[7]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_terminal_", input[8]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_sourceID_", input[9]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_sourceName_", input[10]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_senderName_", input[11]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_senderAddress_", input[12]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_senderID_", input[13]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_senderPhone_", input[14]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_senderCity_", input[15]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_senderCountry_", input[16]);		
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_recipientName_", input[17]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_recipientPhone_", input[18]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_recipientAddress_", input[19]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_recipientCity_", input[20]);		
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_recipientCountry_", input[21]);		
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_notiDesc_", input[22]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_traxId_", input[23]);
		cashInConfRequestXML = StringUtils.replaceOnce(cashInConfRequestXML,"_refCode_", input[24]);
		
		return cashInConfRequestXML;
		
	}
	
	/*Extract Bank XML cash In Check Status Response*/ 
	public String[] cashInCheckResponse(String input)
	{
		xml = new XMLRead();

		return xml.execute(input,"outputTransaction",cashInCheckResponseTAG);

	}

	/*Create Bank XML cash In Check Status Request*/ 
	public String cashInCheckRequest(String[] input)
	{
        cashInCheckRequestXML=propXML.getProperty("cashincheckbankreqXML");
		
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_userName_", input[0]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_signature_", input[1]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_productCode_", input[2]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_destAmount_", input[3]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_transactionType_", input[4]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_terminal_", input[5]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_sourceID_", input[6]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_traxId_", input[7]);
		cashInCheckRequestXML = StringUtils.replaceOnce(cashInCheckRequestXML,"_refCode_", input[8]);
		
		return cashInCheckRequestXML;
	}

}
