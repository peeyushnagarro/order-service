package com.order.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.order.dto.Article;


@FeignClient(name = "inventory-service", url = "inventory-service:8080",  path = "/inventory-service/api/articles")
public interface InventoryServiceProxy {
	@GetMapping("/{articleId}")
	public Article getArticle(@PathVariable("articleId") int articleId);
}
