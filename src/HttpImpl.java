/**
 * 
 */

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;


/**
 * @author yutama
 *
 */


public class HttpImpl
{
	private Logger logger = Logger.getLogger(HttpImpl.class);
    private String url = "";
    private String data = "";
    private String responsePOST = "";
    private String responseGET = "";
    private int fail = 0;
    private String proxyAddr = "";
    private int proxyPort = 0;
    
    /*public static void main(String[] args)
    {
    	HttpImpl http = new HttpImpl();
    	System.out.println("Xxx");
    	http.sendHTTPPOST("http://192.241.143.187", "data", "files/test.txt", 30000, "");
    }*/
    
    public HttpImpl()
    {
		PropertyConfigurator.configure("conf/log4j.properties");    	
    }
    
    class MyTrustManager implements X509TrustManager 
    {
    	public void checkClientTrusted(X509Certificate[] chain, String authType) 
    	{
    	}

    	public void checkServerTrusted(X509Certificate[] chain, String authType) 
    	{
    	}

    	public X509Certificate[] getAcceptedIssuers() {
    		return new X509Certificate[0];
    	}
   	}

    
    public void setProxy(String proxyAddr, int proxyPort)
    {
    	this.proxyAddr = proxyAddr;
    	this.proxyPort = proxyPort;    	
    }
    
    
    public void sendHTTPSGET(String url, int timeout)
    {
    	this.url=url;
    	responseGET="";
    	this.fail = 0;
    	try
    	{
	    	SSLContext sslctx = SSLContext.getInstance("SSL");
	    	sslctx.init(null, new X509TrustManager[] { new MyTrustManager()
	    	}, null);
	
	    	HttpsURLConnection.setDefaultSSLSocketFactory(sslctx.getSocketFactory());

	    	URL urls = new URL(url);
    		if(proxyAddr.length()>0)
    		{	
    			URL urlProxy = new URL(urls.getProtocol(),proxyAddr,proxyPort,url);
    			urls = urlProxy;
    		}	
	
	    	HttpsURLConnection con = (HttpsURLConnection) urls.openConnection();
	    	con.setReadTimeout(timeout);
	    	con.setConnectTimeout(timeout);
	    	con.setRequestMethod("GET");
            logger.info("HTTPS GET Request ["+url+"] : ["+data+"]");

	    	BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    	String line;
	    	while((line = br.readLine()) != null) 
	    	{
	    		responseGET+=line;
	    	}
	    	br.close();
            logger.info("HTTPS GET Response ["+url+"] : ["+responseGET+"]");
            //writeToFile(response,filename);
    	}
    	catch(IOException e)
    	{
    		fail = 1;
    		System.out.println(e.getMessage());
    	}
    	catch(KeyManagementException e)
    	{
    		fail = 1;
    		System.out.println(e.getMessage());
    	}
    	catch(NoSuchAlgorithmException e)
    	{
    		fail = 1;
    		System.out.println(e.getMessage());
    	}
   	}
    
    public void sendHTTPPOST(String url, String data, int timeout, String header)
    {
    	this.url=url;
    	responsePOST="";
    	this.fail = 0;
    	try
    	{
    		URL urls = new URL(url);
    		if(proxyAddr.length()>0)
    		{	
    			URL urlProxy = new URL(urls.getProtocol(),proxyAddr,proxyPort,url);
    			urls = urlProxy;
    		}	

    		HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
    		conn.setConnectTimeout(timeout);
    		conn.setReadTimeout(timeout);
    		conn.setRequestMethod("POST");
    		conn.setRequestProperty("Content-Type","text/xml;charset=UTF-8");
    		conn.setRequestProperty("SOAPAction", header);
    		conn.setDoOutput(true);
            logger.info("HTTP POST Request ["+url+"] : ["+data+"]");
    		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
    		wr.write(data);
            wr.flush();
            BufferedReader rd = null;
            
            if (conn.getHeaderField("Content-Encoding")!=null && conn.getHeaderField("Content-Encoding").equals("gzip"))
            {
            	rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
            }
            else
            {	
            	rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) 
            {
            	responsePOST+=line;
            }
            wr.close();
            rd.close();
            logger.info("HTTP POST Response ["+url+"] : ["+responsePOST+"]");
//            writeToFile(response,filename);
    	}
    	catch(MalformedURLException e)
    	{
    		fail = 1;
			logger.error(this.getClass().getName()+" "+e.getMessage());    		
    	}
    	catch(SocketTimeoutException e)
    	{
    		fail = 1;
			logger.error(this.getClass().getName()+" "+e.getMessage());    		
    	}
    	catch(ProtocolException e)
    	{
    		fail = 1;
			logger.error(this.getClass().getName()+" "+e.getMessage());    		
    	}
    	catch(IOException e)
    	{
    		fail = 1;
			logger.error(this.getClass().getName()+" "+e.getMessage());    		
    	}
    }
    
    public void sendHTTPGET(String url, int timeout)
    {
    	this.url=url;
    	responseGET="";
    	this.fail = 0;
    	try
    	{
    		URL urls = new URL(url);
    		if(proxyAddr.length()>0)
    		{	
    			URL urlProxy = new URL(urls.getProtocol(),proxyAddr,proxyPort,url);
    			urls = urlProxy;
    		}	

    		HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
    		conn.setRequestMethod("GET");
    		conn.setConnectTimeout(timeout);
    		conn.setReadTimeout(timeout);

    		logger.info("HTTP GET Request ["+url+"]");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) 
            {
            	responseGET+=line;
            }
            rd.close();
            logger.info("HTTP GET Response ["+url+"] : ["+responseGET+"]");
            //writeToFile(response,filename);
    	}
    	catch(MalformedURLException e)
    	{
    		fail = 1;
			logger.error(this.getClass().getName()+" "+e.getMessage());    		    		
    	}    	
    	catch(IOException e)
    	{
    		fail = 1;
			logger.error(this.getClass().getName()+" "+e.getMessage());    		
    	}
    }
    
    public void writeToFile(String input, String filename) throws IOException
    {
    	File f = new File(filename);
    	FileWriter log	= new FileWriter(f);
		log.write(input);
		log.close();
    }
    
    public String getUrl()
    {
    	return this.url;
    }

    public String getData()
    {
    	return this.data;
    }
    
    public String getGETResponse()
    {
    	return this.responseGET;
    }
    
    public String getPOSTResponse()
    {
    	return this.responsePOST;
    }

    public int getFail()
    {
    	return this.fail;
    }
}
