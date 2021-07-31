package com.augen.test.cryptocurrencyexchange.service;

import com.augen.test.cryptocurrencyexchange.apiclient.ApiClient;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.client.HttpClientErrorException;

import com.augen.test.cryptocurrencyexchange.config.ProfitFactor;
import com.augen.test.cryptocurrencyexchange.dto.CoinbaseExchange;
import com.augen.test.cryptocurrencyexchange.dto.SpotPrice;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CoinbaseExchangeService {

    public static final String errorsMessageFormat = "{\"errors\":[{\"id\":\"%s\",\"message\":\"%s\"}]}";

    @Value("${coinbase.api.endpoint:https://api.coinbase.com/v2/}")
    private String coinbaseEndPoint;

    @Autowired
    private ProfitFactor profitFactor;

    @Autowired
    private SpotPriceService spotPriceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiClient apiClient;

    public String coinExchange(String input) {
        String errorMessage = "";
        try {
            String transactionType = "";
            String currency = "";
            String strAmount = "";
            String[] inp = input.split("/");
            if (inp.length == 3) {
                transactionType = inp[0];
                currency = inp[1];
                strAmount = inp[2];
                if (!"buy".equals(transactionType) && !"sell".equals(transactionType)) {
                    return String.format(errorsMessageFormat, "invalid_request", "Transaction not supported");
                }
                if (!strAmount.matches("[0-9]+")) {
                    return String.format(errorsMessageFormat, "invalid_request", "Amount must be a number");
                }
            } else {
                errorMessage = String.format(errorsMessageFormat, "invalid_request", "Invalid Request (" + input + ")");
                return errorMessage;
            }

            SpotPrice spotPrice = spotPriceService.getSpotPrice(currency, transactionType);
            double randProfitFactor = profitFactor.getCurrentProfitFactor();

            CoinbaseExchange coinbaseExchange = new CoinbaseExchange();
            coinbaseExchange.setId(UUID.randomUUID().toString());
            coinbaseExchange.setSpotPrice(Double.valueOf(spotPrice.getData().getAmount()));
            coinbaseExchange.setProfitFactor(randProfitFactor);
            coinbaseExchange.setAmount(Integer.valueOf(strAmount));

            BigDecimal pFactorOne = new BigDecimal("1");
            BigDecimal pFactor = pFactorOne.add(new BigDecimal(String.valueOf(randProfitFactor)));
            BigDecimal bAmount = new BigDecimal(strAmount);
            BigDecimal bspotPriceAmount = new BigDecimal(spotPrice.getData().getAmount());
            BigDecimal btotalPrice = bAmount.multiply(bspotPriceAmount).multiply(pFactor);
            coinbaseExchange.setTotalPrice(btotalPrice.doubleValue());

            String responseJson = objectMapper.writeValueAsString(coinbaseExchange);
            log.info(responseJson);

            return responseJson;
        } catch(HttpClientErrorException ex) {
            log.info(ex.getResponseBodyAsString());
            return ex.getResponseBodyAsString();
        } catch(Exception ex) {
            ex.printStackTrace();
            errorMessage = String.format(errorsMessageFormat, "server_error", ex.getMessage());
            log.info(errorMessage);
            return errorMessage;
        }
    }
}
