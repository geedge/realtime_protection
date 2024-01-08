package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.Arrays;
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

    public List<Template> queryTemplates(String templateName, Integer page, Integer pageSize) {
        return templateMapper.queryTemplates(templateName, page, pageSize);
    }

    public Boolean updateTemplate(Integer templateId, Template template) {
        template.setTemplateId(templateId);

        return templateMapper.updateTemplateInformation(template);
    }

    public Boolean addTemplateUsedTimes(Integer templateId, Integer addTimes) {
        return templateMapper.addTemplateUsedTimes(templateId, addTimes);
    }

}
