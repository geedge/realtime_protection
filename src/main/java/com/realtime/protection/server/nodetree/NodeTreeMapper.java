package com.realtime.protection.server.nodetree;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//李福连

@Mapper
public interface NodeTreeMapper {

    @Select("select * from realtime_protection.t_judian_dict where is_deleted=0")
    List<NodeEntity> get();
}
