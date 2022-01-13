package com.order.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notification-service", url = "notification-service:9090", path = "/notification-service/api/notifications")
public interface NotificationServiceProxy {

	@PostMapping("/{customerId}")
	public String publishNotification(@PathVariable("customerId") int customerId);
}
