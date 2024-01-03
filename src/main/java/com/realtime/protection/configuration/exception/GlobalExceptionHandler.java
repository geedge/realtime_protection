package com.realtime.protection.configuration.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.realtime.protection.configuration.response.ResponseResult;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Order(3)
    @ExceptionHandler(value = Exception.class)
    public ResponseResult handleGlobalException(Exception e) {
        return ResponseResult.error().setMessage(e.getMessage());
    }

    @Order(2)
    @ExceptionHandler(value = NotLoginException.class)
    public ResponseResult handleNotLoginException(NotLoginException e) {
        return new ResponseResult(
                400,
                e.getMessage()
        );
    }

    @Order(2)
    @ExceptionHandler(value = PersistenceException.class)
    public ResponseResult handleSQLException() {
        return new ResponseResult(
                400,
                "please check the integrity of the data. check if the json data exists in the database");
    }

    @Order(2)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult handleBindException(MethodArgumentNotValidException e) {
        return new ResponseResult(
                400,
                e.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining())
        );
    }
}
