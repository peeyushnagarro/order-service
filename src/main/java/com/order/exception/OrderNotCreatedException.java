package com.order.exception;

public class OrderNotCreatedException extends RuntimeException {
	private String notFoundArticleIds;
	
	public OrderNotCreatedException(String notFoundArticleIds) {
		this.notFoundArticleIds = notFoundArticleIds;
	}

	public String getNotFoundArticleIds() {
		return notFoundArticleIds;
	}
	
	
}
