package com.bearhive.bearhive.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bearhive.bearhive.Config.MomoConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MomoPaymentService {
    @Autowired
    private MomoConfig momoConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
}
