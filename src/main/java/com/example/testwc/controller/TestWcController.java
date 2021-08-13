package com.example.testwc.controller;

import com.example.testwc.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.example.testwc.util.WebClientUtil.*;

/**
 * Default WebClient를 사용한 호출 예제
 */
@Slf4j
@RestController
@RequestMapping("/wctest")
public class TestWcController {
    String baseUrl = "http://localhost:8080/rcvtest";

    @GetMapping("/get_object")
    public Mono<Article> cGetObject() {
        return getObject(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/get")
                        .queryParam("title", "test1")
                        .queryParam("summery", "test summery1")
                        .queryParam("id", 1)
                        .build(),
                Article.class
        );
    }

    @GetMapping("/get_list_mono")
    public Mono<List<Article>> cGetListMono() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        return getList(
                getDefaultWebClient(baseUrl),
                "",
                parameterizedTypeReference,
                b -> b.path("/get_list")
                        .build()
        );
    }


    @GetMapping("/get_flux")
    public Flux<Article> cGetFlux() {
        return getList(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/get_list")
                        .build(),
                Article.class
        );
    }

    @GetMapping("/get_list_by_mono_block")
    public List<Article> cGetListByMonoBlock() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        return getList(
                getDefaultWebClient(baseUrl),
                "",
                parameterizedTypeReference,
                b -> b.path("/get_list")
                        .build()
        ).block();
    }

    @GetMapping("/get_list_by_mono_lazy")
    public List<Article> cGetListByMonoLazy() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        return getList(
                getDefaultWebClient(baseUrl),
                "",
                parameterizedTypeReference,
                b -> b.path("/get_list")
                        .build()
        ).flux()
                .toStream()
                .findFirst()
                .orElse(null);

    }


    @PostMapping("/post_object")
    public Mono<Article> cPostObject() {
        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        return postObject(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/post")
                        .build(),
                req,
                Article.class
        );
    }

    @PostMapping("/post_list_mono")
    public Mono<List<Article>> cPostListMono() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        return postList(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/post_list")
                        .build(),
                req,
                parameterizedTypeReference
        )
                .doOnError(e -> log.warn("amoogi:" + e.getMessage(), e));

    }

    @PostMapping("/post_flux")
    public Flux<Article> cPostFlux() {
        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        return postList(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/post_list")
                        .build(),
                req,
                Article.class
        );
    }

    @PostMapping("/post_list_by_mono_block")
    public List<Article> cPostListByMonoBlock() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        return postList(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/post_list")
                        .build(),
                req,
                parameterizedTypeReference
        )
                .doOnError(e -> log.warn("amoogi:" + e.getMessage(), e))
                .block();
    }

    @PostMapping("/post_list_by_mono_lazy")
    public List<Article> cPostListByMonoLazy() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        return postList(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/post_list")
                        .build(),
                req,
                parameterizedTypeReference
        )
                .doOnError(e -> log.warn("amoogi:" + e.getMessage(), e))
                .flux()
                .toStream()
                .findFirst()
                .orElse(null);
    }

    @PutMapping("/put")
    public Mono<Article> cPut() {
        Article req = Article.builder()
                .title("put test req")
                .summery("put test req")
                .id(1000L)
                .build();

        return put(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/put")
                        .build(),
                req,
                Article.class
        );
    }

    @DeleteMapping("/delete")
    public Mono<String> cDelete() {
        return delete(
                getDefaultWebClient(baseUrl),
                "",
                b -> b.path("/delete")
                        .build(),
                String.class
        );
    }
}
