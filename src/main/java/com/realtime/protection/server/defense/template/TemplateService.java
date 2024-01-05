package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TemplateService {

    private final TemplateMapper templateMapper;

    private final String[] permittedOps = new String[]{"阻断", "清洗", "篡改", "反制"};

    public TemplateService(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    public Integer newTemplate(Template template) throws IllegalArgumentException {
        if (!Arrays.asList(permittedOps).contains(template.getDefaultOp())) {
            throw new IllegalArgumentException();
        }

        setTemplateElements(template);

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
        setTemplateElements(template);
        template.setTemplateId(templateId);

        return templateMapper.updateTemplateInformation(template);
    }

    private void setTemplateElements(Template template) {
        for (String choice : template.getTemplateElements()) {
            switch (choice) {
                case "防护对象IP" -> template.setHasProtectObjectIP(true);
                case "防护对象端口" -> template.setHasProtectObjectPort(true);
                case "对端IP" -> template.setHasPeerIP(true);
                case "对端端口" -> template.setHasPeerPort(true);
                case "协议" -> template.setHasProtocol(true);
                case "URL" -> template.setHasURL(true);
                case "DNS" -> template.setHasDNS(true);

                default -> throw new IllegalArgumentException();
            }
        }
    }

    public Boolean addTemplateUsedTimes(Integer templateId, Integer addTimes) {
        return templateMapper.addTemplateUsedTimes(templateId, addTimes);
    }

}
