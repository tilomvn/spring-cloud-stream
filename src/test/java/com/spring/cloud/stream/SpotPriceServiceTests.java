package com.spring.cloud.stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;

import com.spring.cloud.stream.CryptoCurrencyExchangeApplication;
import com.spring.cloud.stream.apiclient.ApiClient;
import com.spring.cloud.stream.dto.Data;
import com.spring.cloud.stream.dto.SpotPrice;
import com.spring.cloud.stream.service.SpotPriceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.boot.test.system.OutputCaptureExtension;

import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.web.client.RestClientException;
import org.junit.jupiter.api.Disabled;

@SpringBootTest(classes = {CryptoCurrencyExchangeApplication.class})
@ExtendWith(OutputCaptureExtension.class)
@Disabled
class SpotPriceServiceTests {

    @Value("${coinbase.api.endpoint:https://api.coinbase.com/v2/}")
    private String coinbaseEndpoint;

    @Autowired
    private SpotPriceService spotPriceService;

    @MockBean
    private ApiClient apiClient;

    @Test
    void getBuySpotPriceSuccess() throws Exception {
        String currency = "NZD";
        String transactionType = "buy";

        Data data = new Data();
        data.setBase("BTC");
        data.setCurrency("NZD");
        data.setAmount("51480.65");
        SpotPrice spotPriceData = new SpotPrice();
        spotPriceData.setData(data);
        when(apiClient.get(eq(coinbaseEndpoint + "prices/" + transactionType + "?currency=" + currency), any())).thenReturn(spotPriceData);

        SpotPrice spotPrice = spotPriceService.getSpotPrice(currency, transactionType);

        assertNotNull(spotPrice);
        assertEquals(spotPriceData, spotPrice);

        verify(apiClient, times(1)).get(eq(coinbaseEndpoint + "prices/" + transactionType + "?currency=" + currency), any());
    }

    @Test
    void getSellSpotPriceSuccess() throws Exception {
        String currency = "USD";
        String transactionType = "sell";

        Data data = new Data();
        data.setBase("BTC");
        data.setCurrency("USD");
        data.setAmount("52480.65");
        SpotPrice spotPriceData = new SpotPrice();
        spotPriceData.setData(data);
        when(apiClient.get(eq(coinbaseEndpoint + "prices/" + transactionType + "?currency=" + currency), any())).thenReturn(spotPriceData);

        SpotPrice spotPrice = spotPriceService.getSpotPrice(currency, transactionType);

        assertNotNull(spotPrice);
        assertEquals(spotPriceData, spotPrice);

        verify(apiClient, times(1)).get(eq(coinbaseEndpoint + "prices/" + transactionType + "?currency=" + currency), any());
    }

    @Test
    void updateSpotPriceTest(CapturedOutput output) throws Exception {
        Data dataNZDBuy = new Data();
        dataNZDBuy.setBase("BTC");
        dataNZDBuy.setCurrency("NZD");
        dataNZDBuy.setAmount("51480.11");
        SpotPrice spotPriceDataBuy = new SpotPrice();
        spotPriceDataBuy.setData(dataNZDBuy);
        when(apiClient.get(eq(coinbaseEndpoint + "prices/buy?currency=NZD"), any())).thenReturn(spotPriceDataBuy);
        Data dataNZDSell = new Data();
        dataNZDSell.setBase("BTC");
        dataNZDSell.setCurrency("NZD");
        dataNZDSell.setAmount("51480.75");
        SpotPrice spotPriceDataSell = new SpotPrice();
        spotPriceDataSell.setData(dataNZDSell);
        when(apiClient.get(eq(coinbaseEndpoint + "prices/sell?currency=NZD"), any())).thenReturn(spotPriceDataSell);

        spotPriceService.updateSpotPrice();

        assertTrue(output.getOut().contains("Spot Price Of NZD Changed To: {Buy Price: 51480.11, Sell Price: 51480.75}"));

        verify(apiClient, times(1)).get(eq(coinbaseEndpoint + "prices/buy?currency=NZD"), any());
        verify(apiClient, times(1)).get(eq(coinbaseEndpoint + "prices/sell?currency=NZD"), any());
    }

    @Test
    void updateSpotPriceExceptionTest(CapturedOutput output) throws Exception {

        String errorMessage = "Any Exception";
        doThrow(new RestClientException(errorMessage)).when(apiClient).get(anyString(), any());
        spotPriceService.updateSpotPrice();

        assertTrue(output.getOut().contains(errorMessage));

        verify(apiClient, times(1)).get(anyString(), any());
    }
}
