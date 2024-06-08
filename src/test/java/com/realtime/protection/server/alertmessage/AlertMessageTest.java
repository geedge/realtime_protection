package com.realtime.protection.server.alertmessage;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.github.xiaoymin.knife4j.annotations.Ignore;
import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.server.defense.object.ProtectObjectService;
import com.realtime.protection.server.defense.templatenew.TemplateService;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleService;
import com.realtime.protection.server.task.TaskService;
import com.realtime.protection.server.task.status.StateChangeService;
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
    private final StateChangeService stateChangeService;
    @Autowired
    public AlertMessageTest(AlertMessageService alertMessageService
    ,ProtectObjectService protectObjectService,TemplateService templateService,
                            DynamicRuleService dynamicRuleService,TaskService taskService,
                            StateChangeService stateChangeService) {
        this.alertMessageService = alertMessageService;
        this.protectObjectService = protectObjectService;
        this.templateService = templateService;
        this.dynamicRuleService = dynamicRuleService;
        this.taskService = taskService;
        this.stateChangeService = stateChangeService;
    }

    @Ignore
    @Test
    @DSTransactional
    void testReceiveAlertMessage() throws DorisStartException {

        for(int n = 10;n < 11;n++) {


            List<ProtectObject> protectObject = protectObjectService.queryProtectObjects(
                    null, null, null, null,
                    null, null, null, null,
                    null, null, 1, 1);
            List<TemplateNew> templates = templateService.queryTemplates(
                    null, null, null, null, null, null, null, 1, 1);

            DynamicRuleObject object = new DynamicRuleObject();
            object.setDynamicRuleName("内部-动态任务测试-"+LocalDateTime.now().toString());
            object.setDescription("内部-动态任务测试");
            object.setDynamicRuleSourceSystem("BW监测系统");
            object.setBwSql("select * from test where event_id = 1");
            object.setDynamicRuleCreateDepart("组织树");
            object.setDynamicRuleCreateUserId(1100664);
            object.setDynamicRuleCreateUsername("NSADD管理员");
            Integer dynamicRuleId = dynamicRuleService.newDynamicRuleObject(object);
            dynamicRuleService.updateAuditStatus(object.getDynamicRuleId(), 2, "NSADD管理员", 1100664, "组织树");


            Task task = new Task();
            task.setTaskName("内部-动态任务测试"+LocalDateTime.now().toString());
            LocalDateTime taskStartTime = LocalDateTime.now().plusMinutes(1);
            LocalDateTime taskEndTime = LocalDateTime.now().plusYears(5);
            task.setTaskStartTime(taskStartTime);
            task.setTaskEndTime(taskEndTime);
            task.setTaskAct("23");
            task.setTaskType(2);
            task.setTaskCreateUserId(1100664);
            task.setTaskCreateUsername("NSADD管理员");
            task.setTaskCreateDepart("组织树");
            task.setTaskRange("1009");


            task.setDynamicRuleIds(List.of(new Integer[]{dynamicRuleId}));
            task.setProtectObjectIds(List.of(new Integer[]{protectObject.get(0).getProtectObjectId()}));
            task.setTemplateId(templates.get(0).getTemplateId());
            task.setSourceSystem(templates.get(0).getSourceSystem());
            task.setEventType(templates.get(0).getEventType());
            task.setProtectLevel(Integer.valueOf(templates.get(0).getProtectLevel()));
            task.setTemplateName(templates.get(0).getTemplateName());
            Long taskId = taskService.newTask(task);
            System.out.println(taskId);
            //审核状态
            taskService.changeTaskAuditStatus(taskId, 2, "NSADD管理员", "1100664", "组织树");
            //启动任务
            stateChangeService.changeState(2, taskId, false);

            for (int i = 0; i < 5; i++) {
                AlertMessage alert = new AlertMessage();
                FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
                fiveTupleWithMask.setSourceIP(n+".5.1." + i);
                fiveTupleWithMask.setDestinationIP(n+".24.2." + i);
                fiveTupleWithMask.setSourcePort("111");
                fiveTupleWithMask.setDestinationPort("80");
                fiveTupleWithMask.setProtocol("6");

                alert.setDynamicRuleId(dynamicRuleId);
                alert.setTaskId(taskId);
                alert.setFiveTupleWithMask(fiveTupleWithMask);
                alert.setContent("testcontent");
                alert.setProtectIsSrcOrDst(1);
                alertMessageService.processAlertMessage(alert);
            }

        }

    }

    @Test
    void queryAlertMessageByCommandId() {

//        String commandId = "3e2fde7c-cd91-41f3-aedf-bd9993a61737";
//
//        System.out.println(alertMessageService.queryAlarmsByCommandId(commandId));
    }
}
