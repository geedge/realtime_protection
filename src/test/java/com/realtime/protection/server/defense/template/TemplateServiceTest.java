package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.defense.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TemplateServiceTest {

    private final TemplateService templateService;
    private Template template;

    @Autowired
    TemplateServiceTest(TemplateService templateService) {
        this.templateService = templateService;
    }

    @BeforeEach
    void mockTemplate() {

        template = new Template();

        template.setTemplateName("反射型DDOS攻击");
        template.setSourceSystem("xxxx系统");

        ProtectLevel protectLevelLow = new ProtectLevel();
        protectLevelLow.setHasPeerPort(true);

        ProtectLevel protectLevelMedium = new ProtectLevel();
        protectLevelMedium.setHasProtectObjectIP(true);

        ProtectLevel protectLevelHigh = new ProtectLevel();
        protectLevelHigh.setHasDNS(true);

        template.setProtectLevelLow(protectLevelLow);
        template.setProtectLevelMedium(protectLevelMedium);
        template.setProtectLevelHigh(protectLevelHigh);
    }

    @Test
    void testNewTemplate() {
        Integer templateId = templateService.newTemplate(template);
        assertTrue(templateId > 0);
    }

    @Test
    void testQueryTemplate() {
        List<Template> templates = templateService.queryTemplates("DDOS", 1, 5);
        System.out.println(templates);
    }

    @Test
    void testUpdateTemplateSuccess() {
        template.setTemplateName("洪泛型DDOS攻击");
        assertTrue(templateService.updateTemplate(3, template));
    }

    @Test
    void testAddTemplateUsedTimes() {
        List<Template> templates = templateService.queryTemplates("洪泛型DDOS攻击", 1, 1);
        Template originalTemplate = templates.get(0);
        Boolean success = templateService.addTemplateUsedTimes(3, 4);
        templates = templateService.queryTemplates("洪泛型DDOS攻击", 1, 1);
        assertEquals(templates.get(0).getTemplateUsedTimes(), originalTemplate.getTemplateUsedTimes() + 4);
        assertTrue(success);
    }
}