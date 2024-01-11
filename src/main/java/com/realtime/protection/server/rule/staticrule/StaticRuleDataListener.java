package com.realtime.protection.server.rule.staticrule;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StaticRuleDataListener implements ReadListener<StaticRuleObject> {
    private static final int batchCount = 100;
    private final List<StaticRuleObject> cachedDataList
            = ListUtils.newArrayListWithExpectedSize(batchCount);

    private final StaticRuleService staticRuleService;

    public StaticRuleDataListener(StaticRuleService staticRuleService) {
        this.staticRuleService = staticRuleService;
    }

    @Override
    public void invoke(StaticRuleObject object, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", object.toString());
        cachedDataList.add(object);
        if (cachedDataList.size() > batchCount) {
            saveData();
            cachedDataList.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        Boolean success = staticRuleService.newStaticRuleObjects(cachedDataList);
        log.info("存储数据库成功！");
        if (!success) {
            throw new RuntimeException("Error reading data in /proobj/new");
        }
    }
}
