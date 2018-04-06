package com.aaa_ngac_demo.AAA_NGAC_Producer;

import java.net.URI;
import java.util.ArrayList;


public class Definitions {

    public static final String systemName = "producer"; 
    public final static String KEY_STORE_PASSWORD_PRODUCER = "password";
    public static final String KEY_STORE_LOCATION_PRODUCER = "resources/producer_keystore.jks";   
    public final static String TRUST_STORE_PASSWORD_PRODUCER = "password";
    public static final String TRUST_STORE_LOCATION_PRODUCER = "resources/producer_truststore.jks";  
    public static final String KEY_STORE_ALIAS_PRODUCER = "producer";
    public static final String TRUST_STORE_ALIAS_ROOT = "root";
    public static final String TRUST_STORE_ALIAS_AAA = "server-cert";    
    public static final String RESOURCE_GET_TEMP_NAME = "get_temp";
    public static final String RESOURCE_GET_TEMP_TITLE = "get_temp service";
    public static final String RESOURCE_GET_STATUS_NAME = "get_status";
    public static final String RESOURCE_GET_STATUS_TITLE = "get_status service";
    public static ArrayList<AcceptedTokens> AcceptedTokensList = new ArrayList<AcceptedTokens>();
    public static ArrayList<AccountingInfo> AccountingInfoList = new ArrayList<AccountingInfo>();
    public static final int CLOSE_SESSION = 1;
    public static final int TOKEN_EXPIRED = 2;
    public static final int INVALID_SESSION = 3;
    public static URI ACCOUNTING_RESOURCE_URI;
    public static int PRODUCER_PORT;
    public static int PRODUCER_ACCOUNTING_CLIENT_PORT;
}
