package com.spring.cloud.stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.cloud.stream.CryptoCurrencyExchangeApplication;
import com.spring.cloud.stream.dto.CoinbaseExchange;
import com.spring.cloud.stream.service.CoinbaseExchangeService;

import java.util.UUID;
import org.junit.jupiter.api.Disabled;

@SpringBootTest(classes = {CryptoCurrencyExchangeApplication.class})
@Disabled
class CloudFunctionTests {

    @MockBean
    private CoinbaseExchangeService coinbaseExchangeService;

    @Autowired
    private Function<String, String> coinexchange;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void coinexchangeCloudFunctionTest() throws Exception {
        String input = "buy/NZD/2";

        CoinbaseExchange coinbaseExchange = new CoinbaseExchange();
        coinbaseExchange.setId(UUID.randomUUID().toString());
        coinbaseExchange.setSpotPrice(50480.65);
        coinbaseExchange.setProfitFactor(0.05);
        coinbaseExchange.setAmount(2);
        coinbaseExchange.setTotalPrice(106009.365);

        String jsonMessage = objectMapper.writeValueAsString(coinbaseExchange);

        when(coinbaseExchangeService.coinExchange(eq(input))).thenReturn(jsonMessage);

        String outMessage = coinexchange.apply(input);

        assertEquals(jsonMessage, outMessage);

        verify(coinbaseExchangeService, times(1)).coinExchange(eq(input));
    }
}
