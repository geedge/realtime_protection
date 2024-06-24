package com.realtime.protection.server.task;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.defense.object.ProtectObjectService;
import com.realtime.protection.server.defense.templatenew.TemplateService;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleService;
import com.realtime.protection.server.rule.staticrule.StaticRuleService;
import com.realtime.protection.server.task.status.StateChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
//        for (int i = 1; i < 10; i++) {
//            List<StaticRuleObject> staticRuleObjects = staticRuleService.queryStaticRule(
//                    null, null, null, null,null, null, null,2, i, 2);
//            List<Integer> staticRuleIds = new ArrayList<>();
//            staticRuleObjects.forEach(staticRuleObject ->
//                    staticRuleIds.add(staticRuleObject.getStaticRuleId()));
//            task.setStaticRuleIds(staticRuleIds);
//
//            List<DynamicRuleObject> dynamicRuleObjects = dynamicRuleService.queryDynamicRuleObject(
//                    null, null, null, null,null, null,null,null,i, 2
//            );
//            List<Integer> dynamicRuleIds = new ArrayList<>();
//            dynamicRuleObjects.forEach(dynamicRuleObject ->
//                    dynamicRuleIds.add(dynamicRuleObject.getDynamicRuleId()));
//            task.setDynamicRuleIds(dynamicRuleIds);
//
//            assertDoesNotThrow(() -> {
//                Long taskId = taskService.newTask(task);
//                assertTrue(taskId > 0);
//            });
//            assertTrue(task.getTaskId() > 0);
//        }
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
    void testPageQueryTask(){
        List<Task> tasks = taskService.queryTasks(null, null, "", "", null,"", null, "", "", null,null,null,null,1, 10);
//        System.out.println(tasks);
        tasks.forEach(task -> System.out.println(task));
    }

    @Test
    void testQueryTasks() {
//        String testName = "test query";
//        String testCreateName = "xxx query";
//
//        for (int i = 0; i < 10; i++) {
//            task.setTaskName(testName);
//            task.setTaskCreateUsername(testCreateName);
//            List<StaticRuleObject> staticRuleObjects = s                                                                                                                              taticRuleService.queryStaticRule(
//                    null, null, null, null, null,null, null, null,1, 5);
//            List<Integer> staticRuleIds = new ArrayList<>();
//            staticRuleObjects.forEach(staticRuleObject ->
//                    staticRuleIds.add(staticRuleObject.getStaticRuleId()));
//            task.setStaticRuleIds(staticRuleIds);
//
//            List<DynamicRuleObject> dynamicRuleObjects = dynamicRuleService.queryDynamicRuleObject(
//                    null, null, null, null, null,null, null, null, 1, 5
//            );
//            List<Integer> dynamicRuleIds = new ArrayList<>();
//            dynamicRuleObjects.forEach(dynamicRuleObject ->
//                    dynamicRuleIds.add(dynamicRuleObject.getDynamicRuleId()));
//            task.setDynamicRuleIds(dynamicRuleIds);
//
//            assertDoesNotThrow(() -> {
//                Long taskId = taskService.newTask(task);
//                assertTrue(taskId > 0);
//            });
//            assertTrue(task.getTaskId() > 0);
//        }
//
//
        List<Task> tasks = taskService.queryTasks(null, null, null, null, null, null, null, null, null,null,null,null,null,1, 10);
        tasks.forEach(task -> System.out.println(task));
        Integer tasksnum = taskService.queryTaskTotalNum(null, null, "", "", null, "", "", "", "上述",null, String.valueOf(LocalDate.now()),null,null);
        System.out.println(tasksnum);
//
//        tasks = taskService.queryTasks(0, null, null, null, null,null, null, null, null, 1, 10);
//        assertEquals(10, tasks.size());
//        tasks.forEach(task -> assertEquals(0, task.getTaskStatus()));
//
//        tasks = taskService.queryTasks(null, 0, null, null, null, null, null, null, null,1, 10);
//        assertEquals(0, tasks.size());
//
//        tasks = taskService.queryTasks(null, null, testName, null, null, null, null, null, null,1, 10);
//        assertEquals(10, tasks.size());
//        tasks.forEach(task -> assertEquals(testName, task.getTaskName()));
    }

    @Test
    void testUpdateTasks() {
        Task originalTask = taskService.queryTasks(
                null, null, null, null, null, null, null, null, null,null,null,null,null,1, 1)
                .get(0);

        List<StaticRuleObject> staticRuleObjects = staticRuleService.queryStaticRule(
                null, null, null, null, null,null, null, null,null,null,1, 4
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
        List<Task> tests = taskService.queryTasks(null, null, null, null, null, null, null, null, null,null,null,null,null,1, 31)
                ;
        for (Task task : tests){
            taskService.deleteTask(task.getTaskId());
        }
//        assertFalse(taskService.deleteTask(235156235L)); // 尝试一个不可能达到的数字
    }

    @Test
    void testChangeAuditStatus() {
//        long testNum = taskService.queryTasks(null, null, null, null, null, null, null, null, null,1, 1)
//                .get(0).getTaskId();
//
//        assertTrue(taskService.changeTaskAuditStatus(testNum, 2));
//        assertFalse(taskService.changeTaskAuditStatus(testNum, 0));
//        assertFalse(taskService.changeTaskAuditStatus(testNum, 1));
    }

    @Test
    void testGetStaticCommands() {
        List<TaskCommandInfo> taskCommandInfos = taskService.getStaticCommandInfos(38L);
        assertNotNull(taskCommandInfos);
    }


    @Test
    void testUpdateTaskAuditStatusBatch(){
//        Map<Integer, Integer> map = new HashMap<>();
//        map.put(43830, 1);
//        map.put(43831, 1);
//        map.put(43832, 1);
//        System.out.println(taskService.updateAuditStatusBatch(map));

        System.out.println(taskService.changeTaskAuditStatus(44047L, 2, null, null, null));


    }
    @Test
    void testGetDynamicTaskInfos(){
        List<DynamicTaskInfo> dynamicTaskInfos = taskService.getDynamicTaskInfos(43844L);
        System.out.println(dynamicTaskInfos);
    }

    @Test
    void changeTaskstatus() throws DorisStartException {
//        stateChangeService.changeState(2, 43844L, false);

    }


    @DSTransactional
    @Test
    void testStartStaticTask() throws DorisStartException {

        for (int j = 0; j < 1; j++) {
            List<Integer> staticRuleIds = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                StaticRuleObject staticRuleTest = new StaticRuleObject();
                staticRuleTest.setStaticRuleName("test_st3aticrule" + i);
                staticRuleTest.setStaticRuleCreateUsername("NSADD管理员");
                staticRuleTest.setStaticRuleCreateDepart("组织树");
                staticRuleTest.setStaticRuleCreateUserId(22222222);

                staticRuleTest.setStaticRuleSip("32.33.3.1");
                staticRuleTest.setStaticRuleDip("2.2.3.2");
                staticRuleTest.setStaticRuleSport(i);
                staticRuleTest.setStaticRuleProtocol("6");
                staticRuleService.newStaticRuleObject(staticRuleTest);
                staticRuleIds.add(staticRuleTest.getStaticRuleId());
                staticRuleService.updateAuditStatus(staticRuleTest.getStaticRuleId(), 2, "NSADD管理员", 1111111, "组织树");
            }

            Task task = new Task();
            task.setTaskName("静态task测试-10000指令"+j);

            LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
            LocalDateTime taskEndTime = LocalDateTime.now().plusYears(1);

            task.setTaskStartTime(taskStartTime);
            task.setTaskEndTime(taskEndTime);
            task.setTaskAct("23");
            task.setTaskType(1);
            task.setTaskRange("1009");
            task.setEventType("泛洪型DDOS");

            task.setTaskCreateUserId(111111);
            task.setTaskCreateUsername("xxx管理员");
            task.setTaskCreateDepart("xxx");

            task.setStaticRuleIds(staticRuleIds);

            Long taskId = taskService.newTask(task);
            taskService.changeTaskAuditStatus(taskId, 2, "NSADD管理员", "1111111", "组织树");

            stateChangeService.changeState(2, taskId, false);

        }



//            System.out.println(commandService.queryCommandInfos(taskId, null, null, null, null, 1, 5));

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
        List<TemplateNew> templates = templateService.queryTemplates(
                null, null, null,null, null,null,null,1, 1);

        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("testStartDynamicTask");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
        object.setDynamicRuleSourceSystem("bw");
//        object.setDynamicRuleSourceSystem("bw");
//        object.setBwSql("select c_src_ipv4,c_src_port,c_dest_ipv4,c_dest_port from topic_xxxxxx where c_event_id in (11113333311,222222222222) and c_netnum = 111111111 and  c_flowid=22222222"  );
//        object.setDynamicRuleRange("北京");
//        object.setDynamicRuleProtectLevel(1);
//        object.setLogRuleId(1L);
//        object.setTemplateId(templates.get(0).getTemplateId());
//        object.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));
        Integer dynamicRuleId = dynamicRuleService.newDynamicRuleObject(object);
        dynamicRuleService.updateAuditStatus(object.getDynamicRuleId(), 2,null,null,null);



        Task task = new Task();
        task.setTaskName("test动态任务sql");
        LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusYears(5);
        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("23");
        task.setTaskType(2);
        task.setTaskRange("1007");
        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");
        task.setDynamicRuleIds(List.of(new Integer[]{dynamicRuleId}));
        task.setEventType(templates.get(0).getEventType());
        task.setProtectLevel(Integer.valueOf(templates.get(0).getProtectLevel()));
        task.setTemplateName(templates.get(0).getTemplateName());
        task.setSourceSystem(templates.get(0).getSourceSystem());
        task.setTemplateId(templates.get(0).getTemplateId());

//        task.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));

        Long taskId = taskService.newTask(task);
        System.out.println(taskId);
        //审核状态
        taskService.changeTaskAuditStatus(taskId, 2);
        //启动任务
        stateChangeService.changeState(2, taskId, false);

//        System.out.println(commandService.queryCommandInfos(taskId, null, null, null, null, 1, 5));
    }


    @Test
    void testUpdateDynamicRuleAuditStatusBatch(){
//        Map<Integer, Integer> map = new HashMap<>();
////        map.put(101, 1);
//        map.put(43848, 0);
//        map.put(43849,0);
//
////        System.out.println(dynamicRuleMapper.queryAuditStatusByIds(map));
//        System.out.println(taskService.updateAuditStatusBatch(map));
    }

    @Test
    void testQueryDynamicRuleAuditStatus(){
        System.out.println(taskService.queryTaskAuditStatus(43848L));
        Map<Integer, Integer> map = new HashMap<>();
        map.put(43848, 0);
        map.put(43849,0);
        System.out.println(taskService.queryAuditStatusBatch(map));
    }

    @Test
    void testQueryTaskTotalNum(){
        System.out.println(taskService.queryTaskTotalNum(null, null, "", "", null, "", "", "", "",null,null,null,null));
    }

    @Test
    void testQueryTaskHistory(){
        System.out.println(taskService.queryHistory(44058L,1,10));
    }

    @Test
    void updateStaticRuleAuditStatusInTask(){
        taskService.updateStaticRuleAuditStatusInTask(44126L, AuditStatusEnum.AUDITED);

    }

    @Test
    void testQueryTask() throws DorisStartException {
//        task/44279/running/2
        stateChangeService.changeState(2, 44279L, false);
    }



    @DSTransactional
    @Test
    void testStartStaticTask_command() throws DorisStartException {

        for (int j = 0; j < 1; j++) {
            List<Integer> staticRuleIds = new ArrayList<>();
            staticRuleService.queryStaticRule(
                    null, null, null, null, null,null, null, 2,null,null,1, 39999)
                    .forEach(staticRuleObject -> staticRuleIds.add(staticRuleObject.getStaticRuleId()));
            HashMap<Integer, Integer> map = new HashMap<>();
//            for (Integer staticRuleId : staticRuleIds) {
//                map.put(staticRuleId, 2);
//            }
//            staticRuleService.updateAuditStatusBatch(map);
            Task task = new Task();
            task.setTaskName("指令生成性能测试任务-2w-0619");

            LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
            LocalDateTime taskEndTime = LocalDateTime.now().plusYears(1);

            task.setTaskStartTime(taskStartTime);
            task.setTaskEndTime(taskEndTime);
            task.setTaskAct("23");
            task.setTaskType(1);
            task.setTaskRange("1009");
            task.setEventType("网络后门");

            task.setTaskCreateUserId(111111);
            task.setTaskCreateUsername("NSADD管理员");
            task.setTaskCreateDepart("一室");

            task.setStaticRuleIds(staticRuleIds);

            Long taskId = taskService.newTask(task);
            System.out.println(staticRuleIds.size());
            System.out.println(taskId);
            taskService.changeTaskAuditStatus(taskId, 2, "NSADD管理员", "1111111", "组织树");

            stateChangeService.changeState(2, taskId, false);

        }



//            System.out.println(commandService.queryCommandInfos(taskId, null, null, null, null, 1, 5));

    }

    @Test
    void testNotificate(){


        List<Integer> staticRuleIds = new ArrayList<>();
        staticRuleService.queryStaticRule(
                        null, null, null, null, null,null, null, 2,null,null,1, 1)
                .forEach(staticRuleObject -> staticRuleIds.add(staticRuleObject.getStaticRuleId()));
        HashMap<Integer, Integer> map = new HashMap<>();
//            for (Integer staticRuleId : staticRuleIds) {
//                map.put(staticRuleId, 2);
//            }
//            staticRuleService.updateAuditStatusBatch(map);
        Task task = new Task();
        task.setTaskName("测试用户审核退回提醒");

        LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusYears(1);

        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("23");
        task.setTaskType(1);
        task.setTaskRange("1009");
        task.setEventType("网络后门");

        task.setTaskCreateUserId(12345);
        task.setTaskCreateUsername("NSADD管理员");
        task.setTaskCreateDepart("一室");

        task.setStaticRuleIds(staticRuleIds);

        Long taskId = taskService.newTask(task);
        System.out.println(staticRuleIds.size());
        System.out.println(taskId);
        taskService.changeTaskAuditStatus(taskId, 1, "NSADD管理员", "123", "组织树");
        taskService.updateAuditInfo(List.of(taskId),"审核不通过，拒绝");

        List<Task> tasks =  taskService.auditInfoNotification("12345");
        System.out.println(tasks);
        assert !tasks.isEmpty();
    }


}
