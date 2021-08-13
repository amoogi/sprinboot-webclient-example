package com.example.testwc.controller;

import com.example.testwc.model.Article;
import com.example.testwc.model.PapiGamePlayLog;
import com.example.testwc.model.PapiRes;
import com.example.testwc.model.PapiResultMessage;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * 호출을 받아주는 Sample Controller
 */
@RestController
@RequestMapping("/rcvtest")
public class TestRcvController {

    @GetMapping("/get")
    public Article getArticle(@ModelAttribute final Article req) {
        return Article.builder()
                .title(req.getTitle())
                .summery(req.getSummery())
                .id(req.getId())
                .build();
    }

    @GetMapping("/get_list")
    public List<Article> getArticleList() {
        List<Article> articles = new ArrayList<>();
        articles.add(
                Article.builder()
                        .title("test1")
                        .summery("test1")
                        .id(1L)
                        .build()
        );
        articles.add(
                Article.builder()
                        .title("test2")
                        .summery("test2")
                        .id(2L)
                        .build()
        );
        articles.add(
                Article.builder()
                        .title("test3")
                        .summery("test3")
                        .id(3L)
                        .build()
        );

        return articles;
    }

    @PostMapping("/post")
    public Article post(@RequestBody final Article req) {
        return req;
    }

    @PostMapping("/post_list")
    public List<Article> postArticleList(@RequestBody final Article req) {
        List<Article> articles = new ArrayList<>();
        articles.add(
                Article.builder()
                        .title("post test1")
                        .summery("post test1")
                        .id(1L)
                        .build()
        );
        articles.add(
                Article.builder()
                        .title("post test2")
                        .summery("post test2")
                        .id(2L)
                        .build()
        );
        articles.add(
                Article.builder()
                        .title("post test3")
                        .summery("post test3")
                        .id(3L)
                        .build()
        );
        articles.add(
                Article.builder()
                        .title(req.getTitle())
                        .summery(req.getSummery())
                        .id(req.getId())
                        .build()
        );

        return articles;
    }

    @PostMapping("/post_error")
    public List<Article> postArticleListThrowError() {
        throw new WebClientResponseException("test", 500, "test", null, null, null);
    }

    @PostMapping("/post_slow")
    public List<Article> postArticleListSlow(@RequestBody final Article req) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Article> articles = new ArrayList<>();
        articles.add(
                Article.builder()
                        .title("post test1")
                        .summery("post test1")
                        .id(1L)
                        .build()
        );
        articles.add(
                Article.builder()
                        .title("post test2")
                        .summery("post test2")
                        .id(2L)
                        .build()
        );
        articles.add(
                Article.builder()
                        .title("post test3")
                        .summery("post test3")
                        .id(3L)
                        .build()
        );

        return articles;
    }

    @PutMapping("/put")
    public Article putArticle(@RequestBody final Article req) {
        return req;
    }

    @DeleteMapping("/delete")
    public String deleteArticle() {
        return "delete success";
    }

    @PostMapping("/game_play_log")
    public PapiRes<PapiResultMessage> setGamePlayLog(@RequestBody final PapiGamePlayLog req) {
        System.out.println(req.toString());

        return PapiRes.<PapiResultMessage>builder()
                .code("OK")
                .message("")
                .data(
                        PapiResultMessage.builder()
                                .result(true)
                                .message("게임 실행 로그가 저장되었습니다.")
                                .build()
                )
                .build();
    }

}
