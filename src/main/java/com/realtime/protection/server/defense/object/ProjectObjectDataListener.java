package com.realtime.protection.server.defense.object;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.user.UserFull;

import java.util.List;

public class ProjectObjectDataListener implements ReadListener<ProtectObject> {

    private final ProtectObjectService protectObjectService;
    private final List<ProtectObject> cachedDataList = ListUtils.newArrayListWithExpectedSize(batchCount);
    private static final int batchCount = 100;
    private UserFull user;

    public ProjectObjectDataListener(ProtectObjectService protectObjectService, UserFull user) {
        this.protectObjectService = protectObjectService;
        this.user = user;
    }

    @Override
    public void invoke(ProtectObject protectObject, AnalysisContext analysisContext) {
        if (user != null) {
            protectObject.setProtectObjectCreateUsername(user.name);
            protectObject.setProtectObjectCreateUserId(Integer.valueOf(user.uid));
            protectObject.setProtectObjectCreateDepart(user.getOrgName());
        }
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
            throw new RuntimeException("在/proobj/upload中读取数据时出现了错误");
        }
    }
}
