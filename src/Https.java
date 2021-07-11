import id.co.bni.tcash.util.CryptoUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Https {

	private String inquiry = new String();
	private String submission = new String();
	private String checkstatus = new String();

	private Logger logger = Logger.getLogger(Https.class);
	private Properties propXML = new Properties();
	private String inquiryResponseTemp[] = new String[6];
	private String inquiryResponseTemp2[] = new String[11];
	private String inquiryResponse[] = new String[16];
	private String submissionResponse[] = new String[4];
	private String checkstatusResponse[] = new String[2];
	private String sequenceId = "";
	private String transactionId = "";
	private String trxDate = "";
	private String key = "";

	/*Load Configuration File HTTPS.txt*/
	public Https()
	{
		try
		{
			PropertyConfigurator.configure("conf/log4j.properties");    	
			propXML.load(new FileInputStream("conf/HTTPS.txt"));
			key=propXML.getProperty("key");		
		}
		catch(IOException e)
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		} 

	}

	/*Extract Sing Cash Inquiry Response*/ 
	public String[] inquiryResponse(String input)
	{
		input = tselDecrypt(key,input);
		
		inquiryResponseTemp=input.split("@~|");

		inquiryResponse[0]=inquiryResponseTemp[0];
		inquiryResponse[1]=inquiryResponseTemp[1];
		inquiryResponse[2]=inquiryResponseTemp[2];
		inquiryResponse[3]=inquiryResponseTemp[3];

		inquiryResponseTemp2=inquiryResponseTemp[4].split("|");

		for(int i=0;i<inquiryResponseTemp2.length;i++)
		{
			inquiryResponse[i+4]=inquiryResponseTemp2[i];
		}

		inquiryResponse[4+inquiryResponseTemp2.length]=inquiryResponseTemp[5];

		return inquiryResponse;
	}

	/*Create Sing Cash Inquiry Request*/ 
	public String inquiry(String terminal, String location, String customer, String refNo)
	{
		inquiry=propXML.getProperty("encashmentinquiry");

		inquiry = StringUtils.replaceOnce(inquiry,"_terminal_", terminal);
		inquiry = StringUtils.replaceOnce(inquiry,"_location_", location);
		inquiry = StringUtils.replaceOnce(inquiry,"_customer_", customer);
		inquiry = StringUtils.replaceOnce(inquiry,"_trx_date_", trxDate);
		inquiry = StringUtils.replaceOnce(inquiry,"_sequence_id_", sequenceId);
		inquiry = StringUtils.replaceOnce(inquiry,"_ref_no_", refNo);

		return tselEncrypt(key,inquiry);

	}

	/*Extract Sing Cash Submission Response*/ 
	public String[] submissionResponse(String input)
	{
		input = tselDecrypt(key,input);

		submissionResponse=input.split("@~|");

		return submissionResponse;
	}

	/*Create Sing Cash Submission Request*/ 
	public String submission(String terminal, String location, String customer, String refNo, String amount)
	{
		submission=propXML.getProperty("enchasmentsubmission");

		submission = StringUtils.replaceOnce(submission,"_terminal_", terminal);
		submission = StringUtils.replaceOnce(submission,"_location_", location);
		submission = StringUtils.replaceOnce(submission,"_customer_", customer);
		submission = StringUtils.replaceOnce(submission,"_trx_date_", trxDate);
		submission = StringUtils.replaceOnce(submission,"_sequence_id_", sequenceId);
		submission = StringUtils.replaceOnce(submission,"_amount_", amount);
		submission = StringUtils.replaceOnce(submission,"_ref_no_", refNo);
		submission = StringUtils.replaceOnce(submission,"_transaction_id_", transactionId);

		return tselEncrypt(key,submission);

	}	

	/*Extract Sing Cash Check Status Response*/ 
	public String[] checkstatusResponse(String input)
	{
		input = tselDecrypt(key,input);

		checkstatusResponse=input.split("@~|");

		return checkstatusResponse;
	}

	/*Create Sing Cash Check Status Request*/ 
	public String checkstatus(String terminal, String location, String customer, String refNo)
	{
		checkstatus=propXML.getProperty("checkstatus");		

		checkstatus = StringUtils.replaceOnce(checkstatus,"_terminal_", terminal);
		checkstatus = StringUtils.replaceOnce(checkstatus,"_location_", location);
		checkstatus = StringUtils.replaceOnce(checkstatus,"_customer_", customer);
		checkstatus = StringUtils.replaceOnce(checkstatus,"_trx_date_", trxDate);
		checkstatus = StringUtils.replaceOnce(checkstatus,"_sequence_id_", sequenceId);
		checkstatus = StringUtils.replaceOnce(checkstatus,"_ref_no_", refNo);

		return tselEncrypt(key,checkstatus);

	}	

	/*Encrypt Sing Cash Content*/
	public String tselEncrypt(String key, String message)
	{

		if (key == null || key.equals("")) 
		{
			return message;
		}

		if (message == null || message.equals("")) 
		{
			return "";
		}

		CryptoUtils enc = new CryptoUtils(key, Cipher.ENCRYPT_MODE);

		String messageEnc = enc.process(message);

		return messageEnc;
	}

	/*Decrypt Sing Cash Content*/
	public String tselDecrypt(String key, String message)
	{

		if (key == null || key.equals("")) 
		{
			return message;
		}

		if (message == null || message.equals("")) 
		{
			return "";
		}
		CryptoUtils enc = new CryptoUtils(key, Cipher.DECRYPT_MODE);
		String messageEnc = enc.process(message);

		return messageEnc;
	}
}
