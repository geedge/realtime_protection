package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.rule.dynamicrule.AlertMessage;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        alertMessageService.receiveAlertMessage(alertMessage);
        return ResponseResult.ok();
    }

}
