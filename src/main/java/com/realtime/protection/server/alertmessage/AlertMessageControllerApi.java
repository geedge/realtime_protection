package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AlertMessageControllerApi {
   @Operation(
            summary = "录入告警信息",
            description = "通过API推送录入告警信息、人工录入告警信息",
            responses = {
                    @ApiResponse(
                            description = "是否录入成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "success": true
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    """
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "告警日志信息")
    )
    @PostMapping("/new")
    ResponseResult receiveAlertMessage(@RequestBody @Valid AlertMessage alertMessage);
}
