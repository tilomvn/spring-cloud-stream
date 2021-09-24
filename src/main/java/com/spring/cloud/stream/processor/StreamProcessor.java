package com.spring.cloud.stream.processor;

import java.util.function.Function;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.cloud.stream.service.CoinbaseExchangeService;

@Configuration
class StreamProcessor {

    //processor: listening msg from producer and send msg to consumer as producer
    @Bean
    public Function<KStream<String, String>, KStream<String, String>> coinexchange(CoinbaseExchangeService coinbaseExchangeService) {
        return kstream -> kstream.map((key, value) -> KeyValue.pair(key, coinbaseExchangeService.coinExchange(value)));
    }
}
