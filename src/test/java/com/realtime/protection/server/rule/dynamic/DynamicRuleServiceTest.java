package com.realtime.protection.server.rule.dynamic;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.server.defense.object.ProtectObjectService;
import com.realtime.protection.server.defense.template.TemplateService;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleService;
import com.realtime.protection.server.task.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class DynamicRuleServiceTest extends ProtectionApplicationTests {
    private final DynamicRuleService dynamicRuleService;
    private final TaskService taskService;

    private final ProtectObjectService protectObjectService;

    private final TemplateService templateService;
    @Autowired
    public DynamicRuleServiceTest(DynamicRuleService dynamicRuleService,TaskService taskService,
                                  ProtectObjectService protectObjectService,TemplateService templateService) {
        this.dynamicRuleService = dynamicRuleService;
        this.taskService = taskService;
        this.protectObjectService = protectObjectService;
        this.templateService = templateService;
    }

    @Test
    void testNewDynamicRule() {
        List<ProtectObject> protectObject = protectObjectService.queryProtectObjects(
                null, null, null,null,
                null,null,null,null,
                null,null, 1, 1);
        List<Template> templates = templateService.queryTemplates(
                null, null, null, 1, 1);

        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
//        object.setDynamicRuleRange("北京");
        object.setDynamicRuleProtectLevel(2);
        object.setTemplateId(templates.get(0).getTemplateId());
        object.setLogRuleId(1L);
        object.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));
        Integer dynamicRuleId = dynamicRuleService.newDynamicRuleObject(object);
    }

    @Test
    void testDeleteDynamicRule() {
        List<ProtectObject> protectObject = protectObjectService.queryProtectObjects(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1, 1);
        List<Template> templates = templateService.queryTemplates(
                null, null,null, 1, 1);

        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
//        object.setDynamicRuleRange("北京");
        object.setDynamicRuleProtectLevel(2);
        object.setTemplateId(templates.get(0).getTemplateId());
        object.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));
        Integer dynamicRuleId = dynamicRuleService.newDynamicRuleObject(object);


        Task task = new Task();
        task.setTaskName("dong态测试");
        LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusYears(5);
        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("阻断");
        task.setTaskType(1);
        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");
        task.setDynamicRuleIds(List.of(new Integer[]{dynamicRuleId}));

        Long taskId = taskService.newTask(task);

        taskService.changeTaskStatus(taskId, 2);

//        dynamicRuleService.deleteDynamicRuleObject(dynamicRuleId);
    }

    @Test
    void testQueryDynamicRule() {
//        DynamicRuleObject object = dynamicRuleService.queryDynamicRuleById(9);
//        System.out.println(object);
    }

    @Test
    void testUpdateDynamicRule() {
        /*
        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
//        object.setDynamicRuleRange("北京");
        object.setDynamicRuleProtectLevel(2);
        object.setTemplateId(1);
        object.setProtectObjectIds(List.of(new Integer[]{6061}));

        dynamicRuleService.updateDynamicRuleObject(5, object);
        */
    }

    @Test
    void testQueryDynamicRuleObject() {
        List<DynamicRuleObject> objects = dynamicRuleService.queryDynamicRuleObject(
                null,null,null, null,1, 10);
        System.out.println(objects);
    }
    @Test
    void testQueryDynamicRuleTotalNum(){
        Integer num = dynamicRuleService.queryDynamicRuleTotalNum(
                null,null,null,null);
        System.out.println(num);
    }

    @Test
    void testUpdateDynamicRuleAuditStatusBatch(){
        Map<Integer, Integer> map = new HashMap<>();
        map.put(101, 1);
        map.put(102, 2);
        map.put(103, 2);


        System.out.println(dynamicRuleService.updateAuditStatusBatch(map));
    }

}
