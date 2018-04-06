package com.aaa_ngac_demo.AAA_NGAC_Producer;

import static org.eclipse.californium.core.coap.MediaTypeRegistry.APPLICATION_JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwt.consumer.JwtContext;
import java.security.Key;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.aaa_ngac_demo.AAA_NGAC_Producer.JWT.JWT_Verifier;

import org.eclipse.californium.core.server.resources.CoapExchange;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;
import static com.aaa_ngac_demo.AAA_NGAC_Producer.Definitions.*;

//get_temp is one of the service by the producer

public class get_status extends ConcurrentCoapResource{

	private static final Logger LOG = Logger.getLogger(get_status.class.getName());
	public get_status() 
	{
		super(RESOURCE_GET_STATUS_NAME);
		getAttributes().setTitle(RESOURCE_GET_STATUS_TITLE);
	}
	public get_status(String name, int threads) {
		super(name, threads);
	}
	
	public void handlePOST(CoapExchange exchange) {
		if (exchange.getRequestOptions().getContentFormat() == APPLICATION_JSON)
		{
			  RequestHandler rh = new RequestHandler();
			  String accessResult = rh.handle(exchange);		
			  exchange.respond(CONTENT,accessResult);
		}
	}
}