package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务控制器API", description = "任务管理模块相关的所有接口")
public interface TaskControllerApi {
    @PostMapping("/new")
    @Operation(
            summary = "添加任务",
            description = "根据任务信息添加任务并返回任务添加结果",
            responses = {
                    @ApiResponse(
                            description = "返回任务添加结果信息",
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
                                                        "task_name": "静态任务",
                                                        "success": true,
                                                        "task_id": 1939
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "task_name": 任务名称
                                                    
                                                    "success": 任务添加是否成功
                                                    
                                                    "task_id": 新建任务ID
                                                    """
                                    )
                            ),
                            responseCode = "200"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务信息")
    )
    ResponseResult newTask(@RequestBody @Valid Task task);

    // API推送Endpoint
    @PostMapping("/api/new")
    @Operation(
            summary = "任务推送外部API",
            description = "提供给外部的任务推送API",
            responses = {
                    @ApiResponse(
                            description = "返回外部任务推送结果",
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
                                                        "success": true,
                                                        "taskId": 1940
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "task_name": 任务名称
                                                    
                                                    "success": 任务添加是否成功
                                                   
                                                    "task_id": 新建任务ID
                                                    """
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务推送信息")
    )
    ResponseResult newTaskWithAPI(@RequestBody @Valid TaskCommandInfo taskCommandInfo) throws DorisStartException;

    @GetMapping("/query")
    @Operation(
            summary = "查询任务",
            description = "按页和搜索内容查询任务相关信息",
            responses = {
                    @ApiResponse(
                            description = "返回查询到的所有任务",
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
                                                        "total_num": 1902,
                                                        "task_list": [
                                                          {
                                                            "task_id": 37,
                                                            "task_name": "静态任务",
                                                            "task_start_time": "2025-01-19T08:46:36",
                                                            "task_end_time": "2027-01-19T08:46:36",
                                                            "task_type": 1,
                                                            "task_create_username": "xxx",
                                                            "task_create_depart": "xxx",
                                                            "static_rule_ids": [
                                                              112
                                                            ],
                                                            "dynamic_rule_ids": [],
                                                            "task_status": 4,
                                                            "task_audit_status": 0
                                                          },
                                                          {
                                                            "task_id": 38,
                                                            "task_name": "修改测试",
                                                            "task_start_time": "2024-02-29T15:16:11",
                                                            "task_end_time": "2024-03-15T04:30:18",
                                                            "task_type": 1,
                                                            "task_create_username": "xxx",
                                                            "task_create_depart": "xxx",
                                                            "static_rule_ids": [],
                                                            "dynamic_rule_ids": [],
                                                            "task_status": 4,
                                                            "task_audit_status": 2
                                                          }
                                                        ]
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "task_id": 任务ID
                                                    
                                                    "task_name": 任务名称
                                                   
                                                    "task_start_time": 任务开始时间
                                                   
                                                    "task_end_time": 任务结束时间
                                                   
                                                    "task_type": 任务类型（静态、动态、研判后对应1，2，3）
                                                    
                                                    "task_create_username": 任务创建人名称
                                                    
                                                    "task_create_depart": 任务创建人处室
                                                    
                                                    "static_rule_ids": 静态规则ID列表
                                                    
                                                    "dynamic_rule_ids": 动态规则ID列表
                                                    
                                                    "task_status": 任务当前运行状态
                                                    
                                                    "task_audit_status": 任务当前审核状态
                                                    
                                                    "total_num": 任务总数
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "task_status", description = "任务状态（0为未启动，1为生成中，2为运行中，3为暂停中，4为已停止，5为已结束，6为失败）"),
                    @Parameter(name = "task_type", description = "任务类型（1为静态，2为实时，3为研判后）"),
                    @Parameter(name = "task_name", description = "任务名称"),
                    @Parameter(name = "task_creator", description = "任务创建人"),
                    @Parameter(name = "audit_status", description = "审批状态"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页查询个数", example = "10")
            }
    )
    ResponseResult queryTasks(@RequestParam(value = "task_status", required = false) Integer taskStatus,
                              @RequestParam(value = "task_type", required = false) Integer taskType,
                              @RequestParam(value = "task_name", required = false) String taskName,
                              @RequestParam(value = "task_creator", required = false) String taskCreator,
                              @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                              @RequestParam("page") @Min(1) Integer page,
                              @RequestParam("page_size") @Min(1) Integer pageSize);

    @GetMapping("/{id}/query")
    @Operation(
            summary = "查询单个任务",
            description = "根据任务ID查询单个任务的所有详细信息",
            responses = {
                    @ApiResponse(
                            description = "返回查询到的单个任务",
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
                                                        "task": {
                                                          "task_id": 38,
                                                          "task_name": "修改测试",
                                                          "task_start_time": "2024-02-29T15:16:11",
                                                          "task_end_time": "2024-03-15T04:30:18",
                                                          "task_type": 1,
                                                          "task_create_username": "xxx",
                                                          "task_create_depart": "xxx",
                                                          "static_rule_ids": [],
                                                          "dynamic_rule_ids": [],
                                                          "task_status": 4,
                                                          "task_audit_status": 2
                                                        }
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "task_id": 任务ID
                                                    
                                                    "task_name": 任务名称
                                                    
                                                    "task_start_time": 任务开始时间
                                                    
                                                    "task_end_time": 任务结束时间
                                                    
                                                    "task_type": 任务类型（静态、动态、研判后对应1，2，3）
                                                    
                                                    "task_create_username": 任务创建人名称
                                                    
                                                    "task_create_depart": 任务创建人处室
                                                    
                                                    "static_rule_ids": 静态规则ID列表
                                                    
                                                    "dynamic_rule_ids": 动态规则ID列表
                                                    
                                                    "task_status": 任务当前运行状态
                                                    
                                                    "task_audit_status": 任务当前审核状态
                                                    """
                                    )
                            )
                    )
            },
            parameters = {@Parameter(name = "id", description = "任务ID", example = "38")}
    )
    ResponseResult queryTask(@PathVariable @Min(1) Long id) throws IllegalAccessException;

    @PostMapping("/{taskId}/update")
    @Operation(
            summary = "更新任务",
            description = "根据任务信息更新任务并返回更新结果",
            responses = {
                    @ApiResponse(
                            description = "返回任务更新结果信息",
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
                                                        "success": true,
                                                        "task_id": 637
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 更新是否成功
                                                    
                                                    "task_id": 更新任务ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "taskId", description = "任务ID")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "任务信息，必须包含任务原有的或者添加/删除部分后的static_rule_ids和dynamic_rule_ids"
            )
    )
    ResponseResult updateTask(@PathVariable Long taskId, @RequestBody @Valid Task task);

    @GetMapping("/{taskId}/audit/{auditStatus}")
    @Operation(
            summary = "任务审核状态修改",
            description = "修改ID对应的任务的审核状态",
            responses = {
                    @ApiResponse(
                            description = "返回任务审核状态修改的信息",
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
                                                        "success": true,
                                                        "task_id": 38,
                                                        "audit_status": 2
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 任务审核状态修改是否成功
                                                    
                                                    "task_id": 任务ID
                                                    
                                                    "audit_status": 任务当前审核状态
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "taskId", description = "任务ID", example = "38"),
                    @Parameter(name = "auditStatus", description = "任务欲修改的审核状态（0为未审核，1为已退回，2为审核通过）", example = "2")
            }
    )
    ResponseResult changeTaskAuditStatus(@PathVariable @NotNull @Max(10) Integer auditStatus,
                                         @PathVariable @NotNull @Min(1) Long taskId);

    @DeleteMapping("/{taskId}/delete")
    @Operation(
            summary = "删除单个任务",
            description = "根据任务ID删除对应任务",
            responses = {
                    @ApiResponse(
                            description = "返回任务删除结果信息",
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
                                                        "success": true,
                                                        "task_id": 889
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 任务删除是否成功
                                                    
                                                    "task_id": 删除的任务ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "taskId", description = "任务ID")
            }
    )
    ResponseResult deleteTask(@PathVariable @NotNull @Min(1) Long taskId);

    @GetMapping("/{taskId}/running/{stateNum}")
    @Operation(
            summary = "修改任务运行状态",
            description = "修改ID对应的任务的运行状态",
            responses = {
                    @ApiResponse(
                            description = "返回任务运行状态修改结果",
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
                                                        "status_now": 2,
                                                        "success": true,
                                                        "task_id": 1012
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "status_now": 当前任务状态

                                                    "success": 任务状态更新是否成功

                                                    "task_id": 任务ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "taskId", description = "任务ID"),
                    @Parameter(name = "stateNum", description = "任务状态编号任务状态（0为未启动，1为生成中，2为运行中，3为暂停中，4为已停止，5为已结束，6为失败）")
            }
    )
    ResponseResult changeTaskStatus(@PathVariable @NotNull @Min(0) @Max(6) Integer stateNum,
                                    @PathVariable @NotNull @Min(1) Long taskId) throws DorisStartException;

    @GetMapping("/{taskId}/commands")
    @Operation(
            summary = "获得任务已推送指令的相关数据",
            description = "获得任务已推送指令的相关数据，包括最新下发时间、首次下发时间、下发次数、下发成功次数等",
            responses = {
                    @ApiResponse(
                            description = "返回任务已推送指令的相关数据",
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
                                                        "success": true,
                                                        "commands": [
                                                          {
                                                            "uuid": "3b42ca64-282f-4040-bd8f-8f895fa82d23",
                                                            "task_act": "篡改",
                                                            "is_valid": true,
                                                            "five_tuple_with_mask": {
                                                              "sourceIP": "1.1.2.3",
                                                              "sourcePort": "80"
                                                            },
                                                            "command_send_times": 0,
                                                            "command_success_times": 0
                                                          }
                                                        ]
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "task_act": 任务行为
                                                    
                                                    "is_valid": 指令是否生效
                                                    
                                                    "five_tuple_with_mask": 指令五元组信息
                                                    
                                                    "command_send_times": 指令下发次数
                                                    
                                                    "command_success_times": 指令下发成功次数
                                                    
                                                    "uuid": 指令UUID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "taskId", description = "任务ID", example = "733"),
                    @Parameter(name = "src_ip", description = "源IP", example = "192.168.0.1"),
                    @Parameter(name = "dst_ip", description = "目的IP"),
                    @Parameter(name = "src_port", description = "源端口"),
                    @Parameter(name = "dst_port", description = "目的端口"),
                    @Parameter(name = "page", description = "页码"),
                    @Parameter(name = "page_num", description = "每页个数")
            }
    )
    ResponseResult queryCommandInfos(@PathVariable Long taskId,
                                     @RequestParam(name = "src_ip", required = false) String sourceIP,
                                     @RequestParam(name = "src_port", required = false) String sourcePort,
                                     @RequestParam(name = "dst_ip", required = false) String destinationIP,
                                     @RequestParam(name = "dst_port", required = false) String destinationPort,
                                     @RequestParam(name = "page") @Min(1) Integer page,
                                     @RequestParam(name = "page_num") @Min(1) Integer pageNum);


    @Operation(
            summary = "下发/取消指令下发",
            description = "下发或取消下发一条指令",
            responses = @ApiResponse(
                    description = "返回指令是否成功下发",
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
                                                "success": true,
                                                "command_uuid": "85f4115b-f9ac-4489-89bc-42ee261d6cd1"
                                              }
                                            }
                                            """,
                                    description = """
                                            "success": 指令下发是否成功
                                            
                                            "command_uuid": 指令UUID
                                            """
                            )
                    )
            )
    )
    @GetMapping("/{commandId}/valid/{isJudged}")
    ResponseResult setCommandJudged(@PathVariable Boolean isJudged,
                                    @PathVariable String commandId);
}
