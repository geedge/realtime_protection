package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class StaticRuleServiceTest extends ProtectionApplicationTests {
    private final StaticRuleService staticRuleService;
    private StaticRuleObject staticRuleTest;
    @Autowired
    public StaticRuleServiceTest(StaticRuleService staticRuleService) {
        this.staticRuleService = staticRuleService;
    }

    @BeforeEach
    void setUp() {
        staticRuleTest = new StaticRuleObject();
        staticRuleTest.setStaticRuleName("test_staticrule");
        staticRuleTest.setStaticRuleCreateUsername("mh");
        staticRuleTest.setStaticRuleCreateDepart("mmeess");
        staticRuleTest.setStaticRuleCreateUserId(2);
        staticRuleTest.setAuditStatus(0);

        staticRuleTest.setStaticRuleSip("2.2.2.2");

//        staticRuleTest.setStaticRuleDip("1.1.1.2");
//        staticRuleTest.setStaticRuleMdip("255.255.255.0");
        staticRuleTest.setStaticRuleSport(11);

        staticRuleTest.setStaticRulePriority(1);
        staticRuleTest.setStaticRuleFrequency(1);
        staticRuleTest.setStaticRuleRange("北京");

    }

    @Test
    void testNewStaticRule(){
        staticRuleService.newStaticRuleObject(staticRuleTest);
        //assertTrue(id>0);
    }
    @Test
    void testNewStaticRules(){
        List<StaticRuleObject> staticRuleObjects = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            staticRuleObjects.add(staticRuleTest);
        }
        Boolean success = staticRuleService.newStaticRuleObjects(staticRuleObjects);
        assertTrue(success);
    }

    @Test
    void testDeleteStaticRule(){
        int i = 0;
        List<Integer> list = new ArrayList<>();
        list.add(1);
        while(i<2) {
            i++;
            staticRuleService.deleteStaticRules(list);
        }
    }
    @Test
    void testUpdateStaticRule(){

        StaticRuleObject object = new StaticRuleObject();

        object.setStaticRuleId(5);
        object.setStaticRuleName("update_staticrule");
        //object.setStaticRuleCreateTime(LocalDateTime.now());
        //object.setStaticRuleCreateUsername("csy");
        //object.setStaticRuleCreateDepart("mmeess");
        //object.setStaticRuleCreateUserId(1);
        object.setStaticRuleSip(null);
        object.setStaticRuleSport(null);
        object.setStaticRuleDip("2.2.3.100");
        object.setStaticRulePriority(2);
        object.setStaticRuleFrequency(1);
        object.setStaticRuleRange("北京");
//        object.setStaticRuleProtectLevel(2);

        staticRuleService.updateStaticRule(5, object);
    }
    @Test
    void testQueryStaticRule(){

        StaticRuleObject object = staticRuleService.queryStaticRuleById(7);
        System.out.println(object);
    }

    @Test
    void testQueryStaticRules(){
        List<StaticRuleObject> staticRuleObjects = staticRuleService.queryStaticRule(null, null, null, null, 1, 10);
        System.out.println(staticRuleObjects);
    }
    @Test
    void testUpdateStaticRuleAuditStatusBatch(){
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1325, 0);
        map.put(1326, 1);
        map.put(1328, 1);


        System.out.println(staticRuleService.updateAuditStatusBatch(map));
    }



}
