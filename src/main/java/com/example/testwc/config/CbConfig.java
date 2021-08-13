package com.example.testwc.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Circuit Breaker Bean 설정
 */
@Configuration
public class CbConfig {
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(
                        CircuitBreakerConfig.custom().
                                failureRateThreshold(10) // 10% Open, default 50%
                                .build()
                ).timeLimiterConfig(TimeLimiterConfig.custom().
                        timeoutDuration(Duration.ofSeconds(2)).
                        build()
                ).build());
    }
}
