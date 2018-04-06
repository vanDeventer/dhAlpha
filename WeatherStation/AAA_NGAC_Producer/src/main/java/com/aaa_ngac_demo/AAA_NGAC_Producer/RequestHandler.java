package com.aaa_ngac_demo.AAA_NGAC_Producer;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.APPLICATION_JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.DTLSSession;
import org.eclipse.californium.scandium.dtls.SessionId;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.jwt.JwtClaims;

import static com.aaa_ngac_demo.AAA_NGAC_Producer.Definitions.*;
import static com.aaa_ngac_demo.AAA_NGAC_Producer.Producer.*;

public class RequestHandler {
	private static final Logger LOG = Logger.getLogger(RequestHandler.class.getName());
	public String handle(CoapExchange exchange)
	{
		String Consumer = null;
		String Producer = null;
		String ServiceName = null;
		InetAddress IPAddress = null;
		int Port = 0;
		String token = null;
		int AccountingID = -1;
		JSONParser parser = new JSONParser();
		try {
				JSONObject jsonObject = (JSONObject) parser.parse(exchange.getRequestText());
				if(exchange.getRequestText().indexOf("ConsumeRequest")> -1)
				{
					System.out.println("Received Consume request");
					JSONObject jo = (JSONObject) jsonObject.get("ConsumeRequest"); 
					Set<String> s = (Set<String>)jo.keySet();
					/*Iterator<String> it = s.iterator();
				     do{
				    	 String key = it.next().toString();
				    	 if()
				     }while(it.hasNext()); */
					 Consumer = (String)jo.get("Consumer");
			    	 Producer = (String) jo.get("Producer");
			    	 ServiceName = (String)jo.get("ServiceName");
			    	 IPAddress = exchange.getSourceAddress();
			    	 Port = exchange.getSourcePort();
			    	 DTLSSession dtlsSession = serverConnector.getSessionByAddress(new InetSocketAddress(IPAddress, Port));
			    	 SessionId DtlsSID = dtlsSession.getSessionIdentifier();
			    	 AcceptedTokens accToken = retrieveAccessToken(Consumer, Producer, ServiceName);
			    	 if(accToken!= null)
			    	 {
				    	 if((accToken.getIPAddress().equals(IPAddress)) && (accToken.getPort() == Port) && !(accToken.getDtlsSID().equals(DtlsSID)))
				    	  {
				    		 System.out.println("----Storing new Dtls Session ID----");
				    		  accToken.setDtlsSID(DtlsSID);
				    		  UpdateDtlsSID(accToken.getSessionID(), DtlsSID);
				    	  }
				    	 else if((accToken.getDtlsSID().equals(DtlsSID)) && (!(accToken.getIPAddress().equals(IPAddress)) || !(accToken.getPort() == Port)))
				    	 {
				    		 System.out.println("-----Storing new IP address and port details----");
				    		 accToken.setIPAddress(IPAddress);
				    		 accToken.setPort(Port);
				    		 UpdateAddress(accToken.getSessionID(), IPAddress, Port);
			       				//Look for IP address change
			       				AcceptedTokens ats = retrieveAcceptedToken(Consumer, Producer, ServiceName);       						
			       				AccountingInfo ai = retrieveRow(ats.getSessionID());
			       					ai.setIPaddressChange(1);
				    	 }
				    	 else if (!(accToken.getIPAddress().equals(IPAddress)) && !(accToken.getPort() == Port) && !(accToken.getDtlsSID().equals(DtlsSID)))
				    	 {
				    		 System.out.println("-----Both address and session Id changed-----");
				    		 System.out.println("The DTLS session cannot be identified, Request cannot be processed");
				    			//retrieve sessionID
			    			 int SID = retrieveSessionID(Consumer,Producer, ServiceName);
			    			 boolean stopAccountingStatus = false;
			    			 while(!stopAccountingStatus)
			    			 {
			    				 stopAccountingStatus = stopAccounting(SID, Producer, INVALID_SESSION);
			    				 
			    			 }
			    			 return "FAIL: Accounting stopped, The DTLS session cannot be identified, please make a new DTLS session with a fresh token; current session closed";

				    	 }
				    		  if (System.currentTimeMillis() < accToken.getExpirationTime()) 
				    		  {
				    			  byte[] b = exchange.getRequestPayload();
				    			    AccountingInfo ai = retrieveRow(accToken.getSessionID());
				    			    ai.incrementInRequestCounter();
				    			    if(ai.getMinRequestSize()> b.length)
				    			    	ai.setMinRequestSize(b.length);
				    			    if(ai.getMaxRequestSize()<b.length)
				    			    	ai.setMaxRequestSize(b.length);
				    			    ai.incrementOutResponseCounter();
				    			    return "Current temperature is 25 degrees";
				    			  
				    		  }
				    		  else
				    		  {
				    			//retrieve sessionID
					    			 int SID = retrieveSessionID(Consumer,Producer, ServiceName);
					    			 byte[] b = exchange.getRequestPayload();
					    			    AccountingInfo ai = retrieveRow(accToken.getSessionID());
					    			    ai.incrementInRequestCounter();
					    			    if(ai.getMinRequestSize()> b.length)
					    			    	ai.setMinRequestSize(b.length);
					    			    if(ai.getMaxRequestSize()<b.length)
					    			    	ai.setMaxRequestSize(b.length);
					    			 System.out.println("token time expired");
					    			 boolean stopAccountingStatus = false;
					    			 while(!stopAccountingStatus)
					    			 {
					    				 stopAccountingStatus = stopAccounting(SID, Producer, TOKEN_EXPIRED);
					    				 
					    			 }
					    			 return "FAIL: Accounting stopped, Previous token expired, contact AAA server for new token and accounting closed";

				    		  }
			    	 }
			    	 else
			    	 {
			    		 
			    		 if ((exchange.getRequestText().indexOf("Token") > -1))
			    		 {				    	 
			    			 token = (String)jo.get("Token");
			    			 LOG.fine("token received is :" + token);
			    			 if(Consumer == null )
				    	 {
				    		 return "FAIL: consume request missing consumer details";
				    	 }
				    	 else if(Producer == null)
				    	 {
				    		 return "FAIL: consume request missing producer details";
				    	 }
				    	 else if(ServiceName == null)
				    	 {
				    		 return "FAIL: consume request missing service details";
				    	 }
				    	 else if(token == null)
				    	 {
				    		 return "FAIL: consume request missing token information. contact http://localhost:5684/Authorisation for a token";
				    	 }
				    	 else 
				    	 {

				    		 JwtClaims jwtClaims = processJWT(token, Producer);
				    		 long ExpirationTime = jwtClaims.getExpirationTime().getValueInMillis();
							   LOG.fine("Expiration time in token is " + ExpirationTime);
					
							    if(jwtClaims.getIssuer().equals("LC_AAA_Server"))  //verify if the token is issued by LC_AAA_Server or not
							    {
							    	if(jwtClaims.getAudience().get(0).equals(Producer)) //verify if the token is created for producer or not
							    	{
							    		if(jwtClaims.getSubject().equals(Consumer)) // verify if the token is issued for consumer or not
							    		{
							    							    	
							    			if(jwtClaims.getClaimValue("ServiceName").equals(ServiceName)) //verify if the token is issued for the service mentioned in the request or not
							    			{
							    				Long accID = (Long) jwtClaims.getClaimValue("AccountingID");
							    				AccountingID = accID.intValue();
							    				 // request to start accounting
							            	    CoapClient AccountingClient = new CoapClient(ACCOUNTING_RESOURCE_URI);
							            	    DTLSConnector clientConnector = DtlsWrap_client(TRUST_STORE_LOCATION_PRODUCER, TRUST_STORE_PASSWORD_PRODUCER, KEY_STORE_LOCATION_PRODUCER, KEY_STORE_PASSWORD_PRODUCER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_PRODUCER, PRODUCER_ACCOUNTING_CLIENT_PORT);
							            	    AccountingClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
							            	    Map<String, String> jsonargs = new HashMap<String, String>();
							            	    jsonargs = new HashMap<String, String>();							        			
							            	    jsonargs.put("Consumer", Consumer);
							        			jsonargs.put("ServiceName", ServiceName);
							        			jsonargs.put("Producer", Producer);
							        			jsonargs.put("AccountingID", Integer.toString(AccountingID));
							        			jsonargs.put("RequestingEntity", Producer);
							        			String json = createJSON("StartAccountingRequest", jsonargs);
							        		//	LOG.info(json);
							        			CoapResponse response =  AccountingClient.post(json,APPLICATION_JSON);
							            	    System.out.println("Received from Accounting server: " + response.getResponseText());
							            	    AccountingClient.getEndpoint().stop();
							            	    String[] str = response.getResponseText().split(",");
							            	    if(str[0].equals("ok"))
							            	    {
									    		 // add token information to the accepted tokens array
							            	    AcceptedTokens ats = new AcceptedTokens(Consumer, Producer, ServiceName, ExpirationTime, IPAddress, Port, DtlsSID, AccountingID);
									    		 AcceptedTokensList.add(ats);
							            	    AccountingInfo ai = new AccountingInfo();
							            	    ai.setSessionID(AccountingID);
							            	    ai.incrementInRequestCounter();
							            	    ai.incrementOutResponseCounter();
							            	    byte[] b = exchange.getRequestPayload();
							    			    	ai.setMinRequestSize(b.length);
							    			    	ai.setMaxRequestSize(b.length);
							    				AccountingInfoList.add(ai);
							    				return "Current temperature is 25 degrees";
							            	    }
							            	    else
							            	    {
							            	    	return "FAIL: Failed to start accounting from producer side, terminating session";
							            	    }

							            	    
							    			}
							    			else
							    				return "FAIL: The requested service is not produced by this producer";
							    		}
							    		else
							    			return "FAIL: The requesting consumer and the subject mismatch";
							    	}
							    	else
							    		return "FAIL: The token is not issued for this producer";
							    }
							    else
							    	return "FAIL: The token is not issed by a valid AAA system";
		
				    	 }
				     }
        return "FAIL: producer failed to identify an open session, contact AAA server at coap://localhost:5684/Authorisation for a token";
			    	 }
			    	 
				     
				}
				else if(exchange.getRequestText().indexOf("CloseSession")> -1)
				{
					JSONObject jo = (JSONObject) jsonObject.get("CloseSession"); 
					System.out.println("-----Received close session request-----");
					Set<String> s = (Set<String>)jo.keySet();
					/*Iterator<String> it = s.iterator();
				     do{
				    	 String key = it.next().toString();
				    	 if()
				     }while(it.hasNext()); */
					 Consumer = (String)jo.get("Consumer");
			    	 Producer = (String) jo.get("Producer");
			    	 ServiceName = (String)jo.get("ServiceName");
			 		//retrieve sessionID
					 int SessionID = retrieveSessionID(Consumer,Producer, ServiceName);
					 if(SessionID == -1)
						 return "FAIL: Already the token is expired and hence session closed and hence close request cannot be processed";
					 else
					 {
		    			 boolean stopAccountingStatus = false;
		    			 while(!stopAccountingStatus)
		    			 {
		    				 stopAccountingStatus = stopAccounting(SessionID, Producer, CLOSE_SESSION);
		    				 
		    			 }
		    			 return "close session request processed and accounting closed";
					 }
				}
				else
					return "FAIL: received request is not a consume request nor a close request";			
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		return "FAIL: received request cannot be parsed";
	
}
	
    public static AcceptedTokens retrieveAcceptedToken(String Consumer, String Producer, String ServiceName)
    {
    	for(AcceptedTokens ats:AcceptedTokensList)
    		if(ats.getConsumer().equals(Consumer) && ats.getProducer().equals(Producer) && ats.getServiceName().equals(ServiceName))
    			return ats;
    	return null;
    }
    
	public boolean stopAccounting(int SessionID, String producer, int TerminationCause)
	{
   	 	try {

		 AccountingInfo ai = retrieveRow(SessionID);
		// request to stop accounting
	    CoapClient AccountingClient = new CoapClient(ACCOUNTING_RESOURCE_URI);
	    DTLSConnector clientConnector  = DtlsWrap_client(TRUST_STORE_LOCATION_PRODUCER, TRUST_STORE_PASSWORD_PRODUCER, KEY_STORE_LOCATION_PRODUCER, KEY_STORE_PASSWORD_PRODUCER, TRUST_STORE_ALIAS_ROOT, KEY_STORE_ALIAS_PRODUCER, PRODUCER_ACCOUNTING_CLIENT_PORT);
	    AccountingClient.setEndpoint(new CoapEndpoint(clientConnector, NetworkConfig.getStandard()));
	    Map<String, String> jsonargs = new HashMap<String, String>();				        			
	    jsonargs.put("SessionID",Integer.toString(SessionID));
	    jsonargs.put("RequestingEntity", producer);
	    jsonargs.put("InRequestCounter", Integer.toString(ai.getInRequestCounter()));
	    jsonargs.put("OutResponseCounter", Integer.toString(ai.getOutResponseCounter()));
	    jsonargs.put("MinRequestSize", Integer.toString(ai.getMaxRequestSize()));
	    jsonargs.put("MaxRequestSize", Integer.toString(ai.getMaxRequestSize()));
	    jsonargs.put("TerminationCause", Integer.toString(TerminationCause));
		String json = createStopRequest("StopAccountingRequest", jsonargs);
	//	LOG.info(json);
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
	public static JwtClaims processJWT(String token, String Producer)
	{
		JwtClaims jwtClaims = null;
		try
		{
		 //retrieve private key of producer
		 KeyStore keyStore = KeyStore.getInstance("JKS");      
		 InputStream in = new FileInputStream(KEY_STORE_LOCATION_PRODUCER);
		 keyStore.load(in, KEY_STORE_PASSWORD_PRODUCER.toCharArray());
		 PrivateKey producer_priv_key = (PrivateKey)keyStore.getKey(KEY_STORE_ALIAS_PRODUCER, KEY_STORE_PASSWORD_PRODUCER.toCharArray());
		 // retrieve public key of aaa server
		 KeyStore trustStore = KeyStore.getInstance("JKS");
		 InputStream inTrust = new FileInputStream(TRUST_STORE_LOCATION_PRODUCER);
		 trustStore.load(inTrust, TRUST_STORE_PASSWORD_PRODUCER.toCharArray());
		 //load certificates
		 Certificate[] trustedCertificates = new Certificate[1];
		 trustedCertificates[0] = trustStore.getCertificate(TRUST_STORE_ALIAS_AAA);
		 Key verificationKey = trustedCertificates[0].getPublicKey();

		 // And set up the allowed/expected algorithms
		 AlgorithmConstraints jwsAlgConstraints = new AlgorithmConstraints(ConstraintType.WHITELIST,
				 AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256, AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384);

		 AlgorithmConstraints jweAlgConstraints = new AlgorithmConstraints(ConstraintType.WHITELIST,
		            KeyManagementAlgorithmIdentifiers.ECDH_ES_A128KW);

		 AlgorithmConstraints jweEncConstraints = new AlgorithmConstraints(ConstraintType.WHITELIST,
		            ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);


	    //  this JwtConsumer is set up to verify
	    // the signature and validate the claims.
		    JwtConsumer secondPassJwtConsumer = new JwtConsumerBuilder()
		            .setExpectedIssuer("LC_AAA_Server")
		            .setDecryptionKey(producer_priv_key)
		            .setVerificationKey(verificationKey)
		            .setRequireExpirationTime()
		            .setAllowedClockSkewInSeconds(30)
		            .setRequireSubject()
		            .setExpectedAudience(Producer)
		            .setJwsAlgorithmConstraints(jwsAlgConstraints)
		            .setJweAlgorithmConstraints(jweAlgConstraints)
		            .setJweContentEncryptionAlgorithmConstraints(jweEncConstraints)
		            .build();
		    jwtClaims = secondPassJwtConsumer.processToClaims(token); // The processtoClaims function raises exception if validation fails 

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    return jwtClaims;
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
	 
    public AcceptedTokens retrieveAccessToken(String Consumer, String Producer, String ServiceName)
    {
      for (AcceptedTokens at : Definitions.AcceptedTokensList) {
        if ((at.getConsumer().equals(Consumer)) && (at.getProducer().equals(Producer)) && (at.getServiceName().equals(ServiceName))) {
          return at;
        }
      }
      return null;
    }
  
  public int retrieveSessionID(String Consumer, String Producer, String ServiceName)
  {
    for (AcceptedTokens at : Definitions.AcceptedTokensList) {
      if ((at.getConsumer().equals(Consumer)) && (at.getProducer().equals(Producer)) && (at.getServiceName().equals(ServiceName))) {
        return at.getSessionID();
      }
    }
    return -1;
  }
  public void deleteAcceptedTokenEntry(int SessionID)
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
  
  public void UpdateDtlsSID(int SessionID, SessionId DtlsSID)
  {
	  int i = 0;
	  for (AcceptedTokens at : Definitions.AcceptedTokensList) {
	      if (at.getSessionID()== SessionID) {
	        AcceptedTokensList.get(i).setDtlsSID(DtlsSID);
	      }
	      i++;
	  }
  }
  public void UpdateAddress(int SessionID, InetAddress IPAddress, int Port)
  {
	  int i = 0;
	  for (AcceptedTokens at : Definitions.AcceptedTokensList) {
	      if (at.getSessionID()== SessionID) {
	        AcceptedTokensList.get(i).setIPAddress(IPAddress);
	        AcceptedTokensList.get(i).setPort(Port);
	      }
	      i++;
	  }
  }
}
