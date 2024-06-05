package com.realtime.protection.configuration.response;

import com.realtime.protection.ProtectionApplication;
import com.realtime.protection.configuration.entity.user.User;
import com.realtime.protection.configuration.entity.user.UserFull;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import reactor.core.publisher.Mono;
/**
 * 修改人： Fulian Li
 * 功能：执行日志审计
 **/

@RestControllerAdvice(basePackageClasses = {ProtectionApplication.class})
@Slf4j
@ControllerAdvice
public class AuditAdvice implements ResponseBodyAdvice<ResponseResult> {

    private final WebClient webClient = WebClient
            .builder()
            .baseUrl("http://39.105.210.156:8090/chanct-log/audit-xgs")
            .build();

    @Data
    @AllArgsConstructor
    private static class AuditData {
        private String userId;
        private String deptId;
        private String userName;
        private String deptName;
        private String menu;
        private String action;
        private String res;
        private String content;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        return false;
        return true;
    }

    @Override

    public ResponseResult beforeBodyWrite(ResponseResult body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        AuditData auditData = getAuditData(body, request);
//        System.out.println("auditData-----------:"+auditData);

        Mono<String> mono = webClient
                .post()
                .uri("/save")
                .bodyValue(auditData)
                .exchangeToMono(res -> {
                    if (res.statusCode().equals(HttpStatus.OK)) {
                        return res.bodyToMono(String.class);
                    }
//                    log.info("发送审计日志成功：{}",res);
                    return null;
                })
                .doOnError(WebClientRequestException.class, err ->
                        log.warn("审计服务器遭遇异常{}", err.getMessage()));

        mono.subscribe(AuditAdvice::handleMono);

        return body;
    }

    @NotNull
    private static AuditData getAuditData(ResponseResult body, ServerHttpRequest request) {
        HttpSession session = ((ServletServerHttpRequest) request).getServletRequest().getSession();
        AuditData auditData;
        if(session!=null || session.getAttribute("user")==null){
            auditData = new AuditData(
                    "xxx","xxx","xxx","xxx",
                    request.getURI().getPath(),
                    request.getMethod().toString(),
                    body.getCode()==200?"成功":"失败",
                    body.getMessage()
            );
        }else {
            UserFull user = (UserFull) session.getAttribute("user");
            auditData = new AuditData(
                    user.uid, user.getOrgCode(),user.name,  user.getOrgName(),
                    request.getURI().getPath(),
                    request.getMethod().toString(),
                    body.getCode()==200?"成功":"失败",
                    body.getMessage()
            );
        }
        return auditData;
    }

    private static void handleMono(String result) {
        log.debug("审计服务器返回结果：" + result);
    }
}

