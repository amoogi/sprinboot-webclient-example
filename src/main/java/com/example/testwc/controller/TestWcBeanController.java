package com.example.testwc.controller;

import com.example.testwc.config.WebClientProperty;
import com.example.testwc.model.Article;
import com.example.testwc.model.PapiGamePlayLog;
import com.example.testwc.model.PapiRes;
import com.example.testwc.model.PapiResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.example.testwc.util.WebClientUtil.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/wcbeantest")
public class TestWcBeanController {
    private final WebClient webClient;
    private final WebClient poolWebClient;


    String baseUrl = "http://localhost:8080/rcvtest";
//    String baseUrl = "https://papi-dev.nwz.cloud";

    private WebClient getWebClient() {
        return webClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue())
                .defaultHeader(HttpHeaders.PRAGMA, CacheControl.noCache().getHeaderValue())
                .build();
    }

    private WebClient getPoolWebClient() {
        return poolWebClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue())
                .defaultHeader(HttpHeaders.PRAGMA, CacheControl.noCache().getHeaderValue())
                .build();
    }

    @GetMapping("/get_object")
    public Mono<Article> cGetObject() {
        return getObject(
                getWebClient(),
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
                getWebClient(),
                "",
                parameterizedTypeReference,
                b -> b.path("/get_list")
                        .build()
        );
    }

    @GetMapping("/get_list_mono_pool")
    public Mono<List<Article>> cGetListMonoPool() {
        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        return getList(
                getPoolWebClient(),
                "",
                parameterizedTypeReference,
                b -> b.path("/get_list")
                        .build()
        );
    }


    @GetMapping("/get_flux")
    public Flux<Article> cGetFlux() {
        return getList(
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
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
                getWebClient(),
                "",
                b -> b.path("/delete")
                        .build(),
                String.class
        );
    }

    private final WebClientProperty webClientProperty;

    // timeout test
    @PostMapping("/post_slow")
    public Mono<List<Article>> cPostListSlow() {
        System.out.println("kkk : " + webClientProperty.getConnectionTimeout());

        ParameterizedTypeReference<List<Article>> parameterizedTypeReference =
                new ParameterizedTypeReference<List<Article>>() {
                };

        Article req = Article.builder()
                .title("post test req")
                .summery("post test req")
                .id(1000L)
                .build();

        return postList(
                getWebClient(),
                "",
                b -> b.path("/post_slow")
                        .build(),
                req,
                parameterizedTypeReference
        )
                .doOnError(e -> log.warn("amoogi:" + e.getMessage(), e));
    }

    @PostMapping("/game_play_log")
    public Mono<PapiRes<PapiResultMessage>> setGamePlayLog() {

        ParameterizedTypeReference<PapiRes<PapiResultMessage>> res =
                new ParameterizedTypeReference<PapiRes<PapiResultMessage>>() {
                };

        return postObject(
                getWebClient(),
                "",
//                b -> b.path("/contents/api/v1/pc/game/play/log")
//                        .build(),
                b -> b.path("/game_play_log")
                        .build(),
                PapiGamePlayLog.builder()
                        .accountSrl(108934993L)
                        .ssn("309")
                        .subSsn("309")
                        .ip("172.26.172.173")
                        .build(),
                res
        );

    }
}
