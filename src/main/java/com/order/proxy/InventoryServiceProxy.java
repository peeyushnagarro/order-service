package com.order.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.order.dto.Article;


@FeignClient(name = "inventory-service", path = "/inventory-service/api/article/")
public interface InventoryServiceProxy {
	@GetMapping("{articleId}")
	public Article getArticle(@PathVariable("articleId") int articleId);
}
