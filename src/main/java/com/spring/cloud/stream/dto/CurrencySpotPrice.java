package com.spring.cloud.stream.dto;

public class CurrencySpotPrice {

    private SpotPrice buySpotPrice;
    private SpotPrice sellSpotPrice;
	public SpotPrice getBuySpotPrice() {
		return buySpotPrice;
	}
	public SpotPrice getSellSpotPrice() {
		return sellSpotPrice;
	}
	public void setBuySpotPrice(SpotPrice buySpotPrice) {
		this.buySpotPrice = buySpotPrice;
	}
	public void setSellSpotPrice(SpotPrice sellSpotPrice) {
		this.sellSpotPrice = sellSpotPrice;
	}

}