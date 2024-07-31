package com.realtime.protection.configuration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.ProtectionApplication;
import com.realtime.protection.configuration.entity.user.User;
import com.realtime.protection.configuration.entity.user.UserFull;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改人： Fulian Li
 * 功能：执行日志审计
 **/

@RestControllerAdvice(basePackageClasses = {ProtectionApplication.class})
@Slf4j
@ControllerAdvice
public class AuditAdvice implements ResponseBodyAdvice<ResponseResult> {

    public static final Map<String, String> URL_TAGS_MAP = new HashMap<String, String>() {{
        put("/api/v1/whiteobj/[^/]+/update", "白名单API");
        put("/api/v1/whiteobj/upload", "白名单API");
        put("/api/v1/whiteobj/new", "白名单API");
        put("/api/v1/whiteobj/auditbatch", "白名单API");
        put("/api/v1/whiteobj/auditInfo/[^/]+", "白名单API");
        put("/api/v1/user/doLogin", "login-controller");
        put("/api/v1/user/auth", "login-controller");
        put("/api/v1/templateold/[^/]+/update", "old防御策略模板API");
        put("/api/v1/templateold/query/templateId", "old防御策略模板API");
        put("/api/v1/templateold/new", "old防御策略模板API");
        put("/api/v1/templateold/auditbatch", "old防御策略模板API");
        put("/api/v1/templateold/auditInfo/[^/]+", "old防御策略模板API");
        put("/api/v1/template/[^/]+/update", "策略模板API");
        put("/api/v1/template/new", "策略模板API");
        put("/api/v1/template/auditbatch", "策略模板API");
        put("/api/v1/template/auditInfo/[^/]+", "策略模板API");
        put("/api/v1/task/[^/]+/update", "任务控制器API");
        put("/api/v1/task/send-pcap", "任务控制器API");
        put("/api/v1/task/new", "任务控制器API");
        put("/api/v1/task/auditbatch", "任务控制器API");
        put("/api/v1/task/auditInfo/[^/]+", "任务控制器API");
        put("/api/v1/task/api/new", "任务控制器API");
        put("/api/v1/staticrule/[^/]+/update", "静态规则API");
        put("/api/v1/staticrule/upload", "静态规则API");
        put("/api/v1/staticrule/new", "静态规则API");
        put("/api/v1/staticrule/auditbatch", "静态规则API");
        put("/api/v1/staticrule/auditInfo/[^/]+", "静态规则API");
        put("/api/v1/proobj/[^/]+/update", "防护对象API");
        put("/api/v1/proobj/upload", "防护对象API");
        put("/api/v1/proobj/new", "防护对象API");
        put("/api/v1/proobj/auditbatch", "防护对象API");
        put("/api/v1/proobj/auditInfo/[^/]+", "防护对象API");
        put("/api/v1/dynamicrule/[^/]+/update", "动态规则API");
        put("/api/v1/dynamicrule/new", "动态规则API");
        put("/api/v1/dynamicrule/auditbatch", "动态规则API");
        put("/api/v1/dynamicrule/auditInfo/[^/]+", "动态规则API");
        put("/api/v1/alertmessage/new", "alert-message-controller");
        put("/api/v1/alertmessage/auditInfo2/[^/]+", "alert-message-controller");
        put("/api/v1/alertmessage/auditInfo/[^/]+", "alert-message-controller");
        put("/api/v1/whiteobj/[^/]+/query", "白名单API");
        put("/api/v1/whiteobj/[^/]+/history", "白名单API");
        put("/api/v1/whiteobj/[^/]+/audit/[^/]+", "白名单API");
        put("/api/v1/whiteobj/statistics", "白名单API");
        put("/api/v1/whiteobj/staticrule/check/[^/]+", "白名单API");
        put("/api/v1/whiteobj/query", "白名单API");
        put("/api/v1/whiteobj/push", "白名单API");
        put("/api/v1/whiteobj/download", "白名单API");
        put("/api/v1/user/auth_redirect", "login-controller");
        put("/api/v1/templateold/[^/]+/query", "old防御策略模板API");
        put("/api/v1/templateold/[^/]+/audit/[^/]+", "old防御策略模板API");
        put("/api/v1/templateold/statistics", "old防御策略模板API");
        put("/api/v1/templateold/query", "old防御策略模板API");
        put("/api/v1/templateold/query/source_system", "old防御策略模板API");
        put("/api/v1/templateold/query/level", "old防御策略模板API");
        put("/api/v1/templateold/query/event_name/[^/]+", "old防御策略模板API");
        put("/api/v1/template/[^/]+/query", "策略模板API");
        put("/api/v1/template/[^/]+/history", "策略模板API");
        put("/api/v1/template/[^/]+/audit/[^/]+", "策略模板API");
        put("/api/v1/template/statistics", "策略模板API");
        put("/api/v1/template/query", "策略模板API");
        put("/api/v1/template/query/source_system", "策略模板API");
        put("/api/v1/task/[^/]+/running/[^/]+", "任务控制器API");
        put("/api/v1/task/[^/]+/commands", "任务控制器API");
        put("/api/v1/task/[^/]+/audit/[^/]+", "任务控制器API");
        put("/api/v1/task/[^/]+/query", "任务控制器API");
        put("/api/v1/task/[^/]+/history", "任务控制器API");
        put("/api/v1/task/[^/]+/valid/[^/]+", "任务控制器API");
        put("/api/v1/task/unaudit/statistics", "任务控制器API");
        put("/api/v1/task/statistics", "任务控制器API");
        put("/api/v1/task/result/push", "任务控制器API");
        put("/api/v1/task/query", "任务控制器API");
        put("/api/v1/task/auditinfo/alert/[^/]+", "任务控制器API");
        put("/api/v1/staticrule/[^/]+/query", "静态规则API");
        put("/api/v1/staticrule/[^/]+/history", "静态规则API");
        put("/api/v1/staticrule/[^/]+/audit/[^/]+", "静态规则API");
        put("/api/v1/staticrule/statistics", "静态规则API");
        put("/api/v1/staticrule/query", "静态规则API");
        put("/api/v1/staticrule/query/[^/]+", "静态规则API");
        put("/api/v1/staticrule/download", "静态规则API");
        put("/api/v1/proobj/[^/]+/query", "防护对象API");
        put("/api/v1/proobj/[^/]+/audit/[^/]+", "防护对象API");
        put("/api/v1/proobj/[^/]+/querybatch", "防护对象API");
        put("/api/v1/proobj/[^/]+/history", "防护对象API");
        put("/api/v1/proobj/statistics", "防护对象API");
        put("/api/v1/proobj/query", "防护对象API");
        put("/api/v1/proobj/download", "防护对象API");
        put("/api/v1/nodeTree/get", "node-tree-controller");
        put("/api/v1/dynamicrule/[^/]+/query", "动态规则API");
        put("/api/v1/dynamicrule/[^/]+/history", "动态规则API");
        put("/api/v1/dynamicrule/[^/]+/audit/[^/]+", "动态规则API");
        put("/api/v1/dynamicrule/statistics", "动态规则API");
        put("/api/v1/dynamicrule/query", "动态规则API");
        put("/api/v1/dynamicrule/query/[^/]+", "动态规则API");
        put("/api/v1/dict/type/[^/]+", "字典表API");
        put("/api/v1/alertmessage/[^/]+/alarms", "alert-message-controller");
        put("/api/v1/whiteobj/[^/]+/delete", "白名单API");
        put("/api/v1/whiteobj/[^/]+", "白名单API");
        put("/api/v1/templateold/[^/]+/delete", "old防御策略模板API");
        put("/api/v1/template/[^/]+/delete", "策略模板API");
        put("/api/v1/task/[^/]+/delete", "任务控制器API");
        put("/api/v1/staticrule/[^/]+/delete", "静态规则API");
        put("/api/v1/staticrule/[^/]+", "静态规则API");
        put("/api/v1/proobj/[^/]+/delete", "防护对象API");
        put("/api/v1/proobj/delete/[^/]+", "防护对象API");
        put("/api/v1/dynamicrule/[^/]+/delete", "动态规则API");
        put("/api/v1/dynamicrule/[^/]+", "动态规则API");
        put("/api/v1/user/auth", "用户认证");
        
    }};

    public static final Map<String, String> URL_SUMMARY_MAP = new HashMap<String, String>() {{
        put("/api/v1/whiteobj/[^/]+/update", "修改白名单");
        put("/api/v1/whiteobj/upload", "批量导入白名单");
        put("/api/v1/whiteobj/new", "新增白名单");
        put("/api/v1/whiteobj/auditbatch", "批量更新审批状态");
        put("/api/v1/whiteobj/auditInfo/[^/]+", "查询审批意见");
        put("/api/v1/templateold/[^/]+/update", "更新防御策略模板信息");
        put("/api/v1/templateold/query/templateId", "查询策略模板id");
        put("/api/v1/templateold/new", "新建防御策略模板");
        put("/api/v1/templateold/auditbatch", "批量更新审批状态");
        put("/api/v1/templateold/auditInfo/[^/]+", "查询审批意见");
        put("/api/v1/template/[^/]+/update", "更新防御策略模板信息");
        put("/api/v1/template/new", "新建防御策略模板");
        put("/api/v1/template/auditbatch", "批量更新审批状态");
        put("/api/v1/template/auditInfo/[^/]+", "查询审批意见");
        put("/api/v1/task/[^/]+/update", "更新任务");
        put("/api/v1/task/send-pcap", "上传pcap文件");
        put("/api/v1/task/new", "添加任务");
        put("/api/v1/task/auditbatch", "批量更新任务审批状态");
        put("/api/v1/task/auditInfo/[^/]+", "查询审批意见");
        put("/api/v1/task/api/new", "任务推送外部API");
        put("/api/v1/staticrule/[^/]+/update", "修改静态规则");
        put("/api/v1/staticrule/upload", "批量导入静态规则");
        put("/api/v1/staticrule/new", "新增静态规则");
        put("/api/v1/staticrule/auditbatch", "批量更新静态规则审批状态");
        put("/api/v1/staticrule/auditInfo/[^/]+", "查询审批意见");
        put("/api/v1/proobj/[^/]+/update", "更新防护对象");
        put("/api/v1/proobj/upload", "批量上传防护对象");
        put("/api/v1/proobj/new", "新建防护对象");
        put("/api/v1/proobj/auditbatch", "批量更新审批状态");
        put("/api/v1/proobj/auditInfo/[^/]+", "查询审批意见");
        put("/api/v1/dynamicrule/[^/]+/update", "修改动态规则");
        put("/api/v1/dynamicrule/new", "新建动态规则");
        put("/api/v1/dynamicrule/auditbatch", "批量更新审批状态");
        put("/api/v1/dynamicrule/auditInfo/[^/]+", "查询审批意见");
        put("/api/v1/whiteobj/[^/]+/query", "查询单个白名单");
        put("/api/v1/whiteobj/[^/]+/history", "查询历史变化");
        put("/api/v1/whiteobj/[^/]+/audit/[^/]+", "修改白名单审核状态");
        put("/api/v1/whiteobj/statistics", "白名单数据统计");
        put("/api/v1/whiteobj/staticrule/check/[^/]+", "查询静态规则命中白名单");
        put("/api/v1/whiteobj/query", "查询白名单");
        put("/api/v1/whiteobj/push", "同步白名单接口");
        put("/api/v1/whiteobj/download", "下载白名单模板");
        put("/api/v1/templateold/[^/]+/query", "查询单个防御策略模板");
        put("/api/v1/templateold/statistics", "数据统计");
        put("/api/v1/templateold/query", "查询多个防御策略模板");
        put("/api/v1/templateold/query/source_system", "查询来源系统名称");
        put("/api/v1/templateold/query/level", "查询所有策略模板是否含有不同类型防护等级");
        put("/api/v1/templateold/query/event_name/[^/]+", "查询事件类型");
        put("/api/v1/template/[^/]+/query", "查询单个防御策略模板");
        put("/api/v1/template/[^/]+/history", "查询历史变化");
        put("/api/v1/template/[^/]+/audit/[^/]+", "按id修改审计状态");
        put("/api/v1/template/statistics", "数据统计");
        put("/api/v1/template/query", "分頁查询策略模板");
        put("/api/v1/template/query/source_system", "查询来源系统名称");
        put("/api/v1/task/[^/]+/running/[^/]+", "修改任务运行状态");
        put("/api/v1/task/[^/]+/commands", "获得任务已推送指令的相关数据");
        put("/api/v1/task/[^/]+/audit/[^/]+", "任务审核状态修改");
        put("/api/v1/task/[^/]+/query", "查询单个任务");
        put("/api/v1/task/[^/]+/history", "查询历史变化");
        put("/api/v1/task/[^/]+/valid/[^/]+", "下发/取消指令下发");
        put("/api/v1/task/unaudit/statistics", "查询规则、任务、配置的未审核数量");
        put("/api/v1/task/statistics", "数据统计");
        put("/api/v1/task/result/push", "处置任务结果推送接口");
        put("/api/v1/task/query", "查询任务");
        put("/api/v1/task/auditinfo/alert/[^/]+", "查询用户下未通知的任务数量");
        put("/api/v1/staticrule/[^/]+/query", "查询单个静态规则");
        put("/api/v1/staticrule/[^/]+/history", "查询历史变化");
        put("/api/v1/staticrule/[^/]+/audit/[^/]+", "更新静态规则审批状态");
        put("/api/v1/staticrule/statistics", "数据统计");
        put("/api/v1/staticrule/query", "分页查询静态规则");
        put("/api/v1/staticrule/query/[^/]+", "批量查询静态规则");
        put("/api/v1/staticrule/download", "下载静态规则模板");
        put("/api/v1/proobj/[^/]+/query", "查询单个防护对象");
        put("/api/v1/proobj/[^/]+/audit/[^/]+", "修改防护对象审核状态");
        put("/api/v1/proobj/[^/]+/querybatch", "批量查询多个防护对象");
        put("/api/v1/proobj/[^/]+/history", "查询历史变化");
        put("/api/v1/proobj/statistics", "数据统计");
        put("/api/v1/proobj/query", "根据条件查询多个防护对象");
        put("/api/v1/proobj/download", "下载模板文件");
        put("/api/v1/dynamicrule/[^/]+/query", "查询单个动态规则");
        put("/api/v1/dynamicrule/[^/]+/history", "查询历史变化");
        put("/api/v1/dynamicrule/[^/]+/audit/[^/]+", "更新批状态");
        put("/api/v1/dynamicrule/statistics", "数据统计");
        put("/api/v1/dynamicrule/query", "根据条件查询多个动态规则");
        put("/api/v1/dynamicrule/query/[^/]+", "查询多个动态规则");
        put("/api/v1/dict/type/[^/]+", "查询字典表数据");
        put("/api/v1/whiteobj/[^/]+/delete", "删除白名单");
        put("/api/v1/whiteobj/[^/]+", "批量删除白名单");
        put("/api/v1/templateold/[^/]+/delete", "删除防御策略模板信息");
        put("/api/v1/template/[^/]+/delete", "删除防御策略模板信息");
        put("/api/v1/task/[^/]+/delete", "删除单个任务");
        put("/api/v1/staticrule/[^/]+/delete", "按id删除静态规则");
        put("/api/v1/staticrule/[^/]+", "删除静态规则");
        put("/api/v1/proobj/[^/]+/delete", "删除防护对象");
        put("/api/v1/proobj/delete/[^/]+", "批量删除防护对象");
        put("/api/v1/dynamicrule/[^/]+/delete", "删除动态规则");
        put("/api/v1/dynamicrule/[^/]+", "批量删除动态规则");
        put("/api/v1/user/auth", "用户身份认证，登陆判断，返回用户信息");

    }};

    private final WebClient webClient = WebClient
            .builder()
//            .baseUrl("http://39.105.210.156:8090/chanct-log/audit-xgs")
            .baseUrl("http://10.58.44.241:1888/api/chanct-log/audit-xgs")
//            .baseUrl("http://10.58.44.241:1888/magic-api/audit/save")
            .build();

    @Data
    @AllArgsConstructor
    private static class AuditRes{
        private AuditData auditBase;
    }
    @Data
    @AllArgsConstructor
    private static class AuditData {
        private String userId;
        private String deptId;
        private String userName;
        private String deptName;
        private String userIp;

        private String menu;
        private String action;
        private String res;
        private String content;

    }
    @Data
    @AllArgsConstructor
    private static class AuditDataNew {
        private String userName;
        private String userIp;
        private String sourceType;
        private String deptName;
        private String menu;
        private String content;
        private String originData;


    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        return false;
        return true;
    }

    @Override
    public ResponseResult beforeBodyWrite(ResponseResult body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
// 可以不发送query的请求，数据量太大
        if (request.getURI().getPath().contains("query")
        || request.getURI().getPath().contains("swagger")
        || request.getURI().getPath().contains("dict")
		|| request.getURI().getPath().contains("statistics")){
            return body;
        }

        AuditData auditData ;
        try {
            auditData = getAuditData(body, request);
        } catch (Exception e) {
            log.error("响应解析失败：{}", e.getMessage());
            return body;
        }

        AuditRes auditRes = new AuditRes(auditData);
        log.info("auditData-----------:"+auditRes.toString());

        Mono<String> mono = webClient
                .post()
                .uri("/save")
                .bodyValue(auditRes)
                .exchangeToMono(res -> {
                    if (res.statusCode().equals(HttpStatus.OK)) {
                        log.info("发送审计日志成功：{}",res.statusCode());
                        return res.bodyToMono(String.class);
                    }
                    log.info("发送审计日志失败：{}",res.statusCode());
                    return null;
                })
                .doOnError(WebClientRequestException.class, err ->
                        log.warn("审计服务器遭遇异常{}", err.getMessage()));

        mono.subscribe(AuditAdvice::handleMono);

        return body;
    }
    // 解析 X-Forwarded-For 头中的第一个 IP 地址
    private static String extractFirstIpAddress(String xForwardedForHeader) {
        if (xForwardedForHeader != null) {
            // 根据逗号分隔获取第一个 IP 地址
            String[] ips = xForwardedForHeader.trim().split("\\s*,\\s*");
            return ips[0];
        }
        return null;
    }
    // 解析menu和action
    private static String extractMenuAndAction(String xForwardedForHeader) {
        if (xForwardedForHeader != null) {
            // 根据逗号分隔获取第一个 IP 地址
            String[] ips = xForwardedForHeader.trim().split("\\s*,\\s*");
            return ips[0];
        }
        return null;
    }

    public static String getSummary(String url) {
        for (Map.Entry<String, String> entry : URL_SUMMARY_MAP.entrySet()) {
            if (url.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static String getTag(String url) {
        for (Map.Entry<String, String> entry : URL_TAGS_MAP.entrySet()) {
            if (url.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }


    private Map<String, List<String>> getQueryParams(ServerHttpRequest request) {
        URI uri = request.getURI();
        return UriComponentsBuilder.fromUri(uri).build().getQueryParams();
    }




    @NotNull
    private static AuditData getAuditData(ResponseResult body, ServerHttpRequest request) throws IOException {
        HttpSession session = ((ServletServerHttpRequest) request).getServletRequest().getSession();
        AuditData auditData;
        log.info(request.getHeaders().getFirst("X-Forwarded-For"));

        String requestBody = request.getBody().toString();
        if(session==null || session.getAttribute("user")==null){
            auditData = new AuditData(
                    "0000000","0000000","NSADD管理员","组织树",
					extractFirstIpAddress(request.getHeaders().getFirst("X-Forwarded-For")),
                    getTag(request.getURI().getPath()),
                    getSummary(request.getURI().getPath()),
                    body.toString(),
                    String.format("名称：%s, 操作：%s, 请求内容：%s, 是否成功：%s, 响应内容：%s",
                                    getTag(request.getURI().getPath()),
                                    getSummary(request.getURI().getPath()),
                                    request.getURI().getPath(),
                                    body.getCode()==200?"成功":"失败",
                                    body.toString())
            );
//            auditData = new AuditData(
//                    "NSADD管理员",extractFirstIpAddress(request.getHeaders().getFirst("X-Forwarded-For"))
//                    "xgs","组织树",
//                    getTag(request.getURI().getPath()),
//                    getSummary(request.getURI().getPath()),
//                    body.toString()+" "+request.getURI().getPath()
//            );
        }else {
            UserFull user = (UserFull) session.getAttribute("user");
            auditData = new AuditData(
                    user.uid, user.getOrgCode(),user.name,  user.getOrgName(),
                    extractFirstIpAddress(request.getHeaders().getFirst("X-Forwarded-For")),
                    getTag(request.getURI().getPath()),
                    getSummary(request.getURI().getPath()),
                    body.toString(),
                    String.format("名称：%s, 操作：%s, 请求内容：%s, 是否成功：%s, 响应内容：%s",
                                    getTag(request.getURI().getPath()),
                                    getSummary(request.getURI().getPath()),
                                    request.getURI().getPath(),
                                    body.getCode()==200?"成功":"失败",
                                    body.toString())
                    // body.getData().toString(),
            );
        }
        return auditData;
    }

    private static void handleMono(String result) {
        log.info("审计服务器返回结果：" + result);
    }
}

