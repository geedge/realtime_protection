package com.realtime.protection.configuration.response;

import com.realtime.protection.ProtectionApplication;
import com.realtime.protection.configuration.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestControllerAdvice(basePackageClasses = {ProtectionApplication.class})
@Slf4j
public class AuditAdvice implements ResponseBodyAdvice<ResponseResult> {

    private final WebClient webClient = WebClient
            .builder()
            .baseUrl("http://39.105.210.156:8090/chanct-log/audit-xgs")
            .build();

    @Data
    @AllArgsConstructor
    private static class AuditData {
        private Long userId;
        private Long deptId;
        private String userName;
        private String deptName;
        private String menu;
        private String action;
        private String res;
        private String content;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return false;
    }

    @Override
    public ResponseResult beforeBodyWrite(ResponseResult body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        User user = getUserData(request);

        AuditData auditData = new AuditData(
                user.getUserId(), user.getDeptId(), user.getUsername(), user.getUserDepart(),
                "",
                Objects.requireNonNull(returnType.getMethod()).getName(),
                success(body),
                body.getMessage()
        );


        Mono<String> mono = webClient
                .post()
                .uri("/save")
                .bodyValue(auditData)
                .exchangeToMono(res -> {
                    if (res.statusCode().equals(HttpStatus.OK)) {
                        return res.bodyToMono(String.class);
                    }

                    return null;
                })
                .doOnError(WebClientRequestException.class, err ->
                        log.warn("审计服务器遭遇异常" + err.getMessage()));

        mono.subscribe(AuditAdvice::handleMono);

        return body;
    }

    private User getUserData(ServerHttpRequest request) {
        return new User(1L, 1L, "xxx", "", "xxx");
    }

    private String success(ResponseResult body) {
        return Boolean.toString(body.getCode() == 200);
    }

    private static void handleMono(String result) {
        log.debug("审计服务器返回结果：" + result);
    }
}

