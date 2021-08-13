package com.example.testwc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("webclient")
public class WebClientProperty {
    private Integer connectionTimeout = 4000; // milliseconds
    private Integer readTimeout = 4; // seconds
    private Integer writeTimeout = 4; // seconds

}
