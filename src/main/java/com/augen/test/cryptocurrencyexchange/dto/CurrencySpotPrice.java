package com.augen.test.cryptocurrencyexchange.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencySpotPrice {

    private SpotPrice buySpotPrice;
    private SpotPrice sellSpotPrice;

}