package com.realtime.protection.configuration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SimpleResponse {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("success")
    private Boolean success;
}
