package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        template.setTemplateElements(new String[]{"对端IP", "协议", "URL"});
        template.setDefaultOp("阻断");
    }

    @Test
    void testNewTemplateSuccess() {
        Integer templateId = templateService.newTemplate(template);
        assertTrue(templateId > 0);
    }

    @Test
    void testNewTemplateIllegalArgument() {
        template.setTemplateElements(new String[]{"DDNS"});
        assertThrows(IllegalArgumentException.class, () -> {
            Integer templateId = templateService.newTemplate(template);
            assertTrue(templateId > 0);
        });
    }
}