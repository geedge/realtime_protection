package com.realtime.protection.configuration.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NonEmptyFieldFetcher {
    public static List<Field> getNonEmptyFields(Object object) throws IllegalAccessException {
        List<Field> nonEmptyFields = new ArrayList<>();
        Class<?> clazz = object.getClass();

        // 获取类中所有的字段，包括继承的字段
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 打开字段的访问权限
            field.setAccessible(true);

            // 获取字段的值
            Object value = field.get(object);

            // 检查字段是否非空
            if (value != null) {
                nonEmptyFields.add(field);
            }
        }

        return nonEmptyFields;
    }
}