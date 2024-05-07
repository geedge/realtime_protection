package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                         Integer templateId, Integer auditStatus,
                                         Integer page, Integer pageSize) {
        return templateMapper.queryTemplates(templateName, sourceSystem, createUserName, templateId, auditStatus, page, pageSize);
    }

    public Boolean updateTemplate(Integer templateId, Template template) {
        template.setTemplateId(templateId);

        return templateMapper.updateTemplateInformation(template);
    }

    public Template queryTemplate(Integer templateId) {
        return templateMapper.queryTemplate(templateId);
    }

    public List<Template> queryTemplatesShort() {
        List<Template> templates = templateMapper.queryTemplates(null, null, null, null, null,1, 10000);

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

    public Map<String, Object> updateAuditStatus(Integer id, Integer auditStatus) {
        Integer originalAuditStatus = templateMapper.queryAuditStatusById(id);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("cannot find audit status of static rule " + id + ", maybe static rule doesn't exist?");
        }
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }
        Boolean success = templateMapper.updateAuditStatusById(id, auditStatus);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);
        return resultMap;
    }
/*

    public Map<String, Object> updateAuditStatus(Integer id, Integer auditStatus) {
        Integer originalAuditStatus = templateMapper.queryAuditStatusById(id);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("cannot find audit status of static rule " + id + ", maybe static rule doesn't exist?");
        }
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }
        Boolean success = templateMapper.updateAuditStatusById(id, auditStatus);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);
        return resultMap;
    }

    public Object updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        Function<DynamicRuleMapper, Function<Map<Integer, Integer>, Boolean>> updateDynamicRuleAuditStatusFunction =
                mapper -> map -> {
                    if (map == null || map.isEmpty()) {
                        return false;
                    }

                    Map<Integer, Integer> idWithAuditStatusBatch = new HashMap<>();
                    for (Map.Entry<Integer, Integer> item : map.entrySet()) {
                        idWithAuditStatusBatch.put(item.getKey(), item.getValue());
                        if (idWithAuditStatusBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(DynamicRuleMapper.class, updateDynamicRuleAuditStatusFunction, idsWithAuditStatusMap);

    }
    */

}
