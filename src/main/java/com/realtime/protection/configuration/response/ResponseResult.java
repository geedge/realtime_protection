package com.realtime.protection.configuration.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Schema(description = "用于所有接口返回的通用返回对象")
public class ResponseResult implements Serializable {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "返回信息")
    private String message;

    @Schema(description = "封装数据")
    private Map<String, Object> data;

    @Schema(description = "返回对象链接的另外一个返回对象")
    private ResponseResult another;

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

    public static ResponseResult invalid(String message) {
        return new ResponseResult(400, message);
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

    public ResponseResult setDataMap(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
