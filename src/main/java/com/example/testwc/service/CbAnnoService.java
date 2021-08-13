package com.example.testwc.service;

import com.example.testwc.model.Article;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.example.testwc.util.WebClientUtil.getDefaultWebClient;
import static com.example.testwc.util.WebClientUtil.postList;

/**
 * Circuit Breaker Annontaiton을 사용한 Service
 * application.yml 설정을 사용한다.
 */
@Slf4j
@Service
public class CbAnnoService {
    private static final String ANNOTEST_CIRCUIT_BREAKER = "annotest";

    String baseUrl = "http://localhost:8080/rcvtest";

    @TimeLimiter(name = ANNOTEST_CIRCUIT_BREAKER)
    @Retry(name = ANNOTEST_CIRCUIT_BREAKER)
    @CircuitBreaker(name = ANNOTEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
    public Mono<List<Article>> cPostListMono() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        Article req = Article.builder()
                .title("post cb anno test req")
                .summery("post cb anno test req")
                .id(1000L)
                .build();

        return postList(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/post_error").build(),
                req,
                parameterizedTypeReference
        );
    }

    public Mono<List<Article>> fallback(Exception e) {
        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        log.warn("amoogi error:", e);
        // default 값
        List<Article> def = new ArrayList<>();
        def.add(req);
        return Mono.just(def);
    }
}
