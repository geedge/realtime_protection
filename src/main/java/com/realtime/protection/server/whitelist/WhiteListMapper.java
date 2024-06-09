package com.realtime.protection.server.whitelist;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface WhiteListMapper {

    //新建
    void newWhiteListObject(@Param("object") WhiteListObject object);

    //分页查询
    List<WhiteListObject> queryWhiteListObject(String whiteListName, String whiteListId,
                                               String systemName, Integer auditStatus, String creator,
                                               Integer page, Integer pageSize);

    //根据主键查询
    WhiteListObject queryWhiteListObjectById(Integer id);

    //根据主键删除
    @Delete("delete from t_white_list where white_list_id = #{id}")
    Integer deleteWhiteListObject(Integer id);

    Integer updateWhiteListObject(@Param("id") Integer id, @Param("object") WhiteListObject object);

    List<String> existWhiteListObject(@Param("staticRuleObject") StaticRuleObject staticRuleObject);

    Integer queryWhiteListObjectAuditStuatusById(Integer id);

    Boolean updateWhiteListObjectAuditStatusWithUser(Integer id, Integer status, String auditUserName, Integer auditUserId, String auditUserDepart);

    Boolean updateWhiteListObjectAuditStatus(Integer id, Integer status);

    void newWhiteListObjects(@Param("whiteListObjects") List<WhiteListObject> whiteListBatch);

    void deleteWhiteListObjects(@Param("whiteListIds") List<Integer> whiteListBatch);

    List<WhiteListObject> whiteListCommandCheck(@Param("command") FiveTupleWithMask fiveTupleWithMaskInCommand);

    Integer queryWhiteListTotalNum(String whiteListName, String whiteListId
            , String systemName, Integer auditStatus, String creator);

    List<WhiteListObject> whiteListCommandsCheck(List<TaskCommandInfo> taskCommandInfos);

    List<WhiteListObject> whiteListCStaticRulesCheck(@Param("staticRuleObjects") List<StaticRuleObject> staticRuleObjects);

    Integer queryAuditWhiteListTotalNum(@Param("auditStatus") Integer auditStatus);

    void updateAuditStatusByIdBatch(@Param("idsWithAuditStatusMap") Map<Integer, Integer> idsWithAuditStatusMap);

    List<Integer> queryAuditStatusByIds(@Param("idsWithAuditStatusMap") Map<Integer, Integer> idsWithAuditStatusMap);

    Boolean updateAuditInfo(@Param("ids") List<Integer> ids, @Param("auditInfo") String auditInfo);

    String queryWhiteListObjectAuditInfo(Integer id);

    void updateStatusLogExpireTime(Integer id);

    void insertStatusLog(Integer id);

    void updateStatusLogExpireTimeBatch(List<Integer> ids);

    void insertStatusLogBatch(List<Integer> ids);

    List<WhiteListObject> queryHistory(Integer id, Integer page, Integer pageSize);


    void createCommandWhiteListConnect(@Param("command_id") String commandId, @Param("whiteLists") List<WhiteListObject> whiteListsHit);

    void updateAuditStatusByIdBatchWithUser(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch,
                                            @Param("auditUserName") String auditUserName,
                                            @Param("auditUserId") Integer auditUserId,
                                            @Param("auditUserDepart") String auditUserDepart);
}