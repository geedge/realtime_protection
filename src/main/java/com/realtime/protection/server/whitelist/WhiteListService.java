package com.realtime.protection.server.whitelist;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import com.realtime.protection.server.rule.staticrule.StaticRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class WhiteListService {

    private final WhiteListMapper whiteListMapper;
    private final StaticRuleMapper staticRuleMapper;

    private final SqlSessionWrapper sqlSessionWrapper;

    public WhiteListService(WhiteListMapper whiteListMapper,
                            StaticRuleMapper staticRuleMapper,
                            SqlSessionWrapper sqlSessionWrapper) {
        this.whiteListMapper = whiteListMapper;
        this.staticRuleMapper = staticRuleMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
    }

    //新建一个whitelist
    public Integer newWhiteListObject(WhiteListObject object) {

        whiteListMapper.newWhiteListObject(object);

        return object.getWhiteListId();
    }

    //批量新建多个whitelist
    public Boolean newWhiteListObjects(List<WhiteListObject> whiteListObjectList) {
        Function<WhiteListMapper, Function<List<WhiteListObject>, Boolean>> newWhiteListObjectFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<WhiteListObject> WhiteListBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (WhiteListObject whiteListObject : whiteListObjectList) {
                        WhiteListBatch.add(whiteListObject);
                        if (WhiteListBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.newWhiteListObjects(WhiteListBatch);
                        WhiteListBatch.clear();
                    }
                    if (!WhiteListBatch.isEmpty()) {
                        mapper.newWhiteListObjects(WhiteListBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(WhiteListMapper.class, newWhiteListObjectFunction, whiteListObjectList);
    }

    public Integer deleteWhiteListObjectById(Integer id) {
        return whiteListMapper.deleteWhiteListObject(id);
    }

    //@Transactional
    public Boolean deleteWhiteListObjects(List<Integer> whiteListObjIds) {
//        for (Integer id : whiteobj_ids) {
//            whiteListMapper.deleteWhiteListObject(id);
//        }
        Function<WhiteListMapper, Function<List<Integer>, Boolean>> deleteWhiteListObjectFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<Integer> WhiteListBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (Integer whiteListObjId : list) {
                        WhiteListBatch.add(whiteListObjId);
                        if (WhiteListBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.deleteWhiteListObjects(WhiteListBatch);
                        WhiteListBatch.clear();
                    }
                    if (!WhiteListBatch.isEmpty()) {
                        mapper.deleteWhiteListObjects(WhiteListBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(WhiteListMapper.class, deleteWhiteListObjectFunction, whiteListObjIds);

    }

    public Integer updateWhiteListObject(Integer id, WhiteListObject object) {
        if (!whiteListMapper.queryWhiteListObjectAuditStuatusById(id).equals(AuditStatusEnum.AUDITED.getNum())) {
            throw new IllegalStateException("无法修改白名单信息，因为其并未处于" + AuditStatusEnum.AUDITED + "状态");
        }

        object.setWhiteListAuditStatus(AuditStatusEnum.PENDING.getNum());

        return whiteListMapper.updateWhiteListObject(id, object);
    }

    public List<WhiteListObject> queryWhiteListObject(String whiteListName, Integer whiteListId,
                                                      Integer page, Integer pageSize) {
        return whiteListMapper.queryWhiteListObject(whiteListName, whiteListId, page, pageSize);
    }

    public WhiteListObject queryWhiteListObjectById(Integer id) {
        return whiteListMapper.queryWhiteListObjectById(id);
    }

    @Transactional
    public List<String> existWhiteListObject(Integer ruleId, Integer ruleType) {
        //应该参数是指令，不管动态静态

        //查询ruleId应对的静态or动态规则
        StaticRuleObject staticRuleObject = staticRuleMapper.queryStaticRuleById(ruleId);

        if (staticRuleObject == null) {
            throw new IllegalArgumentException("invalid rule id");
        }
        // 命中的whitelist列表：每一列包含ip port url
        return whiteListMapper.existWhiteListObject(staticRuleObject);

    }

    public List<WhiteListObject> whiteListCommandCheck(TaskCommandInfo taskCommandInfo) {
        //参数应该是指令，不管动态静态
        // 命中的whitelist列表：每一列包含ip port url
        return whiteListMapper.whiteListCommandCheck(taskCommandInfo.getFiveTupleWithMask());

    }


    public List<WhiteListObject> whiteListCommandsCheck(List<TaskCommandInfo> taskCommandInfoList) {
        //参数应该是指令，不管动态静态
        // 命中的whitelist列表：每一列包含ip port url
        return whiteListMapper.whiteListCommandsCheck(taskCommandInfoList);

    }

    public List<WhiteListObject> whiteListStaticRulesCheck(List<Integer> staticRuleId) {
        //参数应该是指令，不管动态静态
        // 命中的whitelist列表：每一列包含ip port url
        List<StaticRuleObject> staticRuleObjects = staticRuleMapper.queryStaticRuleByIds(staticRuleId);
        return whiteListMapper.whiteListCStaticRulesCheck(staticRuleObjects);

    }
    public List<WhiteListObject> whiteListStaticRulesObjectCheck(List<StaticRuleObject> staticRuleObjects) {
        //参数应该是指令，不管动态静态
        // 命中的whitelist列表：每一列包含ip port url
        return whiteListMapper.whiteListCStaticRulesCheck(staticRuleObjects);

    }

    @Transactional
    public Map<String, Object> updateWhiteListObjectAuditStatus(Integer id, Integer auditStatus) {
        //查询目前curStatus
        Integer originalAuditStatus = whiteListMapper.queryWhiteListObjectAuditStuatusById(id);
        if (originalAuditStatus == null){
            throw new IllegalArgumentException("cannot find audit status of whitelist " + id + ", maybe whitelist doesn't exist?");
        }
        //判断是否可以修改
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }

        Boolean success = whiteListMapper.updateWhiteListObjectAuditStatus(id, auditStatus);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);

        return resultMap;
    }

    public Integer queryWhiteListTotalNum(String whiteListName, Integer whiteListId){
        return whiteListMapper.queryWhiteListTotalNum(whiteListName, whiteListId);
    }


    public Integer queryAuditWhiteListTotalNum(Integer auditStatus) {
        return whiteListMapper.queryAuditWhiteListTotalNum(auditStatus);
    }
}
