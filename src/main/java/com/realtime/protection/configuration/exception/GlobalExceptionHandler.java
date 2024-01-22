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

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
        log.error("遭遇全局异常：" + e.getMessage());
        return ResponseResult.error().setMessage(e.getMessage());
    }


    @Order(2)
    @ExceptionHandler(value = {
            PersistenceException.class,
            SQLException.class,
            SQLIntegrityConstraintViolationException.class
    })
    public ResponseResult handleSQLException(Exception e) {
        log.info("遭遇数据库异常：" + e.getMessage());
        return ResponseResult.invalid().setMessage(
                "请检查json字段的完整性，确保json字段按照文档中要求填写。");
    }

    @Order(2)
    @ExceptionHandler(value = DuplicateKeyException.class)
    public ResponseResult handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseResult.invalid().setMessage(
                "插入/更新失败，请检查当前插入字段和数据库中是否存在相同数据"
        );
    }

    @Order(2)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult handleBindException(MethodArgumentNotValidException e) {
        log.debug("遭遇数据绑定异常：" + e.getMessage());
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
        log.debug("遭遇非法参数异常：" + e.getMessage());
        return ResponseResult.invalid().setMessage(e.getMessage());
    }

    @Order(2)
    @ExceptionHandler(value = NotLoginException.class)
    public ResponseResult handleNotLoginException(NotLoginException e) {
        log.debug("遭遇Sa-Token登录异常，登录类型为：" + e.getLoginType());
        return new ResponseResult(
                401,
                e.getMessage()
        );
    }

    @Order(2)
    @ExceptionHandler(value = SaTokenException.class)
    public ResponseResult handleSaTokenException(SaTokenException e) {
        log.debug("Sa-token模块遭遇异常：" + e.getMessage());
        return ResponseResult.unAuthorized().setMessage(e.getMessage());
    }

    @Order(2)
    @ExceptionHandler(value = DorisStartException.class)
    public ResponseResult handleDorisStartException(DorisStartException e) {
        log.warn("Doris数据库遭遇异常：" + e.getMessage());
        ResponseResult responseResult = ResponseResult.error()
                .setMessage("Doris数据库指令生成遭遇异常：" + e.getMessage());

        try {
            // 内部修改状态，可以跳过一切状态检查
            stateChangeService.changeState(StateEnum.FAILED.getStateNum(), e.taskId, true);
        } catch (Exception another) {
            responseResult.setAnother(ResponseResult.error().setMessage(e.getMessage()));
        }

        log.error(responseResult.getMessage());
        return responseResult;
    }
}
