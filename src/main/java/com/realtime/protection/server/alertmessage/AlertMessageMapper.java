package com.realtime.protection.server.alertmessage;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface AlertMessageMapper {

    TaskCommandInfo getDynamicTaskInfos(Long taskId, Integer dynamicRuleId) ;


    ProtectLevel queryTemplateProtectLevel(Integer templateId, Integer protectLevel);

    @DS("doris")
    void insertAlertMessage(AlertMessage alertMessage);
    @DS("doris")
    List<AlertMessage> queryAlermsByCommandId(String commandId);
}
