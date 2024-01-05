package com.realtime.protection.server.defense.template;

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
        template.setTemplateElements(List.of("对端IP", "协议", "URL"));
        template.setDefaultOp("阻断");
    }

    @Test
    void testNewTemplateSuccess() {
        Integer templateId = templateService.newTemplate(template);
        assertTrue(templateId > 0);
    }

    @Test
    void testNewTemplateIllegalArgument() {
        template.setTemplateElements(List.of("DDNS"));
        assertThrows(IllegalArgumentException.class, () -> {
            Integer templateId = templateService.newTemplate(template);
            assertTrue(templateId > 0);
        });
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