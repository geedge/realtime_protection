package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.rule.dynamicrule.AlertMessage;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("alertmessage")
@Slf4j
public class AlertMessageController
{
    private final AlertMessageService alertMessageService;
    public AlertMessageController(final AlertMessageService alertMessageService) {
        this.alertMessageService = alertMessageService;
    }

    @PostMapping("/new")
    public ResponseResult receiveAlertMessage(@RequestBody @Valid AlertMessage alertMessage){
        alertMessageService.processAlertMessage(alertMessage);
        return ResponseResult.ok();
    }

    //实时任务、研判后任务：查看指令对应的告警信息
    @GetMapping("/{commandId}/alarms")
    public ResponseResult queryAlarmsByCommandId(@PathVariable String commandId) {
        return ResponseResult.ok()
                .setData("success", true)
                .setData("alarms", alertMessageService.queryAlarmsByCommandId(commandId));
    }

}
