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
public class PapiGamePlayLog {
    private Long accountSrl;
    private String ssn;
    private String subSsn;
    private String ip;

    public PapiGamePlayLog(Long accountSrl, String ssn, String subSsn, String ip) {
        this.accountSrl = accountSrl;
        this.ssn = ssn;
        this.subSsn = subSsn;
        this.ip = ip;
    }

    public PapiGamePlayLog() {
    }
}
