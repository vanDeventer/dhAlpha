package com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer;

import static com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.Definitions.*;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;


import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.server.resources.CoapExchange;

import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.aaa_ngac_demo.AAA_NGAC_Server.arrowhead_models.*;
import com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database.*;
import com.aaa_ngac_demo.AAA_NGAC_Server.Authorisation.*;

import com.aaa_ngac_demo.AAA_NGAC_Server.JWTCreator.JWT_Creator;

import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

import static com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.Definitions.*
;public class AuthorisationResource extends ConcurrentCoapResource{
	private static final Logger LOG = Logger.getLogger(AuthorisationResource.class.getName());
	public AuthorisationResource() 
	{
		super(RESOURCE_AUTHORISATION_NAME);
		getAttributes().setTitle(RESOURCE_AUTHORISATION_TITLE);
	}
	public AuthorisationResource(String name, int threads) {
		super(name, threads);
	}
public void handlePOST(CoapExchange exchange) {
if (exchange.getRequestOptions().getContentFormat() == APPLICATION_JSON)
	 {
		 DatabaseManager databaseManager = new DatabaseManager();
		 Authorisation authObj= new Authorisation();
		 boolean allow = false;
		  JSONParser parser = new JSONParser();
		  String token = null;
		  String Consumer = null;
		  String Producer = null;
		  String ServiceName = null;
          node ua; 
          node oa;
          node op;
          operation_set op_set;
          int AccountingID = -1;
		   try{
			  JSONObject jsonObject = (JSONObject) parser.parse(exchange.getRequestText());
		  
		  JSONObject jo = (JSONObject) jsonObject.get("AuthRequest");
		  Set<String> s = (Set<String>)jo.keySet();
		  Consumer = (String)jo.get("Consumer");
		  Producer = (String) jo.get("Producer");
		  ServiceName = (String)jo.get("ServiceName");
		  System.out.println("Consumer: " + Consumer + "\nProducer: "+ Producer + "\nServiceName: "+ ServiceName);
		  if(Consumer == null || Producer == null || ServiceName == null)
		  {
			  exchange.respond(CONTENT, "Authorisation request missing required information: consumer, producer or service name");
		  }
		  else
		  {
			  	arrowheadsystem consumer = databaseManager.getSystemByName(Consumer);
			  	arrowheadsystem producer =databaseManager.getSystemByName(Producer);
			  	arrowheadservice service =databaseManager.getServiceByName(ServiceName);
			  	ua=consumer.getNode_id();
			 // 	LOG.info(Integer.toString(ua.getId()));
			  	oa=producer.getNode_id();
			 // 	LOG.info(Integer.toString(oa.getId()));
			  	op_set=service.getSet_id();
			  	op=databaseManager.getNodeBySet(op_set.getId());
			//  	LOG.info(op.getName());
			  	allow=authObj.isAllowed(ua,oa,op.getName());
			  	/*  Iterator<String> i = s.iterator();
	        		do{
	            		String key = i.next().toString();
	            		String value = (String) jo.get(key);
	            		LOG.info(key + ":" + value);

	        		}while(i.hasNext()); */
			  	if(allow == true)
			  	{
				  try {
					  AccountingID = getSaltString();
					  JWT_Creator jwt = new JWT_Creator();
					  System.out.println("AccountingID: " + AccountingID);
					  JwtClaims claim = jwt.createClaim(Consumer, Producer, ServiceName, AccountingID);
					  String innertoken  = jwt.signClaim(claim);
					  KeyStore trustStore = KeyStore.getInstance("JKS");
						InputStream inTrust = new FileInputStream(TRUST_STORE_LOCATION_AAA);
						trustStore.load(inTrust, TRUST_STORE_PASSWORD_AAA.toCharArray());
					   //load certificates
						Certificate[] trustedCertificates = new Certificate[1];
						trustedCertificates[0] = trustStore.getCertificate(TRUST_STORE_ALIAS_PRODUCER);
						Key producer_pu_key = trustedCertificates[0].getPublicKey();
					  JsonWebEncryption jwe = jwt.encrypt(innertoken, producer_pu_key);
					  token = jwe.getCompactSerialization();
					  
				  } catch (Exception e) {
					  // TODO Auto-generated catch block
					  e.printStackTrace();
				  }
				  Map<String, String> jsonargs = new HashMap<String, String>();
				  jsonargs.put("Consumer", Consumer);
				  jsonargs.put("ServiceName", ServiceName);
				  jsonargs.put("Producer", Producer);
				  jsonargs.put("AccessResponse", String.valueOf(allow));
				  jsonargs.put("AccountingID", String.valueOf(AccountingID));
				  jsonargs.put("Token", token);
				  String json = createJSON("AuthResponse", jsonargs);
				  LOG.fine(json);			
				  exchange.respond(CONTENT, json, APPLICATION_JSON);
			  	}
			  	else
			  		exchange.respond(CONTENT, "access rejected");
		  	}
		   }
		   catch(Exception e)
		   {
			   System.out.println(e);;
		   }
		   
	 }

}
protected int getSaltString() {
    String SALTCHARS = "1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 6) { // length of the random string.
        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
        salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    return Integer.parseInt(saltStr);

}
public static String createJSON(String response, Map<String, String> jsonargs)
{
    JSONObject obj = new JSONObject();
    for(String key: jsonargs.keySet())
	{
        obj.put(key, jsonargs.get(key));
	}
  
    JSONObject json = new JSONObject();
    json.put(response, obj);
  return json.toString();
}
}
