package com.realtime.protection.configuration.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.server.task.status.StateChangeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final StateChangeService stateChangeService;

    public GlobalExceptionHandler(StateChangeService stateChangeService) {
        this.stateChangeService = stateChangeService;
    }

    @Order(3)
    @ExceptionHandler(value = {Exception.class})
    public ResponseResult handleGlobalException(Exception e) {
        log.error("meets global exception: " + e.getMessage());
        return ResponseResult.error().setMessage(e.getMessage());
    }


    @Order(2)
    @ExceptionHandler(value = {PersistenceException.class})
    public ResponseResult handleSQLException(PersistenceException e) {
        log.error("meets database exception: " + e.getMessage());
        return ResponseResult.invalid().setMessage(
                "please check the integrity of the data. check if the json data exists in the database");
    }

    @Order(2)
    @ExceptionHandler(value = DuplicateKeyException.class)
    public ResponseResult handleDuplicateKeyException(DuplicateKeyException e) {
        log.debug("meets duplicate key exception: " + e.getMessage());
        return ResponseResult.invalid().setMessage("duplicate key in json data");
    }

    @Order(2)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult handleBindException(MethodArgumentNotValidException e) {
        log.debug("meets data bind exception: " + e.getMessage());
        return ResponseResult.invalid().setMessage(
                e.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining())
        );
    }

    @Order(2)
    @ExceptionHandler(value = {
            HandlerMethodValidationException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseResult handleHandlerMethodValidationException(Exception e) {
        log.debug("meets illegal argument exception: " + e.getMessage());
        return ResponseResult.invalid().setMessage(e.getMessage());
    }

    @Order(2)
    @ExceptionHandler(value = NotLoginException.class)
    public ResponseResult handleNotLoginException(NotLoginException e) {
        log.debug("meets not login exception, login type: " + e.getLoginType());
        return new ResponseResult(
                401,
                e.getMessage()
        );
    }

    @Order(2)
    @ExceptionHandler(value = SaTokenException.class)
    public ResponseResult handleSaTokenException(SaTokenException e) {
        log.debug("sa-token meets exception: " + e.getMessage());
        return ResponseResult.unAuthorized().setMessage(e.getMessage());
    }

    @Order(2)
    @ExceptionHandler(value = DorisStartException.class)
    public ResponseResult handleDorisStartException(DorisStartException e) {
        log.warn("doris database meets exception: " + e.getMessage());
        ResponseResult responseResult = ResponseResult.error()
                .setMessage("Doris command creation meets error: " + e.getMessage());

        try {
            stateChangeService.changeState(StateEnum.FAILED.getStateNum(), e.taskId);
        } catch (Exception another) {
            responseResult.setAnother(ResponseResult.error().setMessage(e.getMessage()));
        }

        log.error(responseResult.getMessage());
        return responseResult;
    }
}
