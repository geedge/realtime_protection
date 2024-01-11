package com.realtime.protection.configuration.utils.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum TaskTypeEnum {
    STATIC(1),
    DYNAMIC(2),
    JUDGED(3);

    private final int taskType;
    private static final Map<Integer, TaskTypeEnum> map = new HashMap<>();

    static {
        for (TaskTypeEnum taskType : TaskTypeEnum.values()) {
            map.put(taskType.getTaskType(), taskType);
        }
    }

    TaskTypeEnum(int taskType) {
        this.taskType = taskType;
    }

    public static TaskTypeEnum getTaskTypeByNum(Integer taskType) {
        if (taskType == null) {
            return null;
        }
        return map.get(taskType);
    }
}
