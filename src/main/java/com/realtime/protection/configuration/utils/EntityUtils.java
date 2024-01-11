package com.realtime.protection.configuration.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityUtils {
    public static Map<String, Object> entityToMap(Object object) throws IllegalAccessException {
        Map<String, Object> resultMap = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object o = field.get(object);
            resultMap.put(field.getName(), o);
        }

        return resultMap;
    }
}
