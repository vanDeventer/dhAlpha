package com.aaa_ngac_demo.AAA_NGAC_Producer;

import java.io.ByteArrayInputStream;
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
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;

import com.aaa_ngac_demo.AAA_NGAC_Producer.JWT.JWT_Verifier;

import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwt.consumer.JwtContext;
import java.security.Key;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static com.aaa_ngac_demo.AAA_NGAC_Producer.Definitions.*;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;

import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import static com.aaa_ngac_demo.AAA_NGAC_Producer.Definitions.*;
/**
 * Hello world!
 *
 */
public class Producer extends CoapServer
{
	private static final Logger LOG = Logger.getLogger(Producer.class.getName());
	public static CoapServer ProducerServer;
	public static DTLSConnector serverConnector;
	  public Producer() {
		    ProducerServer = new CoapServer();
	        get_temp temp_resource = new get_temp(RESOURCE_GET_TEMP_NAME, 10);
	        get_status status_resource = new get_status(RESOURCE_GET_STATUS_NAME, 10);
	        ProducerServer.add(status_resource);
	        ProducerServer.add(temp_resource);
	        serverConnector =DtlsWrap_Server(KEY_STORE_LOCATION_PRODUCER, KEY_STORE_PASSWORD_PRODUCER, KEY_STORE_ALIAS_PRODUCER, TRUST_STORE_LOCATION_PRODUCER, TRUST_STORE_PASSWORD_PRODUCER,TRUST_STORE_ALIAS_ROOT, PRODUCER_PORT);
	        if(serverConnector!=null)
	        ProducerServer.addEndpoint( new CoapEndpoint(serverConnector, NetworkConfig.getStandard()));
	        else
	        {
	        	LOG.info("failed to set endpoint with DTLS, aborting producer application");
	        	return;
	        }
	  }
	  
	    // To start the CoAP server
	    public void start() {
	    	ProducerServer.start();
	    }
	    // To stop the CoAP server
	    public void stop() {
	    	ProducerServer.stop();

	    }

	    public String getName() {
	        return getClass().getSimpleName();
	    }
	    
    
    public static void main( String[] args )
    {

        //  server.addEndpoints();
    	Properties prop = new Properties();
	    InputStream input = null;
    	try {

	        input = new FileInputStream("resources/config.properties");
	        // load a properties file
	        prop.load(input);
            ACCOUNTING_RESOURCE_URI = new URI(prop.getProperty("ACCOUNTING_RESOURCE_URI"));
            PRODUCER_PORT = Integer.valueOf(prop.getProperty("PRODUCER_PORT"));
            PRODUCER_ACCOUNTING_CLIENT_PORT = Integer.valueOf(prop.getProperty("PRODUCER_ACCOUNTING_CLIENT_PORT"));
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
    	Producer prod_server = new Producer();
          prod_server.start();
          
		  
            
    }
    
//function to wrap a socket with DTLS 
    
    public static DTLSConnector DtlsWrap_Server(String server_key_store, String server_key_password, String server_alias, String server_trust_store, String server_trust_password, String server_trust_alias,  int port)
	{
    	DTLSConnector serverConnector = null;
        try {
        KeyStore keyStore = KeyStore.getInstance("JKS");      
        InputStream in = new FileInputStream(server_key_store);
        keyStore.load(in, server_key_password.toCharArray());
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream inTrust = new FileInputStream(server_trust_store);
        trustStore.load(inTrust, server_trust_password.toCharArray());
        //load certificates
        Certificate[] trustedCertificates = new Certificate[1];
        trustedCertificates[0] = trustStore.getCertificate(server_trust_alias);
        DtlsConnectorConfig.Builder serverConfig = new DtlsConnectorConfig.Builder();
        serverConfig.setAddress(new InetSocketAddress(port));
        serverConfig.setAutoResumptionTimeoutMillis(2000);
        serverConfig.setIdentity((PrivateKey)keyStore.getKey(server_alias, server_key_password.toCharArray()),
                        keyStore.getCertificateChain(server_alias), false);
        serverConfig.setClientAuthenticationRequired(true);
        serverConfig.setMaxConnections(10);
        serverConfig.setTrustStore(trustedCertificates);
        serverConfig.setStaleConnectionThreshold(300);
        serverConnector = new DTLSConnector(serverConfig.build(), null);
        serverConnector.clearConnectionState();


        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        return serverConnector;
	}
 
}
