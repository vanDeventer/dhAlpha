package com.aaa_ngac_demo.AAA_NGAC_Server.JWTCreator;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jws.JsonWebSignature;
import java.util.logging.Logger;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import static com.aaa_ngac_demo.AAA_NGAC_Server.AAACoapServer.Definitions.*;
public class JWT_Creator {
	private static final Logger LOG = Logger.getLogger(JWT_Creator.class.getName());
	
	public JWT_Creator()
	{

	}
	
	public JwtClaims createClaim(String Consumer, String Producer, String ServiceName, int AccountingID)
	{
		 JwtClaims claims = new JwtClaims();
		    claims.setIssuer("LC_AAA_Server");  // who creates the token and signs it
		    claims.setAudience(Producer); // to whom the token is intended to be sent
		    claims.setExpirationTimeMinutesInTheFuture(TOKEN_EXPIRY_TIME_IN_MIN); // time when the token will expire (10 minutes from now)
		    claims.setGeneratedJwtId(); // a unique identifier for the token
		    claims.setIssuedAtToNow();  // when the token was issued/created (now)
		    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		    claims.setSubject(Consumer); // the subject/principal is whom the token is about
		    claims.setClaim("ServiceName",ServiceName); // additional claims/attributes about the subject can be added
		    claims.setClaim("AccountingID", AccountingID);
		    return claims;
	}
	
	public String signClaim(JwtClaims claims)
	{
	  JsonWebSignature jws = new JsonWebSignature();
	  jws.setPayload(claims.toJson());
	  jws.setKey(retrievePrivateKey(KEY_STORE_LOCATION_AAA, KEY_STORE_PASSWORD_AAA, KEY_STORE_ALIAS_AAA));
	  jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);
	  String jwt = null;
	  try
	  {
	  jwt = jws.getCompactSerialization();
	  }
	  catch(Exception e)
	  {
		  System.out.println(e);
	  }
	  LOG.fine("JWT: " + jwt.toString());
	  return jwt;
	}
	
	public JsonWebEncryption encrypt(String innertoken, Key key)
	{
		 JsonWebEncryption senderJwe = new JsonWebEncryption();
		 senderJwe.setContentTypeHeaderValue("JWT");
		 senderJwe.setPayload(innertoken);
		 senderJwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.ECDH_ES_A128KW);
		 senderJwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
		 senderJwe.setKey(key);		 
		 return senderJwe;
	}
	
	
	public PrivateKey retrievePrivateKey(String server_key_store, String server_key_password, String server_alias)
	{
		PrivateKey prKey = null;
		try {
			
			KeyStore keyStore = KeyStore.getInstance("JKS");      
			InputStream in = new FileInputStream(server_key_store);
			keyStore.load(in, server_key_password.toCharArray());
			prKey =  (PrivateKey)keyStore.getKey(server_alias, server_key_password.toCharArray());

		}
		catch(Exception e)
		{
			System.out.println(e);;
		}
		return prKey;
	}
}
