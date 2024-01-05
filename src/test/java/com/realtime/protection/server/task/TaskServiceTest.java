package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceTest {
    private final TaskService taskService;
    private Task task;

    @Autowired
    TaskServiceTest(TaskService taskService) {
        this.taskService = taskService;
    }

    @BeforeEach
    public void taskInit() {
        this.task = new Task();
        task.setTaskName("静态测试");

        LocalDateTime taskStartTime = LocalDateTime.parse("2023-12-24T11:45:14");
        LocalDateTime taskEndTime = LocalDateTime.parse("2023-12-29T11:45:12");

        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("阻断");
        task.setTaskType("静态任务");
        task.setStaticRuleIds(List.of(1, 2));
        task.setDynamicRuleIds(List.of());
        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");
        task.setProtectObjectIds(List.of(1));
    }

    @Test
    void testNewTaskSuccess() {
        assertDoesNotThrow(() -> {Integer taskId = taskService.newTask(task); assertTrue(taskId > 0);});
    }

    @Test
    void testNewTaskLostData() {
        this.task.setTaskStartTime(null);
        assertThrows(PersistenceException.class, () -> {
            Integer taskId = taskService.newTask(task);
            assertTrue(taskId > 0);
        });
    }

    @Test
    void testQueryTasks() {
        List<Task> tasks = taskService.queryTasks(null, null, null, null, 1, 5);
        assertEquals(5, tasks.size());
        assertTrue(tasks.get(0).getTaskId() > 0);
    }
}