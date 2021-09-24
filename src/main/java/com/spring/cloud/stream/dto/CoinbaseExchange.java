package com.spring.cloud.stream.dto; 

public class CoinbaseExchange {

    private String id;

    private double spotPrice;

    private double profitFactor;

    private int amount;

    private double totalPrice;

	public void setId(String id) {
		this.id = id;
	}

	public void setProfitFactor(double profitFactor) {
		this.profitFactor = profitFactor;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setSpotPrice(double spotPrice) {
		this.spotPrice = spotPrice;
	}

	public String getId() {
		return id;
	}

	public double getProfitFactor() {
		return profitFactor;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public int getAmount() {
		return amount;
	}

	public double getSpotPrice() {
		return spotPrice;
	}

}