package com.order.exception;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(OrderNotCreatedException.class)
	public ResponseEntity<Object> handleCityNotFoundException(OrderNotCreatedException exception, WebRequest request) {
		Map<String, String> body = new LinkedHashMap<>();
        body.put("reason", String.format("Given atricle(s) : [%s] not found in inventory", exception.getNotFoundArticleIds()));
        body.put("message", "Order not created");
        return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
	}
}
