package com.realtime.protection.server.defense.object;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProtectObjectService {
    private final ProtectObjectMapper protectObjectMapper;

    public ProtectObjectService(ProtectObjectMapper protectObjectMapper) {
        this.protectObjectMapper = protectObjectMapper;
    }

    public Integer newProtectObject(ProtectObject protectObject) {
        protectObjectMapper.newProtectObject(protectObject);

        if (protectObject.getProtectObjectId() == null) {
            return 0;
        }
        return protectObject.getProtectObjectId();
    }

    @Transactional
    public List<Integer> newProtectObjects(List<ProtectObject> protectObjectList) {
        protectObjectList.forEach(protectObjectMapper::newProtectObject);
        return protectObjectList.stream().map(ProtectObject::getProtectObjectId).collect(Collectors.toList());
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

    @Transactional
    public Boolean deleteProtectObjects(List<Integer> protectObjectIds) {
        return protectObjectIds.stream().allMatch(protectObjectMapper::deleteProtectObject);
    }
}
