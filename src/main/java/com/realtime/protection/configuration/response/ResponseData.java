package com.realtime.protection.configuration.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "xxx")
public class ResponseData extends HashMap<String, Object> {

    public Object put(String key, Object value, String description) {
        return super.put(key, value);
    }
}
