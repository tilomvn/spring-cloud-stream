package com.spring.cloud.stream;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.cloud.stream.CryptoCurrencyExchangeApplication;
import com.spring.cloud.stream.dto.CoinbaseExchange;
import com.spring.cloud.stream.service.CoinbaseExchangeService;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.UUID;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {CryptoCurrencyExchangeApplication.class}, properties = {"spring.profiles.active=default"})
@EmbeddedKafka(topics = {"coinexchangedIn", "coinexchangeOut"}, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
//@Disabled
class CloudFunctionKafkaIntegrationTests {

    private static final String INPUT_TOPIC = "coinexchangedIn";
    private static final String OUTPUT_TOPIC = "coinexchangeOut";
    private static final String GROUP_NAME = "coinexchangeGroup";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private CoinbaseExchangeService coinbaseExchangeService;

    @Test
    public void testSendReceive() throws Exception {
        String input = "buy/NZD/2";
        CoinbaseExchange coinbaseExchange = new CoinbaseExchange();
        coinbaseExchange.setId(UUID.randomUUID().toString());
        coinbaseExchange.setSpotPrice(50480.65);
        coinbaseExchange.setProfitFactor(0.05);
        coinbaseExchange.setAmount(2);
        coinbaseExchange.setTotalPrice(106009.365);

        String jsonMessage = objectMapper.writeValueAsString(coinbaseExchange);

        when(coinbaseExchangeService.coinExchange(eq(input))).thenReturn(jsonMessage);

        Map<String, Object> producerConfigs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        DefaultKafkaProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(producerConfigs, new StringSerializer(), new StringSerializer());
        KafkaTemplate<String, String> template = new KafkaTemplate<>(pf, true);
        template.setDefaultTopic(INPUT_TOPIC);
        template.sendDefault(input);

        Map<String, Object> consumerConfigs = new HashMap<>(KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafkaBroker));
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerConfigs, new StringDeserializer(), new StringDeserializer());
        Consumer<String, String> consumer = cf.createConsumer();
        consumer.subscribe(Collections.singleton(OUTPUT_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
        consumer.commitSync();

        assertEquals(1, records.count());
        assertEquals(jsonMessage, records.iterator().next().value());

        verify(coinbaseExchangeService, times(1)).coinExchange(eq(input));
    }
}
