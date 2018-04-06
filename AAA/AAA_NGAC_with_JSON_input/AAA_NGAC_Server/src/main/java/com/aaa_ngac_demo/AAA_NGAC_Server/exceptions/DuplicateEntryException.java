package com.aaa_ngac_demo.AAA_NGAC_Server.exceptions;

public class DuplicateEntryException extends RuntimeException{

	private static final long serialVersionUID = 615148647757242985L;
	
	public DuplicateEntryException(String message) {
		super(message);
	}

}
