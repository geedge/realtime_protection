package com.realtime.protection.server.defense.object;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProtectObjectMapper {
    void newProtectObject(@Param("proobj") ProtectObject protectObject);

    List<ProtectObject> queryProtectObjects(@Param("proobj_name") String protectObjectName,
                                            @Param("proobj_id") Integer protectObjectId,
                                            @Param("page") Integer page,
                                            @Param("page_size") Integer pageSize);

    ProtectObject queryProtectObject(@Param("proobj_id") Integer protectObjectId);

    Boolean updateProtectObject(@Param("proobj") ProtectObject protectObject);

    Boolean deleteProtectObject(@Param("proobj_id") Integer protectObjectId);

    Boolean changeProtectObjectAuditStatus(@Param("proobj_audit_status") Integer protectObjectAuditStatus);
}
