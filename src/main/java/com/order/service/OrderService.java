package com.order.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.order.controller.OrderController;
import com.order.dto.Article;
import com.order.dto.Billing;
import com.order.dto.Order;
import com.order.dto.Shipping;
import com.order.exception.OrderNotCreatedException;
import com.order.proxy.InventoryServiceProxy;
import com.order.proxy.NotificationServiceProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.ResourceAccessException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Service
public class OrderService {
	
	private static final Logger logger = LogManager.getLogger(OrderService.class);
	private static final String GET_ARTICLE_URL = "api/article";
	private static final String PUBLISH_NOTIFICATION_URL = "/api/notification/publishNotification";
	private Order order;
	
	{
		order = new Order();
		Article artical1 = new Article();
		artical1.setArticleId(220);
		artical1.setTaxRate(12);
		artical1.setArticleCode("SW10001");
		artical1.setPrice(new BigDecimal("35.99"));
		artical1.setQuantity(1);
		artical1.setArticleName("Versandkostenfreier Artikel");
		
		Article artical2 = new Article();
		artical2.setArticleId(221);
		artical2.setTaxRate(18);
		artical2.setArticleCode("SW12345");
		artical2.setPrice(new BigDecimal("42.99"));
		artical2.setQuantity(1);
		artical2.setArticleName("Versandkostenfreier Artikel");
		
		Article artical3 = new Article();
		artical3.setArticleId(232);
		artical3.setTaxRate(5);
		artical3.setArticleCode("SW9999");
		artical3.setPrice(new BigDecimal("10.99"));
		artical3.setQuantity(1);
		artical3.setArticleName("Versandkostenfreier Artikel");
		List<Article> details = new ArrayList<>();
		/*Article[] details = new Article[3];*/
		details.add(artical1);
		details.add(artical2);
		details.add(artical3);
		/*details[0] = artical1;
		details[1] = artical2;
		details[2] = artical3;*/
		
		Billing billing = new Billing();
		billing.setId(2);
		billing.setCustomerId(1);
		billing.setCountryId(2);
		billing.setStateId(3);
		billing.setCompany("shopware AG");
		billing.setSalutation("mr");
		billing.setFirstName("Ajay");
		billing.setLastName("Sharma");
		billing.setStreet("Sadar Bazar");
		billing.setZipcode(48624);
		billing.setCity("Delhi");
		
		Shipping shipping = new Shipping();
		
		shipping.setId(2);
		shipping.setCustomerId(1);
		shipping.setCountryId(2);
		shipping.setStateId(3);
		shipping.setCompany("shopware AG");
		shipping.setSalutation("mr");
		shipping.setFirstName("Ajay");
		shipping.setLastName("Sharma");
		shipping.setStreet("Sadar Bazar");
		shipping.setZipcode(110006);
		shipping.setCity("Delhi");
		
		order.setId(60);
		order.setCustomerId(1);
		order.setInvoiceAmount(new BigDecimal("201.86"));
		order.setInvoiceAmountNet(new BigDecimal("169.63"));
		/*order.setOrderTime(LocalDateTime.of(2012, 8, 31, 8, 51, 46));*/
		order.setCurrency("INR");
		order.setCurrencyFactor(1);
		order.setDetails(details);
		order.setBilling(billing);
		order.setShipping(shipping);
		
	}
	
	@Autowired
	private InventoryServiceProxy inventoryServiceProxy;
	
	@Autowired
	private NotificationServiceProxy notificationServiceProxy;
	
	public Order getOrder(int orderId) {
		order.setId(orderId);
		return  order;
	}
	
	public int createOrder(Order order) {
		if(order == null || order.getDetails() == null || order.getDetails().isEmpty()) {
			logger.info("No article found in order request");
			throw new OrderNotCreatedException("No article id provided");
		}
		List<CompletableFuture<Integer>> futures = new ArrayList<>();
		boolean allArticleFound = true;
		List<Article> articles = order.getDetails();
		String articleNotFound = "";
		AtomicInteger i = new AtomicInteger(0);
		
		for(Article article : articles) {
			int articleId = article.getArticleId();
			CompletableFuture<Integer> future = getArticle(articleId);
			futures.add(future);
		}
		
		for(CompletableFuture<Integer> future : futures) {
			Article article = articles.get(i.get());
			int result = 0;
			
			try {
				result = future.get();
			} catch(Exception exception) {
				logger.info("Exception occurred while getting result from future object.");
				result = -1;
			}
			
			if(result  < 0) {
				allArticleFound = false;
				articleNotFound += article.getArticleId() + ", ";
			}
			i.incrementAndGet();
		}
		
		if(!articleNotFound.isEmpty()) {
			articleNotFound = articleNotFound.substring(0, articleNotFound.length()-2);
			throw new OrderNotCreatedException(articleNotFound);
		}
		
		if(allArticleFound) {
			logger.info("Going to publish notification for given cutomer id : " + order.getCustomerId());
			publishNotification(order.getCustomerId());
		}
		
		return 60;
	}

	@Async
	private void publishNotification(int customerId) {
		try {
			publishNotificationFromNotificationService(customerId);
			logger.info("Notification published for given customer id : " + customerId);
		} catch(Exception exception) {
			logger.info("Exception occurred while publishing notification for given customer id : " + customerId + " from notification-service");
			exception.printStackTrace();
		}
	}
	
	@HystrixCommand(fallbackMethod = "fallbackForPublishNotificationFromNotificationService",
            commandProperties = {
                     @HystrixProperty(
                                       name= "circuitBreaker.requestVolumeThreshold",
                                       value="6"),
                     @HystrixProperty(
                                      name= "circuitBreaker.enabled", 
                                      value = "false")
           } )
	private void publishNotificationFromNotificationService(int customerId) {
		notificationServiceProxy.publishNotification(customerId);
	}
	
	private void fallbackForPublishNotificationFromNotificationService() {
		logger.info("Into fallback method to publish notification from notification service");
		logger.info("Notification is not published");
	}

	@Async
	public CompletableFuture<Integer> getArticle(int articleId) {
		logger.info("Going to make request to get article from inventory-service for given article id : " + articleId);
		try {
			Article article = getArticleFromInventoryService(articleId);
			
			if(article == null) {
				logger.info("Article not found for given article id : " + articleId);
				return CompletableFuture.completedFuture(-1);
			} else {
				logger.info("Article found for given article id : " + articleId);
				return CompletableFuture.completedFuture(1);
			}
		} catch(Exception exception) {
			logger.info("Exception occurred while getting article for given article id : " + articleId + " from inventory-service");
			exception.printStackTrace();
			return CompletableFuture.completedFuture(-1);
		}
	}
	
	@HystrixCommand(fallbackMethod = "fallbackForGetArticleFromInventoryService",
            commandProperties = {
                     @HystrixProperty(
                                       name= "circuitBreaker.requestVolumeThreshold",
                                       value="6"),
                     @HystrixProperty(
                                      name= "circuitBreaker.enabled", 
                                      value = "false")
           } )
	public Article getArticleFromInventoryService(int articleId) {
		return inventoryServiceProxy.getArticle(articleId);
	}
	
	public Article fallbackForGetArticleFromInventoryService() {
		logger.info("Into fallback method to get article from inventory service");
		return null;
	}
}
