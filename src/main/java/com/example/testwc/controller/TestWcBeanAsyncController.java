package com.example.testwc.controller;

import com.example.testwc.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Locale;

import static com.example.testwc.util.WebClientUtil.postList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/wcbeanasynctest")
public class TestWcBeanAsyncController {
    private final WebClient webClient;

    String baseUrl = "http://localhost:8080/rcvtest";

    private WebClient getWebClient() {
        return webClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue())
                .defaultHeader(HttpHeaders.PRAGMA, CacheControl.noCache().getHeaderValue())
                .build();
    }

    @PostMapping("/post_list_async")
    public void cPostListAsync() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        postList(
                getWebClient(),
                "",
                b -> b.path("/post_list")
                        .build(),
                req,
                parameterizedTypeReference
        )
                .log()
                .doOnNext(v -> System.out.println("next -> " + v.toString()))
                .doOnError(e -> System.out.println("error -> " + e.getMessage()))
                .doOnSubscribe(v -> System.out.println("subscribe 하고 있어요!"))
                .subscribe(
                        v -> System.out.println("rcv data : " + v.toString())
                );

        System.out.println("딴 일을 열심히 해요!");
    }

    @PostMapping("/post_list_async_flux")
    public void cPostListAsyncFlux() {
        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        postList(
                getWebClient(),
                "",
                b -> b.path("/post_list")
                        .build(),
                req,
                Article.class
        ).log()
                .doOnNext(v -> System.out.println("next -> " + v.getTitle()))
                .doOnError(e -> System.out.println("error -> " + e.getMessage()))
                .doOnSubscribe(v -> System.out.println("subscribe 하고 있어요!"))
                .flatMap(
                        value -> Mono.just("제목 : " + value.getTitle())
                        // 주석 삭제하고 병렬로 돌려보면 재미있는거 볼 수 있다.
                        //.subscribeOn(Schedulers.parallel()), 2
                )
                .subscribe(
                        title -> {
                            System.out.println("제목 대문자로 변경 : " + title.toUpperCase(Locale.ROOT));
                        }
                );

        System.out.println("딴 일을 열심히 해요!");
    }
}
