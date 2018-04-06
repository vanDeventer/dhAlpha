package com.aaa_ngac_demo.AAA_NGAC_Consumer;

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
import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;


import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;
import static com.aaa_ngac_demo.AAA_NGAC_Consumer.Definitions.*;
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
    	AnsiConsole.systemInstall();
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
	    //Connect to producer without a token
	    
	    //create DTLS connector to producer
	    System.out.println("-----creating DTLS connector to producer at uri: "+ PRODUCER_URI + "-----\n");
		CoapClient ProducerClient = new CoapClient(PRODUCER_URI);
		DTLSConnector clientConnector = DtlsWrap_client(TRUST_STORE_LOCATION_CONSUMER, TRUST_STORE_PASSWORD_CONSUMER, KEY_STORE_LOCATION_CONSUMER, KEY_STORE_PASSWORD_CONSUMER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_CONSUMER, CONSUMER_PORT);
		if(clientConnector!= null)
			ProducerClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
		else
		{
			LOG.info("unable to set endpoint for DTLS, aborting consumer application");
			return;
		}
		
		//First consume request
		Map<String, String> jsonargs = new HashMap<String, String>();
    		jsonargs = new HashMap<String, String>();
    		jsonargs.put("Consumer", Consumer);
    		jsonargs.put("ServiceName", ServiceName);
    		jsonargs.put("Producer", Producer);
    		String json = createJSON("ConsumeRequest", jsonargs);
    		System.out.println("------First consume request to producer with out token------");
    		System.out.println( JsonWriter.formatJson(json)+ "\n\n");
    		CoapResponse response =  ProducerClient.post(json,APPLICATION_JSON);
    		if(!response.getResponseText().contains("FAIL"))
    		{

			System.out.println(ansi().fg(GREEN).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
    		}
    		else
    		{
				System.out.println(ansi().fg(RED).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
    		}
    		clientConnector.stop();

    		try 
    		{  
    			TimeUnit.SECONDS.sleep(5); // wait till token expires
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		} 
    		
    		
	    System.out.println(ansi().fg(WHITE).a("----Creating DTLS connection to Authorisation server----").reset());
    	CoapClient AuthorisationClient = new CoapClient(AUTHORISATION_RESOURCE_URI);
		clientConnector = DtlsWrap_client(TRUST_STORE_LOCATION_CONSUMER, TRUST_STORE_PASSWORD_CONSUMER, KEY_STORE_LOCATION_CONSUMER, KEY_STORE_PASSWORD_CONSUMER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_CONSUMER, CONSUMER_PORT);
		if(clientConnector!= null)
		AuthorisationClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
		else
		{
			LOG.info("unable to set endpoint for DTLS, aborting consumer application");
			return;
		}
    
			Integer timeout = new Integer(10000);
		  	AuthorisationClient.setTimeout(timeout.longValue());
		//	Map<String, String> jsonargs = new HashMap<String, String>();
			jsonargs.put("Consumer", Consumer);
			jsonargs.put("ServiceName", ServiceName);
			jsonargs.put("Producer", Producer);
			json = createJSON("AuthRequest", jsonargs);
			//LOG.info(json);
		    System.out.println(ansi().fg(WHITE).a("----Sending Authorization Request-----\n").reset());
    		System.out.println( JsonWriter.formatJson(json)+ "\n\n");
    	//	CoapResponse response = null;
    		try
    		{
			response =  AuthorisationClient.post(json,APPLICATION_JSON);
    		

	      	AuthorisationClient.getEndpoint().stop();
			if(response.getOptions().getContentFormat()== APPLICATION_JSON)
			{
	    	 //   LOG.info("Received from server: " + response.getResponseText());
	    	    String accessResponse = retrieveAccessResponse(response.getResponseText());  	    
			    
	    	    if(accessResponse == "false")
    	    {
	    	    	System.out.println(ansi().fg(RED).a("Access not granted by authorisation system").reset());
    	    }
    	    else
    	    {
    	    	System.out.println(ansi().fg(GREEN).a("Access granted to producer: "+ Producer + "\nService: " + ServiceName + " \nAccounting ID: " + retrieveAccountingID(response.getResponseText())+"\n").reset());
    	    	String token = retrieveToken(response.getResponseText());
    	    	int AccountingID = retrieveAccountingID(response.getResponseText());
    	    	if(AccountingID == -1)
    	    	{
    	    		System.out.println(ansi().fg(RED).a("Accounting ID is missing in AAA server response, cannot start accounting from client side ").reset());
	 	    	}
    	    	else
    	    	{
    	    		try 
    	    		{  
    	    			TimeUnit.SECONDS.sleep(5); // wait till token expires
    	    		}
    	    		catch(Exception e)
    	    		{
    	    			e.printStackTrace();
    	    		} 
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
        		System.out.println("----Sending start accounting request to AAA server----\n");
        		System.out.println( JsonWriter.formatJson(json)+ "\n\n");
       			response =  AccountingClient.post(json,APPLICATION_JSON);
       			//System.out.println("Received from Accounting server: " + response.getResponseText());
           	    AccountingClient.getEndpoint().stop();
           	    String[] str = response.getResponseText().split(",");
           	    if(str[0].equals("ok"))
           	    {
		    		//create DTLS connector to producer
           	    	System.out.println("-----creating DTLS connector to producer at uri: "+ PRODUCER_URI + "-----\n");
    	    		ProducerClient = new CoapClient(PRODUCER_URI);
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
    	    		try 
    	    		{  
    	    			TimeUnit.SECONDS.sleep(5); // wait till token expires
    	    		}
    	    		catch(Exception e)
    	    		{
    	    			e.printStackTrace();
    	    		} 
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
	       				System.out.println("------First consume request with token------");
	       				//Look for IP address change
	       				ats = retrieveAcceptedToken(Consumer, Producer, ServiceName);       						
	       				if(ats!= null && ProducerClient.getEndpoint().getAddress().getAddress() != ats.getIPAddress() &&  ProducerClient.getEndpoint().getAddress().getPort() != ats.getPort())
	       					ai.setIPaddressChange(1);
	    	    		System.out.println( JsonWriter.formatJson(json)+ "\n\n");
	    	    		response =  ProducerClient.post(json,APPLICATION_JSON);
	    	    		if(!response.getResponseText().contains("FAIL"))
	    	    		{
	    	    			ai.incrementOutResponseCounter();

	    				System.out.println(ansi().fg(GREEN).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
	    	    		}
	    	    		else
	    	    		{
    	    				System.out.println(ansi().fg(RED).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
	    	    		}
	    	    		if(response.getResponseText().indexOf("Accounting stopped") > 1)
	    	    		{
	    	    			if(accountingStatus == true)
	    	    				System.out.println(ansi().fg(RED).a("Accounting already stopped" + "\n\n").reset());
	    	    			else
	    	    			{
	        	    		 accountingStatus = stopAccounting(AccountingID, Consumer, TOKEN_EXPIRED);
	        	    		if(accountingStatus == false)
	        	    			System.out.println(ansi().fg(RED).a("Failed to stop Accounting from consumer side" + "\n\n").reset());
	    	    			}
	    	    		}
	    	    		
	    	    		try 
	    	    		{  
	    	    			TimeUnit.SECONDS.sleep(5); // wait till token expires
	    	    		}
	    	    		catch(Exception e)
	    	    		{
	    	    			e.printStackTrace();
	    	    		} 
	    	    		
	    	    		
	            	    // second consume request without token but within the token validity period
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
	       				if(ats!= null & ProducerClient.getEndpoint().getAddress().getAddress() != ats.getIPAddress() &&  ProducerClient.getEndpoint().getAddress().getPort() != ats.getPort())
	       					ai.setIPaddressChange(1);
		       			System.out.println("-----Second consume request without token-----");
	    	    		System.out.println( JsonWriter.formatJson(json)+ "\n\n");
	    	    		response =  ProducerClient.post(json,APPLICATION_JSON);
	    	    		if(!response.getResponseText().contains("FAIL"))
	    	    		{
	    	    			ai.incrementOutResponseCounter();

	    				System.out.println(ansi().fg(GREEN).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
	    	    		}
	    	    		else
	    	    		{
    	    				System.out.println(ansi().fg(RED).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
	    	    		}
	    	    		if(response.getResponseText().indexOf("Accounting stopped") > 1)
	    	    		{
	    	    			if(accountingStatus == true)
	    	    				System.out.println(ansi().fg(RED).a("Accounting already stopped" + "\n\n").reset());
	    	    			else
	    	    			{
	        	    		 accountingStatus = stopAccounting(AccountingID, Consumer, TOKEN_EXPIRED);
	        	    		if(accountingStatus == false)
	        	    			System.out.println(ansi().fg(RED).a("Failed to stop Accounting from consumer side" + "\n\n").reset());
	    	    			}
	    	    		}
	    	    		
	    	    		System.out.println(ansi().fg(YELLOW).a("The consumer system is set to wait for a short period to demonstrate the token expiry scenario, this is just for demonstration purpose").reset());
	    	    		//   	    ProducerClient.getEndpoint().stop();
	    	    		clientConnector.stop();
	    	    		try 
	    	    		{  
	    	    			TimeUnit.SECONDS.sleep(50); // wait till token expires
	    	    		}
	    	    		catch(Exception e)
	    	    		{
	    	    			e.printStackTrace();
	    	    		} 
	    	    		
	    	    		// Consume request after sleep of 90 seconds (token expired)
	    	    		
	    	    		try
	    	    		{
    	    			clientConnector.start();
	    	    		}
	    	    		catch(Exception e)
	    	    		{
	    	    			e.printStackTrace();
	    	    		}
	    	    		
	    	    		for(int i=0;i<1;i++)
	    	    		{
		    	    	   	System.out.println(ansi().fg(WHITE).a("-----Consume request after token expirt period-----\n").reset());
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
		    	    		System.out.println( JsonWriter.formatJson(json)+ "\n\n");
		    				response =  ProducerClient.post(json,APPLICATION_JSON);
		    	    		if(!response.getResponseText().contains("FAIL"))
		    	    		{
		    	    			ai.incrementOutResponseCounter();
	
    	    				System.out.println(ansi().fg(GREEN).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
		    	    		}
		    	    		else
		    	    		{
	    	    				System.out.println(ansi().fg(RED).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
		    	    		}
    	    	    		if(response.getResponseText().indexOf("Accounting stopped") > 1)
    	    	    		{
    	    	    			if(accountingStatus == true)
    	    	    				System.out.println(ansi().fg(RED).a("Accounting already stopped" + "\n\n").reset());
    	    	    			else
    	    	    			{
    	        	    		 accountingStatus = stopAccounting(AccountingID, Consumer, TOKEN_EXPIRED);
    	        	    		if(accountingStatus == false)
    	        	    			System.out.println(ansi().fg(RED).a("Failed to stop Accounting from consumer side" + "\n\n").reset());
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
	    	    		if(!response.getResponseText().contains("FAIL"))
	    	    		{
	    	    			ai.incrementOutResponseCounter();

	    				System.out.println(ansi().fg(GREEN).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
	    	    		}
	    	    		else
	    	    		{
    	    				System.out.println(ansi().fg(RED).a("Received from " + Producer +": " + response.getResponseText()+ "\n").reset());
	    	    		}
		    			if(accountingStatus == true)
		    				System.out.println(ansi().fg(RED).a("Accounting already stopped").reset());
		    			else
		    			{
		    			accountingStatus = stopAccounting(AccountingID, Consumer, CLOSE_SESSION);
	    	    		if(accountingStatus == false)
	    	    			System.out.println(ansi().fg(RED).a("Failed to stop Accounting from consumer side").reset());
		    			}
    	    	}
    	    }   
    	 }
			}

			else
			{
				System.out.println(ansi().fg(RED).a("Received from server: " + response.getResponseText()).reset());
			}
    		}
    		catch(Exception E)
    		{
    			System.out.println(ansi().fg(RED).a("Failed to create DTLS session with AAA system. Required certificates are missing in keystore or truststore").reset());
    			return;
    		}
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
        String s = br.readLine();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }    
        AnsiConsole.systemUninstall();
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
    
	 public static JSONObject createJSONObject(String requestType, Map<String, String> jsonargs)
	    {
	        JSONObject obj = new JSONObject();
	        for(String key: jsonargs.keySet())
			{
	            obj.put(key, jsonargs.get(key));
			}
	      
	        JSONObject json = new JSONObject();
	        json.put(requestType, obj);
	      return json;
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
