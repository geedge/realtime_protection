package com.realtime.protection.configuration.utils.enums;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleEnumTest {
    StaticRuleObject staticRuleObject;

    public RuleEnumTest() {
        staticRuleObject = new StaticRuleObject();
    }

    @Test
    public void testRuleEnum() {
        assertDoesNotThrow(() -> {
            this.staticRuleObject.setStaticRuleDip("255.255.255.255");
            assertTrue(RuleEnum.checkValidate(staticRuleObject));
            this.staticRuleObject.setStaticRuleMsip("255.255.255.255");
            assertFalse(RuleEnum.checkValidate(staticRuleObject));
        });
    }
}