package com.realtime.protection.server.task.status;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.server.command.CommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommandServiceTest extends ProtectionApplicationTests {

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
        taskCommandInfo.setTaskAct("阻断");
        taskCommandInfo.setStartTime(LocalDateTime.now().plusDays(10));
        taskCommandInfo.setEndTime(LocalDateTime.now().plusDays(140));
        taskCommandInfo.setFrequency(30);

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
        assertNotNull(taskCommandInfo.getUUID());
    }

    @Test
    void createCommands() {
        List<TaskCommandInfo> taskCommandInfos = ListUtils.newArrayListWithExpectedSize(100);
        for (int i = 0; i < 100; i++) {
            int port = i + 1000;
            TaskCommandInfo taskCommandInfo = new TaskCommandInfo();
            taskCommandInfo.setFiveTupleWithMask(new FiveTupleWithMask());
            taskCommandInfo.setTaskId(30L);
            taskCommandInfo.setTaskAct("阻断");
            taskCommandInfo.getFiveTupleWithMask().setSourcePort(Integer.toString(port));
            taskCommandInfo.setStartTime(LocalDateTime.now().plusDays(5));
            taskCommandInfo.setEndTime(LocalDateTime.now().plusDays(10));
            taskCommandInfo.setFrequency(20);

            taskCommandInfos.add(taskCommandInfo);
        }

        for (TaskCommandInfo info : taskCommandInfos) {
            if (info.getFrequency() == null) {
                throw new IllegalArgumentException();
            }
        }

        assertDoesNotThrow(() -> commandService.createCommands(taskCommandInfos));
    }

    @Test
    void queryCommandByUUID() {
        List<TaskCommandInfo> taskCommandInfos = commandService.queryCommandInfoByTaskId(30L);
        assertTrue(taskCommandInfos != null && !taskCommandInfos.isEmpty());

        for (TaskCommandInfo taskCommandInfo : taskCommandInfos) {
            assertNotNull(commandService.queryCommandInfoByUUID(taskCommandInfo.getUUID()));
        }
    }
}