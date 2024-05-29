package com.realtime.protection.server.rule.dynamic;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
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
//        List<ProtectObject> protectObject = protectObjectService.queryProtectObjects(
//                null, null, null,null,
//                null,null,null,null,
//                null,null, 1, 1);
//        List<Template> templates = templateService.queryTemplates(
//                null, null, null, null, null,1, 1);
        for (int i = 0;i<30; i++) {
            DynamicRuleObject object = new DynamicRuleObject();
            object.setDynamicRuleName("动态规则"+i);
            object.setDescription("动态规则新建测试");
            object.setDynamicRuleSourceSystem("BW");
            object.setBwSql("select * from test where event_id = "+i);
            object.setDynamicRuleCreateDepart("组织树");
            object.setDynamicRuleCreateUserId(1100664);
            object.setDynamicRuleCreateUsername("NSADD管理员");
            dynamicRuleService.newDynamicRuleObject(object);
        }
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
                null, null,null, null, null,1, 1);

        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
//        object.setDynamicRuleRange("北京");
//        object.setDynamicRuleProtectLevel(2);
//        object.setTemplateId(templates.get(0).getTemplateId());
//        object.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));
        Integer dynamicRuleId = dynamicRuleService.newDynamicRuleObject(object);
        dynamicRuleService.updateAuditStatus(dynamicRuleId, 2,null,null,null);


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
        DynamicRuleObject object = dynamicRuleService.queryDynamicRuleById(166);
        System.out.println(object);
    }

    @Test
    void testUpdateDynamicRule() {

        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
//        object.setDynamicRuleFrequency(1);
//        object.setDynamicRulePriority(1);
////        object.setDynamicRuleRange("北京");
//        object.setDynamicRuleProtectLevel(2);
//        object.setTemplateId(1);
//        object.setProtectObjectIds(List.of(new Integer[]{6061}));

        dynamicRuleService.updateDynamicRuleObject(166, object);

    }

    @Test
    void testQueryDynamicRuleObject() {
        List<DynamicRuleObject> objects = dynamicRuleService.queryDynamicRuleObject(
                null,null,null, null,2,null,null,null,null,null,1, 10);
        objects.forEach(object-> System.out.println(object));
        Integer num = dynamicRuleService.queryDynamicRuleTotalNum(
                null,null,null,null,null,null,null,null,null,null);
        System.out.println(num);
    }
    @Test
    void testQueryDynamicRuleTotalNum(){
        Integer num = dynamicRuleService.queryDynamicRuleTotalNum(
                null,null,null,null,null,null,null,null,null,null);
        System.out.println(num);
    }

    @Test
    void testUpdateDynamicRuleAuditStatusBatch(){
        Map<Integer, Integer> map = new HashMap<>();
//        map.put(101, 1);
//        map.put(110, 2);
//        map.put(112, 2);
        for(int i = 167; i < 197; i++){
            map.put(i, 2);
        }

//        System.out.println(dynamicRuleMapper.queryAuditStatusByIds(map));
        System.out.println(dynamicRuleService.updateAuditStatusBatch(map, "NSADD管理员", "1100664", "组织树"));

    }


    @Test
    void testParseSql(){
        List<DynamicTaskInfo> dynamicTaskInfos = taskService.getDynamicTaskInfos(43991L);

        dynamicTaskInfos.forEach(dynamicTaskInfo -> {
                dynamicTaskInfo.parseSql();
                System.out.println(dynamicTaskInfo.getConditions());

          });
    }

}
