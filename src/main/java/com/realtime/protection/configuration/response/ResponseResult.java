package com.realtime.protection.configuration.response;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;

@Data
public class ResponseResult implements Serializable {

    private int code;
    private String message;
    private LinkedHashMap<String, Object> data;

    public ResponseResult(int code, String message, LinkedHashMap<String, Object> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseResult(int code) {
        this.code = code;
        this.data = new LinkedHashMap<>();
    }

    public ResponseResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = new LinkedHashMap<>();
    }

    public static ResponseResult ok() {
        return new ResponseResult(200, "request succeed");
    }

    public static ResponseResult ok(String message) {
        return new ResponseResult(200, message);
    }

    public static ResponseResult error() {
        return new ResponseResult(500, "request failed");
    }

    public static ResponseResult invalid() {
        return new ResponseResult(400, "invalid request");
    }

    public static ResponseResult unAuthorized() {
        return new ResponseResult(401, "UnAuthorized User");
    }

    public static ResponseResult error(String message) {
        return new ResponseResult(500, message);
    }

    public ResponseResult setCode(int code) {
        this.code = code;
        return this;
    }

    public ResponseResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseResult setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ResponseResult setDataMap(LinkedHashMap<String, Object> data) {
        this.data = data;
        return this;
    }
}
