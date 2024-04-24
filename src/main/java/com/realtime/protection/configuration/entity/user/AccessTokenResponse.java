package com.realtime.protection.configuration.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixiang Zhao
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private int expires_in;
}
