package com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer;


import static com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.Definitions.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;


public class CoapServ extends CoapServer {
	private static final Logger LOG = Logger.getLogger(CoapServ.class.getName());
	private CoapServer AAA;
 /* private final static String KEY_STORE_PASSWORD_AAA = "password";
    private static final String KEY_STORE_LOCATION_AAA = "resources/server_keystore.jks";
    private static final String KEY_STORE_ALIAS_AAA = "auth_server"; 
    private final static String TRUST_STORE_PASSWORD_AAA = "password";
    private static final String TRUST_STORE_LOCATION_AAA = "resources/server_truststore.jks";
    private static final String TRUST_STORE_ALIAS_AAA = "auth_server"; 
    */

    
    public CoapServ() {
    	AAA = new CoapServer();
        AuthorisationResource authorizationResource = new AuthorisationResource(RESOURCE_AUTHORISATION_NAME, 10);
        AAA.add(authorizationResource);
        AccountingResource accountingResource = new AccountingResource(RESOURCE_ACCOUNTING_NAME, 10);
        AAA.add(accountingResource);
        DTLSConnector serverConnector = DtlsWrap_Server(KEY_STORE_LOCATION_AAA, KEY_STORE_PASSWORD_AAA, KEY_STORE_ALIAS_AAA, TRUST_STORE_LOCATION_AAA, TRUST_STORE_PASSWORD_AAA, TRUST_STORE_ALIAS_AAA, AAA_SERVER_COAP_PORT);
        AAA.addEndpoint( new CoapEndpoint(serverConnector, NetworkConfig.getStandard()));
    }

    // To start the CoAP server
    public void start() {
    	AAA.start();
    }
    // To stop the CoAP server
    public void stop() {
    	AAA.stop();

    }

    public String getName() {
        return getClass().getSimpleName();
    }
    
    //TO add endpoint to coap server
    public void addEndpoints() {
    	for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
    		// only binds to IPv4 addresses and local host
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, DEFAULT_COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
    }
    
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
   //     trustedCertificates[0] = trustStore.getCertificate(server_trust_alias);
        trustedCertificates[0] = trustStore.getCertificate(server_trust_alias);
        LOG.fine(trustedCertificates[0].toString());
        DtlsConnectorConfig.Builder serverConfig = new DtlsConnectorConfig.Builder();
        serverConfig.setAddress(new InetSocketAddress(port));
     //   serverConfig.setSupportedCipherSuites(new CipherSuite[]{
       //                 CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8});
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