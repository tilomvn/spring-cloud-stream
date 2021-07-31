package com.augen.test.cryptocurrencyexchange.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.augen.test.cryptocurrencyexchange.apiclient.ApiClient;
import com.augen.test.cryptocurrencyexchange.dto.SpotPrice;
import com.augen.test.cryptocurrencyexchange.dto.CurrencySpotPrice;

import java.util.Map;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SpotPriceService {

    private Map<String, CurrencySpotPrice> spotPriceMap = new HashMap<>();

    @Value("${coinbase.api.endpoint:https://api.coinbase.com/v2/}")
    private String coinbaseEndPoint;

    @Autowired
    private ApiClient apiClient;

    public SpotPrice getSpotPrice(String currency, String transactionType) throws Exception {
        CurrencySpotPrice currencySpotPrice = spotPriceMap.get(currency);
        if (currencySpotPrice == null) {
            currencySpotPrice = getSpotPriceByCurrency(currency);
            spotPriceMap.put(currency, currencySpotPrice);
        }

        if ("buy".equals(transactionType)) {
            return currencySpotPrice.getBuySpotPrice();
        }
        return currencySpotPrice.getSellSpotPrice();
    }

    private CurrencySpotPrice getSpotPriceByCurrency(String currency) throws Exception {
        SpotPrice buySpotPrice = (SpotPrice) apiClient.get(coinbaseEndPoint + "prices/buy?currency=" + currency, SpotPrice.class);
        SpotPrice sellSpotPrice = (SpotPrice) apiClient.get(coinbaseEndPoint + "prices/sell?currency=" + currency, SpotPrice.class);
        CurrencySpotPrice currencySpotPrice = new CurrencySpotPrice();
        currencySpotPrice.setBuySpotPrice(buySpotPrice);
        currencySpotPrice.setSellSpotPrice(sellSpotPrice);
        return currencySpotPrice;
    }

    @Scheduled(fixedDelay = 2000, initialDelay = 2000)
    public void updateSpotPrice() {
        try {
            for (Map.Entry entry: spotPriceMap.entrySet()) {
                String currency = String.valueOf(entry.getKey());
                CurrencySpotPrice currencyPrice = (CurrencySpotPrice)entry.getValue();
                CurrencySpotPrice currencySpotPrice = getSpotPriceByCurrency(currency);
                if (!currencyPrice.getBuySpotPrice().getData().getAmount().equals(currencySpotPrice.getBuySpotPrice().getData().getAmount()) || !currencyPrice.getSellSpotPrice().getData().getAmount().equals(currencySpotPrice.getSellSpotPrice().getData().getAmount())) {
                    log.info("Spot Price Of " + currency + " Changed To: {Buy Price: " + currencySpotPrice.getBuySpotPrice().getData().getAmount() + ", Sell Price: " + currencySpotPrice.getSellSpotPrice().getData().getAmount() + "}");
                    spotPriceMap.put(currency, currencySpotPrice);
                }
            }
        } catch(Exception ex) {
            log.info(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
