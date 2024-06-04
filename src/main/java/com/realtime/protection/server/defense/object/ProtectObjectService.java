package com.realtime.protection.server.defense.object;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
public class ProtectObjectService {
    private final ProtectObjectMapper protectObjectMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private static final Integer batchSize = 100;
    private final Counter counter;

    public ProtectObjectService(ProtectObjectMapper protectObjectMapper, SqlSessionWrapper sqlSessionWrapper, Counter counter) {
        this.protectObjectMapper = protectObjectMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.counter = counter;
    }

    public Integer newProtectObject(ProtectObject protectObject) {
        protectObject.setProtectObjectDisplayId(
                "FHDX-"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-"
                        + String.format("%06d", counter.generateId("protect_object"))
        );

        protectObjectMapper.newProtectObject(protectObject);

        if (protectObject.getProtectObjectId() == null) {
            return 0;
        }
        insertStaticRuleStatusLog(protectObject.getProtectObjectId());
        return protectObject.getProtectObjectId();
    }

    public Boolean newProtectObjects(List<ProtectObject> protectObjectList) {
        Function<ProtectObjectMapper, Function<List<ProtectObject>, Boolean>> newProtectObjectFunction = mapper -> list -> {
            if (list == null || list.isEmpty()) {
                return false;
            }

            List<ProtectObject> protectObjectBatch = ListUtils.newArrayListWithExpectedSize(batchSize);
            for (ProtectObject protectObject : list) {
                protectObject.setProtectObjectDisplayId(
                        "FHDX-"
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                                + "-"
                                + String.format("%06d", counter.generateId("protect_object"))
                );

                protectObjectBatch.add(protectObject);
                if (protectObjectBatch.size() < batchSize) {
                    continue;
                }
                mapper.newProtectObjects(protectObjectBatch);
                protectObjectBatch.clear();
            }
            if (!protectObjectBatch.isEmpty()) {
                mapper.newProtectObjects(protectObjectBatch);
            }
            return true;
        };

        return sqlSessionWrapper.startBatchSession(ProtectObjectMapper.class, newProtectObjectFunction, protectObjectList);
    }

    public List<ProtectObject> queryProtectObjects(String protectObjectName, Integer protectObjectId,
                                                   String protectObjectSystemName, String protectObjectIPAddress,
                                                   Integer protectObjectPortMin, Integer protectObjectPortMax,
                                                   String protectObjectURL, String protectObjectProtocol,
                                                   String protectObjectCreateUsername, Integer protectObjectAuditStatus,
                                                   Integer page, Integer pageSize) {
        return protectObjectMapper.queryProtectObjects(
                protectObjectName, protectObjectId, protectObjectSystemName, protectObjectIPAddress,
                protectObjectPortMin, protectObjectPortMax, protectObjectURL, protectObjectProtocol,
                protectObjectCreateUsername, protectObjectAuditStatus,
                page, pageSize);
    }

    public ProtectObject queryProtectObject(Integer protectObjectId) {
        return protectObjectMapper.queryProtectObject(protectObjectId);
    }

    public Boolean updateProtectObject(ProtectObject protectObject) {
        if (!protectObjectMapper.queryProtectObject(protectObject.getProtectObjectId())
                .getProtectObjectAuditStatus()
                .equals(AuditStatusEnum.AUDITED.getNum())) {
            return false;
        }

        protectObject.setProtectObjectAuditStatus(AuditStatusEnum.PENDING.getNum());

        return protectObjectMapper.updateProtectObject(protectObject);
    }

    public Boolean deleteProtectObject(Integer protectObjectId) {
        return protectObjectMapper.deleteProtectObject(protectObjectId);
    }

    public Boolean deleteProtectObjects(List<Integer> protectObjectIds) {
        Function<ProtectObjectMapper, Function<List<Integer>, Boolean>> deleteProtectObjectFunction = mapper -> list -> {
            if (list == null || list.isEmpty()) {
                return false;
            }
            boolean success = true;

            List<Integer> protectObjectBatch = ListUtils.newArrayListWithExpectedSize(batchSize);
            for (Integer protectObjectId : list) {
                protectObjectBatch.add(protectObjectId);
                if (protectObjectBatch.size() < batchSize) {
                    continue;
                }
                mapper.deleteProtectObjects(protectObjectBatch);
                protectObjectBatch.clear();
            }
            if (!protectObjectBatch.isEmpty()) {
                mapper.deleteProtectObjects(protectObjectBatch);
            }
            return success;
        };

        return sqlSessionWrapper.startBatchSession(ProtectObjectMapper.class, deleteProtectObjectFunction, protectObjectIds);
    }

    @Transactional
    public Map<String, Object> changeProtectObjectAuditStatus(Integer protectObjectId, Integer auditStatus) {
        Integer originalAuditStatus = protectObjectMapper.queryProtectObject(protectObjectId).getProtectObjectAuditStatus();
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("无效的审核状态");
        }
        Boolean success = protectObjectMapper.changeProtectObjectAuditStatus(protectObjectId, auditStatus);

        insertStaticRuleStatusLog(protectObjectId);
        Integer auditStatusNow = protectObjectMapper.queryProtectObject(protectObjectId).getProtectObjectAuditStatus();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatusNow);

        return resultMap;
    }

    public Integer queryProtectObjectsTotalNum(String protectObjectName, Integer protectObjectId,
                                               String protectObjectSystemName, String protectObjectIPAddress,
                                               Integer protectObjectPortMin, Integer protectObjectPortMax,
                                               String protectObjectURL, String protectObjectProtocol,
                                               String protectObjectCreateUsername, Integer protectObjectAuditStatus) {
        return protectObjectMapper.queryProtectObjectsTotalNum(
                protectObjectName, protectObjectId, protectObjectSystemName, protectObjectIPAddress,
                protectObjectPortMin, protectObjectPortMax, protectObjectURL, protectObjectProtocol,
                protectObjectCreateUsername, protectObjectAuditStatus
        );
    }

    public Integer queryUsedProtectObjectsTotalNum() {
        return protectObjectMapper.queryUsedProtectObjectsTotalNum();
    }

    public Boolean updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = protectObjectMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            throw new IllegalArgumentException("防护对象部分不存在");
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
            throw new IllegalArgumentException("防护对象无法修改为对应审核状态, 错误id: " + errorIds);
        }

        Function<ProtectObjectMapper, Function<Map<Integer, Integer>, Boolean>> updateProtectObjectAuditStatusFunction =
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
                        insertStaticRuleStatusLog(idWithAuditStatusBatch);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                        insertStaticRuleStatusLog(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(ProtectObjectMapper.class, updateProtectObjectAuditStatusFunction, idsWithAuditStatusMap);

    }

    public Boolean updateAuditInfo(List<Integer> ids, String auditInfo) {
        return protectObjectMapper.updateAuditInfo(ids, auditInfo);
    }

    public String queryAuditInfo(Integer id) {
        return protectObjectMapper.queryProtectObjectAuditInfo(id);
    }

    public void insertStaticRuleStatusLog(Integer id) {
        //先把之前的状态日志过期，再插入新的状态日志
        protectObjectMapper.updateStaticRuleStatusLogExpireTime(id);
        protectObjectMapper.insertStaticRuleStatusLog(id);
    }
    public void insertStaticRuleStatusLog(Map<Integer, Integer> idWithAuditStatusBatch) {
        Set<Integer> keys = idWithAuditStatusBatch.keySet();
        ArrayList<Integer> ids = new ArrayList<>(keys);

        protectObjectMapper.updateStaticRuleStatusLogExpireTimeBatch(ids);
        protectObjectMapper.insertStaticRuleStatusLogBatch(ids);
    }

    public void insertStaticRuleStatusLog(List<StaticRuleObject> objects) {
        List<Integer> ids = new ArrayList<>();
        for (StaticRuleObject staticRuleObject : objects) {
            ids.add(staticRuleObject.getStaticRuleId());
        }

        protectObjectMapper.updateStaticRuleStatusLogExpireTimeBatch(ids);
        protectObjectMapper.insertStaticRuleStatusLogBatch(ids);
    }
}
