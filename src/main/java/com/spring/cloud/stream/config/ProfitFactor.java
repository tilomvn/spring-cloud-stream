package com.spring.cloud.stream.config;

import java.util.Random;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class ProfitFactor {

	Logger log = LoggerFactory.getLogger(getClass());

	private final double[] profitFactors = { 0.05, 0.10, 0.12 };

	private double currentProfitFactor = profitFactors[0];

	public double getCurrentProfitFactor() {
		return currentProfitFactor;
	}

	@Scheduled(fixedDelay = 1000, initialDelay = 1000)
	public void randomProfitFactor() {
		double randomValue = profitFactors[new Random().nextInt(3)];
		if (randomValue != currentProfitFactor) {
			log.info("Profit Factor Changed To: " + randomValue);
			currentProfitFactor = randomValue;
		}
	}

}