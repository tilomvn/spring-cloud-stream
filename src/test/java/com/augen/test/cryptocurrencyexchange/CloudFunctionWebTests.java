package com.augen.test.cryptocurrencyexchange;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.augen.test.cryptocurrencyexchange.service.CoinbaseExchangeService;
import com.augen.test.cryptocurrencyexchange.dto.CoinbaseExchange;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Disabled;

@SpringBootTest(classes = {CryptoCurrencyExchangeApplication.class})
@AutoConfigureMockMvc
@Disabled
class CloudFunctionWebTests {

    @MockBean
    private CoinbaseExchangeService coinbaseExchangeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
	private MockMvc mockMvc;

    @Test
    void coinexchangeCloudFunctionWebTest() throws Exception {
        String input = "buy/NZD/2";

        CoinbaseExchange coinbaseExchange = new CoinbaseExchange();
        coinbaseExchange.setId(UUID.randomUUID().toString());
        coinbaseExchange.setSpotPrice(50480.65);
        coinbaseExchange.setProfitFactor(0.05);
        coinbaseExchange.setAmount(2);
        coinbaseExchange.setTotalPrice(106009.365);

        String jsonMessage = objectMapper.writeValueAsString(coinbaseExchange);

        when(coinbaseExchangeService.coinExchange(eq(input))).thenReturn(jsonMessage);

        MvcResult mvcResult = mockMvc.perform(get("/coinexchange/buy/NZD/2")).andExpect(status().isOk()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult)).andExpect(content().string(jsonMessage));

        verify(coinbaseExchangeService, times(1)).coinExchange(eq(input));
    }
}
