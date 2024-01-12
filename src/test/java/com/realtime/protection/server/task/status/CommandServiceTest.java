package com.realtime.protection.server.task.status;

import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.server.command.CommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CommandServiceTest {

    private final CommandService commandService;
    private TaskCommandInfo taskCommandInfo;

    private Long startTime;

    @Autowired
    CommandServiceTest(CommandService commandService) {
        this.commandService = commandService;
    }

    @BeforeEach
    void mockCommand() {
        FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
        fiveTupleWithMask.setMaskSourceIP("192.168.155.24");

        taskCommandInfo = new TaskCommandInfo();
        taskCommandInfo.setFrequency(30);
        taskCommandInfo.setTaskId(30L);
        taskCommandInfo.setFiveTupleWithMask(fiveTupleWithMask);
        taskCommandInfo.setOperation("阻断");
        taskCommandInfo.setEndTime(LocalDateTime.now().plusDays(1));

        startTime = System.currentTimeMillis();
    }

    @AfterEach
    void summary() {
        Long endTime = System.currentTimeMillis();
        System.out.printf("total time: %d ms", endTime - startTime);
    }

    @Test
    void createCommand() {
        assertDoesNotThrow(() -> commandService.createCommand(taskCommandInfo));
    }

    @Test
    void createCommands() {
        List<TaskCommandInfo> taskCommandInfos = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int port = i + 1000;
            taskCommandInfo = new TaskCommandInfo();
            taskCommandInfo.setFiveTupleWithMask(new FiveTupleWithMask());
            taskCommandInfo.setTaskId(24L);
            taskCommandInfo.getFiveTupleWithMask().setSourcePort(Integer.toString(port));
            taskCommandInfo.setStartTime(LocalDateTime.now().plusDays(5));
            taskCommandInfo.setEndTime(LocalDateTime.now().plusDays(10));
            taskCommandInfo.setFrequency(20);

            taskCommandInfos.add(taskCommandInfo);
        }

        assertDoesNotThrow(() -> commandService.createCommands(taskCommandInfos));

    }
}