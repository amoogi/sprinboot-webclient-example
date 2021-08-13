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
public class PapiRes<T> {
    String code;
    String message;
    T data;

    public PapiRes(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public PapiRes() {
    }
}
