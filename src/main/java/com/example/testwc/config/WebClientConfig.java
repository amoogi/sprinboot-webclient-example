package com.example.testwc.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

/**
 * WebClient Bean 설정 (추천 설정, 글로벌 리소스 사용)
 */
@ConditionalOnProperty("webclient.active")
@Configuration
public class WebClientConfig {
    @Override
    public String toString() {
        // UtilityClass로 인식되지 않도록 함
        return getClass().getSimpleName() + "{}";
    }

    @Setter(onMethod_ = @Autowired)
    private WebClientProperty webClientProperty;

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector() throws SSLException {
        // SSL Validation을 확인하지 않는다.
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000) // connection timeout, milliseconds
                .secure(t -> t.sslContext(sslContext))
                .doOnConnected(connection ->
                        // ReadTimeoutHandler/WriteTimeoutHandler는
                        // HTTP와 관련이 없다. 이거는 다른 read, write 작업들 사이에 시간을 체크하는 표준 netty 핸들러이다.
                        // connection timeout과 동일값으로 설정했다.
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(1)) // read timeout, seconds
                                .addHandlerLast(new WriteTimeoutHandler(1)) // write timeout, seconds
                );
                // responseTimeout은 순수 http요청/응답 시간에 대한 timeout이다.
                // 즉 idle 커넥션을 닫거나 케넥션을 맺거나 하는 시간을 고려하지 않은 순수 http 요청/응답 시간을 제한한다.
                // connection timeout만 관리한다.
                //.responseTimeout(Duration.ofSeconds(2));

        return new ReactorClientHttpConnector(httpClient);
    }

    @Bean
    public WebClient webClient(ReactorClientHttpConnector reactorClientHttpConnector) {
        return WebClient.builder()
                .clientConnector(reactorClientHttpConnector)
                .build();
    }
}
