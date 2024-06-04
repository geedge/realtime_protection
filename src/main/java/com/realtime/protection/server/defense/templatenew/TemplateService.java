package com.realtime.protection.server.defense.templatenew;

import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
public class TemplateService {

    private final TemplateMapper templateMapper;
    private final Counter counter;
    private final SqlSessionWrapper sqlSessionWrapper;

    public TemplateService(TemplateMapper templateMapper, Counter counter, SqlSessionWrapper sqlSessionWrapper) {
        this.templateMapper = templateMapper;
        this.counter = counter;
        this.sqlSessionWrapper = sqlSessionWrapper;
    }


    public Integer newTemplate( TemplateNew template) {
        template.setTemplateDisplayId(
                "CLMB-"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-"
                        + String.format("%06d", counter.generateId("strategy_template"))
        );

        templateMapper.newTemplate(template);

        if (template.getTemplateId() == null) {
            return 0;
        }
        insertStatusLog(template.getTemplateId());
        return template.getTemplateId();
    }

    public List<TemplateNew> queryTemplates(String templateName, String sourceSystem, String createUserName,
                                         Integer templateId, Integer auditStatus,
                                        String eventType, Integer protectLevel,
                                         Integer page, Integer pageSize) {
        return templateMapper.queryTemplates(templateName, sourceSystem, createUserName,
                templateId, auditStatus,eventType, protectLevel, page, pageSize);
    }

    public Boolean updateTemplate(Integer templateId, TemplateNew template) {
        template.setTemplateId(templateId);

        return templateMapper.updateTemplateInformation(template);
    }

    public TemplateNew queryTemplate(Integer templateId) {
        return templateMapper.queryTemplate(templateId);
    }



    public Boolean deleteTemplate(Integer templateId) {
        return templateMapper.deleteTemplate(templateId);
    }

    public Integer queryTemplateTotalNum(
            String templateName, String sourceSystem, String createUserName,
            Integer templateId, Integer auditStatus,
            String eventType, Integer protectLevel) {
        return templateMapper.queryTemplateTotalNum(templateName, sourceSystem, createUserName,
                templateId,auditStatus,eventType, protectLevel );
    }

    public List<String> querySourceSystem() {
        return templateMapper.querySourceSystem();
    }

//    public List<String> queryEventName(String sourceSystem) {
//        return templateMapper.queryEventName(sourceSystem);
//    }



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
        insertStatusLog(id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);
        return resultMap;
    }

    public Boolean updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = templateMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            throw new IllegalArgumentException("策略模板部分不存在");
        }
        int index = 0;
        List<Integer> errorIds = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry: idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            Integer originalAuditStatus = originalAuditStatusList.get(index);
            index++;
            if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()){
            throw new IllegalArgumentException("策略模板无法修改为对应审核状态, 错误id: " + errorIds);
        }

        Function<TemplateMapper, Function<Map<Integer, Integer>, Boolean>> updateTemplateAuditStatusFunction =
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
                        insertStatusLog(idWithAuditStatusBatch);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                        insertStatusLog(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(TemplateMapper.class, updateTemplateAuditStatusFunction, idsWithAuditStatusMap);

    }


    public Boolean updateAuditInfo(List<Integer> ids, String auditInfo) {
        return templateMapper.updateAuditInfo(ids, auditInfo);
    }

    public String queryAuditInfo(Integer id) {
        return templateMapper.queryAuditInfo(id);
    }

    public void insertStatusLog(Integer id) {
        templateMapper.updateStatusLogExpireTime(id);
        templateMapper.insertStatusLog(id);
    }
    public void insertStatusLog(Map<Integer, Integer> idWithAuditStatusBatch) {
        Set<Integer> keys = idWithAuditStatusBatch.keySet();
        ArrayList<Integer> ids = new ArrayList<>(keys);

        templateMapper.updateStatusLogExpireTimeBatch(ids);
        templateMapper.insertStatusLogBatch(ids);
    }

    public void insertStatusLog(List<StaticRuleObject> objects) {
        List<Integer> ids = new ArrayList<>();
        for (StaticRuleObject staticRuleObject : objects) {
            ids.add(staticRuleObject.getStaticRuleId());
        }

        templateMapper.updateStatusLogExpireTimeBatch(ids);
        templateMapper.insertStatusLogBatch(ids);
    }
}
