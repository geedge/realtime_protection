package com.realtime.protection.server.task.status;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommandServiceTest extends ProtectionApplicationTests {

    private final CommandService commandService;
    private final TaskService taskService;
    private TaskCommandInfo taskCommandInfo;

    private Long startTime;

    @Autowired
    CommandServiceTest(CommandService commandService, TaskService taskService) {
        this.commandService = commandService;
        this.taskService = taskService;
    }

    @BeforeEach
    void mockCommand() {
        Task task = taskService.queryTasks(null, null, null, null, null, null, null, null, null,null,null,null,null,1, 1).get(0);
        FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
        fiveTupleWithMask.setMaskSourceIP("192.168.155.24");

        taskCommandInfo = new TaskCommandInfo();
        taskCommandInfo.setFrequency(30);
        taskCommandInfo.setTaskId(task.getTaskId());
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
        assertDoesNotThrow(() -> assertNotNull(commandService.createCommand(taskCommandInfo)));
    }

    @Test
    void createCommands() {
        List<TaskCommandInfo> taskCommandInfos = ListUtils.newArrayListWithExpectedSize(100);
        Task task = taskService.queryTasks(null, null, null, null, null, null, null, null, null,null,null,null,null,1, 1).get(0);
        for (int i = 0; i < 100; i++) {
            int port = i + 1000;
            TaskCommandInfo taskCommandInfo = new TaskCommandInfo();
            taskCommandInfo.setFiveTupleWithMask(new FiveTupleWithMask());
            taskCommandInfo.setTaskId(task.getTaskId());
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
    void queryCommandInfos() {
        Task task = taskService.queryTasks(null, null, null, null, null, null, null, null, null,null,null,null,null,1, 1).get(0);
        List<TaskCommandInfo> taskCommandInfos = commandService.queryCommandInfos(task.getTaskId(),
                null, null, null, null,1, 5);
        assertTrue(taskCommandInfos != null && !taskCommandInfos.isEmpty());

        for (TaskCommandInfo taskCommandInfo : taskCommandInfos) {
            assertNotNull(commandService.queryCommandInfoByUUID(taskCommandInfo.getUUID()));
        }
    }

    @Test
    void queryCommandByUUID() {
        taskCommandInfo.setTaskId(new Random().nextLong());
        String uuid = commandService.createCommand(taskCommandInfo);
        TaskCommandInfo taskCommandInfo = commandService.queryCommandInfoByUUID(uuid);
        assertNotNull(taskCommandInfo);
        assertNotNull(taskCommandInfo.getUUID());
        assertEquals(uuid, taskCommandInfo.getUUID());
    }
}