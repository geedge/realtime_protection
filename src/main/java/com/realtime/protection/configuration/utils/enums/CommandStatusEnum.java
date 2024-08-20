package com.realtime.protection.configuration.utils.enums;

import com.realtime.protection.server.task.status.states.State;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
@Getter
public enum CommandStatusEnum {
    START(1),//指令生成(研判后任务需要研判，其他任务可直接下发)
    SEND(2),//（已研判）下发
    RCPHIT(3),//..
    FLOWHIT(4),//..
    UNRCPHIT(5),//..
    UNFLOWHIT(6),//..
    IGNORE(7),//本次忽略
    WHOLEIGNORE(8),//全部忽略
    SUCCESS(9),//下发成功
    FAIL(10),//下发失败
    STOP(11),
    PAUSE(12),
    CANCEL(13),//撤销
    END(14);//任务结束

    private final int commandStatusNum;
    private static final Map<Integer, CommandStatusEnum> map = new HashMap<>();

    static {
        for (CommandStatusEnum commandStatus : CommandStatusEnum.values()) {
            map.put(commandStatus.getCommandStatusNum(), commandStatus);
        }
    }

    CommandStatusEnum(int commandStatus) {
        this.commandStatusNum = commandStatus;
    }

    public static CommandStatusEnum getCommandStatusByNum(Integer commandStatus) {
        if (commandStatus == null) {
            return null;
        }
        return map.get(commandStatus);
    }

}

