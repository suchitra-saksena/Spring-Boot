package com.barclays.cardservice.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.barclays.cardservice.utility.ErrorInfo;

/**
 * ExceptionControllerAdvice - Automatically catch and handle exception
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {

	/**
	 * exceptionHandler - Catch any uncaught exceptions
	 * @param exception
	 * @return ErrorInfo response 
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> exceptionHandler(Exception exception) {
		ErrorInfo error = new ErrorInfo();
		error.setErrorMessage("Request proccessing failed");
		error.setErrorCode(HttpStatus.ACCEPTED.value());
		error.setTimestamp(LocalDateTime.now());
		return new ResponseEntity<ErrorInfo>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * barclaysExceptionHandler - Catch Barclays exception
	 * @param exception
	 * @return ErrorInfo response
	 */
	@ExceptionHandler(BarclaysException.class)
	public ResponseEntity<ErrorInfo> barclaysExceptionHandler(BarclaysException exception) {
		ErrorInfo error = new ErrorInfo();
		error.setErrorMessage(exception.getMessage());
		error.setTimestamp(LocalDateTime.now());
		error.setErrorCode(HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<ErrorInfo>(error, HttpStatus.NOT_FOUND);
	}

}