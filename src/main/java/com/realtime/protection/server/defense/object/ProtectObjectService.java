package com.realtime.protection.server.defense.object;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class ProtectObjectService {
    private final ProtectObjectMapper protectObjectMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private static final Integer batchSize = 100;

    public ProtectObjectService(ProtectObjectMapper protectObjectMapper, SqlSessionWrapper sqlSessionWrapper) {
        this.protectObjectMapper = protectObjectMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
    }

    public Integer newProtectObject(ProtectObject protectObject) {
        protectObjectMapper.newProtectObject(protectObject);

        if (protectObject.getProtectObjectId() == null) {
            return 0;
        }
        return protectObject.getProtectObjectId();
    }

    public Boolean newProtectObjects(List<ProtectObject> protectObjectList) {
        Function<ProtectObjectMapper, Function<List<ProtectObject>, Boolean>> newProtectObjectFunction = mapper -> list -> {
            if (list == null || list.isEmpty()) {
                return false;
            }

            List<ProtectObject> protectObjectBatch = ListUtils.newArrayListWithExpectedSize(batchSize);
            for (ProtectObject protectObject : list) {
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
}
