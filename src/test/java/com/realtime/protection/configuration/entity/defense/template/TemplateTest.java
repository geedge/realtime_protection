package com.realtime.protection.configuration.entity.defense.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    @Test
    public void testSetHasProtectLevel() {
        Template template = new Template();
        ProtectLevel protectLevel = new ProtectLevel();
        protectLevel.setHasDNS(true);

        template.setProtectLevelLow(new ProtectLevel());
        template.setProtectLevelMedium(new ProtectLevel());
        template.setProtectLevelHigh(new ProtectLevel());

        assertDoesNotThrow(() -> {
            template.setProtectLevelHigh(protectLevel);
            template.setHasProtectLevel();
            assertTrue(template.getHasProtectLevelHigh());
            assertFalse(template.getHasProtectLevelMedium());
            assertFalse(template.getHasProtectLevelLow());
        });

        assertDoesNotThrow(() -> {
            template.setProtectLevelMedium(protectLevel);
            template.setProtectLevelHigh(new ProtectLevel());
            template.setHasProtectLevel();
            assertFalse(template.getHasProtectLevelHigh());
            assertTrue(template.getHasProtectLevelMedium());
            assertFalse(template.getHasProtectLevelLow());
        });
    }
}