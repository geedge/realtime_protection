package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    }

    @Test
    void testNewStaticRule(){
        Integer i = 0;
        while(i<2) {
            i++;
            StaticRuleObject object = new StaticRuleObject();

            object.setStaticRuleName("test_staticrule" + i);
            //object.setStaticRuleCreateTime(LocalDateTime.now());
            object.setStaticRuleCreateUsername("boy" + i);
            object.setStaticRuleCreateDepart("2chu" + i);
            object.setStaticRuleCreateUserId(i);
            object.setStaticRuleSip("1.1.1." + i);
            object.setStaticRuleSport(i);
            object.setStaticRuleDip("2.2.3." + i);
            object.setStaticRulePriority(2);
            object.setStaticRuleFrequency(1);
            object.setStaticRuleRange("北京");

            Integer id = staticRuleService.newStaticRuleObject(object);
        }
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



}
