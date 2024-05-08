package com.solum.lidl.custom.exception;

public class RabbitMQDownException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public RabbitMQDownException(String msg) {
		super(msg);
	}

}
