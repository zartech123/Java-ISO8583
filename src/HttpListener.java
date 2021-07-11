import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HttpListener 
{

	private static Properties propXML = new Properties();
	private static Properties propISO = new Properties();
	private static int port = 0;
	private static String directory = "";
	private static Logger logger = Logger.getLogger(HttpListener.class);

	public static void main(String[] args) throws Exception 
	{
		PropertyConfigurator.configure("conf/log4j.properties");
		logger.info("HTTP Listener is running");    		    		
		propXML.load(new FileInputStream("conf/XML.txt"));
		propISO.load(new FileInputStream("conf/ISO.txt"));
		port = new Integer(propXML.getProperty("port")).intValue();
		directory = propXML.getProperty("directory");
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/"+directory, new MyHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

	}

	static class MyHandler implements HttpHandler 
	{
		private String mainTAGres[] = new String[23];
		private ConnectionImpl connDb = new ConnectionImpl();
		private HttpImpl http = new HttpImpl();
		private eVa eva = new eVa();
		private Https https = new Https();
		private String timestamp = "";
		private XML xmlRemittance;
		private XMLBank xmlRemittanceBank = new XMLBank();
		private String cashInResponseBank[] = new String[25];
		private String cashInConfResponseBank[] = new String[25];
		private String cashInCheckResponseBank[] = new String[9];
		private String request[] = new String[25];
		private String cashInRequest[] = new String[24];
		private String cashInResponse[] = new String[29];
		private String cashInConfRequest[] = new String[26];
		private String cashInConfResponse[] = new String[29];
		private String cashInCheckRequest[] = new String[9];
		private String cashInCheckResponse[] = new String[10];
		private String cashOutRequest[] = new String[10];
		private String cashOutResponse[] = new String[26];
		private String cashOutCheckRequest[] = new String[9];
		private String cashOutCheckResponse[] = new String[10];
		private String cashOutConfRequest[] = new String[23];
		private String cashOutConfResponse[] = new String[26];
		private String cashOutCheckISOResponseTAG[] = new String[6];
		private String cashOutCheckISOResponse[] = new String[6];
		private String cashOutISOResponseTAG[] = new String[28];
		private String cashOutISOResponse[] = new String[28];
		private String cashOutConfISOResponseTAG[] = new String[6];
		private String cashOutConfISOResponse[] = new String[6];
		private String response = "";
		private String decimal[] = new String[3];
		private String decimal2[] = new String[4];
		private String sysCode = "";
		private String refCode = "";
		private long number = 0;
		private String pin = "";
		private String traxId = "";
		private String dest1Acc = "";
		private String httpsurl = "";
		private String wsdl = "";
		private String httpstimeout = "";
		private String inquiryResponse[] = new String[16];
		private String submissionResponse[] = new String[4];
		private String checkstatusResponse[] = new String[2];
		private Object[][] results_inquiry = new Object[1][1];
		private Object[][] results_inquiry2 = new Object[1][1];
		private Object[][] results_inquiry3 = new Object[1][1];
		private Object[][] results_inquiry4 = new Object[1][1];
		private Object[][] results_inquiry5 = new Object[1][1];
		private Object[][] results_inquiry6 = new Object[1][1];
		private Object[][] results_inquiry7 = new Object[1][1];
		private Object[][] results_inquiry8 = new Object[1][1];
		private Object[][] results_inquiry9 = new Object[1][13];
		private Object[][] results_inquiry10 = new Object[1][3];
		private Object[][] results_inquiry11 = new Object[1][13];
		private Object[][] results_inquiry12 = new Object[50][1];
		private String[] userName = new String[50];
		private Map <Integer, String> field_inquiry = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry2 = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry3 = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry4 = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry5 = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry6 = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry7 = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry8 = new HashMap<Integer, String>();
		private Map <Integer, String> field_inquiry9 = new HashMap<Integer, String>();
		private ISOClient isoclient = new ISOClient();

		private String responseHangleon = "";
		private String inputHangleon = "";

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
		private String bit39 = "";
		private String bit41 = "";
		private String bit42 = "";
		private String bit43 = "";
		private String bit49 = "";
		private String bit61 = "";
		private String bit103 = "";
		private String mti = "";
		int inc = 0;
		private String inputXML = "";

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

		
		/*Create ISO Message Request to Hangleon*/
		public String buildISORequestMessage() 
		{
			byte[] result = new byte[30];

			try 
			{
				GenericPackager packager = new GenericPackager("conf/basic200.xml");

				mti="0200";
				bit2="0000000000000000000";

				SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
				bit7 = sdf.format(new Date()); 

				number = (long) Math.floor(Math.random() * 900000L) + 100000L;  

				bit11 = new Long(number).toString();
				bit12 = bit7.substring(5);
				bit13 = bit7.substring(0, 4);
				bit14 = "0000";
				bit15 = new Integer(new Integer(bit13).intValue()+1).toString();
				bit18 = "6010";
				bit32 = "770";
				bit33 = "770924";
				bit37 = "000000000000";
				bit49 = "360";	        	

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
				isoMsg.set(41, String.format("%-8s",bit41));
				isoMsg.set(42, String.format("%-15s",bit42));
				isoMsg.set(43, String.format("%-40s",bit43));
				isoMsg.set(49, bit49);
				isoMsg.set(61, bit61);
				isoMsg.set(103,bit103);

				printISOMessage(isoMsg);

				result = isoMsg.pack();

			} 
			catch (ISOException e) 
			{
				logger.error(this.getClass().getName()+" "+e.getMessage());
			}

			return new String(result);
		}	

		public void printISOMessage(ISOMsg isoMsg) 
		{
			try 
			{
				logger.info("[Request | MTI ] : "+isoMsg.getMTI());
				for (int i = 1; i <= isoMsg.getMaxField(); i++) 
				{
					if (isoMsg.hasField(i)) 
					{
						logger.info("[Request | Field "+i+" ] : "+isoMsg.getString(i));
					}
				}
			}
			catch (ISOException e)
			{
				logger.error(this.getClass().getName()+" "+e.getMessage());
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
					if(i==17)
					{
						bit39 = output[i];					
					}
				}
			}
		}

		/*SHA-1 Encryption*/ 
		private String encryptPassword(String password)
		{
			String sha1 = "";
			try
			{
				MessageDigest crypt = MessageDigest.getInstance("SHA-1");
				crypt.reset();
				crypt.update(password.getBytes("UTF-8"));
				sha1 = byteToHex(crypt.digest());
			}
			catch(NoSuchAlgorithmException e)
			{
				logger.error(this.getClass().getName()+" "+e.getMessage());
			}
			catch(UnsupportedEncodingException e)
			{
				logger.error(this.getClass().getName()+" "+e.getMessage());
			}
			return sha1;
		}

		/*Convert byte to hexadecimal*/ 
		private String byteToHex(final byte[] hash)
		{
			Formatter formatter = new Formatter();
			for (byte b : hash)
			{
				formatter.format("%02x", b);
			}
			String result = formatter.toString();
			formatter.close();
			return result;
		}    	

		/*Constructor*/ 
		public MyHandler()
		{
	
			/*Create Database Connection*/
			while(connDb.isConnected()==false)
			{	
				connDb.setProperties(propXML);
				connDb.setUrl();
				connDb.setConnection();
			}
	
			pin=propXML.getProperty("pin");
	
			cashOutISOResponseTAG=propISO.getProperty("cashoutres").split(",");
			cashOutConfISOResponseTAG=propISO.getProperty("cashoutconfres").split(",");
			cashOutCheckISOResponseTAG=propISO.getProperty("cashoutcheckres").split(",");

			mainTAGres=propISO.getProperty("mainres").split(",");					
	
			field_inquiry = new TreeMap<Integer, String>();
			field_inquiry.put(0, "refCode");
	
			field_inquiry2 = new TreeMap<Integer, String>();
			field_inquiry2.put(0, "NAMAPENGGUNA");
	
			field_inquiry3 = new TreeMap<Integer, String>();
			field_inquiry3.put(0, "fee");
	
			field_inquiry4 = new TreeMap<Integer, String>();
			field_inquiry4.put(0, "destAmount");
			field_inquiry4.put(1, "feeAmount");
			field_inquiry4.put(2, "senderName");
			field_inquiry4.put(3, "senderAddress");
			field_inquiry4.put(4, "senderID");
			field_inquiry4.put(5, "senderPhone");
			field_inquiry4.put(6, "senderCity");
			field_inquiry4.put(7, "senderCountry");
			field_inquiry4.put(8, "recipientName");
			field_inquiry4.put(9, "recipientPhone");
			field_inquiry4.put(10, "recipientAddress");
			field_inquiry4.put(11, "recipientCity");
			field_inquiry4.put(12, "recipientCountry");
	
			field_inquiry9 = new TreeMap<Integer, String>();
			field_inquiry9.put(0, "amount");
			field_inquiry9.put(1, "fee");
			field_inquiry9.put(2, "nama1");
			field_inquiry9.put(3, "alamat1");
			field_inquiry9.put(4, "idcard1");
			field_inquiry9.put(5, "telp1");
			field_inquiry9.put(6, "kota1");
			field_inquiry9.put(7, "negara1");
			field_inquiry9.put(8, "nama2");
			field_inquiry9.put(9, "telp2");
			field_inquiry9.put(10, "alamat2");
			field_inquiry9.put(11, "kota2");
			field_inquiry9.put(12, "negara2");
	
			field_inquiry5 = new TreeMap<Integer, String>();
			field_inquiry5.put(0, "dest1Acc");
	
			field_inquiry6 = new TreeMap<Integer, String>();
			field_inquiry6.put(0, "bankurl");
			field_inquiry6.put(1, "banktimeout");
			field_inquiry6.put(2, "dest1Acc");				
	
			field_inquiry7 = new TreeMap<Integer, String>();
			field_inquiry7.put(0, "httpsurl");
			field_inquiry7.put(1, "httpstimeout");
			field_inquiry7.put(2, "dest1Acc");				
	
			field_inquiry8 = new TreeMap<Integer, String>();
			field_inquiry8.put(0, "kodetransfer");
			
			/*Get All Data from WSUSERS Table*/
			checkUserName();
			
		}
		
		/*Handle HTTP Request (GET & POST)*/
		public void handle(HttpExchange t)  
		{
			try
			{
				/*Process HTTP GET Request*/
				if(t.getRequestMethod().compareTo("GET")==0)
				{
					logger.info("[Request] : "+t.getRequestURI());

					wsdl = readFile("conf/wsdl.xml");
					response=wsdl;
				}
				else if(t.getRequestMethod().compareTo("POST")==0)	/*Process HTTP POST Request*/
				{						
					xmlRemittance = new XML();

					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmssS");
					inputXML=IOUtils.toString(t.getRequestBody());
					request=xmlRemittance.cashInCheckRequest(inputXML);	//Extract XML Request Compare with cash In Check Status XML Tag

					logger.info("[Request] : "+inputXML);

					sysCode = sdf.format(new Date());	//Create sysCode

					if(getCategory(request[2],"internal","xml"))	/*XML Internal*/
					{
						/*Cash In Inquiry XML Internal*/
						if(request[4].compareTo("11")==0)	
						{
							cashInRequest = xmlRemittance.cashInRequest(inputXML);	//Extract cash In Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (description,userName,signature,productCode,destBankAcc,destAmount,transactionType,terminal,sourceID,sourceName,senderName,senderAddress,senderID,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry,notiDesc,traxId,recipientID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{cashInRequest[0],cashInRequest[1],cashInRequest[2],cashInRequest[3],cashInRequest[4],cashInRequest[5],cashInRequest[6],cashInRequest[7],cashInRequest[8],cashInRequest[9],cashInRequest[10],cashInRequest[11],cashInRequest[12],cashInRequest[13],cashInRequest[14],cashInRequest[15],cashInRequest[16],cashInRequest[17],cashInRequest[18],cashInRequest[19],cashInRequest[20],cashInRequest[21],cashInRequest[22],cashInRequest[23]});

							/*cashInRequest[0]=Description
							 *cashInRequest[1]=UserName 
							 *cashInRequest[2]=Signature
							 *cashInRequest[3]=ProductCode
							 *cashInRequest[4]=Destination Bank Account
							 *cashInRequest[5]=Destination Amount
							 *cashInRequest[6]=Transaction Type
							 *cashInRequest[7]=Terminal
							 *cashInRequest[8]=Source ID
							 *cashInRequest[9]=Source Name
							 *cashInRequest[10]=Sender Name
							 *cashInRequest[11]=Sender Address
							 *cashInRequest[12]=Sender ID
							 *cashInRequest[13]=Sender Phone
							 *cashInRequest[14]=Sender City
							 *cashInRequest[15]=Sender Country
							 *cashInRequest[16]=Recipient Name
							 *cashInRequest[17]=Recipient Phone
							 *cashInRequest[18]=Recipient Address
							 *cashInRequest[19]=Recipient City
							 *cashInRequest[20]=Recipient Country
							 *cashInRequest[21]=NotiDesc
							 *cashInRequest[22]=Transaction ID
							 *cashInRequest[23]=Recipient ID
							 */
							
							cashInResponse[0]=cashInRequest[0];
							cashInResponse[1]=cashInRequest[1];
							cashInResponse[2]=cashInRequest[2];
							cashInResponse[3]=sysCode;
							cashInResponse[7]=cashInRequest[3];
							cashInResponse[8]=cashInRequest[4];
							cashInResponse[9]=cashInRequest[5];
							cashInResponse[10]=getFee(cashInRequest[5],cashInRequest[1]);
							cashInResponse[11]=cashInRequest[8];
							cashInResponse[12]=cashInRequest[9];
							cashInResponse[13]=cashInRequest[7];
							cashInResponse[14]=cashInRequest[10];
							cashInResponse[15]=cashInRequest[11];
							cashInResponse[16]=cashInRequest[12];
							cashInResponse[17]=cashInRequest[13];
							cashInResponse[18]=cashInRequest[14];
							cashInResponse[19]=cashInRequest[15];
							cashInResponse[20]=cashInRequest[16];
							cashInResponse[21]=cashInRequest[17];
							cashInResponse[22]=cashInRequest[18];
							cashInResponse[23]=cashInRequest[19];
							cashInResponse[24]=cashInRequest[20];
							cashInResponse[25]=cashInRequest[21];
							cashInResponse[26]=cashInRequest[6];
							cashInResponse[27]=cashInRequest[22];	       	 						       	 						       	 					
							cashInResponse[28]=cashInRequest[23];	       	 						       	 						       	 					

							if(Arrays.asList(userName).indexOf(cashInRequest[1]+pin)>=0)	/*Check userName & PIN*/
							{       
								if(!checkSignature(cashInRequest,2))
								{		       	 				
									if(checkBlank(cashInRequest))	/*Check if any empty XML Tag Value*/
									{
										decimal[0]=cashInRequest[5];
										decimal[1]=cashInRequest[13];
										decimal[2]=cashInRequest[17];
										if(checkDecimal(decimal))	/*Check if Destination Amount, senderPhone, recipientPhone value is decimal*/
										{
											number = (long) Math.floor(Math.random() * 90000000L) + 10000000L;  
											refCode = "00"+new Long(number).toString();

											cashInResponse[4]=refCode;
											cashInResponse[5]="00";
											cashInResponse[6]="Approve";
										}
										else
										{
											cashInResponse[4]="";
											cashInResponse[5]="7050";
											cashInResponse[6]="Invalid amount";	       	 						
										}
									}
									else
									{
										cashInResponse[4]="";
										cashInResponse[5]="7012";
										cashInResponse[6]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashInResponse[4]="";
									cashInResponse[5]="7127";
									cashInResponse[6]="Invalid signature";	       	 					       	 					
								}
							}
							else
							{
								cashInResponse[4]="";
								cashInResponse[5]="7020";
								cashInResponse[6]="Invalid account";	       	 				
							}

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set feeAmount='"+cashInResponse[10]+"', resultCode='"+cashInResponse[5]+"', resultDesc='"+cashInResponse[6]+"',refCode='"+refCode+"' where traxId='"+cashInRequest[22]+"' and transactionType='11' and resultCode is null", new Object[]{});

							response=xmlRemittance.cashInResponse(cashInResponse);	//Create cash In XML Response
						}
						else if(request[4].compareTo("12")==0)	/*Cash In Confirmation XML Internal*/
						{
							cashInConfRequest = xmlRemittance.cashInConfRequest(inputXML);	//Extract cash In Confirmation Request

							cashInConfRequest[6]=getFee(cashInConfRequest[5],cashInConfRequest[1]);

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (description,userName,signature,productCode,destBankAcc,destAmount,feeAmount,transactionType,terminal,sourceID,sourceName,senderName,senderAddress,senderID,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry,notiDesc,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{cashInConfRequest[0],cashInConfRequest[1],cashInConfRequest[2],cashInConfRequest[3],cashInConfRequest[4],cashInConfRequest[5],cashInConfRequest[6],cashInConfRequest[7],cashInConfRequest[8],cashInConfRequest[9],cashInConfRequest[10],cashInConfRequest[11],cashInConfRequest[12],cashInConfRequest[13],cashInConfRequest[14],cashInConfRequest[15],cashInConfRequest[16],cashInConfRequest[17],cashInConfRequest[18],cashInConfRequest[19],cashInConfRequest[20],cashInConfRequest[21],cashInConfRequest[22],cashInConfRequest[23],cashInConfRequest[24],cashInConfRequest[25]});

							/*cashInConfRequest[0]=Description
							 *cashInConfRequest[1]=UserName 
							 *cashInConfRequest[2]=Signature
							 *cashInConfRequest[3]=ProductCode
							 *cashInConfRequest[4]=Destination Bank Account
							 *cashInConfRequest[5]=Destination Amount
							 *cashInConfRequest[6]=Fee Amount
							 *cashInConfRequest[7]=Transaction Type
							 *cashInConfRequest[8]=Terminal
							 *cashInConfRequest[9]=Source ID
							 *cashInConfRequest[10]=Source Name
							 *cashInConfRequest[11]=Sender Name
							 *cashInConfRequest[12]=Sender Address
							 *cashInConfRequest[13]=Sender ID
							 *cashInConfRequest[14]=Sender Phone
							 *cashInConfRequest[15]=Sender City
							 *cashInConfRequest[16]=Sender Country
							 *cashInConfRequest[17]=Recipient Name
							 *cashInConfRequest[18]=Recipient Phone
							 *cashInConfRequest[19]=Recipient Address
							 *cashInConfRequest[20]=Recipient City
							 *cashInConfRequest[21]=Recipient Country
							 *cashInConfRequest[22]=NotiDesc
							 *cashInConfRequest[23]=Transaction ID
							 *cashInConfRequest[24]=Reference Code
							 *cashInConfRequest[25]=Recipient ID
							 */

							cashInConfResponse[0]=cashInConfRequest[0];
							cashInConfResponse[1]=cashInConfRequest[1];
							cashInConfResponse[2]=cashInConfRequest[2];
							cashInConfResponse[3]=sysCode;
							cashInConfResponse[4]=cashInConfRequest[24];
							cashInConfResponse[7]=cashInConfRequest[3];
							cashInConfResponse[8]=cashInConfRequest[4];
							cashInConfResponse[9]=cashInConfRequest[5];
							cashInConfResponse[10]=cashInConfRequest[6];
							cashInConfResponse[11]=cashInConfRequest[9];
							cashInConfResponse[12]=cashInConfRequest[10];
							cashInConfResponse[13]=cashInConfRequest[8];
							cashInConfResponse[14]=cashInConfRequest[11];
							cashInConfResponse[15]=cashInConfRequest[12];
							cashInConfResponse[16]=cashInConfRequest[13];
							cashInConfResponse[17]=cashInConfRequest[14];
							cashInConfResponse[18]=cashInConfRequest[15];
							cashInConfResponse[19]=cashInConfRequest[16];
							cashInConfResponse[20]=cashInConfRequest[17];
							cashInConfResponse[21]=cashInConfRequest[18];
							cashInConfResponse[22]=cashInConfRequest[19];
							cashInConfResponse[23]=cashInConfRequest[20];
							cashInConfResponse[24]=cashInConfRequest[21];
							cashInConfResponse[25]=cashInConfRequest[22];
							cashInConfResponse[26]=cashInConfRequest[7];
							cashInConfResponse[27]=cashInConfRequest[23];	       	 				       	 			
							cashInConfResponse[28]=cashInConfRequest[25];	       	 				       	 			

							if(Arrays.asList(userName).indexOf(cashInConfRequest[1]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashInConfRequest,2))
								{	
									if(checkBlank(cashInConfRequest))	/*Check if any empty XML Tag Value*/
									{
										decimal2[0]=cashInConfRequest[5];
										decimal2[1]=cashInConfRequest[14];
										decimal2[2]=cashInConfRequest[18];
										decimal2[3]=cashInConfRequest[6];
										if(checkDecimal(decimal2))	/*Check if Destination Amount, feeAmount, senderPhone, recipientPhone value is decimal*/
										{
											if(checkRefCode(cashInConfRequest[24]))	/*Check if Ref Code is available*/
											{	
												if(checkRefCodeDouble(cashInConfRequest[24],"12"))	/*Check if there is a Success Cash In Confirmation with same Ref Code*/
												{	
													if(eva.getID(dest1Acc,cashInConfRequest[14], cashInConfRequest[0], cashInConfRequest[5], "CASHIN", cashInConfRequest[23], cashInConfRequest[22], cashInConfRequest[6])==0)	/*Send eVa Request*/
													{	
														cashInConfResponse[5]="00";
														cashInConfResponse[6]="Approve";
													}
													else
													{
														cashInConfResponse[5]="7000";
														cashInConfResponse[6]="System Maintenance";	       	 							
													}
												}
												else
												{
													cashInConfResponse[5]="7106";
													cashInConfResponse[6]="Invalid or unknown bank account number";	       	 																				
												}
											}
											else
											{
												cashInConfResponse[5]="7061";
												cashInConfResponse[6]="Reference code not found";	       	 						
											}
										}
										else
										{
											cashInConfResponse[5]="7050";
											cashInConfResponse[6]="Invalid amount";	       	 						
										}
									}
									else
									{
										cashInConfResponse[5]="7012";
										cashInConfResponse[6]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashInConfResponse[5]="7127";
									cashInConfResponse[6]="Invalid signature";	       	 					       	 						
								}
							}
							else
							{
								cashInConfResponse[5]="7020";
								cashInConfResponse[6]="Invalid account";	       	 				
							}

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashInConfResponse[5]+"', resultDesc='"+cashInConfResponse[6]+"' where traxId=? and transactionType='12' and resultCode is NULL", new Object[]{cashInConfRequest[23].toString()});

							response=xmlRemittance.cashInConfResponse(cashInConfResponse);	//Create cash In Confirmation XML Response

						}
						else if(request[4].compareTo("13")==0)	/*Cash In Check Status XML Internal*/
						{
							cashInCheckRequest = xmlRemittance.cashInCheckRequest(inputXML);	//Extract cash In Check Status Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,transactionType,terminal,sourceID,traxId,refCode) values (?,?,?,?,?,?,?,?,?)",new Object[]{cashInCheckRequest[0],cashInCheckRequest[1],cashInCheckRequest[2],cashInCheckRequest[3],cashInCheckRequest[4],cashInCheckRequest[5],cashInCheckRequest[6],cashInCheckRequest[7],cashInCheckRequest[8]});

							/*cashInCheckRequest[0]=UserName 
							 *cashInCheckRequest[1]=Signature
							 *cashInCheckRequest[2]=ProductCode
							 *cashInCheckRequest[3]=Destination Amount
							 *cashInCheckRequest[4]=Transaction Type
							 *cashInCheckRequest[5]=Terminal
							 *cashInCheckRequest[6]=Source ID
							 *cashInCheckRequest[7]=Transaction ID
							 *cashInCheckRequest[8]=Reference Code
							 */

							cashInCheckResponse[0]=sysCode;
							cashInCheckResponse[1]=cashInCheckRequest[8];
							cashInCheckResponse[4]=cashInCheckRequest[2];
							cashInCheckResponse[5]=cashInCheckRequest[3];
							cashInCheckResponse[6]=cashInCheckRequest[6];
							cashInCheckResponse[7]=cashInCheckRequest[4];
							cashInCheckResponse[8]=cashInCheckRequest[7];	       	 				       	 				       	 		


							if(Arrays.asList(userName).indexOf(cashInCheckRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashInCheckRequest,1))
								{	
									if(checkBlank(cashInCheckRequest))	/*Check if any empty XML Tag Value*/
									{
										if(new Integer(cashInCheckRequest[3]).intValue()>0)	/*Check if Destination Amount > 0*/
										{	
											if(checkRefCode(cashInCheckRequest[8]))	/*Check if Ref Code is available*/
											{	
												if(cashInCheck(cashInCheckRequest[8]))	/*Check if Cash In & Cash In Confirmation are Success*/
												{	
													cashInCheckResponse[2]="7133";
													cashInCheckResponse[3]="Transaction already confirm";
												}
												else
												{
													cashInCheckResponse[2]="7134";
													cashInCheckResponse[3]="Transaction has not confirm";	       	 								       	 								
												}
											}
											else
											{
												cashInCheckResponse[2]="7061";
												cashInCheckResponse[3]="Reference code not found";	       	 						
											}
										}
										else
										{
											cashInCheckResponse[2]="7050";
											cashInCheckResponse[3]="Invalid amount";	       	 							       	 						
										}
									}
									else
									{
										cashInCheckResponse[2]="7012";
										cashInCheckResponse[3]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashInCheckResponse[2]="7127";
									cashInCheckResponse[3]="Invalid signature";	       	 					       	 						
								}
							}
							else
							{
								cashInCheckResponse[2]="7020";
								cashInCheckResponse[3]="Invalid account";	       	 				
							}

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashInCheckResponse[2]+"', resultDesc='"+cashInCheckResponse[3]+"' where traxId=? and transactionType='13' and resultCode is NULL", new Object[]{cashInCheckRequest[7].toString()});

							response=xmlRemittance.cashInCheckResponse(cashInCheckResponse);	//Create cash In Check Status XML Response
						}
						else if(request[4].compareTo("14")==0)	/*Cash Out Inquiry XML Internal*/
						{

							int jml9=0;
							cashOutRequest = xmlRemittance.cashOutRequest(inputXML);	//Extract cash Out Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,transactionType,terminal,sourceID,sourceName,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?)",new Object[]{cashOutRequest[0],cashOutRequest[1],cashOutRequest[2],cashOutRequest[3],cashOutRequest[4],cashOutRequest[5],cashOutRequest[6],cashOutRequest[7],cashOutRequest[8],cashOutRequest[9]});

							results_inquiry9=connDb.getQuery("select destAmount,feeAmount,senderName,senderAddress,senderID,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry from transaction where refCode=? and transactionType='11' and resultCode='00'",new Object[]{"","","","","","","","","","","","",""}, field_inquiry4, new Object[]{cashOutRequest[8]},0);

							/*cashOutRequest[0]=UserName 
							 *cashOutRequest[1]=Signature
							 *cashOutRequest[2]=ProductCode
							 *cashOutRequest[3]=Transaction Type
							 *cashOutRequest[4]=Terminal
							 *cashOutRequest[5]=Source ID
							 *cashOutRequest[6]=Source Name
							 *cashOutRequest[7]=Transaction ID
							 *cashOutRequest[8]=Reference Code
							 *cashOutRequest[9]=Recipient ID
							 */

							cashOutResponse[0]=cashOutRequest[0];
							cashOutResponse[1]=cashOutRequest[1];
							cashOutResponse[2]=sysCode;
							cashOutResponse[3]=cashOutRequest[8];
							cashOutResponse[6]=cashOutRequest[2];
							cashOutResponse[9]=cashOutRequest[5];
							cashOutResponse[10]=cashOutRequest[6];
							cashOutResponse[11]=cashOutRequest[4];
							cashOutResponse[23]=cashOutRequest[3];
							cashOutResponse[24]=cashOutRequest[7];
							cashOutResponse[25]=cashOutRequest[9];

							if(connDb.getRowCount(0)>0)
							{	
								jml9=1;
								cashOutResponse[7]=results_inquiry9[0][0].toString();
								cashOutResponse[8]=results_inquiry9[0][1].toString();
								cashOutResponse[12]=results_inquiry9[0][2].toString();
								cashOutResponse[13]=results_inquiry9[0][3].toString();
								cashOutResponse[14]=results_inquiry9[0][4].toString();
								cashOutResponse[15]=results_inquiry9[0][5].toString();
								cashOutResponse[16]=results_inquiry9[0][6].toString();
								cashOutResponse[17]=results_inquiry9[0][7].toString();
								cashOutResponse[18]=results_inquiry9[0][8].toString();
								cashOutResponse[19]=results_inquiry9[0][9].toString();
								cashOutResponse[20]=results_inquiry9[0][10].toString();
								cashOutResponse[21]=results_inquiry9[0][11].toString();
								cashOutResponse[22]=results_inquiry9[0][12].toString();
							}
							else
							{
								results_inquiry11=connDb.getQuery("select amount, fee, nama1, alamat1, idcard1, telp1, kota1, negara1, nama2, telp2, alamat2, kota2, negara2 from transactioniso where kodetransfer=? and bit3='002014' and bit103='380099' and bit39='00'",new Object[]{"","","","","","","","","","","","",""},field_inquiry9, new Object[]{cashOutRequest[8]},0);
								if(connDb.getRowCount(0)>0)
								{	
									jml9=2;
									cashOutResponse[7]=results_inquiry11[0][0].toString();
									cashOutResponse[8]=results_inquiry11[0][1].toString();
									cashOutResponse[12]=results_inquiry11[0][2].toString();
									cashOutResponse[13]=results_inquiry11[0][3].toString();
									cashOutResponse[14]=results_inquiry11[0][4].toString();
									cashOutResponse[15]=results_inquiry11[0][5].toString();
									cashOutResponse[16]=results_inquiry11[0][6].toString();
									cashOutResponse[17]=results_inquiry11[0][7].toString();
									cashOutResponse[18]=results_inquiry11[0][8].toString();
									cashOutResponse[19]=results_inquiry11[0][9].toString();
									cashOutResponse[20]=results_inquiry11[0][10].toString();
									cashOutResponse[21]=results_inquiry11[0][11].toString();
									cashOutResponse[22]=results_inquiry11[0][12].toString();
								}
								else
								{	
									cashOutResponse[7]="";
									cashOutResponse[8]="";
									cashOutResponse[12]="";
									cashOutResponse[13]="";
									cashOutResponse[14]="";
									cashOutResponse[15]="";
									cashOutResponse[16]="";
									cashOutResponse[17]="";
									cashOutResponse[18]="";
									cashOutResponse[19]="";
									cashOutResponse[20]="";
									cashOutResponse[21]="";
									cashOutResponse[22]="";
								}	
							}

							if(Arrays.asList(userName).indexOf(cashOutRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutRequest,1))
								{	
									if(checkBlank(cashOutRequest))	/*Check if any empty XML Tag Value*/
									{
										if(checkRefCode(cashOutRequest[8]) || checkRefCodeISO(cashOutRequest[8]))	/*Check if Ref Code is available from XML and ISO*/
										{	
											if(checkRefCodeDouble(cashOutRequest[8],"15"))	/*Check if there is a Success Cash Out Confirmation with same Ref Code*/
											{	
												if(cashInCheck(cashOutRequest[8]) || cashInCheckISO(cashOutRequest[8]))
												{	
													cashOutResponse[4]="00";
													cashOutResponse[5]="Approve";
												}
												else
												{
													cashOutResponse[4]="7000";
													cashOutResponse[5]="System Maintenance";       	 								
												}
											}
											else
											{
												cashOutResponse[4]="7135";
												cashOutResponse[5]="Transaction already cashout";       	 								
											}
										}
										else
										{
											cashOutResponse[4]="7061";
											cashOutResponse[5]="Reference code not found";	       	 						
										}
									}
									else
									{
										cashOutResponse[4]="7012";
										cashOutResponse[5]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutResponse[4]="7127";
									cashOutResponse[5]="Invalid signature";	       	 					       	 						
								}
							}
							else
							{
								cashOutResponse[4]="7020";
								cashOutResponse[5]="Invalid account";	       	 				
							}

							//Update Response to Transaction Table
							if(jml9==2)
							{	
								connDb.updateQuery("update transaction set destAmount='"+results_inquiry11[0][0].toString()+"',feeAmount='"+results_inquiry11[0][1].toString()+"',senderName='"+results_inquiry11[0][2].toString()+"',senderAddress='"+results_inquiry11[0][3].toString()+"',senderID='"+results_inquiry11[0][4].toString()+"',senderPhone='"+results_inquiry11[0][5].toString()+"',senderCity='"+results_inquiry11[0][6].toString()+"',senderCountry='"+results_inquiry11[0][7].toString()+"',recipientName='"+results_inquiry11[0][8].toString()+"',recipientPhone='"+results_inquiry11[0][9].toString()+"',recipientAddress='"+results_inquiry11[0][10].toString()+"',recipientCity='"+results_inquiry11[0][11].toString()+"',recipientCountry='"+results_inquiry11[0][12].toString()+"', resultCode='"+cashOutResponse[4]+"', resultDesc='"+cashOutResponse[5]+"' where traxId=? and transactionType='14' and resultCode is NULL", new Object[]{cashOutRequest[7].toString()});
							}
							else if(jml9==1)
							{	
								connDb.updateQuery("update transaction set destAmount='"+results_inquiry9[0][0].toString()+"',feeAmount='"+results_inquiry9[0][1].toString()+"',senderName='"+results_inquiry9[0][2].toString()+"',senderAddress='"+results_inquiry9[0][3].toString()+"',senderID='"+results_inquiry9[0][4].toString()+"',senderPhone='"+results_inquiry9[0][5].toString()+"',senderCity='"+results_inquiry9[0][6].toString()+"',senderCountry='"+results_inquiry9[0][7].toString()+"',recipientName='"+results_inquiry9[0][8].toString()+"',recipientPhone='"+results_inquiry9[0][9].toString()+"',recipientAddress='"+results_inquiry9[0][10].toString()+"',recipientCity='"+results_inquiry9[0][11].toString()+"',recipientCountry='"+results_inquiry9[0][12].toString()+"', resultCode='"+cashOutResponse[4]+"', resultDesc='"+cashOutResponse[5]+"' where traxId=? and transactionType='14' and resultCode is NULL", new Object[]{cashOutRequest[7].toString()});
							}
							else
							{
								connDb.updateQuery("update transaction set resultCode='"+cashOutResponse[4]+"', resultDesc='"+cashOutResponse[5]+"' where traxId=? and transactionType='14' and resultCode is NULL", new Object[]{cashOutRequest[7].toString()});								
							}

							response=xmlRemittance.cashOutResponse(cashOutResponse);	//Create cash Out XML Response       	 				
						}
						else if(request[4].compareTo("15")==0)	/*Cash Out Confirmation XML Internal*/
						{
							cashOutConfRequest = xmlRemittance.cashOutConfRequest(inputXML);	//Extract cash Out Confirmation Request

							cashOutConfRequest[4]=getFee(cashOutConfRequest[3],cashOutConfRequest[0]);

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,feeAmount,transactionType,terminal,sourceID,sourceName,senderName,senderAddress,senderId,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{cashOutConfRequest[0],cashOutConfRequest[1],cashOutConfRequest[2],cashOutConfRequest[3],cashOutConfRequest[4],cashOutConfRequest[5],cashOutConfRequest[6],cashOutConfRequest[7],cashOutConfRequest[8],cashOutConfRequest[9],cashOutConfRequest[10],cashOutConfRequest[11],cashOutConfRequest[12],cashOutConfRequest[13],cashOutConfRequest[14],cashOutConfRequest[15],cashOutConfRequest[16],cashOutConfRequest[17],cashOutConfRequest[18],cashOutConfRequest[19],cashOutConfRequest[20],cashOutConfRequest[21],cashOutConfRequest[22]});

							/*cashOutConfRequest[0]=UserName 
							 *cashOutConfRequest[1]=Signature
							 *cashOutConfRequest[2]=ProductCode
							 *cashOutConfRequest[3]=Destination Amount
							 *cashOutConfRequest[4]=Fee Amount
							 *cashOutConfRequest[5]=Transaction Type
							 *cashOutConfRequest[6]=Terminal
							 *cashOutConfRequest[7]=Source ID
							 *cashOutConfRequest[8]=Source Name
							 *cashOutConfRequest[9]=Sender Name
							 *cashOutConfRequest[10]=Sender Address
							 *cashOutConfRequest[11]=Sender ID
							 *cashOutConfRequest[12]=Sender Phone
							 *cashOutConfRequest[13]=Sender City
							 *cashOutConfRequest[14]=Sender Country
							 *cashOutConfRequest[15]=Recipient Name
							 *cashOutConfRequest[16]=Recipient Phone
							 *cashOutConfRequest[17]=Recipient Address
							 *cashOutConfRequest[18]=Recipient City
							 *cashOutConfRequest[19]=Recipient Country
							 *cashOutConfRequest[20]=Transaction ID
							 *cashOutConfRequest[21]=Reference Code
							 *cashOutConfRequest[22]=Recipient ID
							 */
							
							
							cashOutConfResponse[0]=cashOutConfRequest[0];
							cashOutConfResponse[1]=cashOutConfRequest[1];
							cashOutConfResponse[2]=sysCode;
							cashOutConfResponse[3]=cashOutConfRequest[21];
							cashOutConfResponse[6]=cashOutConfRequest[2];
							cashOutConfResponse[7]=cashOutConfRequest[3];
							cashOutConfResponse[8]=cashOutConfRequest[4];
							cashOutConfResponse[9]=cashOutConfRequest[7];
							cashOutConfResponse[10]=cashOutConfRequest[8];
							cashOutConfResponse[11]=cashOutConfRequest[6];
							cashOutConfResponse[12]=cashOutConfRequest[9];
							cashOutConfResponse[13]=cashOutConfRequest[10];
							cashOutConfResponse[14]=cashOutConfRequest[11];
							cashOutConfResponse[15]=cashOutConfRequest[12];
							cashOutConfResponse[16]=cashOutConfRequest[13];
							cashOutConfResponse[17]=cashOutConfRequest[14];
							cashOutConfResponse[18]=cashOutConfRequest[15];
							cashOutConfResponse[19]=cashOutConfRequest[16];
							cashOutConfResponse[20]=cashOutConfRequest[17];
							cashOutConfResponse[21]=cashOutConfRequest[18];
							cashOutConfResponse[22]=cashOutConfRequest[19];
							cashOutConfResponse[23]=cashOutConfRequest[5];
							cashOutConfResponse[24]=cashOutConfRequest[20];
							cashOutConfResponse[25]=cashOutConfRequest[22];

							if(Arrays.asList(userName).indexOf(cashOutConfRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutConfRequest,1))
								{	
									if(checkBlank(cashOutConfRequest))	/*Check if any empty XML Tag Value*/
									{
										decimal2[0]=cashOutConfRequest[3];
										decimal2[1]=cashOutConfRequest[12];
										decimal2[2]=cashOutConfRequest[16];
										decimal2[3]=cashOutConfRequest[4];
										if(checkDecimal(decimal2))	/*Check if Destination Amount, feeAmount, senderPhone, recipientPhone value is decimal*/
										{
											if(checkRefCode(cashOutConfRequest[21]) || checkRefCodeISO(cashOutConfRequest[21]))	/*Check if Ref Code is available from XML / ISO*/
											{	
												if(checkRefCodeDouble(cashOutConfRequest[21],"15"))	/*Check if there is a Success Cash Out Confirmation with same Ref Code*/
												{	
													if(eva.getID(dest1Acc,cashOutConfRequest[12], "Cashout Remittance", cashOutConfRequest[3], "CASHOUT", cashOutConfRequest[20], "notiDesc", cashOutConfRequest[4])==0)	/*Send eVa Request*/
													{	
														cashOutConfResponse[4]="00";
														cashOutConfResponse[5]="Approve";
													}
													else
													{
														cashOutConfResponse[4]="7000";
														cashOutConfResponse[5]="System Maintenance";	       	 							
													}
												}
												else
												{
													cashOutConfResponse[4]="7135";
													cashOutConfResponse[5]="Transaction already cashout";	       	 								       	 								
												}	       	 						
											}
											else
											{
												cashOutConfResponse[4]="7061";
												cashOutConfResponse[5]="Reference code not found";	       	 						
											}
										}
										else
										{
											cashOutConfResponse[4]="7050";
											cashOutConfResponse[5]="Invalid amount";	       	 						
										}
									}
									else
									{
										cashOutConfResponse[4]="7012";
										cashOutConfResponse[5]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutConfResponse[4]="7127";
									cashOutConfResponse[5]="Invalid signature";	       	 					       	 						
								}
							}
							else
							{
								cashOutConfResponse[4]="7020";
								cashOutConfResponse[5]="Invalid account";	       	 				
							}

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashOutConfResponse[4]+"', resultDesc='"+cashOutConfResponse[5]+"' where traxId=? and transactionType='15' and resultCode is NULL", new Object[]{cashOutConfRequest[20].toString()});

							response=xmlRemittance.cashOutConfResponse(cashOutConfResponse);	//Create cash Out Confirmation XML Response

						}
						else if(request[4].compareTo("16")==0)	/*Cash Out Check Status XML Internal*/
						{
							cashOutCheckRequest = xmlRemittance.cashOutCheckRequest(inputXML);	//Extract cash Out Check Status Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,transactionType,terminal,sourceID,traxId,refCode) values (?,?,?,?,?,?,?,?,?)",new Object[]{cashOutCheckRequest[0],cashOutCheckRequest[1],cashOutCheckRequest[2],cashOutCheckRequest[3],cashOutCheckRequest[4],cashOutCheckRequest[5],cashOutCheckRequest[6],cashOutCheckRequest[7],cashOutCheckRequest[8]});

							/*cashOutCheckRequest[0]=UserName 
							 *cashOutCheckRequest[1]=Signature
							 *cashOutCheckRequest[2]=ProductCode
							 *cashOutCheckRequest[3]=Destination Amount
							 *cashOutCheckRequest[4]=Transaction Type
							 *cashOutCheckRequest[5]=Terminal
							 *cashOutCheckRequest[6]=Source ID
							 *cashOutCheckRequest[7]=Transaction ID
							 *cashOutCheckRequest[8]=Reference Code
							 */

							cashOutCheckResponse[0]=sysCode;
							cashOutCheckResponse[1]=cashOutCheckRequest[8];
							cashOutCheckResponse[4]=cashOutCheckRequest[2];
							cashOutCheckResponse[5]=cashOutCheckRequest[3];
							cashOutCheckResponse[6]=cashOutCheckRequest[6];
							cashOutCheckResponse[7]=cashOutCheckRequest[4];
							cashOutCheckResponse[8]=cashOutCheckRequest[7];	       	 				       	 				       	 		


							if(Arrays.asList(userName).indexOf(cashOutCheckRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutCheckRequest,1))
								{	
									if(checkBlank(cashOutCheckRequest))	/*Check if any empty XML Tag Value*/
									{
										if(new Integer(cashOutCheckRequest[3]).intValue()>0)	/*Check if Destination Amount > 0*/
										{	
											if(checkRefCode(cashOutCheckRequest[8]) || checkRefCodeISO(cashOutCheckRequest[8]))	/*Check if Ref Code is available from XML / ISO*/
											{	
												if(cashOutCheck(cashOutCheckRequest[8]))	/*Check if Cash Out & Cash Out Confirmation are Success*/
												{	
													cashOutCheckResponse[2]="7133";
													cashOutCheckResponse[3]="Transaction already confirm";
												}
												else
												{
													cashOutCheckResponse[2]="7134";
													cashOutCheckResponse[3]="Transaction has not confirm";	       	 								
												}
											}
											else
											{
												cashOutCheckResponse[2]="7061";
												cashOutCheckResponse[3]="Reference code not found";	       	 						
											}
										}
										else
										{
											cashOutCheckResponse[2]="7050";
											cashOutCheckResponse[3]="Invalid amount";	       	 							       	 						
										}
									}
									else
									{
										cashOutCheckResponse[2]="7012";
										cashOutCheckResponse[3]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutCheckResponse[2]="7127";
									cashOutCheckResponse[3]="Invalid signature";	       	 					       	 						
								}
							}
							else
							{
								cashOutCheckResponse[2]="7020";
								cashOutCheckResponse[3]="Invalid account";	       	 				
							}

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashOutCheckResponse[2]+"', resultDesc='"+cashOutCheckResponse[3]+"' where traxId=? and transactionType='16' and resultCode is NULL", new Object[]{cashOutCheckRequest[7].toString()});

							response=xmlRemittance.cashOutCheckResponse(cashOutCheckResponse);	//Create cash Out Check Status XML Response

						}
						else
						{
							response=xmlRemittance.errorResponse("7060","Invalid transaction type",request[2]);	//Create Error Response
						}	
					}
					else if(getCategory(request[2],"bank","xml"))	/*XML to Bank*/
					{
						int check=0;

						/*Cash In Inquiry XML Bank*/
						if(request[4].compareTo("11")==0)
						{
							cashInRequest = xmlRemittance.cashInRequest(inputXML);	//Extract cash In Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (description,userName,signature,productCode,destBankAcc,destAmount,transactionType,terminal,sourceID,sourceName,senderName,senderAddress,senderID,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry,notiDesc,traxId,recipientID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{cashInRequest[0],cashInRequest[1],cashInRequest[2],cashInRequest[3],cashInRequest[4],cashInRequest[5],cashInRequest[6],cashInRequest[7],cashInRequest[8],cashInRequest[9],cashInRequest[10],cashInRequest[11],cashInRequest[12],cashInRequest[13],cashInRequest[14],cashInRequest[15],cashInRequest[16],cashInRequest[17],cashInRequest[18],cashInRequest[19],cashInRequest[20],cashInRequest[21],cashInRequest[22],cashInRequest[23]});

							if(Arrays.asList(userName).indexOf(cashInRequest[1]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashInRequest,2))
								{	
									if(checkBlank(cashInRequest))	/*Check if any empty XML Tag Value*/
									{
										decimal[0]=cashInRequest[5];
										decimal[1]=cashInRequest[13];
										decimal[2]=cashInRequest[17];
										if(checkDecimal(decimal))	/*Check if Destination Amount, senderPhone, recipientPhone value is decimal*/
										{
											traxId = cashInRequest[22];
											http.sendHTTPPOST(httpsurl, inputXML,new Integer(httpstimeout),"urn:routeDx#route");	/*Send XML to Bank*/

											if(http.getFail()==0)	
											{	
												check=1;
												response = http.getPOSTResponse();	/*Extract  Response from Bank*/
												response = response.replaceFirst("<feeAmount xsi:type='xsd:string'>.*</feeAmount>", "<feeAmount xsi:type='xsd:string'>"+getFee(cashInResponse[5],cashInResponse[1])+"</feeAmount>");

												//Replace Fee di dlm XML
												cashInResponseBank=xmlRemittanceBank.cashInResponse(response);
												cashInResponse[5]=cashInResponseBank[2];
												cashInResponse[6]=cashInResponseBank[3];
											}	
											else
											{	
												cashInResponse[5]="7000";
												cashInResponse[6]="System Maintenance";	       	 							
											}	
										}
										else
										{
											cashInResponse[5]="7050";
											cashInResponse[6]="Invalid amount";	       	 						
										}
									}
									else
									{
										cashInResponse[5]="7012";
										cashInResponse[6]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashInResponse[5]="7127";
									cashInResponse[6]="Invalid signature";	       	 						    	       				
								}
							}
							else
							{
								cashInResponse[5]="7020";
								cashInResponse[6]="Invalid account";
							}

							if(check==0)
							{
								/*cashInRequest[0]=Description
								 *cashInRequest[1]=UserName 
								 *cashInRequest[2]=Signature
								 *cashInRequest[3]=ProductCode
								 *cashInRequest[4]=Destination Bank Account
								 *cashInRequest[5]=Destination Amount
								 *cashInRequest[6]=Transaction Type
								 *cashInRequest[7]=Terminal
								 *cashInRequest[8]=Source ID
								 *cashInRequest[9]=Source Name
								 *cashInRequest[10]=Sender Name
								 *cashInRequest[11]=Sender Address
								 *cashInRequest[12]=Sender ID
								 *cashInRequest[13]=Sender Phone
								 *cashInRequest[14]=Sender City
								 *cashInRequest[15]=Sender Country
								 *cashInRequest[16]=Recipient Name
								 *cashInRequest[17]=Recipient Phone
								 *cashInRequest[18]=Recipient Address
								 *cashInRequest[19]=Recipient City
								 *cashInRequest[20]=Recipient Country
								 *cashInRequest[21]=NotiDesc
								 *cashInRequest[22]=Transaction ID
								 *cashInRequest[23]=Recipient ID
								 */

								cashInResponse[0]=cashInRequest[0];
								cashInResponse[1]=cashInRequest[1];
								cashInResponse[2]=cashInRequest[2];
								cashInResponse[3]=sysCode;
								cashInResponse[4]="";
								cashInResponse[7]=cashInRequest[3];
								cashInResponse[8]=cashInRequest[4];
								cashInResponse[9]=cashInRequest[5];
								cashInResponse[10]="";
								cashInResponse[11]=cashInRequest[8];
								cashInResponse[12]=cashInRequest[9];
								cashInResponse[13]=cashInRequest[7];
								cashInResponse[14]=cashInRequest[10];
								cashInResponse[15]=cashInRequest[11];
								cashInResponse[16]=cashInRequest[12];
								cashInResponse[17]=cashInRequest[13];
								cashInResponse[18]=cashInRequest[14];
								cashInResponse[19]=cashInRequest[15];
								cashInResponse[20]=cashInRequest[16];
								cashInResponse[21]=cashInRequest[17];
								cashInResponse[22]=cashInRequest[18];
								cashInResponse[23]=cashInRequest[19];
								cashInResponse[24]=cashInRequest[20];
								cashInResponse[25]=cashInRequest[21];
								cashInResponse[26]=cashInRequest[6];
								cashInResponse[27]=cashInRequest[22];	       	 						       	 						       	 					
								cashInResponse[28]=cashInRequest[23];	       	 						       	 						       	 					


								response=xmlRemittance.cashInResponse(cashInResponse);	//Create cash In XML Response
							}
							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set refCode='"+cashInResponseBank[1]+"', feeAmount='"+getFee(cashInResponse[5],cashInResponse[1])+"', resultCode='"+cashInResponse[5]+"', resultDesc='"+cashInResponse[6]+"',refCode='"+refCode+"' where traxId=? and transactionType='11' and resultCode is NULL", new Object[]{cashInRequest[22].toString()});
						}
						else if(request[4].compareTo("12")==0)	/*Cash In Confirmation XML Bank*/
						{
							cashInConfRequest = xmlRemittance.cashInConfRequest(inputXML);	//Extract cash In Confirmation Request

							cashInConfRequest[6]=getFee(cashInConfRequest[5],cashInConfRequest[1]);

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (description,userName,signature,productCode,destBankAcc,destAmount,feeAmount,transactionType,terminal,sourceID,sourceName,senderName,senderAddress,senderID,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry,notiDesc,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{cashInConfRequest[0],cashInConfRequest[1],cashInConfRequest[2],cashInConfRequest[3],cashInConfRequest[4],cashInConfRequest[5],cashInConfRequest[6],cashInConfRequest[7],cashInConfRequest[8],cashInConfRequest[9],cashInConfRequest[10],cashInConfRequest[11],cashInConfRequest[12],cashInConfRequest[13],cashInConfRequest[14],cashInConfRequest[15],cashInConfRequest[16],cashInConfRequest[17],cashInConfRequest[18],cashInConfRequest[19],cashInConfRequest[20],cashInConfRequest[21],cashInConfRequest[22],cashInConfRequest[23],cashInConfRequest[24],cashInConfRequest[25]});

							if(Arrays.asList(userName).indexOf(cashInConfRequest[1]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashInConfRequest,2))
								{		
									if(checkBlank(cashInConfRequest))	/*Check if any empty XML Tag Value*/
									{
										decimal2[0]=cashInConfRequest[5];
										decimal2[1]=cashInConfRequest[14];
										decimal2[2]=cashInConfRequest[18];
										decimal2[3]=cashInConfRequest[6];
										if(checkDecimal(decimal2))	/*Check if Destination Amount, feeAmount, senderPhone, recipientPhone value is decimal*/
										{
											traxId = cashInConfRequest[23];

											http.sendHTTPPOST(httpsurl, inputXML,new Integer(httpstimeout),"urn:routeDx#route");	/*Send XML to Bank*/

											if(http.getFail()==0)	
											{	
												check=1;
												response = http.getPOSTResponse();	/*Extract Response from Bank*/
												response = response.replaceFirst("<feeAmount xsi:type='xsd:string'>.*</feeAmount>", "<feeAmount xsi:type='xsd:string'>"+cashInConfRequest[6]+"</feeAmount>");
												cashInConfResponseBank=xmlRemittanceBank.cashInConfResponse(response);
												cashInConfResponse[5]=cashInConfResponseBank[2];
												cashInConfResponse[6]=cashInConfResponseBank[3];	       	 																				
												//Edit Fee di dlm XML
											}	
											else
											{	
												cashInConfResponse[5]="7000";
												cashInConfResponse[6]="System Maintenance";	       	 							
											}	
										}
										else
										{
											cashInConfResponse[5]="7050";
											cashInConfResponse[6]="Invalid amount";	       	 						
										}
									}
									else
									{
										cashInConfResponse[5]="7012";
										cashInConfResponse[6]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashInConfResponse[5]="7127";
									cashInConfResponse[6]="Invalid signature";	       	 						       	 					
								}

							}
							else
							{
								cashInConfResponse[5]="7020";
								cashInConfResponse[6]="Invalid account";	       	 				
							}

							if(check==0)
							{
								/*cashInConfRequest[0]=Description
								 *cashInConfRequest[1]=UserName 
								 *cashInConfRequest[2]=Signature
								 *cashInConfRequest[3]=ProductCode
								 *cashInConfRequest[4]=Destination Bank Account
								 *cashInConfRequest[5]=Destination Amount
								 *cashInConfRequest[6]=Fee Amount
								 *cashInConfRequest[7]=Transaction Type
								 *cashInConfRequest[8]=Terminal
								 *cashInConfRequest[9]=Source ID
								 *cashInConfRequest[10]=Source Name
								 *cashInConfRequest[11]=Sender Name
								 *cashInConfRequest[12]=Sender Address
								 *cashInConfRequest[13]=Sender ID
								 *cashInConfRequest[14]=Sender Phone
								 *cashInConfRequest[15]=Sender City
								 *cashInConfRequest[16]=Sender Country
								 *cashInConfRequest[17]=Recipient Name
								 *cashInConfRequest[18]=Recipient Phone
								 *cashInConfRequest[19]=Recipient Address
								 *cashInConfRequest[20]=Recipient City
								 *cashInConfRequest[21]=Recipient Country
								 *cashInConfRequest[22]=NotiDesc
								 *cashInConfRequest[23]=Transaction ID
								 *cashInConfRequest[24]=Reference Code
								 *cashInConfRequest[25]=Recipient ID
								 */

								cashInConfResponse[0]=cashInConfRequest[0];
								cashInConfResponse[1]=cashInConfRequest[1];
								cashInConfResponse[2]=cashInConfRequest[2];
								cashInConfResponse[3]=sysCode;
								cashInConfResponse[4]=cashInConfRequest[24];
								cashInConfResponse[7]=cashInConfRequest[3];
								cashInConfResponse[8]=cashInConfRequest[4];
								cashInConfResponse[9]=cashInConfRequest[5];
								cashInConfResponse[10]=cashInConfRequest[6];
								cashInConfResponse[11]=cashInConfRequest[9];
								cashInConfResponse[12]=cashInConfRequest[10];
								cashInConfResponse[13]=cashInConfRequest[8];
								cashInConfResponse[14]=cashInConfRequest[11];
								cashInConfResponse[15]=cashInConfRequest[12];
								cashInConfResponse[16]=cashInConfRequest[13];
								cashInConfResponse[17]=cashInConfRequest[14];
								cashInConfResponse[18]=cashInConfRequest[15];
								cashInConfResponse[19]=cashInConfRequest[16];
								cashInConfResponse[20]=cashInConfRequest[17];
								cashInConfResponse[21]=cashInConfRequest[18];
								cashInConfResponse[22]=cashInConfRequest[19];
								cashInConfResponse[23]=cashInConfRequest[20];
								cashInConfResponse[24]=cashInConfRequest[21];
								cashInConfResponse[25]=cashInConfRequest[22];
								cashInConfResponse[26]=cashInConfRequest[7];
								cashInConfResponse[27]=cashInConfRequest[23];	       	 				       	 			
								cashInConfResponse[28]=cashInConfRequest[25];	       	 				       	 			

								response=xmlRemittance.cashInConfResponse(cashInConfResponse);	//Create cash In Confirmation XML Response
							}
							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashInConfResponse[5]+"', resultDesc='"+cashInConfResponse[6]+"' where traxId=? and transactionType='12' and resultCode is NULL", new Object[]{cashInConfRequest[23].toString()});

						}
						else if(request[4].compareTo("13")==0)	/*Cash In Check Status XML Bank*/
						{
							cashInCheckRequest = xmlRemittance.cashInCheckRequest(inputXML);	//Extract cash In Check Status Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,transactionType,terminal,sourceID,traxId,refCode) values (?,?,?,?,?,?,?,?,?)",new Object[]{cashInCheckRequest[0],cashInCheckRequest[1],cashInCheckRequest[2],cashInCheckRequest[3],cashInCheckRequest[4],cashInCheckRequest[5],cashInCheckRequest[6],cashInCheckRequest[7],cashInCheckRequest[8]});

							if(Arrays.asList(userName).indexOf(cashInCheckRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashInCheckRequest,1))
								{	
									if(checkBlank(cashInCheckRequest))	/*Check if any empty XML Tag Value*/
									{
										if(new Integer(cashInCheckRequest[3]).intValue()>0)	/*Check if Destination Amount > 0*/
										{	
											traxId = cashInCheckRequest[7];

											http.sendHTTPPOST(httpsurl, inputXML,new Integer(httpstimeout),"urn:routeDx#route");	/*Send XML to Bank*/

											if(http.getFail()==0)	
											{	
												check=1;
												response=http.getPOSTResponse();	/*Extract Response from Bank*/
												cashInCheckResponseBank=xmlRemittanceBank.cashInCheckResponse(http.getPOSTResponse());
												cashInCheckResponse[2]=cashInCheckResponseBank[2];
												cashInCheckResponse[3]=cashInCheckResponseBank[3];	       	 							
											}	
											else
											{	
												cashInCheckResponse[2]="7000";
												cashInCheckResponse[3]="System Maintenance";	       	 							
											}	
										}
										else
										{
											cashInCheckResponse[2]="7050";
											cashInCheckResponse[3]="Invalid amount";	       	 							       	 						
										}
									}
									else
									{
										cashInCheckResponse[2]="7012";
										cashInCheckResponse[3]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashInCheckResponse[2]="7127";
									cashInCheckResponse[3]="Invalid signature";	       	 						       	 					
								}
							}
							else
							{
								cashInCheckResponse[2]="7020";
								cashInCheckResponse[3]="Invalid account";	       	 				
							}

							if(check==0)
							{
								/*cashInCheckRequest[0]=UserName 
								 *cashInCheckRequest[1]=Signature
								 *cashInCheckRequest[2]=ProductCode
								 *cashInCheckRequest[3]=Destination Amount
								 *cashInCheckRequest[4]=Transaction Type
								 *cashInCheckRequest[5]=Terminal
								 *cashInCheckRequest[6]=Source ID
								 *cashInCheckRequest[7]=Transaction ID
								 *cashInCheckRequest[8]=Reference Code
								 */

								cashInCheckResponse[0]=sysCode;
								cashInCheckResponse[1]=cashInCheckRequest[8];
								cashInCheckResponse[4]=cashInCheckRequest[2];
								cashInCheckResponse[5]=cashInCheckRequest[3];
								cashInCheckResponse[6]=cashInCheckRequest[6];
								cashInCheckResponse[7]=cashInCheckRequest[4];
								cashInCheckResponse[8]=cashInCheckRequest[7];	       	 				       	 				       	 		

								response=xmlRemittance.cashInCheckResponse(cashInCheckResponse);	//Create cash In Check Status XML Response
							}
							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashInCheckResponse[2]+"', resultDesc='"+cashInCheckResponse[3]+"' where traxId=? and transactionType='13' and resultCode is NULL", new Object[]{cashInCheckRequest[7].toString()});

						}
						else
						{
							response=xmlRemittance.errorResponse("7060","Invalid transaction type",request[2]);	//Create Error Response XML
						}

					}
					else if(getCategory(request[2],"https","https"))	/*XML to SingCash*/
					{
						if(request[4].compareTo("14")==0)	/*Cash Out Inquiry XML SingCash*/
						{

							cashOutRequest = xmlRemittance.cashOutRequest(inputXML);	//Extract cash out Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,transactionType,terminal,sourceID,sourceName,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?)",new Object[]{cashOutRequest[0],cashOutRequest[1],cashOutRequest[2],cashOutRequest[3],cashOutRequest[4],cashOutRequest[5],cashOutRequest[6],cashOutRequest[7],cashOutRequest[8],cashOutRequest[9]});

							if(Arrays.asList(userName).indexOf(cashOutRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutRequest,1))
								{	
									if(checkBlank(cashOutRequest))	/*Check if any empty XML Tag Value*/
									{
										http.sendHTTPPOST(httpsurl+https.inquiry(cashOutRequest[4], "", "", cashOutRequest[8]),"",new Integer(httpstimeout),"");	/*Send Request to Sing Cash*/

										if(http.getFail()==0)	
										{	
											inquiryResponse = https.inquiryResponse(http.getPOSTResponse());	/*Extract Response from Sing Cash*/
											if(inquiryResponse[0].compareTo("00")==0)
											{
												cashOutResponse[4]="00";
												cashOutResponse[5]="Approve";	       	 								       			       	 						       			       	 					
												cashOutResponse[7]=inquiryResponse[3];
												cashOutResponse[12]=inquiryResponse[15];
												cashOutResponse[18]=inquiryResponse[4];
												cashOutResponse[19]=inquiryResponse[14];
												cashOutResponse[20]=inquiryResponse[6];
												cashOutResponse[21]=inquiryResponse[7];
												cashOutResponse[22]=inquiryResponse[9];
											}
											else
											{
												cashOutResponse[4]="7000";
												cashOutResponse[5]="System Maintenance";	       	 								       			       	 					
											}
										}	
										else
										{	
											cashOutResponse[4]="7000";
											cashOutResponse[5]="System Maintenance";	       	 							
										}		
									}
									else
									{
										cashOutResponse[4]="7012";
										cashOutResponse[5]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutResponse[4]="7127";
									cashOutResponse[5]="Invalid signature";	       	 					

								}

							}
							else
							{
								cashOutResponse[4]="7020";
								cashOutResponse[5]="Invalid account";	       	 				
							}

							/*cashOutRequest[0]=UserName 
							 *cashOutRequest[1]=Signature
							 *cashOutRequest[2]=ProductCode
							 *cashOutRequest[3]=Transaction Type
							 *cashOutRequest[4]=Terminal
							 *cashOutRequest[5]=Source ID
							 *cashOutRequest[6]=Source Name
							 *cashOutRequest[7]=Transaction ID
							 *cashOutRequest[8]=Reference Code
							 *cashOutRequest[9]=Recipient ID
							 */

							cashOutResponse[0]=cashOutRequest[0];
							cashOutResponse[1]=cashOutRequest[1];
							cashOutResponse[2]=sysCode;
							cashOutResponse[3]=cashOutRequest[8];
							cashOutResponse[6]=cashOutRequest[2];
							cashOutResponse[9]=cashOutRequest[5];
							cashOutResponse[10]=cashOutRequest[6];
							cashOutResponse[11]=cashOutRequest[4];
							cashOutResponse[23]=cashOutRequest[3];
							cashOutResponse[24]=cashOutRequest[7];
							cashOutResponse[25]=cashOutRequest[9];

							response=xmlRemittance.cashOutResponse(cashOutResponse);	//Create cash Out XML Response

							//Update Response to Transaction Table							
							connDb.updateQuery("update transaction set destAmount='"+cashOutResponse[7]+"',feeAmount='"+cashOutResponse[8]+"',senderName='"+cashOutResponse[12]+"',senderAddress='"+cashOutResponse[13]+"',senderID='"+cashOutResponse[14]+"',senderPhone='"+cashOutResponse[15]+"',senderCity='"+cashOutResponse[16]+"',senderCountry='"+cashOutResponse[17]+"',recipientName='"+cashOutResponse[18]+"',recipientPhone='"+cashOutResponse[19]+"',recipientAddress='"+cashOutResponse[20]+"',recipientCity='"+cashOutResponse[21]+"',recipientCountry='"+cashOutResponse[22]+"',resultCode='"+cashOutResponse[4]+"', resultDesc='"+cashOutResponse[5]+"' where traxId=? and transactionType='15' and resultCode is NULL", new Object[]{cashOutRequest[7].toString()});

						}
						else if(request[4].compareTo("15")==0)	/*Cash Out Confirmation XML SingCash*/
						{

							cashOutConfRequest = xmlRemittance.cashOutConfRequest(inputXML);	//Extract cash out Confirmation Request

							cashOutConfRequest[4]=getFee(cashOutConfRequest[3],cashOutConfRequest[0]);												

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,feeAmount,transactionType,terminal,sourceID,sourceName,senderName,senderAddress,senderId,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{cashOutConfRequest[0],cashOutConfRequest[1],cashOutConfRequest[2],cashOutConfRequest[3],cashOutConfRequest[4],cashOutConfRequest[5],cashOutConfRequest[6],cashOutConfRequest[7],cashOutConfRequest[8],cashOutConfRequest[9],cashOutConfRequest[10],cashOutConfRequest[11],cashOutConfRequest[12],cashOutConfRequest[13],cashOutConfRequest[14],cashOutConfRequest[15],cashOutConfRequest[16],cashOutConfRequest[17],cashOutConfRequest[18],cashOutConfRequest[19],cashOutConfRequest[20],cashOutConfRequest[21],cashOutConfRequest[22]});

							if(Arrays.asList(userName).indexOf(cashOutConfRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutConfRequest,1))
								{	
									if(checkBlank(cashOutConfRequest))	/*Check if any empty XML Tag Value*/
									{
										decimal2[0]=cashOutConfRequest[3];
										decimal2[1]=cashOutConfRequest[12];
										decimal2[2]=cashOutConfRequest[16];
										decimal2[3]=cashOutConfRequest[4];
										if(checkDecimal(decimal2))	/*Check if Destination Amount, feeAmount, senderPhone, recipientPhone value is decimal*/
										{
											if(eva.getID(dest1Acc,cashOutConfRequest[12], "Cashout Remittance", cashOutConfRequest[3], "CASHOUT", cashOutConfRequest[20], "notiDesc", cashOutConfRequest[4])==0)	/*Send eVa Request*/
											{	
												http.sendHTTPPOST(httpsurl+https.submission(cashOutConfRequest[6], cashOutConfRequest[13], cashOutConfRequest[12], cashOutConfRequest[21], cashOutConfRequest[3]),"",new Integer(httpstimeout),"");	/*Send Request to Sing Cash*/

												if(http.getFail()==0)	
												{	
													submissionResponse = https.submissionResponse(http.getPOSTResponse());	/*Extract Response from Sing Cash*/
													if(submissionResponse[0].compareTo("00")==0)
													{
														cashOutConfResponse[4]="00";
														cashOutConfResponse[5]="Approve";	       	 							
													}
													else
													{
														cashOutConfResponse[4]="7000";
														cashOutConfResponse[5]="System Maintenance";	       	 							
													}
												}	
												else
												{	
													cashOutConfResponse[4]="7000";
													cashOutConfResponse[5]="System Maintenance";	       	 							
												}		
											}	
										}
										else
										{
											cashOutConfResponse[4]="7050";
											cashOutConfResponse[5]="Invalid amount";	       	 						
										}
									}
									else
									{
										cashOutConfResponse[4]="7012";
										cashOutConfResponse[5]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutConfResponse[4]="7127";
									cashOutConfResponse[5]="Invalid signature";	       	 						       	 					
								}
							}
							else
							{
								cashOutConfResponse[4]="7020";
								cashOutConfResponse[5]="Invalid account";	       	 				
							}

							/*cashOutConfRequest[0]=UserName 
							 *cashOutConfRequest[1]=Signature
							 *cashOutConfRequest[2]=ProductCode
							 *cashOutConfRequest[3]=Destination Amount
							 *cashOutConfRequest[4]=Fee Amount
							 *cashOutConfRequest[5]=Transaction Type
							 *cashOutConfRequest[6]=Terminal
							 *cashOutConfRequest[7]=Source ID
							 *cashOutConfRequest[8]=Source Name
							 *cashOutConfRequest[9]=Sender Name
							 *cashOutConfRequest[10]=Sender Address
							 *cashOutConfRequest[11]=Sender ID
							 *cashOutConfRequest[12]=Sender Phone
							 *cashOutConfRequest[13]=Sender City
							 *cashOutConfRequest[14]=Sender Country
							 *cashOutConfRequest[15]=Recipient Name
							 *cashOutConfRequest[16]=Recipient Phone
							 *cashOutConfRequest[17]=Recipient Address
							 *cashOutConfRequest[18]=Recipient City
							 *cashOutConfRequest[19]=Recipient Country
							 *cashOutConfRequest[20]=Transaction ID
							 *cashOutConfRequest[21]=Reference Code
							 *cashOutConfRequest[22]=Recipient ID
							 */

							cashOutConfResponse[0]=cashOutConfRequest[0];
							cashOutConfResponse[1]=cashOutConfRequest[1];
							cashOutConfResponse[2]=sysCode;
							cashOutConfResponse[3]=cashOutConfRequest[21];
							cashOutConfResponse[6]=cashOutConfRequest[2];
							cashOutConfResponse[7]=cashOutConfRequest[3];
							cashOutConfResponse[8]=cashOutConfRequest[4];
							cashOutConfResponse[9]=cashOutConfRequest[7];
							cashOutConfResponse[10]=cashOutConfRequest[8];
							cashOutConfResponse[11]=cashOutConfRequest[6];
							cashOutConfResponse[12]=cashOutConfRequest[9];
							cashOutConfResponse[13]=cashOutConfRequest[10];
							cashOutConfResponse[14]=cashOutConfRequest[11];
							cashOutConfResponse[15]=cashOutConfRequest[12];
							cashOutConfResponse[16]=cashOutConfRequest[13];
							cashOutConfResponse[17]=cashOutConfRequest[14];
							cashOutConfResponse[18]=cashOutConfRequest[15];
							cashOutConfResponse[19]=cashOutConfRequest[16];
							cashOutConfResponse[20]=cashOutConfRequest[17];
							cashOutConfResponse[21]=cashOutConfRequest[18];
							cashOutConfResponse[22]=cashOutConfRequest[19];
							cashOutConfResponse[23]=cashOutConfRequest[5];
							cashOutConfResponse[24]=cashOutConfRequest[20];
							cashOutConfResponse[25]=cashOutConfRequest[22];

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashOutConfResponse[4]+"', resultDesc='"+cashOutConfResponse[5]+"' where traxId=? and transactionType='15' and resultCode is NULL", new Object[]{cashOutConfRequest[20].toString()});

							response=xmlRemittance.cashOutConfResponse(cashOutConfResponse);	//Create cash Out Confirmation XML Response


						}
						else if(request[4].compareTo("16")==0)	/*Cash Out Check Status XML SingCash*/
						{

							cashOutCheckRequest = xmlRemittance.cashOutCheckRequest(inputXML);	//Extraxt cash out Check Status Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,transactionType,terminal,sourceID,traxId,refCode) values (?,?,?,?,?,?,?,?,?)",new Object[]{cashOutCheckRequest[0],cashOutCheckRequest[1],cashOutCheckRequest[2],cashOutCheckRequest[3],cashOutCheckRequest[4],cashOutCheckRequest[5],cashOutCheckRequest[6],cashOutCheckRequest[7],cashOutCheckRequest[8]});

							if(Arrays.asList(userName).indexOf(cashOutCheckRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutCheckRequest,1))
								{	
									if(checkBlank(cashOutCheckRequest))	/*Check if any empty XML Tag Value*/
									{
										if(new Integer(cashOutCheckRequest[3]).intValue()>0)	/*Check if Destination Amount > 0*/
										{	
											http.sendHTTPPOST(httpsurl+https.checkstatus(cashOutCheckRequest[5], "", "", cashOutCheckRequest[8]),"",new Integer(httpstimeout),"");	/*Send Request to Sing Cash*/

											if(http.getFail()==0)	
											{	
												checkstatusResponse = https.checkstatusResponse(http.getPOSTResponse());	/*Extract Response from Sing Cash*/
												if(checkstatusResponse[0].compareTo("00")==0)
												{
													cashOutCheckResponse[2]="7133";
													cashOutCheckResponse[3]="Transaction already confirm";	       	 							
												}
												else
												{
													cashOutCheckResponse[2]="7134";
													cashOutCheckResponse[3]="Transaction has not confirm";	       	 							
												}
											}	
											else
											{	
												cashOutCheckResponse[2]="7000";
												cashOutCheckResponse[3]="System Maintenance";	       	 							
											}		
										}
										else
										{
											cashOutCheckResponse[2]="7050";
											cashOutCheckResponse[3]="Invalid amount";	       	 							       	 						
										}
									}
									else
									{
										cashOutCheckResponse[2]="7012";
										cashOutCheckResponse[3]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutCheckResponse[2]="7127";
									cashOutCheckResponse[3]="Invalid signature";	       	 						       	 					
								}
							}
							else
							{
								cashOutCheckResponse[2]="7020";
								cashOutCheckResponse[3]="Invalid account";	       	 				
							}

							/*cashOutCheckRequest[0]=UserName 
							 *cashOutCheckRequest[1]=Signature
							 *cashOutCheckRequest[2]=ProductCode
							 *cashOutCheckRequest[3]=Destination Amount
							 *cashOutCheckRequest[4]=Transaction Type
							 *cashOutCheckRequest[5]=Terminal
							 *cashOutCheckRequest[6]=Source ID
							 *cashOutCheckRequest[7]=Transaction ID
							 *cashOutCheckRequest[8]=Reference Code
							 */

							cashOutCheckResponse[0]=sysCode;
							cashOutCheckResponse[1]=cashOutCheckRequest[8];
							cashOutCheckResponse[4]=cashOutCheckRequest[2];
							cashOutCheckResponse[5]=cashOutCheckRequest[3];
							cashOutCheckResponse[6]=cashOutCheckRequest[6];
							cashOutCheckResponse[7]=cashOutCheckRequest[4];
							cashOutCheckResponse[8]=cashOutCheckRequest[7];	       	 				       	 				       	 		

							response=xmlRemittance.cashOutCheckResponse(cashOutCheckResponse);	//Create cash Out Check Status XML Response

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashOutCheckResponse[2]+"', resultDesc='"+cashOutCheckResponse[3]+"' where traxId=? and transactionType='16' and resultCode is NULL", new Object[]{cashOutCheckRequest[7].toString()});


						}
						else
						{
							response=xmlRemittance.errorResponse("7060","Invalid transaction type",request[2]);	//Create Error Response
						}
					}	
					else if(getCategory(request[2],"hongleong","iso"))	/*XML to Hangleong*/
					{
						if(request[4].compareTo("14")==0)	/*Cash Out Inquiry XML Hangleong*/
						{
							cashOutRequest = xmlRemittance.cashOutRequest(inputXML);	//Extract cash out Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,transactionType,terminal,sourceID,sourceName,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?)",new Object[]{cashOutRequest[0],cashOutRequest[1],cashOutRequest[2],cashOutRequest[3],cashOutRequest[4],cashOutRequest[5],cashOutRequest[6],cashOutRequest[7],cashOutRequest[8],cashOutRequest[9]});

							if(Arrays.asList(userName).indexOf(cashOutRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutRequest,1))
								{	
									if(checkBlank(cashOutRequest))	/*Check if any empty XML Tag Value*/
									{
										bit3 = "380099";
										bit4 = "000000000000";
										bit41 = cashOutRequest[4];
										bit42 = cashOutRequest[2];
										bit43 = "City";
										bit61 = String.format("%-25s",cashOutRequest[9])+String.format("%-16s",cashOutRequest[8])+String.format("%-12s","Referensi");
										bit103 = "002014";
										inputHangleon=buildISORequestMessage();
										responseHangleon=isoclient.sendISOPacket(httpsurl, httpstimeout, inputHangleon, 671);	/*Send Hangleong Request*/

										responseHangleon=responseHangleon.substring(2);
										extractMainResponse(mainTAGres,responseHangleon);	/*Extract Hangleong Response*/
										cashOutISOResponse = extract(cashOutISOResponseTAG,bit61);

										cashOutResponse[7]=cashOutISOResponse[2];
										cashOutResponse[8]=getFee(cashOutISOResponse[2],cashOutRequest[0]);
										cashOutResponse[12]=cashOutISOResponse[5];
										cashOutResponse[13]=cashOutISOResponse[7];
										cashOutResponse[14]=cashOutISOResponse[12];
										cashOutResponse[15]=cashOutISOResponse[15];
										cashOutResponse[16]=cashOutISOResponse[8];
										cashOutResponse[17]=cashOutISOResponse[10];
										cashOutResponse[18]=cashOutISOResponse[16];
										cashOutResponse[19]=cashOutISOResponse[26];
										cashOutResponse[20]=cashOutISOResponse[18];
										cashOutResponse[21]=cashOutISOResponse[19];
										cashOutResponse[22]=cashOutISOResponse[21];
										//Send ISO
										if(bit39.compareTo("00")==0)
										{
											cashOutConfResponse[4]="00";
											cashOutConfResponse[5]="Approve";	       	 								       	 									       	 																					
										}
									}
									else
									{
										cashOutResponse[4]="7012";
										cashOutResponse[5]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutResponse[4]="7127";
									cashOutResponse[5]="Invalid suignature";	       	 						       	 					
								}

							}
							else
							{
								cashOutResponse[4]="7020";
								cashOutResponse[5]="Invalid account";	       	 				
							}

							/*cashOutRequest[0]=UserName 
							 *cashOutRequest[1]=Signature
							 *cashOutRequest[2]=ProductCode
							 *cashOutRequest[3]=Transaction Type
							 *cashOutRequest[4]=Terminal
							 *cashOutRequest[5]=Source ID
							 *cashOutRequest[6]=Source Name
							 *cashOutRequest[7]=Transaction ID
							 *cashOutRequest[8]=Reference Code
							 *cashOutRequest[9]=Recipient ID
							 */

							cashOutResponse[0]=cashOutRequest[0];
							cashOutResponse[1]=cashOutRequest[1];
							cashOutResponse[2]=sysCode;
							cashOutResponse[3]=cashOutRequest[8];
							cashOutResponse[6]=cashOutRequest[2];
							cashOutResponse[9]=cashOutRequest[5];
							cashOutResponse[10]=cashOutRequest[6];
							cashOutResponse[11]=cashOutRequest[4];
							cashOutResponse[23]=cashOutRequest[3];
							cashOutResponse[24]=cashOutRequest[7];
							cashOutResponse[25]=cashOutRequest[9];


							response=xmlRemittance.cashOutResponse(cashOutResponse);	//Create cash Out XML Response

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set destAmount='"+cashOutResponse[7]+"',feeAmount='"+cashOutResponse[8]+"',senderName='"+cashOutResponse[12]+"',senderAddress='"+cashOutResponse[13]+"',senderID='"+cashOutResponse[14]+"',senderPhone='"+cashOutResponse[15]+"',senderCity='"+cashOutResponse[16]+"',senderCountry='"+cashOutResponse[17]+"',recipientName='"+cashOutResponse[18]+"',recipientPhone='"+cashOutResponse[19]+"',recipientAddress='"+cashOutResponse[20]+"',recipientCity='"+cashOutResponse[21]+"',recipientCountry='"+cashOutResponse[22]+"', resultCode='"+cashOutResponse[4]+"', resultDesc='"+cashOutResponse[5]+"' where traxId=? and transactionType='15' and resultCode is NULL", new Object[]{cashOutRequest[7].toString()});

						}
						else if(request[4].compareTo("15")==0)	/*Cash Out Confirmation XML Hangleong*/
						{

							cashOutConfRequest = xmlRemittance.cashOutConfRequest(inputXML);	//Extract cash out Confirmation Request

							cashOutConfRequest[4]=getFee(cashOutConfRequest[3],cashOutConfRequest[0]);												

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,feeAmount,transactionType,terminal,sourceID,sourceName,senderName,senderAddress,senderId,senderPhone,senderCity,senderCountry,recipientName,recipientPhone,recipientAddress,recipientCity,recipientCountry,traxId,refCode,recipientID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{cashOutConfRequest[0],cashOutConfRequest[1],cashOutConfRequest[2],cashOutConfRequest[3],cashOutConfRequest[4],cashOutConfRequest[5],cashOutConfRequest[6],cashOutConfRequest[7],cashOutConfRequest[8],cashOutConfRequest[9],cashOutConfRequest[10],cashOutConfRequest[11],cashOutConfRequest[12],cashOutConfRequest[13],cashOutConfRequest[14],cashOutConfRequest[15],cashOutConfRequest[16],cashOutConfRequest[17],cashOutConfRequest[18],cashOutConfRequest[19],cashOutConfRequest[20],cashOutConfRequest[21],cashOutConfRequest[22]});

							if(Arrays.asList(userName).indexOf(cashOutConfRequest[0]+pin)>=0)	/*Check userName & PIN*/
							{       	 					
								if(!checkSignature(cashOutConfRequest,1))
								{	
									if(checkBlank(cashOutConfRequest))	/*Check if any empty XML Tag Value*/
									{
										decimal2[0]=cashOutConfRequest[3];
										decimal2[1]=cashOutConfRequest[12];
										decimal2[2]=cashOutConfRequest[16];
										decimal2[3]=cashOutConfRequest[4];
										if(checkDecimal(decimal2))
										{
											if(eva.getID(dest1Acc,cashOutConfRequest[12], "Cashout Remittance", cashOutConfRequest[3], "CASHOUT", cashOutConfRequest[20], "notiDesc", cashOutConfRequest[4])==0)	/*Send eVa Request*/
											{	
												bit3 = "500099";
												bit4 = String.format("%012d",new Integer(cashOutConfRequest[3]).intValue());
												bit41 = cashOutConfRequest[6];
												bit42 = cashOutConfRequest[2];
												bit43 = cashOutConfRequest[13];
												bit61 = String.format("%-25s",cashOutConfRequest[22])+String.format("%-16s",cashOutConfRequest[21])+String.format("%012d",new Integer(cashOutConfRequest[3]).intValue())+String.format("%012d",new Integer(cashOutConfRequest[4]).intValue())+String.format("%-12s","Referensi");
												bit61 = bit61 + String.format("%-30s",cashOutConfRequest[15])+"M"+String.format("%-30s",cashOutConfRequest[17])+String.format("%-20s",cashOutConfRequest[18])+String.format("%-10s","Kode Pos")+String.format("%-16s",cashOutConfRequest[19])+String.format("%-10s","Type")+String.format("%-25s",cashOutConfRequest[22])+String.format("%-20s","Tempat Lahir")+"DDMMYYYY"+String.format("%-15s",cashOutConfRequest[16]); 
												bit103 = "002014";
												inputHangleon=buildISORequestMessage();
												responseHangleon=isoclient.sendISOPacket(httpsurl, httpstimeout, inputHangleon, 671);	/*Send Hangleong Request*/
												//Send ISO
												responseHangleon=responseHangleon.substring(2);
												extractMainResponse(mainTAGres,responseHangleon);	/*Extract Hangleong Response*/
												cashOutConfISOResponse = extract(cashOutConfISOResponseTAG,bit61);
												if(bit39.compareTo("00")==0)
												{
													cashOutConfResponse[4]="00";
													cashOutConfResponse[5]="Approve";	       	 								       	 									       	 																					
												}
											}	
										}
										else
										{
											cashOutConfResponse[4]="7050";
											cashOutConfResponse[5]="Invalid amount";	       	 						
										}
									}
									else
									{
										cashOutConfResponse[4]="7012";
										cashOutConfResponse[5]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutConfResponse[4]="7127";
									cashOutConfResponse[5]="Invalid signature";	       	 						       	 					
								}
							}
							else
							{
								cashOutConfResponse[4]="7020";
								cashOutConfResponse[5]="Invalid account";	       	 				
							}

							/*cashOutConfRequest[0]=UserName 
							 *cashOutConfRequest[1]=Signature
							 *cashOutConfRequest[2]=ProductCode
							 *cashOutConfRequest[3]=Destination Amount
							 *cashOutConfRequest[4]=Fee Amount
							 *cashOutConfRequest[5]=Transaction Type
							 *cashOutConfRequest[6]=Terminal
							 *cashOutConfRequest[7]=Source ID
							 *cashOutConfRequest[8]=Source Name
							 *cashOutConfRequest[9]=Sender Name
							 *cashOutConfRequest[10]=Sender Address
							 *cashOutConfRequest[11]=Sender ID
							 *cashOutConfRequest[12]=Sender Phone
							 *cashOutConfRequest[13]=Sender City
							 *cashOutConfRequest[14]=Sender Country
							 *cashOutConfRequest[15]=Recipient Name
							 *cashOutConfRequest[16]=Recipient Phone
							 *cashOutConfRequest[17]=Recipient Address
							 *cashOutConfRequest[18]=Recipient City
							 *cashOutConfRequest[19]=Recipient Country
							 *cashOutConfRequest[20]=Transaction ID
							 *cashOutConfRequest[21]=Reference Code
							 *cashOutConfRequest[22]=Recipient ID
							 */

							cashOutConfResponse[0]=cashOutConfRequest[0];
							cashOutConfResponse[1]=cashOutConfRequest[1];
							cashOutConfResponse[2]=sysCode;
							cashOutConfResponse[3]=cashOutConfRequest[21];
							cashOutConfResponse[6]=cashOutConfRequest[2];
							cashOutConfResponse[7]=cashOutConfRequest[3];
							cashOutConfResponse[8]=cashOutConfRequest[4];
							cashOutConfResponse[9]=cashOutConfRequest[7];
							cashOutConfResponse[10]=cashOutConfRequest[8];
							cashOutConfResponse[11]=cashOutConfRequest[6];
							cashOutConfResponse[12]=cashOutConfRequest[9];
							cashOutConfResponse[13]=cashOutConfRequest[10];
							cashOutConfResponse[14]=cashOutConfRequest[11];
							cashOutConfResponse[15]=cashOutConfRequest[12];
							cashOutConfResponse[16]=cashOutConfRequest[13];
							cashOutConfResponse[17]=cashOutConfRequest[14];
							cashOutConfResponse[18]=cashOutConfRequest[15];
							cashOutConfResponse[19]=cashOutConfRequest[16];
							cashOutConfResponse[20]=cashOutConfRequest[17];
							cashOutConfResponse[21]=cashOutConfRequest[18];
							cashOutConfResponse[22]=cashOutConfRequest[19];
							cashOutConfResponse[23]=cashOutConfRequest[5];
							cashOutConfResponse[24]=cashOutConfRequest[20];
							cashOutConfResponse[25]=cashOutConfRequest[22];

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashOutConfResponse[4]+"', resultDesc='"+cashOutConfResponse[5]+"' where traxId=? and transactionType='15' and resultCode is NULL", new Object[]{cashOutConfRequest[20].toString()});

							response=xmlRemittance.cashOutConfResponse(cashOutConfResponse);	//Create cash Out Confirmation XML Response


						}
						else if(request[4].compareTo("16")==0)	/*Cash Out Check Status XML Hangleong*/
						{

							cashOutCheckRequest = xmlRemittance.cashOutCheckRequest(inputXML);	//Extract cash out XML Request

							//Insert Request to Transaction Table
							connDb.updateQuery("insert into transaction (userName,signature,productCode,destAmount,transactionType,terminal,sourceID,traxId,refCode) values (?,?,?,?,?,?,?,?,?)",new Object[]{cashOutCheckRequest[0],cashOutCheckRequest[1],cashOutCheckRequest[2],cashOutCheckRequest[3],cashOutCheckRequest[4],cashOutCheckRequest[5],cashOutCheckRequest[6],cashOutCheckRequest[7],cashOutCheckRequest[8]});

							if(Arrays.asList(userName).indexOf(cashOutCheckRequest[0]+pin)>=0)
							{       	 					
								if(!checkSignature(cashOutCheckRequest,1))
								{	
									if(checkBlank(cashOutCheckRequest))	/*Check if any empty XML Tag Value*/
									{
										if(new Integer(cashOutCheckRequest[3]).intValue()>0)
										{	
											bit3 = "500099";
											bit4 = String.format("%012d",new Integer(cashOutCheckRequest[3]).intValue());
											bit41 = cashOutCheckRequest[5];
											bit42 = cashOutCheckRequest[2];
											bit43 = "City";
											bit61 = String.format("%-25s","Identitas")+String.format("%-16s",cashOutCheckRequest[8])+String.format("%012d",new Integer(cashOutCheckRequest[3]).intValue())+String.format("%012d",new Integer("0").intValue())+String.format("%-12s",bit32+bit41);
											bit103 = "002016";
											inputHangleon=buildISORequestMessage();
											responseHangleon=isoclient.sendISOPacket(httpsurl, httpstimeout, inputHangleon, 301);	/*Send Hangleong Request*/
											//Send ISO
											responseHangleon=responseHangleon.substring(2);
											extractMainResponse(mainTAGres,responseHangleon);	/*Extract Hangleong Response*/
											cashOutCheckISOResponse = extract(cashOutCheckISOResponseTAG,bit61);

											if(bit39.compareTo("00")==0)
											{
												cashOutCheckResponse[2]="00";
												cashOutCheckResponse[3]="Approve";	       	 								       	 									       	 																					
											}
										}
										else
										{
											cashOutCheckResponse[2]="7050";
											cashOutCheckResponse[3]="Invalid amount";	       	 							       	 						
										}
									}
									else
									{
										cashOutCheckResponse[2]="7012";
										cashOutCheckResponse[3]="Syntax format wrong";	       	 					
									}
								}
								else
								{
									cashOutCheckResponse[2]="7127";
									cashOutCheckResponse[3]="Invalid signature";	       	 						       	 					
								}
							}
							else
							{
								cashOutCheckResponse[2]="7020";
								cashOutCheckResponse[3]="Invalid account";	       	 				
							}

							/*cashOutCheckRequest[0]=UserName 
							 *cashOutCheckRequest[1]=Signature
							 *cashOutCheckRequest[2]=ProductCode
							 *cashOutCheckRequest[3]=Destination Amount
							 *cashOutCheckRequest[4]=Transaction Type
							 *cashOutCheckRequest[5]=Terminal
							 *cashOutCheckRequest[6]=Source ID
							 *cashOutCheckRequest[7]=Transaction ID
							 *cashOutCheckRequest[8]=Reference Code
							 */

							cashOutCheckResponse[0]=sysCode;
							cashOutCheckResponse[1]=cashOutCheckRequest[8];
							cashOutCheckResponse[4]=cashOutCheckRequest[2];
							cashOutCheckResponse[5]=cashOutCheckRequest[3];
							cashOutCheckResponse[6]=cashOutCheckRequest[6];
							cashOutCheckResponse[7]=cashOutCheckRequest[4];
							cashOutCheckResponse[8]=cashOutCheckRequest[7];	       	 				       	 				       	 		

							response=xmlRemittance.cashOutCheckResponse(cashOutCheckResponse);	//Create cash Out Check Status XML Response

							//Update Response to Transaction Table
							connDb.updateQuery("update transaction set resultCode='"+cashOutCheckResponse[2]+"', resultDesc='"+cashOutCheckResponse[3]+"' where traxId=? and transactionType='16' and resultCode is NULL", new Object[]{cashOutCheckRequest[7].toString()});

						}	
						else
						{
							response=xmlRemittance.errorResponse("7060","Invalid transaction type",request[2]);	//Create Error Response
						}

					}
					else
					{
						response=xmlRemittance.errorResponse("7107","Invalid product code","");	//Create Error Response
					}
				}	
				t.sendResponseHeaders(200, response.length());	//Create HTTP Response 200
				OutputStream os = t.getResponseBody();
				logger.info("[Response] : "+response);
				os.write(response.getBytes());	//Send HTTP Response
				os.close();
			}
			catch(IOException e)
			{
				logger.error(this.getClass().getName()+" "+e.getMessage());
			}
		}

		/*Get Data from WSUSERS Table*/
		public void checkUserName()
		{
			results_inquiry12=connDb.getQuery("SELECT NAMAPENGGUNA||PIN as NAMAPENGGUNA FROM WSUSERS", new Object[]{""}, field_inquiry2, new Object[]{},0);
			
			for(int i=0;i<connDb.getRowCount(0);i++)
			{
				userName[i]=results_inquiry12[i][0].toString();
			}
		}

		public boolean checkBlank(String[] input)
		{
			boolean result = true;

			for(int i=0;i<input.length;i++)
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

		/*Check Reference Code from ISO is available*/
		public boolean checkRefCodeISO(String kodetransfer)
		{
			boolean result = false;

			results_inquiry=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit3='380099' and kodetransfer='"+kodetransfer+"' and bit103='002013'", new Object[]{""}, field_inquiry8, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{
				result=true;
			}	
			
			return result;

		}

		
		/*Check Reference Code from XML is available*/
		public boolean checkRefCode(String refCode)
		{
			boolean result = false;

			results_inquiry=connDb.getQuery("SELECT refCode FROM transaction WHERE transactionType='11' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{
				result=true;
			}	

			return result;

		}

		public boolean checkRefCodeDouble(String refCode, String transactionType)
		{
			boolean result = true;

			results_inquiry7=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='"+transactionType+"' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{
				result=false;
			}	

			if(result==true)
			{
				if(transactionType.compareTo("14")==0)
				{	
					results_inquiry7=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit103='002014' and bit3='380099' and kodetransfer='"+refCode+"'", new Object[]{""}, field_inquiry8, new Object[]{},0);
				}
				else if(transactionType.compareTo("15")==0)
				{
					results_inquiry7=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit103='002014' and bit3='500099' and kodetransfer='"+refCode+"'", new Object[]{""}, field_inquiry8, new Object[]{},0);
				}
				if(connDb.getRowCount(0)>0)
				{
					result=false;
				}	
				
			}
			
			return result;

		}
		
		/*Get Fee Amount*/
		public String getFee(String destAmount, String userName)
		{
			String feeAmount = "";

			results_inquiry8=connDb.getQuery("SELECT fee FROM fee WHERE namapengguna='"+userName+"' and mintrx<="+destAmount+" and maxtrx>="+destAmount, new Object[]{""}, field_inquiry3, new Object[]{},0);
			feeAmount = results_inquiry8[0][0].toString();

			return feeAmount;

		}

		
		/*Check if cash In & cash In Confirmation from ISO are Success*/
		public boolean cashInCheckISO(String kodetransfer)
		{
			boolean result = false;

			results_inquiry6=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='380099' and bit103='002013' and kodetransfer='"+kodetransfer+"'", new Object[]{""}, field_inquiry8, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    			
				results_inquiry7=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='500099' and bit103='002013' and kodetransfer='"+kodetransfer+"'", new Object[]{""}, field_inquiry8, new Object[]{},0);
				if(connDb.getRowCount(0)>0)
				{
					result=true;
				}	
			}	

			return result;

		}

		
		/*Check if cash In & cash In Confirmation are Success*/
		public boolean cashInCheck(String refCode)
		{
			boolean result = false;

			results_inquiry3=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='11' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    			
				results_inquiry4=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='12' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
				if(connDb.getRowCount(0)>0)
				{
					result=true;
				}	
			}    		

			return result;

		}

		/*Check if cash out & cash out Confirmation are Success*/
		public boolean cashOutCheck(String refCode)
		{
			boolean result = false;

			results_inquiry5=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='14' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    			
				results_inquiry6=connDb.getQuery("SELECT refCode FROM transaction WHERE resultCode='00' and transactionType='15' and refCode='"+refCode+"'", new Object[]{""}, field_inquiry, new Object[]{},0);
				if(connDb.getRowCount(0)>0)
				{
					result=true;
				}	
			}	
			
			if(result==false)
			{	
				results_inquiry5=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='380099' and bit103='002014' and kodetransfer='"+refCode+"'", new Object[]{""}, field_inquiry8, new Object[]{},0);
				if(connDb.getRowCount(0)>0)
				{    			
					results_inquiry6=connDb.getQuery("SELECT kodetransfer FROM transactioniso WHERE bit39='00' and bit3='500099' and bit103='002014' and kodetransfer='"+refCode+"'", new Object[]{""}, field_inquiry8, new Object[]{},0);
					if(connDb.getRowCount(0)>0)
					{
						result=true;
					}	
				}	
			}	
			
			return result;

		}

		/*Check Signature XML Request*/
		public boolean checkSignature(String[] input, int index)
		{
			boolean result=false;

			/*String signature = "";

			for(int i=0;i<input.length;i++)
			{
				if(i==index)
				{
					signature=signature+encryptPassword(pin);        			
				}
				if(i!=index)
				{
					signature=signature+input[i];
				}
			}


			signature=getMD5(signature);

			if(input[index].compareTo(signature)==0)	result=true;*/

			return result;
		}

		public boolean getCategory(String productCode, String category, String type)
		{
			boolean result=false;

			results_inquiry10=connDb.getQuery("SELECT ' '||httpsurl as httpsurl, httpstimeout, ' '||dest1Acc as dest1Acc FROM routing WHERE productCode='"+productCode+"' and category='"+category+"' and types='"+type+"'", new Object[]{"","",""}, field_inquiry7, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    		
				httpsurl = results_inquiry10[0][0].toString();
				httpstimeout = results_inquiry10[0][1].toString();
				dest1Acc = results_inquiry10[0][2].toString();
				result = true;
			}        		
			
			return result;
		}

		/*MD5 Encoder*/
		public String getMD5(String input) 
		{
			String hashtext = "";
			try 
			{
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] messageDigest = md.digest(input.getBytes());
				BigInteger number = new BigInteger(1, messageDigest);
				hashtext = number.toString(16);
				// Now we need to zero pad it if you actually want the full 32 chars.
				while (hashtext.length() < 32) 
				{
					hashtext = "0" + hashtext;
				}
			}
			catch (NoSuchAlgorithmException e) 
			{
				logger.error(this.getClass().getName()+" "+e.getMessage());
			}
			return hashtext;
		}

	    public String readFile(String fileName)
	    {
	    	
	    	String content = null;
	   	    File file = new File(fileName); //for ex foo.txt
	    	   
	   	    try 
	   	    {
	   	    	FileReader reader = new FileReader(file);
	    	    char[] chars = new char[(int) file.length()];
	    	    reader.read(chars);
	    	    content = new String(chars);
	    	    reader.close();
	    	} 
	   	    catch (IOException e) 
	    	{
				logger.error(this.getClass().getName()+" "+e.getMessage());    		    		
	    	}
	    	   
	   	    return content;    
		}

	}



}
