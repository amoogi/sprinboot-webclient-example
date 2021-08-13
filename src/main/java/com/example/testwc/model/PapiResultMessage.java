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
public class PapiResultMessage {
    Boolean result;
    String message;

    public PapiResultMessage(Boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public PapiResultMessage() {
    }
}
