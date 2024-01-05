package com.realtime.protection.server.defense.object;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.utils.AuditStatusValidator;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProtectObjectService {
    private final ProtectObjectMapper protectObjectMapper;
    private final SqlSessionWrapper sqlSessionWrapper;

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
            List<ProtectObject> protectObjectBatch = ListUtils.newArrayListWithExpectedSize(100);
            for (ProtectObject protectObject : protectObjectList) {
                protectObjectBatch.add(protectObject);
                if (protectObjectBatch.size() < 1000) {
                    continue;
                }
                mapper.newProtectObjects(protectObjectBatch);
                protectObjectBatch.clear();
            }
            mapper.newProtectObjects(protectObjectBatch);
            return true;
        };

        return sqlSessionWrapper.startBatchSession(ProtectObjectMapper.class, newProtectObjectFunction, protectObjectList);
    }

    public List<ProtectObject> queryProtectObjects(String protectObjectName, Integer protectObjectId, Integer page, Integer pageSize) {
        return protectObjectMapper.queryProtectObjects(protectObjectName, protectObjectId, page, pageSize);
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
        Function<ProtectObjectMapper, Function<List<Integer>, Void>> deleteProtectObjectFunction = mapper -> list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }

            List<Integer> protectObjectBatch = ListUtils.newArrayListWithExpectedSize(100);
            for (Integer protectObjectId : list) {
                protectObjectBatch.add(protectObjectId);
                if (protectObjectBatch.size() < 100) {
                    continue;
                }
                mapper.deleteProtectObjects(protectObjectIds);
                protectObjectBatch.clear();
            }
            mapper.deleteProtectObjects(protectObjectBatch);
            return null;
        };

        sqlSessionWrapper.startBatchSession(ProtectObjectMapper.class, deleteProtectObjectFunction, protectObjectIds);
        return true;
    }

    @Transactional
    public Map<String, Object> changeProtectObjectAuditStatus(Integer protectObjectId, Integer auditStatus) {
        Integer originalAuditStatus = protectObjectMapper.queryProtectObject(protectObjectId).getProtectObjectAuditStatus();
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }
        Boolean success = protectObjectMapper.changeProtectObjectAuditStatus(protectObjectId, auditStatus);

        Integer auditStatusNow = protectObjectMapper.queryProtectObject(protectObjectId).getProtectObjectAuditStatus();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatusNow);

        return resultMap;
    }
}
