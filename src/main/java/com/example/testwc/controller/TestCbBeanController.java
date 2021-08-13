package com.example.testwc.controller;

import com.example.testwc.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.example.testwc.util.WebClientUtil.getDefaultWebClient;
import static com.example.testwc.util.WebClientUtil.postList;

/**
 * Circuit Breaker Bean 설정을 사용한 Test Controller
 */
@Slf4j
@RestController
@RequestMapping("/cbbeantest")
public class TestCbBeanController {
    String baseUrl = "http://localhost:8080/rcvtest";

    private ReactiveCircuitBreaker circuitBreaker;

    public TestCbBeanController(ReactiveCircuitBreakerFactory defaultCustomizer) {
        this.circuitBreaker = defaultCustomizer.create("wctest");
    }

    // circuitbreaker test
    @PostMapping("/post_list_mono")
    public Mono<List<Article>> cPostListMono() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        Article req = Article.builder()
                .title("post cb bean test req")
                .summery("post cb bean test req")
                .id(1000L)
                .build();

        return circuitBreaker.run(
                postList(
                        getDefaultWebClient(baseUrl),
                        "",
                        b -> b.path("/post_error").build(),
                        req,
                        parameterizedTypeReference
                ),
                throwable -> {
                    log.warn("amoogi error:");
                    // default 값
                    List<Article> def = new ArrayList<>();
                    def.add(req);
                    return Mono.just(def);
                });
    }
}
