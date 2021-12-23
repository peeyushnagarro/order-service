package com.order.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "notification-service", path = "/notification-service/api/notification/")
public interface NotificationServiceProxy {

	@GetMapping("publishNotification/{customerId}")
	public String publishNotification(@PathVariable("customerId") int customerId);
}
