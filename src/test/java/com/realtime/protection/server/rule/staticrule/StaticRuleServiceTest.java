package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StaticRuleServiceTest {
    @Autowired
    private StaticRuleService staticRuleService;

    @Test
    void testNewStaticRule(){
        StaticRuleObject object = new StaticRuleObject();

        object.setStaticRuleName("test_staticrule");
        object.setStaticRuleCreateTime(LocalDateTime.now());
        object.setStaticRuleCreateUsername("boy");
        object.setStaticRuleCreateDepart("1chu");
        object.setStaticRuleCreateUserId(22);
        object.setStaticRulePriority(1);
        object.setStaticRuleFrequency(1);
        object.setStaticRuleRange("北京");
        object.setStaticRuleProtectLevel(2);

        Integer id = staticRuleService.newStaticRuleObject(object);
        assertTrue(id>0);
    }

}
