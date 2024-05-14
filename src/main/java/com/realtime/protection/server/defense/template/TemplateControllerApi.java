package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "防御策略模板API", description = "防御策略模板模块所有接口")
public interface TemplateControllerApi {
    @PostMapping("/new")
    @Operation(
            summary = "新建防御策略模板",
            description = "根据信息新建一个防护策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回新建防御策略模板结果",
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
                                                        "template_id": 1442
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 新建防御策略模板是否成功
                                                    "template_id": 新建防御策略模板ID
                                                    """
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(

                    description = "防御策略模板信息")

    )
    ResponseResult newTemplate(@RequestBody @Valid Template template);

    @GetMapping("/query")
    @Operation(
            summary = "查询多个防御策略模板",
            description = "根据查询条件查询多个防御策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回所有查询到的防御策略模板",
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
                                                        "templates": [
                                                          {
                                                            "template_id": 18,
                                                            "template_name": "洪泛型DDOS攻击-2024-01-18T16:46:14.640176900",
                                                            "protect_level_low": {
                                                              "protectLevelId": 46,
                                                              "hasProtectObjectIP": false,
                                                              "hasProtectObjectPort": false,
                                                              "hasPeerIP": false,
                                                              "hasPeerPort": true,
                                                              "hasProtocol": false,
                                                              "hasURL": false,
                                                              "hasDNS": false
                                                            },
                                                            "protect_level_medium": {
                                                              "protectLevelId": 47,
                                                              "hasProtectObjectIP": true,
                                                              "hasProtectObjectPort": false,
                                                              "hasPeerIP": false,
                                                              "hasPeerPort": false,
                                                              "hasProtocol": false,
                                                              "hasURL": false,
                                                              "hasDNS": false
                                                            },
                                                            "protect_level_high": {
                                                              "protectLevelId": 48,
                                                              "hasProtectObjectIP": false,
                                                              "hasProtectObjectPort": false,
                                                              "hasPeerIP": false,
                                                              "hasPeerPort": false,
                                                              "hasProtocol": false,
                                                              "hasURL": false,
                                                              "hasDNS": true
                                                            },
                                                            "template_used_times": 0,
                                                            "running_tasks": 0,
                                                            "description": "xxx"
                                                          },
                                                          {
                                                            "template_id": 24,
                                                            "template_name": "反射型DDOS攻击-2024-01-12T17:52:32.077477700",
                                                            "protect_level_low": {
                                                              "protectLevelId": 64,
                                                              "hasProtectObjectIP": false,
                                                              "hasProtectObjectPort": false,
                                                              "hasPeerIP": false,
                                                              "hasPeerPort": true,
                                                              "hasProtocol": false,
                                                              "hasURL": false,
                                                              "hasDNS": false
                                                            },
                                                            "protect_level_medium": {
                                                              "protectLevelId": 65,
                                                              "hasProtectObjectIP": true,
                                                              "hasProtectObjectPort": false,
                                                              "hasPeerIP": false,
                                                              "hasPeerPort": false,
                                                              "hasProtocol": false,
                                                              "hasURL": false,
                                                              "hasDNS": false
                                                            },
                                                            "protect_level_high": {
                                                              "protectLevelId": 66,
                                                              "hasProtectObjectIP": false,
                                                              "hasProtectObjectPort": false,
                                                              "hasPeerIP": false,
                                                              "hasPeerPort": false,
                                                              "hasProtocol": false,
                                                              "hasURL": false,
                                                              "hasDNS": true
                                                            },
                                                            "template_used_times": 0,
                                                            "running_tasks": 0,
                                                            "description": "xxx"
                                                          }
                                                        ],
                                                        "total_num": 708
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "template_id": 防御策略模板ID
                                                    
                                                    "template_name": 防御策略模板名称
                                                    
                                                    "protect_level_low": 防御策略模板日常态数据
                                                    
                                                    "protect_level_medium": 防御策略模板应急态数据
                                                    
                                                    "protect_level_high": 防御策略模板紧急态数据
                                                    
                                                    "template_used_times": 防御策略模板使用次数
                                                    
                                                    "running_tasks": 防御策略模板任务运行数量
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "template_name", description = "防御策略模板名称", example = "DDOS"),
                    @Parameter(name = "source_system", description = "策略模板来源那系统", example = "xxx系统"),
                    @Parameter(name = "create_user_name", description = "创建人名称", example = "xxx"),
                    @Parameter(name = "template_id", description = "防御策略模板ID", example = "1"),
                    @Parameter(name = "audit_status", description = "审核状态", example = "0"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页对象数量", example = "5")
            }
    )
    ResponseResult queryTemplates(@RequestParam(value = "template_name", required = false) String templateName,
                                  @RequestParam(value = "source_system", required = false) String sourceSystem,
                                  @RequestParam(value = "create_user_name", required = false) String createUserName,
                                  @RequestParam(value = "template_id", required = false) Integer templateId,
                                  @RequestParam(value = "audit_status", required = false) Integer auditStatus,

                                  @RequestParam("page") @Min(1) Integer page,
                                  @RequestParam("page_size") @Min(1) Integer pageSize);

    @GetMapping("/{templateId}/query")
    @Operation(
            summary = "查询单个防御策略模板",
            description = "根据模板ID查询单个防御策略模板信息",
            responses = {
                    @ApiResponse(
                            description = "返回单个防御策略模板信息",
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
                                                        "template": {
                                                          "template_id": 245,
                                                          "template_name": "反射型DDOS攻击-2024-01-12T18:10:49.975561700",
                                                          "protect_level_low": {
                                                            "protectLevelId": 727,
                                                            "hasProtectObjectIP": false,
                                                            "hasProtectObjectPort": false,
                                                            "hasPeerIP": false,
                                                            "hasPeerPort": true,
                                                            "hasProtocol": false,
                                                            "hasURL": false,
                                                            "hasDNS": false
                                                          },
                                                          "protect_level_medium": {
                                                            "protectLevelId": 728,
                                                            "hasProtectObjectIP": true,
                                                            "hasProtectObjectPort": false,
                                                            "hasPeerIP": false,
                                                            "hasPeerPort": false,
                                                            "hasProtocol": false,
                                                            "hasURL": false,
                                                            "hasDNS": false
                                                          },
                                                          "protect_level_high": {
                                                            "protectLevelId": 729,
                                                            "hasProtectObjectIP": false,
                                                            "hasProtectObjectPort": false,
                                                            "hasPeerIP": false,
                                                            "hasPeerPort": false,
                                                            "hasProtocol": false,
                                                            "hasURL": false,
                                                            "hasDNS": true
                                                          },
                                                          "template_used_times": 0,
                                                          "running_tasks": 0,
                                                          "description": "xxx"
                                                        }
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "template_id": 防御策略模板ID
                                                    
                                                    "template_name": 防御策略模板名称
                                                    
                                                    "protect_level_low": 防御策略模板日常态数据
                                                    
                                                    "protect_level_medium": 防御策略模板应急态数据
                                                    
                                                    "protect_level_high": 防御策略模板紧急态数据
                                                    
                                                    "template_used_times": 防御策略模板使用次数
                                                    
                                                    "running_tasks": 防御策略模板任务运行数量
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "templateId", description = "防御策略模板ID", example = "5")
            }
    )
    ResponseResult queryTemplate(@PathVariable Integer templateId) throws IllegalAccessException;

    @GetMapping("/query/level")
    @Operation(
            summary = "查询所有策略模板是否含有不同类型防护等级",
            description = "查询所有的策略模板是否还有日常态/应急态/紧急态三种防护等级",
            responses = {
                    @ApiResponse(
                            description = "返回策略模板防护等级信息",
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
                                                        "templates": [
                                                          {
                                                            "template_id": 18,
                                                            "template_name": "洪泛型DDOS攻击-2024-01-18T16:46:14.640176900",
                                                            "has_protect_level_low": false,
                                                            "has_protect_level_medium": true,
                                                            "has_protect_level_high": false
                                                          },
                                                          {
                                                            "template_id": 24,
                                                            "template_name": "反射型DDOS攻击-2024-01-12T17:52:32.077477700",
                                                            "has_protect_level_low": false,
                                                            "has_protect_level_medium": true,
                                                            "has_protect_level_high": true
                                                          }
                                                        ],
                                                        "total_num": 708
                                                      }
                                                    }
                                                    """,
                                            description =
                                                    """
                                                    "has_protect_level_low": 是否包含日常态防护等级
                                                    
                                                    "has_protect_level_medium": 是否包含应急态防护等级
                                                    
                                                    "has_protect_level_high": 是否包含紧急态防护等级
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseResult queryTemplateProtectLevels();

    @PostMapping("/{templateId}/update")
    @Operation(
            summary = "更新防御策略模板信息",
            description = "根据提供的防御策略模板信息更新指定ID对应的防御策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回防御策略模板信息更新结果",
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
                                                        "template_id": 262
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 更新是否成功
                                                    
                                                    "template_id": 更新的策略模板ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "templateId", description = "防御策略模板ID", example = "5")
            }
    )
    ResponseResult updateTemplate(@PathVariable @Min(1) Integer templateId,
                                  @RequestBody @Valid Template template);

    @DeleteMapping("/{templateId}/delete")
    @Operation(
            summary = "删除防御策略模板信息",
            description = "根据策略模板ID删除指定的防御策略模板信息",
            responses = {
                    @ApiResponse(
                            description = "返回防御策略模板删除结果",
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
                                                        "template_id": 262
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 删除是否成功
                                                    
                                                    "template_id": 删除的策略模板ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "templateId", description = "防御策略模板ID", example = "5")
            }
    )
    ResponseResult deleteTemplate(@PathVariable @Min(1) Integer templateId);


    @GetMapping("/source_system")
    @Operation(
            summary = "查询来源系统名称",
            description = "查询策略模板中的所有来源系统名称",
            responses = {
                    @ApiResponse(
                            description = "返回来源系统名称查询结果，返回字符串列表",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            }
    )
    ResponseResult querySourceSystem();

    @GetMapping("/query/event_name/{sourceSystem}")
    @Operation(
            summary = "查询事件类型",
            description = "查询当前来源系统下的事件类型",
            responses = {
                    @ApiResponse(
                            description = "返回当前来源系统下的事件类型，返回字符串列表",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            }
    )
    ResponseResult queryEventName(@PathVariable String sourceSystem);

    @PostMapping("/query/templateId")
    @Operation(
            summary = "查询策略模板id",
            description = "根据来源系统和事件类型查询策略模板id",
            responses = {
                    @ApiResponse(
                            description = "返回来源系统和事件类型对应的策略模板id，整数",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            }
    )
    ResponseResult queryTemplateId(@RequestBody Map<String, String> map);

    /*
        策略模板数据统计
         */
    @Operation(
            summary = "数据统计",
            description = "数据统计",
            responses = {
                    @ApiResponse(
                            description = "返回数据统计",
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
                                                          "template_num": 1,
                                                          "template_used_num": 1,
                                                          "template_audit_num": 0,
                                                          "template_unaudit_num": 1
                                                        }
                                                      }
                                                    """
                                    )
                            )
                    )
            },
            parameters = {

            }
    )
    @GetMapping("/statistics")
    ResponseResult statisticsTemplate();

    @Operation(
            summary = "批量更新审批状态",
            description = "批量更新审批状态，0未审核、1审核不通过、2审核通过",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回审核结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "字典，key是id，value是审核状态",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)

                    )
            )
    )
    @PostMapping("/auditbatch")
    ResponseResult updateWhiteListAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap);

    @Operation(
            summary = "更新审批意见",
            description = "批量更新审批意见，接收多个id",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回是否成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "字典，key是'audit_info'，value是审核意见",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "example",
                                    value = """
                                    {
                                         "audit_info": "审核不通过，原因是xxxx"
                                       }
                                    """
                            )

                    )
            ),
            parameters = {
                    @Parameter(name = "ids", description = "欲更新的ID", example = "2,3"),
            }
    )
    @PostMapping("/auditInfo/{ids}")
    ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                   @RequestBody Map<String, String> auditInfo);


    @Operation(
            summary = "查询审批意见",
            description = "查询审批意见，只接收一个id",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回是否成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "查询的ID", example = "2"),
            }
    )
    @GetMapping("/auditInfo/{id}")
    ResponseResult queryAuditInfo(@PathVariable Integer id);
}