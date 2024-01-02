package com.realtime.protection.configuration.exception;

import com.realtime.protection.configuration.response.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseResult handleGlobalException(Exception e) {




        return ResponseResult.error().setMessage(e.getMessage());
    }
}
