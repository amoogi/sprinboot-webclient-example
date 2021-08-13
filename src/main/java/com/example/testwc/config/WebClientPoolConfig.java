package com.example.testwc.config;


import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.SSLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * WebClient Bean 설정 (심화, 글로벌 리소스를 사용하지 않고, 전용 pool 설정
 * https://projectreactor.io/docs/netty/snapshot/reference/index.html#_connection_pool
 * 추천하지 않는다.
 */
@ConditionalOnProperty("webclient.active")
@Configuration
public class WebClientPoolConfig {
    @Bean
    public NioEventLoopGroup poolNioEventLoopGroup() {
        int THREADS = 30;

        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("amoogi-%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                THREADS, // core pool size < 0, 유휴상태인 경우에도 pool에 유지할 스레드 수
                THREADS, // maximum pool size <=0, pool에서 허용할 최대 스레드 수
                0L, // keep alive time, 스레드 수가 코어 스레드 수보다 많을 경우 초과 된 스레드가 종료되기전 대기하는 최대 시간
                TimeUnit.MILLISECONDS, // keepAliveTime 단우
                new LinkedBlockingQueue<>(), // 작업이 실행되기 전에 보류하는데 사용할 대기열
                threadFactory, // 새 스레드를 생성할 때 사용할 factory
                new ThreadPoolExecutor.AbortPolicy() // 기준치에 도달하여 실행이 차단될 때 사용할 핸들러
        );

        return new NioEventLoopGroup(THREADS, executor);
    }

    @Bean
    public ReactorResourceFactory poolReactorResourceFactory(NioEventLoopGroup poolNioEventLoopGroup) {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setLoopResources(ls -> poolNioEventLoopGroup);
        // 글로벌 리소스 사용하지 않음
        factory.setUseGlobalResources(false);
        return factory;
    }

    @Bean
    public ReactorClientHttpConnector poolReactorClientHttpConnector(ReactorResourceFactory poolReactorResourceFactory) throws SSLException {
        // SSL Validation을 확인하지 않는다.
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        return new ReactorClientHttpConnector(
                poolReactorResourceFactory,
                m -> m
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000) // connection timeout
                        .secure(t -> t.sslContext(sslContext))
                        .doOnConnected(connection ->
                                // ReadTimeoutHandler/WriteTimeoutHandler는
                                // TCP level에서 적용되므로 TLS handshake 동안에도 적용된다.
                                // 관련된 암호화로 인해 전형적인 HTTP 응답보다 오래 걸릴 수 있다.
                                // 따라서 HTTP 응답에 대해 원하는 것보다 timeout을 높게 설정해야한다.
                                connection
                                        .addHandlerLast(new ReadTimeoutHandler(1)) // read timeout, second
                                        .addHandlerLast(new WriteTimeoutHandler(1)) // write timeout, second
                        ));
    }

    @Bean
    public WebClient poolWebClient(ReactorClientHttpConnector poolReactorClientHttpConnector) {
        // Spring WebFlux 에서는 어플리케이션 메모리 문제를 피하기 위해 codec 처리를 위한
        // in-memory buffer 값이 256KB로 기본설정 되어 있다.
        // 이 제약 때문에 256KB보다 큰 HTTP 메시지를 처리하려고 하면 DataBufferLimitException
        // 에러가 발생하게 된다.
        // 이 값을 늘려주기 위해서는 ExchageStrategies.builder() 를 통해 값을 늘려줘야 한다.
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50))
                .build();

        // Debug 레벨 일 때 form Data 와 Trace 레벨 일 때 header 정보는 민감한 정보를 포함하고 있기 때문에,
        // 기본 WebClient 설정에서는 위 정보를 로그에서 확인할 수 가 없다.
        // 개발 진행 시 Request/Response 정보를 상세히 확인하기 위해서는 ExchageStrateges 와
        // logging level 설정을 통해 로그 확인이 가능하도록 해 주는 것이 좋다.
        // application.yml 에 개발용 로깅 레벨은 DEBUG 로 설정해 주면 된다.
        // logging:
        //  level:
        //    org.springframework.web.reactive.function.client.ExchangeFunctions: DEBUG
        exchangeStrategies
                .messageWriters().stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .forEach(writer -> ((LoggingCodecSupport) writer).setEnableLoggingRequestDetails(true));

        return WebClient.builder()
                .clientConnector(poolReactorClientHttpConnector)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
