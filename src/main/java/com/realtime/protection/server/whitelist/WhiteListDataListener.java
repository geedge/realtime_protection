package com.realtime.protection.server.whitelist;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class WhiteListDataListener implements ReadListener<WhiteListObject> {

    private static final int batchCount = 100;
    private final List<WhiteListObject> cachedDataList = ListUtils.newArrayListWithExpectedSize(batchCount);

    private final WhiteListService whiteListService;

    public WhiteListDataListener(WhiteListService whiteListService) {
        this.whiteListService = whiteListService;
    }

    @Override
    public void invoke(WhiteListObject object, AnalysisContext analysisContext) {
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
        Boolean success = whiteListService.newWhiteListObjects(cachedDataList);
        log.info("存储数据库成功！");
        if (!success) {
            throw new RuntimeException("Error reading data in /proobj/new");
        }
    }
}
