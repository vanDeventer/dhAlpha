package com.WSClient;

import java.net.URI;
import java.util.ArrayList;


public class Definitions {

    public static final String systemName = "Consumer"; 
    public final static String KEY_STORE_PASSWORD_CONSUMER = "password";
    public static final String KEY_STORE_LOCATION_CONSUMER = "resources/consumer_keystore.jks";   
    public final static String TRUST_STORE_PASSWORD_CONSUMER = "password";
    public static final String TRUST_STORE_LOCATION_CONSUMER = "resources/consumer_truststore.jks";  
    public static final String KEY_STORE_ALIAS_CONSUMER = "consumer";
    public static final String TRUST_STORE_ALIAS_ROOT = "consumer";
    public static final String TRUST_STORE_ALIAS_AAA = "server-cert";
    public static final String TRUST_STORE_ALIAS_PRODUCER = "producer";
    public static ArrayList<AcceptedTokens> AcceptedTokensList = new ArrayList<AcceptedTokens>();
    public static ArrayList<AccountingInfo> AccountingInfoList = new ArrayList<AccountingInfo>();
    public static final int CLOSE_SESSION = 1;
    public static final int TOKEN_EXPIRED = 2;
    public static boolean accountingStatus = false;
    public static String Consumer;
    public static String Producer;
    public static String ServiceName;
    public static URI AUTHORISATION_RESOURCE_URI;
    public static URI ACCOUNTING_RESOURCE_URI;
    public static URI PRODUCER_URI;
    public static int CONSUMER_PORT;
    public static int CONSUMER_ACCOUNTING_CLIENT_PORT;
}
