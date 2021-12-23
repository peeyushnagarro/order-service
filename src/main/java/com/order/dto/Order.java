package com.order.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Order {
	private int id;
	private int customerId;
	private BigDecimal invoiceAmount;
	private BigDecimal invoiceAmountNet;
	/*private LocalDateTime orderTime;*/
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Timestamp orderTime;
	private String currency;
	private int currencyFactor;
	private List<Article> details;
	private Billing billing;
	private Shipping shipping;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public BigDecimal getInvoiceAmountNet() {
		return invoiceAmountNet;
	}
	public void setInvoiceAmountNet(BigDecimal invoiceAmountNet) {
		this.invoiceAmountNet = invoiceAmountNet;
	}
	/*public LocalDateTime getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}*/
	public String getCurrency() {
		return currency;
	}
	public Timestamp getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Timestamp orderTime) {
		this.orderTime = orderTime;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public int getCurrencyFactor() {
		return currencyFactor;
	}
	public void setCurrencyFactor(int currencyFactor) {
		this.currencyFactor = currencyFactor;
	}
	public List<Article> getDetails() {
		return details;
	}
	public void setDetails(List<Article> details) {
		this.details = details;
	}
	public Billing getBilling() {
		return billing;
	}
	public void setBilling(Billing billing) {
		this.billing = billing;
	}
	public Shipping getShipping() {
		return shipping;
	}
	public void setShipping(Shipping shipping) {
		this.shipping = shipping;
	}
	
	
}
