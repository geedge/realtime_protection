package com.realtime.protection.server.alertmessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import jdk.jfr.DataAmount;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("alertmessage")
@Slf4j
public class AlertMessageController
{
    private final AlertMessageService alertMessageService;
    public AlertMessageController(AlertMessageService alertMessageService) {
        this.alertMessageService = alertMessageService;
    }

    @PostMapping("/new")
    public ResponseResult receiveAlertMessage(@RequestBody @Valid AlertMessage alertMessage){
        alertMessageService.processAlertMessage(alertMessage);
        return ResponseResult.ok().setData("success", true);
    }

    //实时任务、研判后任务：查看指令对应的告警信息
    @GetMapping("/{commandId}/alarms")
    public ResponseResult queryAlarmsByCommandId(@PathVariable String commandId) {
        return ResponseResult.ok()
                .setData("success", true)
                .setData("alarms", alertMessageService.queryAlarmsByCommandId(commandId));
    }

    @Data
    class AlertMessageAuditInfo {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("audit_info")
        private String auditInfo;
    }
    //告警信息审计接口
    @PostMapping("/auditInfo/{id}")
    public ResponseResult updateAuditInfo(@PathVariable String id,
                                              @RequestBody Map<String, Object> auditInfo) {
        if (auditInfo.get("auditInfo") == null ) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success",
                        alertMessageService.updateAuditInfo(
                                id, auditInfo.get("auditInfo").toString()));
    }

    //告警信息审计接口
    @PostMapping("/auditInfo2/{id}")
    public ResponseResult updateAuditInfo2(@PathVariable String id,
                                          @RequestBody Map<String, List<String>> auditInfo) {
        if (auditInfo.get("auditInfo").get(0) == null ||
                auditInfo.get("auditInfo").get(0) .isEmpty()) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success",
                        alertMessageService.updateAuditInfo(id, auditInfo.get("auditInfo").get(0)));
    }


}
