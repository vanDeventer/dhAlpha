package com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer;

public class Definitions {
    public static final String DEFAULT_COAP = "CoAP";
    public static final int DEFAULT_COAP_PORT = 5684;
    
    /* Names */
    public static final String RESOURCE_AUTHORISATION_NAME = "Authorisation";
    public static final String RESOURCE_AUTHORISATION_TITLE = "Authorisation Resource";
    public static final String RESOURCE_ACCOUNTING_NAME = "Accounting";
    public static final String RESOURCE_ACCOUNTING_TITLE = "Accounting Resource";
    public static final String KEY_STORE_PASSWORD_AAA = "password";
    public static final String KEY_STORE_LOCATION_AAA = "resources/aaa_server_keystore.jks";
    public static final String KEY_STORE_ALIAS_AAA = "aaa-server"; 
    public static final String TRUST_STORE_PASSWORD_AAA = "password";
    public static final String TRUST_STORE_LOCATION_AAA = "resources/aaa_server_truststore.jks";
    public static final String TRUST_STORE_ALIAS_AAA = "aaa-server"; 
    public static final String TRUST_STORE_ALIAS_PRODUCER = "producer";
    public static int TOKEN_EXPIRY_TIME_IN_MIN;
    public static int AAA_SERVER_COAP_PORT;
    public static String NGAC_DB_FILE;
}
