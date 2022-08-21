package com.barclays.cardservice.exception;

/**
 * BarclaysException - Custom exception class to throw exception
 * @author Ved
 *
 */
public class BarclaysException extends Exception {
	private static final long serialVersionUID = 1L;

	public BarclaysException(String message) {
		super(message);
	}
}
