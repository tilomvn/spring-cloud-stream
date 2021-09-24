package com.spring.cloud.stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.system.CapturedOutput;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spring.cloud.stream.CryptoCurrencyExchangeApplication;
import com.spring.cloud.stream.config.ProfitFactor;

import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.Disabled;

@SpringBootTest(classes = {CryptoCurrencyExchangeApplication.class})
@ExtendWith(OutputCaptureExtension.class)
@Disabled
class ProfitFactorTests {

    @Autowired
    private ProfitFactor profitFactor;

    private final List<Double> constProfitFactor = Arrays.asList(0.05, 0.10, 0.12);

    @Test
    void getCurrentProfitFactorTest() throws Exception {
        double profit = profitFactor.getCurrentProfitFactor();
        assertTrue(constProfitFactor.contains(profit));
    }

    @Test
    void randomProfitFactorTest(CapturedOutput output) throws Exception {
        profitFactor.randomProfitFactor();
        double profit = profitFactor.getCurrentProfitFactor();
        assertTrue(constProfitFactor.contains(profit));
        assertTrue(output.getOut().contains("Profit Factor Changed To: " + profit));
    }
}
