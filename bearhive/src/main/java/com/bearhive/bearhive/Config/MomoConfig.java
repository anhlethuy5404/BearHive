package com.bearhive.bearhive.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MomoConfig {
    @Value("${momo.partner-code}")
    public String PARTNER_CODE;

    @Value("${momo.access-key}")
    public String ACCESS_KEY;

    @Value("${momo.secret-key}")
    public String SECRET_KEY;

    @Value("${momo.redirect-url}")
    public String REDIRECT_URL;

    @Value("${momo.ipn-url}")
    public String IPN_URL;

    @Value("${momo.request-type}")
    public String REQUEST_TYPE;

    @Value("${momo.endpoint}")
    public String ENDPOINT;
}
