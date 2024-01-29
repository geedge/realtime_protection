package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.server.defense.object.ProtectObjectService;
import com.realtime.protection.server.defense.template.TemplateService;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleService;
import com.realtime.protection.server.task.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class AlertMessageTest {

    private final AlertMessageService alertMessageService;
    private final ProtectObjectService protectObjectService;
    private final TemplateService templateService;
    private final DynamicRuleService dynamicRuleService;
    private final TaskService taskService;
    @Autowired
    public AlertMessageTest(AlertMessageService alertMessageService
    ,ProtectObjectService protectObjectService,TemplateService templateService,
                            DynamicRuleService dynamicRuleService,TaskService taskService) {
        this.alertMessageService = alertMessageService;
        this.protectObjectService = protectObjectService;
        this.templateService = templateService;
        this.dynamicRuleService = dynamicRuleService;
        this.taskService = taskService;
    }

    @Test
    void testReceiveAlertMessage() {

        List<ProtectObject> protectObject = protectObjectService.queryProtectObjects(null, null, 1, 1);
        List<Template> templates = templateService.queryTemplates(null, 1, 1);

        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
        object.setDynamicRuleRange("北京");
        object.setDynamicRuleProtectLevel(2);
        object.setTemplateId(templates.get(0).getTemplateId());
        object.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));
        Integer dynamicRuleId = dynamicRuleService.newDynamicRuleObject(object);


        Task task = new Task();
        task.setTaskName("dong态测试2");
        LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEndTime = LocalDateTime.now().plusYears(5);
        task.setTaskStartTime(taskStartTime);
        task.setTaskEndTime(taskEndTime);
        task.setTaskAct("阻断");
        task.setTaskType(2);
        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");
        task.setDynamicRuleIds(List.of(new Integer[]{dynamicRuleId}));

        Long taskId = taskService.newTask(task);

        AlertMessage alert = new AlertMessage();
        FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
        fiveTupleWithMask.setSourceIP("1.1.1.1");

        alert.setDynamicRuleId(dynamicRuleId);
        alert.setTaskId(taskId);
        alert.setFiveTupleWithMask(fiveTupleWithMask);
        alert.setContent("testcontent");
        alertMessageService.processAlertMessage(alert);

    }

    @Test
    void queryAlertMessageByCommandId() {

//        String commandId = "3e2fde7c-cd91-41f3-aedf-bd9993a61737";
//
//        System.out.println(alertMessageService.queryAlarmsByCommandId(commandId));
    }
}
