package com.example.testwc.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

public class WebClientUtil {
    /**
     * Default WebClient
     *
     * @param baseUrl 호출할 URL
     * @return webclient
     */
    public static WebClient getDefaultWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue())
                .defaultHeader(HttpHeaders.PRAGMA, CacheControl.noCache().getHeaderValue())
                .build();
    }

    /**
     * GET으로 단일값을 가져온다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param resModelClass
     * @param <T>
     * @return
     */
    public static <T> Mono<T> getObject(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final Class<T> resModelClass) {
        return webClient
                .get()
                .uri(uriFunc)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToMono(resModelClass);
    }

    /**
     * GET으로 List값을 가져온다.
     *
     * @param webClient
     * @param bearerToken
     * @param parameterizedTypeReference super token 사용 (List<T>와 같은 Generic 대응)
     * @param uriFunc
     * @param <T>
     * @return Mono
     */
    public static <T> Mono<List<T>> getList(
            final WebClient webClient,
            final String bearerToken,
            final ParameterizedTypeReference<List<T>> parameterizedTypeReference,
            final Function<UriBuilder, URI> uriFunc) {
        return webClient
                .get()
                .uri(uriFunc)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToMono(parameterizedTypeReference);
    }


    /**
     * GET으로 List값을 가져온다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param resModelClass
     * @param <T>
     * @return Flux
     */
    public static <T> Flux<T> getList(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final Class<T> resModelClass) {
        return webClient
                .get()
                .uri(uriFunc)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                //.bodyToMono(resModelClass);
                .bodyToFlux(resModelClass);
    }

    /**
     * POST로 값을 가져온다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param req           요청 param 객체
     * @param resModelClass
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Mono<T> postObject(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final U req,
            final Class<T> resModelClass) {
        return webClient
                .post()
                .uri(uriFunc)
                .body(BodyInserters.fromValue(req))
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToMono(resModelClass);
    }

    /**
     * POST로 값을 가져온다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param req                        요청 param 객체
     * @param parameterizedTypeReference
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Mono<T> postObject(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final U req,
            final ParameterizedTypeReference<T> parameterizedTypeReference) {
        return webClient
                .post()
                .uri(uriFunc)
                .body(BodyInserters.fromValue(req))
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToMono(parameterizedTypeReference);
    }

    /**
     * POST로 List값을 가져온다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param req                        요청 param 객체
     * @param parameterizedTypeReference
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Mono<List<T>> postList(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final U req,
            final ParameterizedTypeReference<List<T>> parameterizedTypeReference) {
        return webClient
                .post()
                .uri(uriFunc)
                .body(BodyInserters.fromValue(req))
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToMono(parameterizedTypeReference);
    }

    /**
     * POST로 List값을 가져온다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param req           요청 param 객체
     * @param resModelClass
     * @param <T>
     * @param <U>
     * @return Flux
     */
    public static <T, U> Flux<T> postList(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final U req,
            final Class<T> resModelClass) {
        return webClient
                .post()
                .uri(uriFunc)
                .body(BodyInserters.fromValue(req))
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToFlux(resModelClass);
    }

    /**
     * PUT으로 요청 후 반환값을 받는다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param req           요청 param 객체
     * @param resModelClass
     * @param <T>
     * @return
     */
    public static <T> Mono<T> put(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final T req,
            final Class<T> resModelClass) {
        return webClient
                .put()
                .uri(uriFunc)
                .body(Mono.just(req), resModelClass)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToMono(resModelClass);
    }

    /**
     * DELET를 요청 후 반환값을 받는다.
     *
     * @param webClient
     * @param bearerToken
     * @param uriFunc
     * @param resModelClass
     * @param <T>
     * @return
     */
    public static <T> Mono<T> delete(
            final WebClient webClient,
            final String bearerToken,
            final Function<UriBuilder, URI> uriFunc,
            final Class<T> resModelClass) {
        return webClient
                .delete()
                .uri(uriFunc)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        res -> res
                                .bodyToMono(WebClientResponseException.class)
                                .flatMap(Mono::error))
                .bodyToMono(resModelClass);
    }
}
