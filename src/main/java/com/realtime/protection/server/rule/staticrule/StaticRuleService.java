package com.realtime.protection.server.rule.staticrule;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.RuleEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import com.realtime.protection.server.whitelist.WhiteListService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.*;
import java.util.function.Function;

@Service
public class StaticRuleService {

    private final StaticRuleMapper staticRuleMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private final WhiteListService whiteListService;
    private final Counter counter;


    public StaticRuleService(StaticRuleMapper staticRuleMapper, SqlSessionWrapper sqlSessionWrapper, WhiteListService whiteListService, Counter counter) {
        this.staticRuleMapper = staticRuleMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.whiteListService = whiteListService;
        this.counter = counter;
    }

    private static int ipToInt(String ip)  {
        try {
            byte[] bytes = InetAddress.getByName(ip).getAddress();
            return ByteBuffer.wrap(bytes).getInt();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public Boolean isIpMaskValid(String ip, String mip)  {
        if (ip == null && mip != null)  throw new IllegalArgumentException("有ip掩码但没设置ip");
        if (mip == null) return true;

        int ipToInt = ipToInt(ip);
        int mipToInt = ipToInt(mip);
        return ( ipToInt == (ipToInt & mipToInt) ) ;
    }


    /*
    新建静态规则
     */
    @SneakyThrows
    public Integer newStaticRuleObject(StaticRuleObject object)  {

        object.setStaticRuleCreateTime(LocalDateTime.now());
        object.setAuditStatus(0);
        /*
        待开发：设置静态规则对象的创建用户、用户所属部门等属性
         */
        if (!isIpMaskValid(object.getStaticRuleSip(),object.getStaticRuleMsip()) ||
                !isIpMaskValid(object.getStaticRuleDip(),object.getStaticRuleMdip())
            ){
            throw new IllegalArgumentException("IP和IP掩码不匹配");
        }
        if (!RuleEnum.checkValidate(object)) {
            throw new IllegalArgumentException("静态规则不符合指定的配置方法，请参考规则模板以配置静态规则");
        }
        /*
        新建静态规则，过一下白名单审核
         */
        List<StaticRuleObject> staticRuleObjects = new ArrayList<>();
        staticRuleObjects.add(object);
        List<WhiteListObject> whiteListsHit = whiteListService.whiteListStaticRulesObjectCheck(staticRuleObjects);
        if (!whiteListsHit.isEmpty()) {
            StringBuilder result = new StringBuilder();
            whiteListsHit.forEach(item -> result.append(item.getWhiteListName()).append(" "));
            throw new IllegalArgumentException("静态规则与白名单规则冲突，冲突白名单名称:"+result.toString().trim());
        }

        object.setStaticRuleDisplayId(
                "JTGZ-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-"
                + String.format("%06d", counter.generateId("static_rule"))
        );

        staticRuleMapper.newStaticRuleObject(object);

        return object.getStaticRuleId();
    }

    public Object deleteStaticRules(List<Integer> staticRuleIds) {
        //判断当前静态规则是否能够删除---是否存在任务选择的静态规则？？

        //删除静态规则
//        for (Integer id : staticRuleIds) {
//            staticRuleMapper.deleteStaticRule(id);
//        }
        Function<StaticRuleMapper, Function<List<Integer>, Boolean>> deleteStaticRulesFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<Integer> staticRuleBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (Integer staticRuleId : list) {
                        staticRuleBatch.add(staticRuleId);
                        if (staticRuleBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.deleteStaticRules(staticRuleBatch);
                        staticRuleBatch.clear();
                    }
                    if (!staticRuleBatch.isEmpty()) {
                        mapper.deleteStaticRules(staticRuleBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(StaticRuleMapper.class, deleteStaticRulesFunction, staticRuleIds);
    }

    @SneakyThrows
    @Transactional
    public Boolean updateStaticRule(Integer id, StaticRuleObject object) {
        StaticRuleObject staticRuleObject = staticRuleMapper.queryStaticRuleById(id);
        if (staticRuleObject == null) {
            throw new IllegalArgumentException("未知的静态规则ID");
        }

        if (!staticRuleObject.getAuditStatus().equals(AuditStatusEnum.AUDITED.getNum())) {
            throw new IllegalStateException("无法修改该静态规则，因为其审核状态未处于" + AuditStatusEnum.AUDITED);
        }

        if (!RuleEnum.checkValidate(object)) {
            throw new IllegalArgumentException("静态规则不符合指定的配置方法，请参考规则模板以配置静态规则");
        }

        List<StaticRuleObject> staticRuleObjects = new ArrayList<>();
        staticRuleObjects.add(object);
        List<WhiteListObject> whiteListsHit = whiteListService.whiteListStaticRulesObjectCheck(staticRuleObjects);
        if (!whiteListsHit.isEmpty()) {
            StringBuilder result = new StringBuilder();
            whiteListsHit.forEach(item -> result.append(item.getWhiteListName()).append(" "));
            throw new IllegalArgumentException("静态规则与白名单规则冲突，冲突白名单名称:"+result.toString().trim());
        }

        //判断当前静态规则是否能够修改---是否存在任务选择的静态规则？？
        //按id查询该静态规则的used_task_id字段，如果不为空，则不能修改
        object.setStaticRuleModifyTime(LocalDateTime.now());
        object.setAuditStatus(AuditStatusEnum.PENDING.getNum());
        //修改静态规则
        return staticRuleMapper.updateStaticRule(id, object);
    }

    public StaticRuleObject queryStaticRuleById(Integer id) {

        return staticRuleMapper.queryStaticRuleById(id);
    }

    /*
    分页查询
     */
    public List<StaticRuleObject> queryStaticRule(
            String static_rule_name, Integer static_rule_id,
            String creator,
            String static_rule_sip, Integer static_rule_sport,
            String static_rule_dip, Integer static_rule_dport,
            Integer auditStatus,
            Integer page, Integer pageSize) {

        return staticRuleMapper.queryStaticRule(static_rule_name, static_rule_id,
                creator, static_rule_sip, static_rule_sport, static_rule_dip, static_rule_dport, auditStatus, page, pageSize);
    }

    public Boolean newStaticRuleObjects(List<StaticRuleObject> staticRuleList) {
        Function<StaticRuleMapper, Function<List<StaticRuleObject>, Boolean>> newStaticRuleFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<StaticRuleObject> StaticRuleBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (StaticRuleObject staticRule : list) {
//                        staticRule.setStaticRuleCreateTime(LocalDateTime.now());
                        staticRule.setStaticRuleDisplayId(
                                "JTGZ-"
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                                + "-"
                                + String.format("%06d", counter.generateId("static_rule"))
                        );
                        StaticRuleBatch.add(staticRule);
                        if (StaticRuleBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.newStaticRules(StaticRuleBatch);
                        StaticRuleBatch.clear();
                    }
                    if (!StaticRuleBatch.isEmpty()) {
                        mapper.newStaticRules(StaticRuleBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(StaticRuleMapper.class, newStaticRuleFunction, staticRuleList);


    }


    @Transactional
    public Map<String, Object> updateAuditStatus(Integer id, Integer auditStatus,
                                                 String auditUserName,
                                                 Integer auditUserId,
                                                 String auditUserDepart) {
        Integer originalAuditStatus = staticRuleMapper.queryAuditStatusById(id);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("cannot find audit status of static rule " + id + ", maybe static rule doesn't exist?");
        }
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }
        Boolean success = staticRuleMapper.updateAuditStatusById(id, auditStatus, auditUserName, auditUserId, auditUserDepart);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);
        return resultMap;
    }


    @Transactional
    public void deleteStaticRuleById(Integer id) {
        StaticRuleObject staticRuleObject = staticRuleMapper.queryStaticRuleById(id);
        if (staticRuleObject == null) {
            return;
        }
        if (Objects.equals(staticRuleObject.getAuditStatus(), AuditStatusEnum.USING.getNum())) {
            throw new IllegalArgumentException("当前静态规则正在使用，无法删除");
        }
        staticRuleMapper.deleteStaticRuleById(id);
    }

    public Integer queryStaticRuleTotalNum(String static_rule_name, Integer static_rule_id,
                                           String static_rule_creator,
                                           String static_rule_sip, Integer static_rule_sport,
                                           String static_rule_dip, Integer static_rule_dport,
                                           Integer auditStatus) {
        return staticRuleMapper.queryStaticRuleTotalNum(static_rule_name, static_rule_id,
                static_rule_creator, static_rule_sip, static_rule_sport,
                static_rule_dip, static_rule_dport, auditStatus);
    }

    public List<StaticRuleObject> queryStaticRuleByIds(List<Integer> ids) {
        return staticRuleMapper.queryStaticRuleByIds(ids);
    }

    public Boolean updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap,
                                          String auditUserName, Integer auditUserId, String auditUserDepart ) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = staticRuleMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            throw new IllegalArgumentException("部分静态规则id不存在");
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
            throw new IllegalArgumentException("静态规则id无法修改为对应审核状态, 错误id: " + errorIds);
        }

        Function<StaticRuleMapper, Function<Map<Integer, Integer>, Boolean>> updateStaticRuleAuditStatusFunction =
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
                        mapper.updateAuditStatusWithAuditorByIdBatch(idWithAuditStatusBatch,
                                auditUserName, auditUserId, auditUserDepart);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusWithAuditorByIdBatch(idWithAuditStatusBatch,
                                auditUserName, auditUserId, auditUserDepart);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(StaticRuleMapper.class, updateStaticRuleAuditStatusFunction, idsWithAuditStatusMap);
    }


    public Boolean updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap
                                          ) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = staticRuleMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            throw new IllegalArgumentException("部分静态规则id不存在");
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
            throw new IllegalArgumentException("静态规则id无法修改为对应审核状态, 错误id: " + errorIds);
        }

        Function<StaticRuleMapper, Function<Map<Integer, Integer>, Boolean>> updateStaticRuleAuditStatusFunction =
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
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(StaticRuleMapper.class, updateStaticRuleAuditStatusFunction, idsWithAuditStatusMap);
    }

    public Integer queryUsedStaticRuleTotalNum() {
        return staticRuleMapper.queryUsedStaticRuleTotalNum();
    }

    public List<Integer>  queryAuditStatusByIds(Map<Integer, Integer> idsWithAuditStatusMap) {
        List<Integer> originalAuditStatusList = staticRuleMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        return originalAuditStatusList;
    }

    public Integer queryAuditStaticRuleTotalNum(Integer auditStatus) {
        return staticRuleMapper.queryAuditStaticRuleTotalNum(auditStatus);
    }

    public Boolean updateAuditInfo(List<Integer> ids, String auditInfo) {
        return staticRuleMapper.updateAuditInfo(ids, auditInfo);
    }

    public String queryAuditInfo(Integer id) {
        return staticRuleMapper.queryAuditInfo(id);
    }
}
