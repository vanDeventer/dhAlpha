package com.aaa_ngac_demo.AAA_NGAC_Producer.JWT;

import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;

import java.security.Key;
public class JWT_Verifier {

public JWT_Verifier()
{
	
}

public JwtConsumer firstPassConsumer()
{
    JwtConsumer firstPassJwtConsumer = new JwtConsumerBuilder()
            .setSkipAllValidators()
            .setDisableRequireSignature()
            .setSkipSignatureVerification()
            .build();
 return firstPassJwtConsumer;
}

public JwtContext getContext(JwtConsumer firstPassJwtConsumer, String token)
{
	 JwtContext jwtContext = null;
	 try{
		 jwtContext = firstPassJwtConsumer.process(token);
	 }
	 catch(Exception e)
	 {
		 System.out.println(e);
	 }
	 return jwtContext;
}

public String retrieve_issuer(JwtContext jwtContext)
{
	 
	  String issuer= null;
	  try
	  {
		  issuer = jwtContext.getJwtClaims().getIssuer();
	  }
	  catch(Exception e)
	  {
		  System.out.println(e);
	  }
	  return issuer;
	  
}

public JwtConsumer secondPassConsumer(Key verificationKey, String issuer, String Producer)
{
	 AlgorithmConstraints algorithmConstraints = new AlgorithmConstraints(ConstraintType.WHITELIST,
	            AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256, AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384);
    JwtConsumer secondPassJwtConsumer = new JwtConsumerBuilder()
            .setExpectedIssuer(issuer)
            .setVerificationKey(verificationKey)
            .setRequireExpirationTime()
            .setAllowedClockSkewInSeconds(30)
            .setRequireSubject()
            .setExpectedAudience(Producer)
            .setJwsAlgorithmConstraints(algorithmConstraints)
            .build();
 return secondPassJwtConsumer;
}
}