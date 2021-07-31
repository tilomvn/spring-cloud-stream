package com.augen.test.cryptocurrencyexchange.apiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class ApiClient {

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    public Object get(String url, Class classes) throws Exception {
        HttpEntity<String> httpEntity = new HttpEntity<>(getRequestHeader());
        String responseString = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class).getBody();
        Object obj = objectMapper.readValue(responseString, classes);
        return obj;
    }

    private HttpHeaders getRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
}