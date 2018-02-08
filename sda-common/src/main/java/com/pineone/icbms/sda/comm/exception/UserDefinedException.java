package com.pineone.icbms.sda.comm.exception;

import org.springframework.http.HttpStatus;

/**
 * 사용자 정의 Exception
 */
public class UserDefinedException extends Exception {
	private static final long serialVersionUID = -978972073880404008L;
	private HttpStatus httpStatus;
	private String msg;
  
    public UserDefinedException(HttpStatus httpStatus, String msg) {
    	this.httpStatus = httpStatus;
    	this.msg = msg;
    }

    public UserDefinedException(HttpStatus httpStatus) {
    	this.httpStatus = httpStatus;
    }

    public String getMsg() {
	   if(msg == null || msg.equals("")) {
		   return httpStatus.getReasonPhrase();		   
	   } else {
		   return msg;
	   }
   }
  
   public int getCode() {
      return httpStatus.value();
   }

 }