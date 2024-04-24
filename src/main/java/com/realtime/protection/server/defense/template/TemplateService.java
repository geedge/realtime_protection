package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemplateService {

    private final TemplateMapper templateMapper;

    public TemplateService(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    @Transactional
    public Integer newTemplate(Template template) {
        templateMapper.newProtectLevel(template.getProtectLevelLow());
        templateMapper.newProtectLevel(template.getProtectLevelMedium());
        templateMapper.newProtectLevel(template.getProtectLevelHigh());

        templateMapper.newTemplate(template);

        if (template.getTemplateId() == null) {
            return 0;
        }
        return template.getTemplateId();
    }

    public List<Template> queryTemplates(String templateName, String sourceSystem, String createUserName,
                                         Integer page, Integer pageSize) {
        return templateMapper.queryTemplates(templateName, sourceSystem, createUserName, page, pageSize);
    }

    public Boolean updateTemplate(Integer templateId, Template template) {
        template.setTemplateId(templateId);

        return templateMapper.updateTemplateInformation(template);
    }

    public Template queryTemplate(Integer templateId) {
        return templateMapper.queryTemplate(templateId);
    }

    public List<Template> queryTemplatesShort() {
        List<Template> templates = templateMapper.queryTemplates(null, null, null, 1, 10000);

        templates.forEach(template -> {
            try {
                template.setHasProtectLevel();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        templates.forEach(Template::shortenTemplate);

        return templates;
    }

    public Boolean deleteTemplate(Integer templateId) {
        return templateMapper.deleteTemplate(templateId);
    }

    public Integer queryTemplateTotalNum(String templateName, String sourceSystem, String createUserName) {
        return templateMapper.queryTemplateTotalNum(templateName, sourceSystem, createUserName);
    }

    public List<String> querySourceSystem() {
        return templateMapper.querySourceSystem();
    }

    public List<String> queryEventName(String sourceSystem) {
        return templateMapper.queryEventName(sourceSystem);
    }

    public Integer queryTemplateId(String sourceSystem, String eventName) {
        return templateMapper.queryTemplateId(sourceSystem, eventName);
    }

    public Integer queryUsedTemplateTotalNum() {
        return templateMapper.queryUsedTemplateTotalNum();
    }

    public Integer queryAuditTemplateTotalNum(Integer auditState) {
        return templateMapper.queryAuditTemplateTotalNum(auditState);
    }
}
