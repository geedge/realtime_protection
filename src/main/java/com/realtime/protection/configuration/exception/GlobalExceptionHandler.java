package com.realtime.protection.configuration.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
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
                401,
                e.getMessage()
        );
    }

    @Order(2)
    @ExceptionHandler(value = PersistenceException.class)
    public ResponseResult handleSQLException(PersistenceException e) {
        return ResponseResult.invalid().setMessage(
                "please check the integrity of the data. check if the json data exists in the database");
    }

    @Order(2)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult handleBindException(MethodArgumentNotValidException e) {
        return ResponseResult.invalid().setMessage(
                e.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining())
        );
    }

    @Order(2)
    @ExceptionHandler(value = SaTokenException.class)
    public ResponseResult handleSaTokenException(SaTokenException e) {
        return ResponseResult.unAuthorized().setMessage(e.getMessage());
    }
}
