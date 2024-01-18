package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AlertMessageMapper {

    TaskCommandInfo getDynamicTaskInfos(Long taskId) ;


    ProtectLevel queryTemplateProtectLevel(Integer templateId, Integer protectLevel, FiveTupleWithMask fiveTupleWithMask);
}
