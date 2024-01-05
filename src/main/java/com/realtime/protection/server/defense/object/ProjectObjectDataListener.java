package com.realtime.protection.server.defense.object;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProjectObjectDataListener implements ReadListener<ProtectObject> {

    private final ProtectObjectService protectObjectService;
    private final List<ProtectObject> cachedDataList = ListUtils.newArrayListWithExpectedSize(batchCount);
    private static final int batchCount = 100;

    public ProjectObjectDataListener(ProtectObjectService protectObjectService) {
        this.protectObjectService = protectObjectService;
    }

    @Override
    public void invoke(ProtectObject protectObject, AnalysisContext analysisContext) {
        cachedDataList.add(protectObject);
        if (cachedDataList.size() > 1000) {
            saveData();
            cachedDataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }

    private void saveData() {
        Boolean success = protectObjectService.newProtectObjects(cachedDataList);
        if (!success) {
            throw new RuntimeException("Error reading data in newProtectObjects");
        }
    }
}
