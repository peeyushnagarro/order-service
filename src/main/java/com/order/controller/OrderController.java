package com.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.order.dto.Order;
import com.order.dto.OrderCreateResponse;
import com.order.service.OrderService;

@RestController
@RequestMapping(path="/api/orders/")
public class OrderController {
	
	private static final Logger logger = LogManager.getLogger(OrderController.class);
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("{orderId}")
	public Order getOrder(@PathVariable int orderId) {
		return orderService.getOrder(orderId);
	}
	
	@PostMapping
	public OrderCreateResponse createOrder(@RequestBody Order order) {
		logger.info("Request to create order is come to order-service");
		int orderNumber = orderService.createOrder(order);
		
		logger.info("Order created. Order Number : " + orderNumber);
		OrderCreateResponse response = new OrderCreateResponse();
		response.setId(orderNumber);
		response.setLocation(String.format("http://localhost:7070/order-service/api/orders/%s", orderNumber));
		
		return response;
	}
}
