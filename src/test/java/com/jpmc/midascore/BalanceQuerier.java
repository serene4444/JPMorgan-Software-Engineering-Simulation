package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Balance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BalanceQuerier {
    private final RestTemplate restTemplate;
    private final int serverPort;

    public BalanceQuerier(RestTemplateBuilder builder, @Value("${local.server.port:33400}") int serverPort) {
        this.restTemplate = builder.build();
        this.serverPort = serverPort;
    }

    public Balance query(Long userId) {
        String url = "http://localhost:" + serverPort + "/balance?userId=" + userId;
        return restTemplate.getForObject(url, Balance.class);
    }
}
