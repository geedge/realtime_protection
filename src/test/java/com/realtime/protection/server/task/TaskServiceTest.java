package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

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

        LocalDateTime taskStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusDays(5);

        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("阻断");
        task.setTaskType(1);
        task.setStaticRuleIds(List.of(1L, 2L));
        task.setDynamicRuleIds(List.of());
        task.setTaskCreateUserId(1L);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");
    }

    @Test
    void testNewTaskSuccess() {
        for (int i = 0; i < 100; i++){
            LocalDateTime taskStartTime = LocalDateTime.now().plusDays(i);
            LocalDateTime taskEndTime = LocalDateTime.now().plusDays(i+10);
            task.setTaskStartTime(taskStartTime);
            task.setTaskEndTime(taskEndTime);
            assertDoesNotThrow(() -> {Long taskId = taskService.newTask(task); assertTrue(taskId > 0);});
            assertTrue(task.getTaskId() > 0);
        }
    }

    @Test
    void testNewTaskLostData() {
        this.task.setTaskStartTime(null);
        assertThrows(DataIntegrityViolationException.class, () -> {
            Long taskId = taskService.newTask(task);
            assertTrue(taskId > 0);
        });
    }

    @Test
    void testQueryTasks() {
        List<Task> tasks = taskService.queryTasks(null, null, null, null, 1, 10);
        assertTrue(tasks.get(0).getTaskId() > 0);
    }

    @Test
    void testUpdateTasks() {
        Task originalTask = taskService.queryTask(38L);

        originalTask.setStaticRuleIds(List.of(16L, 17L, 18L, 19L));
        originalTask.setTaskName("修改测试");

        assertTrue(taskService.updateTask(originalTask));
        assertEquals("修改测试", taskService.queryTask(38L).getTaskName());
    }

    @Test
    void testDeleteTask() {
        long  testNum = taskService.queryTasks(null, null, null, null, 1, 10)
                .get(0).getTaskId();

        assertTrue(taskService.deleteTask(testNum));
        assertFalse(taskService.deleteTask(235235L));
    }

    @Test
    void testChangeAuditStatus() {
        long testNum = taskService.queryTasks(null, null, null, null, 1, 1)
                .get(0).getTaskId();

        assertTrue(taskService.changeTaskAuditStatus(testNum, 2));
        assertFalse(taskService.changeTaskAuditStatus(testNum, 0));
        assertFalse(taskService.changeTaskAuditStatus(testNum, 1));
    }

    @Test
    void testGetStaticCommands() {
        List<TaskCommandInfo> taskCommandInfos = taskService.getStaticCommandInfos(38L);
        assertNotNull(taskCommandInfos);
    }
}