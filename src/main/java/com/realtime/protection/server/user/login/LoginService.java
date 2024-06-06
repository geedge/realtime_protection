package com.realtime.protection.server.user.login;

import com.realtime.protection.configuration.utils.OkHttpUtil;
import org.apache.logging.log4j.util.Strings;

import javax.security.auth.login.LoginException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime.protection.configuration.entity.user.AccessTokenResponse;
import com.realtime.protection.configuration.entity.user.User;
import com.realtime.protection.configuration.entity.user.UserFull;

import cn.dev33.satoken.stp.StpUtil;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import io.micrometer.common.util.StringUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Service
// just for example, not for production environment
public class LoginService {

    private final LoginMapper loginMapper;

    public LoginService(LoginMapper loginMapper) {
        this.loginMapper = loginMapper;
    }

    public Integer login(User user) throws LoginException {
        String username = user.getUsername();
        String password = user.getPassword();

        Integer userId = loginMapper.login(username, password);
        if (userId == null) {
            throw new LoginException();
        }

        StpUtil.login(userId);
        return userId;
    }

    public UserFull loginWithSSO(String sessionData) throws LoginException, NoSuchAlgorithmException, KeyManagementException {
        String accessToken = "";
        // 获取 ACCESS_TOKEN
        ObjectMapper objectMapper = new ObjectMapper();

 //       OkHttpClient client = new OkHttpClient();

        //不做证书验证的OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(),OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                .build();

        Request request = new Request.Builder()
//            .url("https://114.243.134.122:10217/passport/accessToken?grant_type=client_credentials")
//                .url("https://passport.iam.pub/passport/accessToken?grant_type=client_credentials")
                .url("http://10.60.15.14:8080/passport/accessToken?grant_type=client_credentials")
//            .header("Authorization", "Basic TlNBRERAWlguT1JHOk14a1hHZ1ltOUNROUE3TCRSOCNLRW02R1pSeEhwd1c2")
//                .header("Authorization", "Basic TlNBREQ6bkhRbE5zN3lLeVd5bzJOc2JmNk5oRmFhYmlWWUlBU1NsdWJRZ3pUaDhOU2xOUkE1V2xQUTF3PT0=")
                .header("Authorization", "Basic TlNBRERAWlguT1JHOm5IUWxOczd5S3lXeW8yTnNiZjZOaEZhYWJpVllJQVNTbHViUWd6VGg4TlNsTlJBNVdsUFExdz09")
                .post(okhttp3.internal.Util.EMPTY_REQUEST)
                .build();
        try  {
            Response response = client.newCall(request).execute();
            String rsp = response.body().string();
            System.out.println("rsp:" + rsp);
            try {
                AccessTokenResponse atr = objectMapper.readValue(rsp, AccessTokenResponse.class);
                accessToken = atr.getAccess_token();
            } catch (Exception e) {
                throw new LoginException("解析 ACCESS_TOKEN 失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LoginException("获取 ACCESS_TOKEN 失败，网络请求错误");
        }
        if (Strings.isBlank(accessToken)) {
            throw new LoginException("获取 ACCESS_TOKEN 失败");
        }
        // 校验 SESSION_DATA
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("sessionData", sessionData).build();
        request = new Request.Builder()
//            .url("https://passport.zx.com:10217/passport/accessApplication")
//            .url("https://114.243.134.122:10217/passport/accessApplication")
//            .url("https://passport.iam.pub/passport/accessApplication")
            .url("http://10.60.15.14:8080/passport/accessApplication")
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .post(body)
            .build();
        try {
            Response response = client.newCall(request).execute();
            String rsp = response.body().string();
            // System.out.println("user: " + rsp);
            if (StringUtils.isBlank(rsp)) {
                throw new LoginException("解析用户数据为空");
            }
            try {
                UserFull userFull = objectMapper.readValue(rsp, UserFull.class);
                return userFull;
            } catch (Exception e) {
                e.printStackTrace();
                throw new LoginException("解析 ACCESS_TOKEN 失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LoginException("校验 SESSION_DATA 失败");
        }
    }



}
