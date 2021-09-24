package com.spring.cloud.stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.cloud.stream.CryptoCurrencyExchangeApplication;
import com.spring.cloud.stream.config.ProfitFactor;
import com.spring.cloud.stream.dto.CoinbaseExchange;
import com.spring.cloud.stream.dto.Data;
import com.spring.cloud.stream.dto.SpotPrice;
import com.spring.cloud.stream.service.CoinbaseExchangeService;
import com.spring.cloud.stream.service.SpotPriceService;

import org.junit.jupiter.api.Disabled;

@SpringBootTest(classes = {CryptoCurrencyExchangeApplication.class})
@Disabled
class CoinbaseExchangeServiceTests {

    @Autowired
    private CoinbaseExchangeService coinbaseExchangeService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfitFactor profitFactor;

    @MockBean
    private SpotPriceService spotPriceService;

    @Test
    void buyCoinSuccess() throws Exception {
        String input = "buy/NZD/2";
        double totalPrice = 115316.656;

        Data data = new Data();
        data.setBase("BTC");
        data.setCurrency("NZD");
        data.setAmount("51480.65");
        SpotPrice spotPrice = new SpotPrice();
        spotPrice.setData(data);
        when(spotPriceService.getSpotPrice(anyString(), anyString())).thenReturn(spotPrice);

        double profitFactorData = 0.12;
        when(profitFactor.getCurrentProfitFactor()).thenReturn(profitFactorData);

        String outMessage = coinbaseExchangeService.coinExchange(input);
        CoinbaseExchange coinbaseExchange = objectMapper.readValue(outMessage, CoinbaseExchange.class);

        assertNotNull(coinbaseExchange.getId());
        assertTrue(coinbaseExchange.getProfitFactor() == profitFactorData);
        assertTrue(coinbaseExchange.getTotalPrice() == totalPrice);
        assertTrue(coinbaseExchange.getAmount() == 2);
        assertTrue(coinbaseExchange.getSpotPrice() == 51480.65);

        verify(profitFactor, times(1)).getCurrentProfitFactor();
        verify(spotPriceService, times(1)).getSpotPrice(anyString(), anyString());
    }

    @Test
    void sellCoinSuccess() throws Exception {
        String input = "sell/NZD/2";
        double totalPrice = 106009.365;

        Data data = new Data();
        data.setBase("BTC");
        data.setCurrency("NZD");
        data.setAmount("50480.65");
        SpotPrice spotPrice = new SpotPrice();
        spotPrice.setData(data);
        when(spotPriceService.getSpotPrice(anyString(), anyString())).thenReturn(spotPrice);

        double profitFactorData = 0.05;
        when(profitFactor.getCurrentProfitFactor()).thenReturn(profitFactorData);

        String outMessage = coinbaseExchangeService.coinExchange(input);
        CoinbaseExchange coinbaseExchange = objectMapper.readValue(outMessage, CoinbaseExchange.class);

        assertNotNull(coinbaseExchange.getId());
        assertTrue(coinbaseExchange.getProfitFactor() == profitFactorData);
        assertTrue(coinbaseExchange.getTotalPrice() == totalPrice);
        assertTrue(coinbaseExchange.getAmount() == 2);
        assertTrue(coinbaseExchange.getSpotPrice() == 50480.65);

        verify(profitFactor, times(1)).getCurrentProfitFactor();
        verify(spotPriceService, times(1)).getSpotPrice(anyString(), anyString());
    }

    @Test
    void sellCoinSuccessForUSD() throws Exception {
        String input = "sell/USD/2";
        double totalPrice = 78709.365;

        Data data = new Data();
        data.setBase("BTC");
        data.setCurrency("USD");
        data.setAmount("37480.65");
        SpotPrice spotPrice = new SpotPrice();
        spotPrice.setData(data);
        when(spotPriceService.getSpotPrice(anyString(), anyString())).thenReturn(spotPrice);

        double profitFactorData = 0.05;
        when(profitFactor.getCurrentProfitFactor()).thenReturn(profitFactorData);

        String outMessage = coinbaseExchangeService.coinExchange(input);
        CoinbaseExchange coinbaseExchange = objectMapper.readValue(outMessage, CoinbaseExchange.class);

        assertNotNull(coinbaseExchange.getId());
        assertTrue(coinbaseExchange.getProfitFactor() == profitFactorData);
        assertTrue(coinbaseExchange.getTotalPrice() == totalPrice);
        assertTrue(coinbaseExchange.getAmount() == 2);
        assertTrue(coinbaseExchange.getSpotPrice() == 37480.65);

        verify(profitFactor, times(1)).getCurrentProfitFactor();
        verify(spotPriceService, times(1)).getSpotPrice(anyString(), anyString());
    }

    @Test
    void invalidInput() {
        String input = "sell/USD/";
        String outMessage = coinbaseExchangeService.coinExchange(input);
        assertEquals(String.format(CoinbaseExchangeService.errorsMessageFormat, "invalid_request", "Invalid Request (" + input + ")"), outMessage);
    }

    @Test
    void invalidTransactionInput() {
        String input = "other/USD/1";
        String outMessage = coinbaseExchangeService.coinExchange(input);
        assertEquals(String.format(CoinbaseExchangeService.errorsMessageFormat, "invalid_request", "Transaction not supported"), outMessage);
    }

    @Test
    void invalidAmountInput() {
        String input = "buy/USD/amount";
        String outMessage = coinbaseExchangeService.coinExchange(input);
        assertEquals(String.format(CoinbaseExchangeService.errorsMessageFormat, "invalid_request", "Amount must be a number"), outMessage);
    }

    @Test
    void requestToCoinbaseApiError() throws Exception {
        String input = "sell/NZDF/2";

        String errorResponseBody = "{\"errors\":[{\"id\":\"invalid_request\",\"message\":\"Invalid currency (NZDF)\"}]}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.name(), null, errorResponseBody.getBytes(), null)).when(spotPriceService).getSpotPrice(anyString(), anyString());

        String outMessage = coinbaseExchangeService.coinExchange(input);
        assertEquals(String.format(CoinbaseExchangeService.errorsMessageFormat, "invalid_request", "Invalid currency (NZDF)"), outMessage);
    }

    @Test
    void anyExceptionTest() throws Exception {
        String input = "sell/NZD/2";
        String internalErrorMessage = "Any Exception";
        doThrow(new RestClientException(internalErrorMessage)).when(spotPriceService).getSpotPrice(anyString(), anyString());

        String outMessage = coinbaseExchangeService.coinExchange(input);
        assertEquals(String.format(CoinbaseExchangeService.errorsMessageFormat, "server_error", internalErrorMessage), outMessage);
    }
}
