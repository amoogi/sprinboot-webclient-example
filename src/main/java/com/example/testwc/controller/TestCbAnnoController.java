package com.example.testwc.controller;

import com.example.testwc.model.Article;
import com.example.testwc.service.CbAnnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Annotation을 사용한 Circuit Breaker Test Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cbannotest")
public class TestCbAnnoController {
    private final CbAnnoService cbAnnoService;

    @PostMapping("/post_list_mono")
    public Mono<List<Article>> cPostListMono() {
        return cbAnnoService.cPostListMono();
    }
}
