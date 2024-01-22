package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.rule.dynamicrule.AlertMessage;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlertMessageTest {

    private final AlertMessageService alertMessageService;
    @Autowired
    public AlertMessageTest(AlertMessageService alertMessageService) {
        this.alertMessageService = alertMessageService;
    }

    @Test
    void testReceiveAlertMessage() {
        for (int i = 1; i < 4; i++) {
            AlertMessage alertMessage = new AlertMessage();
            FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
            fiveTupleWithMask.setSourceIP("1.1.1." + i);
            fiveTupleWithMask.setMaskSourceIP("255.255.255.0");
            fiveTupleWithMask.setDestinationIP("2.2.3." + i);
            fiveTupleWithMask.setMaskDestinationIP("255.255.255.255");
            fiveTupleWithMask.setSourcePort("80");
            fiveTupleWithMask.setDestinationPort("80");
            fiveTupleWithMask.setProtocol("TCP");

            alertMessage.setTaskId(1937L);
            alertMessage.setFiveTupleWithMask(fiveTupleWithMask);
            alertMessage.setDynamicRuleId(31);
            alertMessageService.processAlertMessage(alertMessage);
        }
    }

    @Test
    void queryAlertMessageByCommandId() {

//        String commandId = "3e2fde7c-cd91-41f3-aedf-bd9993a61737";
//
//        System.out.println(alertMessageService.queryAlarmsByCommandId(commandId));
    }
}
