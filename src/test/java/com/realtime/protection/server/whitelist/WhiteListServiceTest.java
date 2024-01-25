package com.realtime.protection.server.whitelist;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.server.rule.staticrule.StaticRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class WhiteListServiceTest extends ProtectionApplicationTests {

    private final WhiteListService whiteListService;
    private WhiteListObject whiteListObject;

    private final StaticRuleService staticRuleService;

    @Autowired
    WhiteListServiceTest(WhiteListService whiteListService, StaticRuleService staticRuleService) {
        this.whiteListService = whiteListService;
        this.staticRuleService = staticRuleService;
    }

    @BeforeEach
    void setUp() {
        whiteListObject = new WhiteListObject();
        whiteListObject.setWhiteListName("test");
        whiteListObject.setWhiteListSystemName("china");
        whiteListObject.setWhiteListIP("128.1.1.1");
        whiteListObject.setWhiteListPort(80);
        whiteListObject.setWhiteListUrl("www.baidu.com");
        whiteListObject.setWhiteListProtocol("TCP");
    }

    @Test
    void testNewWhiteList() {
        WhiteListObject object = new WhiteListObject();
        object.setWhiteListName("test");

        Integer objectId = whiteListService.newWhiteListObject(object);
        assertTrue(objectId > 0);
    }

    @Test
    void newProtectObjects() {
        List<WhiteListObject> whiteListObjects = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            whiteListObjects.add(whiteListObject);
        }
        Boolean success = whiteListService.newWhiteListObjects(whiteListObjects);
        assertTrue(success);
    }

    @Test
    void testUpdateWhiteList() {
        WhiteListObject object = new WhiteListObject();
        //object.setWhiteListId(7);
        object.setWhiteListName("test_update");

        whiteListService.updateWhiteListObject(7, object);
    }

    @Test
    void testExistWhiteList() {
        List<Integer> ruleIds = List.of(6, 7, 8);
//        List<String> ip_list = whiteListService.existWhiteListObject(ruleIds);
//        System.out.println(ip_list);
    }

    @Test
    void testUpdateWhiteListAuditStatus() {
       //whiteListService.updateWhiteListObjectAuditStatus(7, 1);
    }

    @Test
    void testWhiteListCommandCheck() {
        FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
        TaskCommandInfo taskCommandInfo = new TaskCommandInfo();
        fiveTupleWithMask.setDestinationIP("128.1.1.123");
        fiveTupleWithMask.setMaskDestinationIP("255.255.255.0");
        fiveTupleWithMask.setDestinationPort("80");
        taskCommandInfo.setFiveTupleWithMask(fiveTupleWithMask);

        List<WhiteListObject> whitelists = whiteListService.whiteListCommandCheck(taskCommandInfo);
        System.out.println(whitelists);
    }

    @Test
    void testWhiteListStaticRulesCheck() {
        StaticRuleObject staticRuleTest = new StaticRuleObject();
        staticRuleTest.setStaticRuleName("test_staticrule");
        staticRuleTest.setStaticRuleCreateTime(LocalDateTime.now());
        staticRuleTest.setStaticRuleCreateUsername("mh");
        staticRuleTest.setStaticRuleCreateDepart("mmeess");
        staticRuleTest.setStaticRuleCreateUserId(2);
        staticRuleTest.setStaticRuleAuditStatus(0);

        staticRuleTest.setStaticRuleSip("1.1.2.3");
        staticRuleTest.setStaticRuleSport(80);

        staticRuleTest.setStaticRulePriority(1);
        staticRuleTest.setStaticRuleFrequency(1);
        staticRuleTest.setStaticRuleRange("北京");

        Integer id = staticRuleService.newStaticRuleObject(staticRuleTest);
        List<Integer> ruleIds = new ArrayList<>(List.of(id));
        List<StaticRuleObject> staticRules= staticRuleService.queryStaticRule(null,null,null,null,1,2);
        for (StaticRuleObject staticRule : staticRules) {
            ruleIds.add(staticRule.getStaticRuleId());
        }
        whiteListObject = new WhiteListObject();
        whiteListObject.setWhiteListName("test");
        whiteListObject.setWhiteListSystemName("china");
        whiteListObject.setWhiteListIP("1.1.2.3");
        whiteListObject.setWhiteListPort(80);
        whiteListObject.setWhiteListUrl("www.baidu.com");
        whiteListObject.setWhiteListProtocol("TCP");
        whiteListService.newWhiteListObject(whiteListObject);
        System.out.println(whiteListService.whiteListStaticRulesCheck(ruleIds));
    }
}