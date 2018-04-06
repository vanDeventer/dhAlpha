package com.aaa_ngac_demo.AAA_NGAC_Server.exceptions;

public class BadPayloadException extends RuntimeException{
	

	private static final long serialVersionUID = -1835158132522682880L;

	public BadPayloadException(String message) {
		super(message);
	}
}
