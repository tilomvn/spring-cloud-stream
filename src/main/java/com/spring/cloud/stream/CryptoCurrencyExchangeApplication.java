package com.spring.cloud.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoCurrencyExchangeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CryptoCurrencyExchangeApplication.class, args);
	}
}
