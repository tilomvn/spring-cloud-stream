package com.spring.cloud.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.spring.cloud.stream.service.CoinbaseExchangeService;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.KeyValue;

@SpringBootApplication
@EnableScheduling
public class CryptoCurrencyExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoCurrencyExchangeApplication.class, args);
	}

    @Autowired
    public CoinbaseExchangeService coinbaseExchangeService;

    //processor: listening msg from producer and send msg to consumer as producer
    @Bean
    public Function<KStream<String, String>, KStream<String, String>> coinexchange() {
        return kstream -> kstream.map((key, value) -> KeyValue.pair(key, coinbaseExchangeService.coinExchange(value)));
    }
}
