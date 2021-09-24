package com.spring.cloud.stream.dto; 

public class Data {

    private String base;
    private String currency;
    private String amount;
	public String getAmount() {
		return amount;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

}