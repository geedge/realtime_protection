package com.realtime.protection.server.rule.dynamic;

import com.github.xiaoymin.knife4j.annotations.Ignore;
import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DynamicRuleServiceTest extends ProtectionApplicationTests {
    private final DynamicRuleService dynamicRuleService;
    @Autowired
    public DynamicRuleServiceTest(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @Test
    void testNewDynamicRule() {
        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
        object.setDynamicRuleRange("北京");
        object.setDynamicRuleProtectLevel(2);
        object.setTemplateId(1);
        object.setProtectObjectIds(List.of(new Integer[]{5521, 5520}));


//        Integer objectId = dynamicRuleService.newDynamicRuleObject(object);
//        assertTrue(objectId > 0);
    }

    @Test
    void testDeleteDynamicRule() {
      //  dynamicRuleService.deleteDynamicRuleObject(8);
    }

    @Test
    void testQueryDynamicRule() {
//        DynamicRuleObject object = dynamicRuleService.queryDynamicRuleById(9);
//        System.out.println(object);
    }

    @Ignore
    @Test
    void testUpdateDynamicRule() {
        DynamicRuleObject object = new DynamicRuleObject();
        object.setDynamicRuleName("UpdateDynamicRule2");
        object.setDynamicRuleFrequency(1);
        object.setDynamicRulePriority(1);
        object.setDynamicRuleRange("北京");
        object.setDynamicRuleProtectLevel(2);
        object.setTemplateId(1);
        object.setProtectObjectIds(List.of(new Integer[]{6061}));

        dynamicRuleService.updateDynamicRuleObject(5, object);
    }

    @Test
    void testQueryDynamicRuleObject() {
        List<DynamicRuleObject> objects = dynamicRuleService.queryDynamicRuleObject(
                null,null,"1", null,1, 10);
        System.out.println(objects);
    }
}
