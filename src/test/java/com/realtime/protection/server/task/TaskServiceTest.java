package com.realtime.protection.server.task;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleService;
import com.realtime.protection.server.rule.staticrule.StaticRuleService;
import com.realtime.protection.server.task.status.StateChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceTest extends ProtectionApplicationTests {
    private final TaskService taskService;
    private final StaticRuleService staticRuleService;
    private final DynamicRuleService dynamicRuleService;
    private final StateChangeService stateChangeService;
    private Task task;

    @Autowired
    TaskServiceTest(TaskService taskService, StaticRuleService staticRuleService, DynamicRuleService dynamicRuleService, StateChangeService stateChangeService) {
        this.taskService = taskService;
        this.staticRuleService = staticRuleService;
        this.dynamicRuleService = dynamicRuleService;
        this.stateChangeService = stateChangeService;
    }

    @BeforeEach
    public void taskInit() {
        this.task = new Task();
        task.setTaskName("静态测试");

        LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusYears(5);

        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("阻断");
        task.setTaskType(1);

        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");
    }

    @Test
    void testNewTaskSuccess() {
        for (int i = 1; i < 1000; i++) {
            List<StaticRuleObject> staticRuleObjects = staticRuleService.queryStaticRule(
                    null, null, null, null, i, 2);
            List<Integer> staticRuleIds = new ArrayList<>();
            staticRuleObjects.forEach(staticRuleObject ->
                    staticRuleIds.add(staticRuleObject.getStaticRuleId()));
            task.setStaticRuleIds(staticRuleIds);

            List<DynamicRuleObject> dynamicRuleObjects = dynamicRuleService.queryDynamicRuleObject(
                    null, null, null, null, i, 2
            );
            List<Integer> dynamicRuleIds = new ArrayList<>();
            dynamicRuleObjects.forEach(dynamicRuleObject ->
                    dynamicRuleIds.add(dynamicRuleObject.getDynamicRuleId()));
            task.setDynamicRuleIds(dynamicRuleIds);

            assertDoesNotThrow(() -> {
                Long taskId = taskService.newTask(task);
                assertTrue(taskId > 0);
            });
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

        originalTask.setStaticRuleIds(List.of(16, 17, 18, 19));
        originalTask.setTaskName("修改测试");

        assertTrue(taskService.updateTask(originalTask));
        assertEquals("修改测试", taskService.queryTask(38L).getTaskName());
    }

    @Test
    void testDeleteTask() {
        long testNum = taskService.queryTasks(null, null, null, null, 1, 10)
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