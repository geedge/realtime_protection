package com.realtime.protection.configuration.entity.task;

import com.realtime.protection.configuration.utils.enums.ProtocolEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Command {
    private FiveTupleWithMask fiveTupleWithMask;
    private Long taskId;

    private String operation;
    private LocalDateTime validTime;
    private LocalDateTime invalidTime;

    public static Command generateCommand(TaskCommandInfo info, LocalDateTime validTime) {
        Command command = new Command();

        FiveTupleWithMask fiveTupleWithMask = info.getFiveTupleWithMask();
        if (fiveTupleWithMask.getProtocol() != null)
            fiveTupleWithMask.setProtocolNum(ProtocolEnum.valueOf(fiveTupleWithMask.getProtocol()).getProtocolNumber());

        command.setFiveTupleWithMask(fiveTupleWithMask);
        command.setTaskId(info.getTaskId());
        command.setOperation(info.getOperation());
        command.setValidTime(validTime);
        command.setInvalidTime(info.getEndTime());

        return command;
    }
}
