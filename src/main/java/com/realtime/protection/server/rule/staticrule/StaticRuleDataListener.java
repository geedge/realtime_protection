package com.realtime.protection.server.rule.staticrule;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.user.UserFull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StaticRuleDataListener implements ReadListener<StaticRuleObject> {
    private static final int batchCount = 100;
    private final List<StaticRuleObject> cachedDataList
            = ListUtils.newArrayListWithExpectedSize(batchCount);

    private final StaticRuleService staticRuleService;
    private UserFull user;

    public StaticRuleDataListener(StaticRuleService staticRuleService, UserFull user) {
        this.staticRuleService = staticRuleService;
        this.user = user;
    }

    @Override
    public void invoke(StaticRuleObject object, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", object.toString());
        if (!staticRuleService.isIpMaskValid(object.getStaticRuleSip(),object.getStaticRuleMsip()) ||
                !staticRuleService.isIpMaskValid(object.getStaticRuleDip(),object.getStaticRuleMdip())
        ){
            throw new IllegalArgumentException("IP和IP掩码不匹配!静态规则名称:" +
                                                object.getStaticRuleName() + "，源ip:" +
                                                object.getStaticRuleSip() + "，源ip掩码:" +
                                                object.getStaticRuleMsip() + "，目的ip:" +
                                                object.getStaticRuleDip() + "，目的ip掩码:" +
                                                object.getStaticRuleMdip() );
        }
        if (user != null) {
            object.setStaticRuleCreateUsername(user.name);
            object.setStaticRuleCreateUserId(Integer.valueOf(user.uid));
            object.setStaticRuleCreateDepart(user.getOrgName());
        }
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
            throw new RuntimeException("批量导入静态规则存储数据库时出现错误");
        }
    }
}
