package com.example.testwc.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Article {
    private String title;
    private String summery;
    private Long id;

    public Article(String title, String body, Long id) {
        this.title = title;
        this.summery = body;
        this.id = id;
    }

    public Article() {
    }
}
