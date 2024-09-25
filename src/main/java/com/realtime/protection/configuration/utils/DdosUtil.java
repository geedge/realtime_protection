package com.realtime.protection.configuration.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 向ddos系统推送任务信息和指令状态信息
 */
@Slf4j
public class DdosUtil {

    // 请求权限系统
    private String getToken1() throws NoSuchAlgorithmException, KeyManagementException {
        //不做证书验证的OkHttpClient
        String access_token = null;
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                .callTimeout(3000, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("https://passport.iam.pub/passport/accessToken?grant_type=client_credentials")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic TlNBRERAWlguT1JHOm5IUWxOczd5S3lXeW8yTnNiZjZOaEZhYWJpVllJQVNTbHViUWd6VGg4TlNsTlJBNVdsUFExdz09") //中心环境
                .post(okhttp3.internal.Util.EMPTY_REQUEST)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String rsp = response.body().string();
            System.out.println("权限系统返回值1:" + rsp);
            JsonNode jsonNode = objectMapper.readTree(rsp);
            access_token = jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_token;
    }

    private String getToken2(String token) throws NoSuchAlgorithmException, KeyManagementException {
        //不做证书验证的OkHttpClient
        String access_token = null;
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                .callTimeout(3000, TimeUnit.SECONDS)
                .build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("grant_type", "client_credentials")
                .addFormDataPart("remote_app_id", "WA1C_DDOS@ZX.ORG")
                .build();
        Request request = new Request.Builder()
                .url("https://passport.iam.pub/passport/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + token) //中心环境
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String rsp = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(rsp);
            access_token = jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_token;
    }


    public void sendTaskStateInfo(Long taskId, Integer auditStatus, Integer stateNum) throws NoSuchAlgorithmException, KeyManagementException {
//        String token = getToken2(getToken1());
        String token = "";
        try {
            WebClient webClient = WebClient.create();
            // 构建 form-data 请求体
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("task_id", taskId);
            requestData.put("task_status", stateNum);
            requestData.put("task_audit_status", auditStatus);
            log.info("请求体：" + requestData);
            // 发送 POST 请求
            String block = webClient.post()
                    .uri("https://10.58.48.92:443/api/v1/center/scrubbingmonitorgroups/status")
//                    .uri("http://127.0.0.1:8082/yy/api/algo/task2")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "token " + token)
                    .bodyValue(requestData)
                    .retrieve()
                    .bodyToMono(String.class).block();
            log.info("返回值：" + block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCommandStateInfo(String commandId, Integer stateNum) throws NoSuchAlgorithmException, KeyManagementException {
        String token = getToken2(getToken1());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WebClient webClient = WebClient.create();
            // 构建 form-data 请求体
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("command_id", commandId);
            requestData.put("command_status", stateNum);
            log.info("请求体：" + requestData);
            // 发送 POST 请求
            String block = webClient.post()
                    .uri("http://10.58.48.92:443/api/v1/center/disposal/status")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "token " + token)
                    .bodyValue(requestData)
                    .retrieve()
                    .bodyToMono(String.class).block();
            log.info("返回值：" + block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
