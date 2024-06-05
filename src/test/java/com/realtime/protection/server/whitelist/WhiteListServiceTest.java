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
import java.util.Map;

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
        object.setWhiteListSystemName("china");
        object.setWhiteListIP("1.1.1.1");
        object.setWhiteListPort(80);
        object.setWhiteListUrl("www.baidu.com");
        object.setWhiteListProtocol("TCP");


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

        whiteListService.updateWhiteListObject(7189, object);
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
        staticRuleTest.setStaticRuleName("test__白名单");
        staticRuleTest.setStaticRuleCreateUsername("mh");
        staticRuleTest.setStaticRuleCreateDepart("mmeess");
        staticRuleTest.setAuditStatus(2);
        staticRuleTest.setStaticRuleSip("1.13.2.3");
//        staticRuleTest.setStaticRuleSport(80);

        Integer id = staticRuleService.newStaticRuleObject(staticRuleTest);
        List<Integer> ruleIds = new ArrayList<>(List.of(id));
//        List<StaticRuleObject> staticRules= staticRuleService.queryStaticRule(null,null,null,null,null,null, null, null,1,2);
//        for (StaticRuleObject staticRule : staticRules) {
//            ruleIds.add(staticRule.getStaticRuleId());
//        }
        whiteListObject = new WhiteListObject();
        whiteListObject.setWhiteListName("test");
        whiteListObject.setWhiteListSystemName("china");
        whiteListObject.setWhiteListIP("1.13.2.3");
        whiteListObject.setWhiteListPort(80);
        whiteListObject.setWhiteListUrl("www.baidu.com");
        whiteListObject.setWhiteListProtocol("6");
        whiteListService.newWhiteListObject(whiteListObject);
        System.out.println(whiteListService.whiteListStaticRulesCheck(ruleIds));
    }

    @Test
    void testQueryWhiteListObject() {
        List<WhiteListObject> whiteListObjects = whiteListService.queryWhiteListObject(null, null, null, null, null, 1, 2);
        System.out.println(whiteListObjects);
    }
    @Test
    void testQueryWhiteListObjectbyid() {
        List<WhiteListObject> whiteListObjects = whiteListService.queryWhiteListObject(null, null, null, null, null, 1, 2);
        System.out.println(
                whiteListService.queryWhiteListObjectById(
                        whiteListObjects.get(0).getWhiteListId()));
    }

    @Test
    void testUpdateAuditStatusByIdBatch() {
        Map<Integer, Integer> idsWithAuditStatusMap = Map.of(7189, 2, 7192, 2);

        whiteListService.updateAuditStatusBatch(idsWithAuditStatusMap);
    }

    @Test
    void testQueryHistory() {
        whiteListService.queryHistory(1, 1, 2).forEach(
                System.out::println
        );

    }
}