package com.WSClient;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.DTLSSession;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;

import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwt.consumer.JwtContext;
import java.security.Key;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static com.WSClient.Definitions.*;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;
/**
 * Hello world!
 *
 */
public class Consumer 
{

	private static final Logger LOG = Logger.getLogger(Consumer.class.getName());

	//The consumer will first send an authorisation request to the AAA system and if it gets a valid token then sends a consume request to the producer and prints 
	//the output from producer
    public static void main( String[] args )
    {
    	Properties prop = new Properties();
	    InputStream input = null;

	    try {

	        input = new FileInputStream("resources/config.properties");
	        // load a properties file
	        prop.load(input);
	        Consumer = prop.getProperty("CONSUMER");
            Producer = prop.getProperty("PRODUCER");
            ServiceName = prop.getProperty("SERVICE_NAME");
            AUTHORISATION_RESOURCE_URI = new URI(prop.getProperty("AUTHORISATION_RESOURCE_URI"));
            ACCOUNTING_RESOURCE_URI = new URI(prop.getProperty("ACCOUNTING_RESOURCE_URI"));
            PRODUCER_URI = new URI(prop.getProperty("PRODUCER_URI"));
            CONSUMER_PORT = Integer.valueOf(prop.getProperty("CONSUMER_PORT"));
            CONSUMER_ACCOUNTING_CLIENT_PORT = Integer.valueOf(prop.getProperty("CONSUMER_ACCOUNTING_CLIENT_PORT"));
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    System.out.println("----Creating DTLS connection to Authorisation server----");
    	CoapClient AuthorisationClient = new CoapClient(AUTHORISATION_RESOURCE_URI);
		DTLSConnector clientConnector = DtlsWrap_client(TRUST_STORE_LOCATION_CONSUMER, TRUST_STORE_PASSWORD_CONSUMER, KEY_STORE_LOCATION_CONSUMER, KEY_STORE_PASSWORD_CONSUMER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_CONSUMER, CONSUMER_PORT);
		if(clientConnector!= null)
		AuthorisationClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
		else
		{
			LOG.info("unable to set endpoint for DTLS, aborting consumer application");
			return;
		}
    
			Integer timeout = new Integer(10000);
		  	AuthorisationClient.setTimeout(timeout.longValue());
			Map<String, String> jsonargs = new HashMap<String, String>();
			jsonargs.put("Consumer", Consumer);
			jsonargs.put("ServiceName", ServiceName);
			jsonargs.put("Producer", Producer);
			String json = createJSON("AuthRequest", jsonargs);
			LOG.info(json);
			CoapResponse response =  AuthorisationClient.post(json,APPLICATION_JSON);
	      	AuthorisationClient.getEndpoint().stop();
			if(response.getOptions().getContentFormat()== APPLICATION_JSON)
			{
	    	 //   LOG.info("Received from server: " + response.getResponseText());
	    	    String accessResponse = retrieveAccessResponse(response.getResponseText());  	    
	    	    System.out.println("Authorisation granted to producer: "+ Producer + " for service: " + ServiceName + " with accounting ID: " + retrieveAccountingID(response.getResponseText()));
	    	    if(accessResponse == "false")
    	    {
	    	    	System.out.println("Access not granted by authorisation system");
    	    }
    	    else
    	    {
    	    	String token = retrieveToken(response.getResponseText());
    	    	int AccountingID = retrieveAccountingID(response.getResponseText());
    	    	if(AccountingID == -1)
    	    	{
    	    		System.out.println("Accounting ID is missing in AAA server response, cannot start accounting from client side ");
	 	    	}
    	    	else
    	    	{
   				 // request to start accounting
           	    CoapClient AccountingClient = new CoapClient(ACCOUNTING_RESOURCE_URI);
           	    clientConnector = DtlsWrap_client(TRUST_STORE_LOCATION_CONSUMER, TRUST_STORE_PASSWORD_CONSUMER, KEY_STORE_LOCATION_CONSUMER, KEY_STORE_PASSWORD_CONSUMER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_CONSUMER, CONSUMER_ACCOUNTING_CLIENT_PORT);
           	    AccountingClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
           	    jsonargs = new HashMap<String, String>();							        			
           	    jsonargs.put("Consumer", Consumer);
       			jsonargs.put("ServiceName", ServiceName);
       			jsonargs.put("Producer", Producer);
       			jsonargs.put("AccountingID", Integer.toString(AccountingID));
       			jsonargs.put("RequestingEntity", Consumer);
       			json = createJSON("StartAccountingRequest", jsonargs);
        		System.out.println("----Sending start accounting request to AAA server----");
        		System.out.println(json+ "\n\n");
       			response =  AccountingClient.post(json,APPLICATION_JSON);
       			//System.out.println("Received from Accounting server: " + response.getResponseText());
           	    AccountingClient.getEndpoint().stop();
           	    String[] str = response.getResponseText().split(",");
           	    if(str[0].equals("ok"))
           	    {
		    		//create DTLS connector to producer
           	    	System.out.println("creating DTLS connector to producer at uri:"+ PRODUCER_URI + "\n\n");
    	    		CoapClient ProducerClient = new CoapClient(PRODUCER_URI);
    	    		clientConnector = DtlsWrap_client(TRUST_STORE_LOCATION_CONSUMER, TRUST_STORE_PASSWORD_CONSUMER, KEY_STORE_LOCATION_CONSUMER, KEY_STORE_PASSWORD_CONSUMER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_CONSUMER, CONSUMER_PORT);
    	    		if(clientConnector!= null)
    	    			ProducerClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
    	    		else
    	    		{
    	    			LOG.info("unable to set endpoint for DTLS, aborting consumer application");
    	    			return;
    	    		}
    	    		
    	    		// add token information to the accepted tokens array
               	    AcceptedTokens ats = new AcceptedTokens(Consumer, Producer, ServiceName, ProducerClient.getEndpoint().getAddress().getAddress(), ProducerClient.getEndpoint().getAddress().getPort(), AccountingID, token);
    		    		 AcceptedTokensList.add(ats);              	    
    	    		ProducerClient.setTimeout(timeout.longValue());
    	    		
       				//First consume request
    	 
	    	    		jsonargs = new HashMap<String, String>();
	    	    		jsonargs.put("Consumer", Consumer);
	    	    		jsonargs.put("ServiceName", ServiceName);
	    	    		jsonargs.put("Producer", Producer);
	    	    		jsonargs.put("Token", token);
	    	    		json = createJSON("ConsumeRequest", jsonargs);
	    	    		int consumeRequestLength = json.length();
	    	    		
	    	    		AccountingInfo ai = new AccountingInfo();
	               	    ai.setSessionID(AccountingID);
	               	    ai.incrementInRequestCounter();
	       			    	ai.setMinRequestSize(consumeRequestLength);
	       			    	ai.setMaxRequestSize(consumeRequestLength);
	       				AccountingInfoList.add(ai);
	       				System.out.println("------consume request------");
	       				//Look for IP address change
	       				ats = retrieveAcceptedToken(Consumer, Producer, ServiceName);       						
	       				if(ats!= null && ProducerClient.getEndpoint().getAddress().getAddress() != ats.getIPAddress() &&  ProducerClient.getEndpoint().getAddress().getPort() != ats.getPort())
	       					ai.setIPaddressChange(1);
	    	    		response =  ProducerClient.post(json,APPLICATION_JSON);
	    	    		System.out.println("Received from weather station: " + response.getResponseText() + "\n");
	    	    		if(!response.getResponseText().contains("FAIL"))
	    	    			ai.incrementOutResponseCounter();
	    	    		if(response.getResponseText().equals("FAIL: Previous token expired, contact AAA server for new token and accounting closed"))
	    	    		{
	    	    			if(accountingStatus == true)
	    	    				System.out.println("Accounting already stopped" + "\n\n");
	    	    			else
	    	    			{
	        	    		 accountingStatus = stopAccounting(AccountingID, Consumer, TOKEN_EXPIRED);
	        	    		if(accountingStatus == false)
	        	    		System.out.println("Failed to stop Accounting from consumer side" + "\n\n");
	    	    			}
	    	    		}
	    	    		

	    	    		for(int i=0;i<5;i++)
	    	    		{
	    	    			jsonargs = new HashMap<String, String>();
		    	    		jsonargs.put("Consumer", Consumer);
		    	    		jsonargs.put("ServiceName", ServiceName);
		    	    		jsonargs.put("Producer", Producer);
		    				//	jsonargs.put("Token", token);
		    				json = createJSON("ConsumeRequest", jsonargs);
		    				//	LOG.info(json);
			    			ai = retrieveRow(AccountingID);
		    			    ai.incrementInRequestCounter();
		    			    if(ai.getMinRequestSize()> json.length())
		    			    	ai.setMinRequestSize(json.length());
		    			    if(ai.getMaxRequestSize()<json.length())
		    			    	ai.setMaxRequestSize(json.length());
		       				//Look for IP address change
		       				ats = retrieveAcceptedToken(Consumer, Producer, ServiceName);       						
		       				if(ats!= null && ProducerClient.getEndpoint().getAddress().getAddress() != ats.getIPAddress() &&  ProducerClient.getEndpoint().getAddress().getPort() != ats.getPort())
		       					ai.setIPaddressChange(1);
		    				response =  ProducerClient.post(json,APPLICATION_JSON);
		    	    		if(!response.getResponseText().contains("FAIL"))
		    	    			ai.incrementOutResponseCounter();
		    	    		System.out.println("Received from weather station: " + response.getResponseText()+"\n");
		    	    		if(response.getResponseText().equals("FAIL: Previous token expired, contact AAA server for new token and accounting closed"))
		    	    		{
		    	    			if(accountingStatus == true)
		    	    				System.out.println("Accounting already stopped");
		    	    			else
		    	    			{
		    	    			accountingStatus = stopAccounting(AccountingID, Consumer, TOKEN_EXPIRED);
		        	    		if(accountingStatus == false)
		        	    			System.out.println("Failed to stop Accounting from consumer side");
		    	    			}
		    	    		}
	    	    		} 
        
        	    
	    	    		//close request
		    	    	jsonargs = new HashMap<String, String>();
	    	    		jsonargs.put("Consumer", Consumer);
	    	    		jsonargs.put("ServiceName", ServiceName);
	    	    		jsonargs.put("Producer", Producer);
	    	    		json = createJSON("CloseSession", jsonargs);
	    	    		//	LOG.info(json);
	    	    		response =  ProducerClient.post(json,APPLICATION_JSON);
	    	    		System.out.println("Received from weather station: " + response.getResponseText());
		    			if(accountingStatus == true)
		    				System.out.println("Accounting already stopped");
		    			else
		    			{
		    			accountingStatus = stopAccounting(AccountingID, Consumer, CLOSE_SESSION);
	    	    		if(accountingStatus == false)
	    	    		LOG.info("Failed to stop Accounting from consumer side");
		    			}
    	    	}
    	    }   
    	 }
			}
			else
			{
				System.out.println("Received from server: " + response.getResponseText());
			}
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
        String s = br.readLine();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }    
    }
    
    public static AcceptedTokens retrieveAcceptedToken(String Consumer, String Producer, String ServiceName)
    {
    	for(AcceptedTokens ats:AcceptedTokensList)
    		if(ats.getConsumer().equals(Consumer) && ats.getProducer().equals(Producer) && ats.getServiceName().equals(ServiceName))
    			return ats;
    	return null;
    }
    	public static boolean stopAccounting(int SessionID, String Consumer, int TerminationCause)
    	{
       	 	try {

    		 AccountingInfo ai = retrieveRow(SessionID);
    		// request to stop accounting
    	    CoapClient AccountingClient = new CoapClient(ACCOUNTING_RESOURCE_URI);
    	    DTLSConnector clientConnector  = DtlsWrap_client(TRUST_STORE_LOCATION_CONSUMER, TRUST_STORE_PASSWORD_CONSUMER, KEY_STORE_LOCATION_CONSUMER, KEY_STORE_PASSWORD_CONSUMER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_CONSUMER, CONSUMER_ACCOUNTING_CLIENT_PORT);
    	    AccountingClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
    	    Map<String, String> jsonargs = new HashMap<String, String>();				        			
    	    jsonargs.put("SessionID",Integer.toString(SessionID));
    	    jsonargs.put("RequestingEntity", Consumer);
    	    jsonargs.put("InRequestCounter", Integer.toString(ai.getInRequestCounter()));
    	    jsonargs.put("OutResponseCounter", Integer.toString(ai.getOutResponseCounter()));
    	    jsonargs.put("MinRequestSize", Integer.toString(ai.getMaxRequestSize()));
    	    jsonargs.put("MaxRequestSize", Integer.toString(ai.getMaxRequestSize()));
    	    jsonargs.put("TerminationCause", Integer.toString(TerminationCause));
    		String json = createStopRequest("StopAccountingRequest", jsonargs);
    		System.out.println("----Sending stop accounting request to AAA server----");
    		System.out.println(json);
    		CoapResponse response =  AccountingClient.post(json,APPLICATION_JSON);
    		System.out.println("Received from Accounting server: " + response.getResponseText());
    	    AccountingClient.getEndpoint().stop();
    		 deleteAcceptedTokenEntry(SessionID);
    	    return true;
       	 	}
       	 	catch(Exception e)
       	 	{
       	 		e.printStackTrace();
       	 	}
    	    return false;
    	}
    	
    	public static AccountingInfo retrieveRow(int SessionID)
    	{
    		for (AccountingInfo ai : AccountingInfoList) {
    			if(ai.getSessionID() == SessionID)
    			{
    				return ai;
    			}
    		}
    		return null;
    	}
    	
   	 public static String createStopRequest(String requestType, Map<String, String> jsonargs)
	    {
	        JSONObject obj = new JSONObject();
	        for(String key: jsonargs.keySet())
			{
	            obj.put(key, jsonargs.get(key));
			}
	      
	        JSONObject json = new JSONObject();
	        json.put(requestType, obj);
	      return json.toString();
	    }
   	 
     public static void deleteAcceptedTokenEntry(int SessionID)
     {
   	  int index = -1;
   	  for (AcceptedTokens at : Definitions.AcceptedTokensList) {
   	      if (at.getSessionID()== SessionID)
   	      {     
   	          index =  AcceptedTokensList.indexOf(at);
   	          break;
   	        }
     }
   	  if(index > -1)
   	  AcceptedTokensList.remove(index);
     }
     
    //serverResponse is a Json object and retrieve token will parse it and retrieves the string corresponding to the key Token
    public static String retrieveToken(String serverResponse)
    {
		  JSONParser parser = new JSONParser();
		  String token = null;
		  try{
			  JSONObject jsonObject = (JSONObject) parser.parse(serverResponse);
		  
			  JSONObject jo = (JSONObject) jsonObject.get("AuthResponse");
			  Set<String> s = (Set<String>)jo.keySet();
			  token = (String)jo.get("Token");
		  }
		  catch(Exception e)
		  {
			  System.out.println(e);
		  }
		  return token;
			  
    }
    
    //serverResponse is a Json object and retrieveAccountingID will parse it and retrieves the int corresponding to the key AccountingID
    public static int retrieveAccountingID(String serverResponse)
    {
		  JSONParser parser = new JSONParser();
		  int accID = -1;
		  try{
			  JSONObject jsonObject = (JSONObject) parser.parse(serverResponse);
		  
			  JSONObject jo = (JSONObject) jsonObject.get("AuthResponse");
			  Set<String> s = (Set<String>)jo.keySet();
			  accID = Integer.valueOf((String)jo.get("AccountingID"));
		//	  System.out.println("Accounting ID for this session: " + accID + "\n\n");
		  }
		  catch(Exception e)
		  {
			  System.out.println(e);
		  }
		  return accID;
			  
    }
    
    
    //serverResponse is a Json object and retrieveAccessResponse will parse it and retrieves the string corresponding to the key AccessResponse
    public static String retrieveAccessResponse(String serverResponse)
    {
		  JSONParser parser = new JSONParser();
		  String accessResponse = null;
		  try{
			  JSONObject jsonObject = (JSONObject) parser.parse(serverResponse);
		  
			  JSONObject jo = (JSONObject) jsonObject.get("AuthResponse");
			  Set<String> s = (Set<String>)jo.keySet();
			  accessResponse = (String)jo.get("AccessResponse");
		  }
		  catch(Exception e)
		  {
			  System.out.println(e);
		  }
		  return accessResponse;
			  
    }
    
	 public static String createJSON(String requestType, Map<String, String> jsonargs)
    {
        JSONObject obj = new JSONObject();
        for(String key: jsonargs.keySet())
		{
            obj.put(key, jsonargs.get(key));
		}
      
        JSONObject json = new JSONObject();
        json.put(requestType, obj);
      return json.toString();
    }

    public static DTLSConnector DtlsWrap_client(String client_trust_store, String client_trust_store_password,  String client_key_store,  String  client_key_password, String trust_store_alias, String client_alias, int port)
    {
    	DTLSConnector clientConnector = null;
  	  try {
	        KeyStore trustStore = KeyStore.getInstance("JKS");
	        InputStream inTrust = new FileInputStream(client_trust_store);
	        trustStore.load(inTrust, client_trust_store_password.toCharArray());
	        //load certificates
	        Certificate[] trustedCertificates = new Certificate[1];
	    //    trustedCertificates[0] = trustStore.getCertificate(trust_store_alias);
	        trustedCertificates[0] = trustStore.getCertificate(trust_store_alias);
	        LOG.fine(trustedCertificates[0].toString());
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream in = new FileInputStream(client_key_store);
            keyStore.load(in, client_key_password.toCharArray());
	        DtlsConnectorConfig.Builder clientConfig = new DtlsConnectorConfig.Builder();
	        clientConfig.setAddress(new InetSocketAddress(port));
	        clientConfig.setAutoResumptionTimeoutMillis(2000);
	        clientConfig.setIdentity((PrivateKey)keyStore.getKey(client_alias, client_key_password.toCharArray()),
	        		keyStore.getCertificateChain(client_alias), false);
	        clientConfig.setMaxConnections(10);
	        clientConfig.setTrustStore(trustedCertificates);
	 //       clientConfig.setSupportedCipherSuites(new CipherSuite[]{
	   //                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8});
	        clientConnector = new DTLSConnector(clientConfig.build(), null);
	        clientConnector.clearConnectionState();

	        
	        		}
			catch(Exception e)
			{
				e.printStackTrace();
			}
      return clientConnector;

    }
}
