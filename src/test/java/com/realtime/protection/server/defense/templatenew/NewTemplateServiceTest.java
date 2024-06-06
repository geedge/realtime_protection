package com.realtime.protection.server.defense.templatenew;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NewTemplateServiceTest {

    private final TemplateService templateService;
    private TemplateNew template;
    private Long startTime;


    @Autowired
    NewTemplateServiceTest(TemplateService templateService) {
        this.templateService = templateService;
    }

    @BeforeEach
    void mockTemplate() {

        template = new TemplateNew();

        template.setTemplateName("反射型DDOS攻击");
        template.setSourceSystem("xxxx系统");
        template.setEventType("dsdos");
        template.setHasPeerIP(true);
        template.setProtectLevel("2");



        startTime = System.currentTimeMillis();
    }

    @AfterEach
    void summary() {
        Long endTime = System.currentTimeMillis();
        System.out.printf("total time: %d ms", endTime - startTime);
    }

    @Test
    void testNewTemplate() {
        try {
            Integer templateId = templateService.newTemplate(template);
            assertTrue(templateId > 0);
        } catch (DuplicateKeyException e) {
            System.out.println(e.getMessage());
        }

        for (int i = 0; i < 2; i++) {
            assertThrows(DuplicateKeyException.class, () -> {
                Integer templateId = templateService.newTemplate(template);
                assertTrue(templateId > 0);
            });
            assertDoesNotThrow(() -> {
                template.setTemplateName("反射型DDOS攻击-" + LocalDateTime.now());
                Integer templateId = templateService.newTemplate(template);
                assertTrue(templateId > 0);
            });
        }


    }
    @Test
    void testUpdateTemplateAuditStatus(){
        List<TemplateNew> templates = templateService.queryTemplates(
                null, null, null, null, null,null, null,1,3);
        TemplateNew testTemplate = templates.get(0);
        templateService.updateAuditStatus(testTemplate.getTemplateId(), 2, "sss",1111,"depart");
    }
}
