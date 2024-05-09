package com.realtime.protection.server.defense.object;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.response.ResponseResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProtectObjectMapper {
    void newProtectObject(@Param("proobj") ProtectObject protectObject);

    void newProtectObjects(@Param("proobjs") List<ProtectObject> protectObject);


    ProtectObject queryProtectObject(@Param("proobj_id") Integer protectObjectId);

    Boolean updateProtectObject(@Param("proobj") ProtectObject protectObject);

    Boolean deleteProtectObject(@Param("proobj_id") Integer protectObjectId);

    void deleteProtectObjects(@Param("proobj_ids") List<Integer> protectObjectIds);

    Boolean changeProtectObjectAuditStatus(@Param("proobj_id") Integer protectObjectId,
                                           @Param("proobj_audit_status") Integer protectObjectAuditStatus);


    Integer queryProtectObjectsTotalNum(@Param("proobj_name") String protectObjectName,
                                        @Param("proobj_id") Integer protectObjectId,
                                        @Param("proobj_system_name") String protectObjectSystemName,
                                        @Param("proobj_ip") String protectObjectIPAddress,
                                        @Param("proobj_port_min") Integer protectObjectPortMin,
                                        @Param("proobj_port_max") Integer protectObjectPortMax,
                                        @Param("proobj_url") String protectObjectURL,
                                        @Param("proobj_protocol") String protectObjectProtocol,
                                        @Param("proobj_username") String protectObjectCreateUsername,
                                        @Param("proobj_audit_status") Integer protectObjectAuditStatus);

    List<ProtectObject> queryProtectObjects(@Param("proobj_name") String protectObjectName,
                                            @Param("proobj_id") Integer protectObjectId,
                                            @Param("proobj_system_name") String protectObjectSystemName,
                                            @Param("proobj_ip") String protectObjectIPAddress,
                                            @Param("proobj_port_min") Integer protectObjectPortMin,
                                            @Param("proobj_port_max") Integer protectObjectPortMax,
                                            @Param("proobj_url") String protectObjectURL,
                                            @Param("proobj_protocol") String protectObjectProtocol,
                                            @Param("proobj_username") String protectObjectCreateUsername,
                                            @Param("proobj_audit_status") Integer protectObjectAuditStatus,
                                            @Param("page") Integer page,
                                            @Param("page_size") Integer pageSize);

    Integer queryUsedProtectObjectsTotalNum();

    void updateAuditStatusByIdBatch(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch);

    List<Integer> queryAuditStatusByIds(@Param("idsWithAuditStatusMap") Map<Integer, Integer> idsWithAuditStatusMap);
}
