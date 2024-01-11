package com.realtime.protection.server.whitelist;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WhiteListMapper {

    //新建
    void newWhiteListObject(@Param("object") WhiteListObject object);

    //分页查询
    List<WhiteListObject> queryWhiteListObject(String whiteListName, Integer whiteListId, Integer page, Integer pageSize);

    //根据主键查询
    WhiteListObject queryWhiteListObjectById(Integer id);

    //根据主键删除
    @Delete("delete from t_white_list where white_list_id = #{id}")
    Integer deleteWhiteListObject(Integer id);

    Integer updateWhiteListObject(@Param("object") WhiteListObject object);

    String existWhiteListObject(@Param("staticRuleObject") StaticRuleObject staticRuleObject);

    Integer queryWhiteListObjectAuditStuatusById(Integer id);

    Boolean updateWhiteListObjectAuditStatus(Integer id, Integer status);

    void newWhiteListObjects(@Param("whiteListObjects") List<WhiteListObject> whiteListBatch);

    void deleteWhiteListObjects(@Param("whiteListIds") List<Integer> whiteListBatch);
}
