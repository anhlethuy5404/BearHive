package com.bearhive.bearhive.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "CLOUD_NAME",
            "api_key", "CLOUD_API_KEY",
            "api_secret", "CLOUD_API_SECRET",
            "secure", true
        ));
    }
}
