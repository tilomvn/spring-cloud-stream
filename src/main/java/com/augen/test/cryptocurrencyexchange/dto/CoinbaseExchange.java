package com.augen.test.cryptocurrencyexchange.dto; 

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinbaseExchange {

    private String id;

    private double spotPrice;

    private double profitFactor;

    private int amount;

    private double totalPrice;

}