package com.wipro.vrs.util;

public class InvalidCustomerException extends Exception {
	public InvalidCustomerException() {
		super();
	}
	public InvalidCustomerException(String message) {
		super(message);
	}
@Override	
public String toString() {
	return "InvalidCustomerException: " + getMessage();
}
}