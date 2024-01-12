package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务信息")
    )
    ResponseResult newTask(@RequestBody @Valid Task task);

    @GetMapping("/query")
    @Operation(
            summary = "查询任务",
            description = "按页和搜索内容查询任务相关信息",
            responses = {
                    @ApiResponse(
                            description = "返回查询到的所有任务",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "task_status", description = "任务状态（0为未启动，1为生成中，2为运行中，3为暂停中，4为已停止，5为已结束，6为失败）"),
                    @Parameter(name = "task_type", description = "任务类型（1为静态，2为实时，3为研判后）"),
                    @Parameter(name = "task_name", description = "任务名称"),
                    @Parameter(name = "task_creator", description = "任务创建人"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页查询个数", example = "10")
            }
    )
    ResponseResult queryTasks(@RequestParam(value = "task_status", required = false) Integer taskStatus,
                              @RequestParam(value = "task_type", required = false) String taskType,
                              @RequestParam(value = "task_name", required = false) String taskName,
                              @RequestParam(value = "task_creator", required = false) String taskCreator,
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
                                    schema = @Schema(implementation = ResponseResult.class)
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
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
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
                                    schema = @Schema(implementation = ResponseResult.class)
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
                                    schema = @Schema(implementation = ResponseResult.class)
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
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "taskId", description = "任务ID"),
                    @Parameter(name = "stateNum", description = "任务状态编号任务状态（0为未启动，1为生成中，2为运行中，3为暂停中，4为已停止，5为已结束，6为失败）")
            }
    )
    ResponseResult changeTaskStatus(@PathVariable @NotNull Integer stateNum,
                                    @PathVariable @NotNull Long taskId) throws DorisStartException;
}
