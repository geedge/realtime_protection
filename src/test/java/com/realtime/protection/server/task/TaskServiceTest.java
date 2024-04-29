package com.realtime.protection.server.task;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.defense.object.ProtectObjectService;
import com.realtime.protection.server.defense.template.TemplateService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceTest extends ProtectionApplicationTests {
    private final TaskService taskService;
    private final StaticRuleService staticRuleService;
    private final DynamicRuleService dynamicRuleService;
    private final StateChangeService stateChangeService;
    private final CommandService commandService;
    private final ProtectObjectService protectObjectService;
    private final TemplateService templateService;
    private Task task;

    @Autowired
    TaskServiceTest(TaskService taskService, StaticRuleService staticRuleService, DynamicRuleService dynamicRuleService, StateChangeService stateChangeService, CommandService commandService, ProtectObjectService protectObjectService, TemplateService templateService) {
        this.taskService = taskService;
        this.staticRuleService = staticRuleService;
        this.dynamicRuleService = dynamicRuleService;
        this.stateChangeService = stateChangeService;
        this.commandService = commandService;
        this.protectObjectService = protectObjectService;
        this.templateService = templateService;
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
        for (int i = 1; i < 10; i++) {
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
        String testName = "test query";
        String testCreateName = "xxx query";

        for (int i = 0; i < 10; i++) {
            task.setTaskName(testName);
            task.setTaskCreateUsername(testCreateName);
            List<StaticRuleObject> staticRuleObjects = staticRuleService.queryStaticRule(
                    null, null, null, null, 1, 5);
            List<Integer> staticRuleIds = new ArrayList<>();
            staticRuleObjects.forEach(staticRuleObject ->
                    staticRuleIds.add(staticRuleObject.getStaticRuleId()));
            task.setStaticRuleIds(staticRuleIds);

            List<DynamicRuleObject> dynamicRuleObjects = dynamicRuleService.queryDynamicRuleObject(
                    null, null, null, null, 1, 5
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


        List<Task> tasks = taskService.queryTasks(null, null, null, null, null, 1, 10);
        assertEquals(10, tasks.size());

        tasks = taskService.queryTasks(0, null, null, null, null, 1, 10);
        assertEquals(10, tasks.size());
        tasks.forEach(task -> assertEquals(0, task.getTaskStatus()));

        tasks = taskService.queryTasks(null, 0, null, null, null, 1, 10);
        assertEquals(0, tasks.size());

        tasks = taskService.queryTasks(null, null, testName, null, null, 1, 10);
        assertEquals(10, tasks.size());
        tasks.forEach(task -> assertEquals(testName, task.getTaskName()));
    }

    @Test
    void testUpdateTasks() {
        Task originalTask = taskService.queryTasks(
                null, null, null, null, null, 1, 1)
                .get(0);

        List<StaticRuleObject> staticRuleObjects = staticRuleService.queryStaticRule(
                null, null, null, null, 1, 4
        );
        List<Integer> staticRuleIds = new ArrayList<>();
        staticRuleObjects.forEach(staticRuleObject -> staticRuleIds.add(staticRuleObject.getStaticRuleId()));
        originalTask.setStaticRuleIds(staticRuleIds);
        originalTask.setTaskName("修改测试");

        assertTrue(taskService.updateTask(originalTask));
        assertEquals("修改测试", taskService.queryTask(originalTask.getTaskId()).getTaskName());
    }

    @Test
    void testDeleteTask() {
        long testNum = taskService.queryTasks(null, null, null, null, null, 1, 10)
                .get(0).getTaskId();

        assertTrue(taskService.deleteTask(testNum));
        assertFalse(taskService.deleteTask(235156235L)); // 尝试一个不可能达到的数字
    }

    @Test
    void testChangeAuditStatus() {
        long testNum = taskService.queryTasks(null, null, null, null, null, 1, 1)
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


    @Test
    void testUpdateTaskAuditStatusBatch(){
        Map<Integer, Integer> map = new HashMap<>();
        map.put(43830, 1);
        map.put(43831, 1);
        map.put(43832, 1);


        System.out.println(taskService.updateAuditStatusBatch(map));
    }
    @Test
    void testGetDynamicTaskInfos(){
        List<DynamicTaskInfo> dynamicTaskInfos = taskService.getDynamicTaskInfos(43844L);
        System.out.println(dynamicTaskInfos);
    }

    @Test
    void changeTaskstatus() throws DorisStartException {
        stateChangeService.changeState(2, 43844L, false);

    }


    @Test
    void testStartStaticTask() throws DorisStartException {
        StaticRuleObject staticRuleTest = new StaticRuleObject();
        staticRuleTest.setStaticRuleName("mh-静态测试");
        staticRuleTest.setStaticRuleCreateUsername("mh");
        staticRuleTest.setStaticRuleCreateDepart("mmeess");
        staticRuleTest.setStaticRuleCreateUserId(2);
        staticRuleTest.setAuditStatus(0);

        staticRuleTest.setStaticRuleSip("1.1.2.0");
//        staticRuleTest.setStaticRuleMsip("255.255.255.0");

//        staticRuleTest.setStaticRuleDip("1.1.1.2");
//        staticRuleTest.setStaticRuleMdip("255.255.255.0");
//        staticRuleTest.setStaticRuleSport(80);

        staticRuleTest.setStaticRulePriority(1);
        staticRuleTest.setStaticRuleFrequency(1);
//        staticRuleTest.setStaticRuleRange("北京");
        Integer staticRuleId = staticRuleService.newStaticRuleObject(staticRuleTest);


        Task task = new Task();
        task.setTaskName("mh-task测试3");

        LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusYears(5);

        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("阻断");
        task.setTaskType(1);
        task.setTaskRange("1009");

        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");

        List<Integer> staticRuleIds = new ArrayList<>();
        staticRuleIds.add(staticRuleId);
        staticRuleService.updateAuditStatus(staticRuleId, 2);

        task.setStaticRuleIds(staticRuleIds);

        Long taskId = taskService.newTask(task);

//
        taskService.changeTaskAuditStatus(taskId, 2);

        stateChangeService.changeState(2, taskId, false);

        System.out.println(commandService.queryCommandInfos(taskId, null, null, null, null, 1, 5));

    }

    @Test
    void testQueryCommandsByTaskId() {
        List<TaskCommandInfo> taskCommandInfos = commandService.queryCommandInfos(43912L, null, null, null, null, 1, 5);
        System.out.println(taskCommandInfos);


    }

    @Test
    void testStartDynamicTask() throws DorisStartException {
        List<ProtectObject> protectObject = protectObjectService.queryProtectObjects(
                null, null, null, null,
                null, null, null, null,
                null, null, 1, 1);
        List<Template> templates = templateService.queryTemplates(
                null, null, null,1, 1);

        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("testStartDynamicTask");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
//        object.setDynamicRuleRange("北京");
        object.setDynamicRuleProtectLevel(1);
        object.setLogRuleId(1L);
        object.setTemplateId(templates.get(0).getTemplateId());
        object.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));
        Integer dynamicRuleId = dynamicRuleService.newDynamicRuleObject(object);
        dynamicRuleService.updateAuditStatus(object.getDynamicRuleId(), 2);



        Task task = new Task();
        task.setTaskName("生产告警信息测试2");
        LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusYears(5);
        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("阻断");
        task.setTaskType(2);
        task.setTaskRange("1009");
        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");
        task.setDynamicRuleIds(List.of(new Integer[]{dynamicRuleId}));
        Long taskId = taskService.newTask(task);
        System.out.println(taskId);
        //审核状态
        taskService.changeTaskAuditStatus(taskId, 2);
        //启动任务
        stateChangeService.changeState(2, taskId, false);

        System.out.println(commandService.queryCommandInfos(taskId, null, null, null, null, 1, 5));
    }
}