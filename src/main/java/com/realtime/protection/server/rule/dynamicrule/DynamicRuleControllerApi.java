package com.realtime.protection.server.rule.dynamicrule;

import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "动态规则API", description = "动态规则模块所有接口")
public interface DynamicRuleControllerApi {

    @Operation(
            summary = "新建动态规则",
            description = "新建一个动态规则",

            responses = {
                    @ApiResponse(
                            description = "返回新建对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
//                                            title = "ResponseResult和DynamicRule的组合模型",
//                                            description = "ResponseResult的data内DynamicRule",
//                                            anyOf = {ResponseResult.class, DynamicRuleObject.class},
                                            implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "动态规则信息")
    )
    ResponseResult newDynamicRuleObject(@RequestBody @Valid DynamicRuleObject dynamicRuleObject) ;

    @Operation(
            summary = "删除动态规则",
            description = "删除一个动态规则",
            responses = {
                    @ApiResponse(
                            description = "返回删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "动态规则id")
            }
    )
    ResponseResult deleteDynamicRuleObject(@PathVariable Integer id) ;

    @Operation(
            summary = "批量删除动态规则",
            description = "批量删除动态规则",
            responses = {
                    @ApiResponse(
                            description = "返回批量删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "返回批量删除结果",
                                                    description = "success：是否修改成功",
                                                    value ="{\n" +
                                                            "  \"code\": 200,\n" +
                                                            "  \"message\": \"request succeed\",\n" +
                                                            "  \"data\": {\n" +
                                                            "    \"success\": true\n" +
                                                            "  }\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "ids", description = "动态规则id列表")
            }
    )
    public ResponseResult deleteDynamicRuleObjects(@PathVariable List<Integer> ids) ;

    @Operation(
            summary = "修改动态规则",
            description = "修改动态规则",
            responses = {
                    @ApiResponse(
                            description = "返回修改对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseResult.class
                                            ),
                                    examples = {
                                    @ExampleObject(
                                            name = "返回修改对象结果",
                                            description = "dynamic_rule_id:动态规则id" +
                                                    "success：是否修改成功",
                                            value = "{\n" +
                                                    "  \"code\": 200,\n" +
                                                    "  \"message\": \"request succeed\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"dynamic_rule_id\": 11,\n" +
                                                    "    \"success\": true\n" +
                                                    "  }\n" +
                                                    "}"
                                    ),
                            }
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "动态规则id")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "动态规则信息")
    )
    public ResponseResult updateDynamicRuleObject(
            @PathVariable Integer id,
            @RequestBody @Valid DynamicRuleObject dynamicRuleObject) ;

    @Operation(
            summary = "查询单个动态规则",
            description = "根据动态规则ID查询单个动态规则的所有详细信息",
            responses = {
                    @ApiResponse(
                            description = "返回查询到的单个动态规则",
                            content = @Content(
                                    mediaType = "application/json",

                                    schema = @Schema(
                                            title = "ResponseResult和DynamicRule的组合模型",
                                            description = "ResponseResult的data内DynamicRule",
                                            anyOf = {ResponseResult.class, DynamicRuleObject.class}
                                            ),
                                    examples = @ExampleObject(
                                    name = "返回查询到的单个动态规则",
                                    description = "dynamic_rule_id:动态规则id\n" +
                                            "dynamic_rule_name:动态规则名称\n" +
                                            "dynamic_rule_modify_time:动态规则修改时间\n" +
                                            "protect_objects:动态规则关联的防护对象\n" +
                                            "dynamic_rule_source_system:动态规则来源系统\n" +
                                            "dynamic_rule_event_type:动态规则事件类型\n" +
                                            "template_id:动态规则关联的策略模板id\n" +
                                            "dynamic_rule_priority:动态规则优先级\n" +
                                            "dynamic_rule_range:动态规则范围\n" +
                                            "dynamic_rule_frequency:动态规则频率\n" +
                                            "success：是否查询成功",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"request succeed\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"dynamic_rule\": {\n" +
                                            "      \"dynamic_rule_id\": 5,\n" +
                                            "      \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                            "      \"dynamic_rule_modify_time\": \"2024-01-16T16:16:05\",\n" +
                                            "      \"protect_objects\": [],\n" +
                                            "      \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                            "      \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                            "      \"template_id\": 27,\n" +
                                            "      \"dynamic_rule_priority\": 1,\n" +
                                            "      \"dynamic_rule_range\": \"北京\",\n" +
                                            "      \"dynamic_rule_frequency\": 1\n" +
                                            "    },\n" +
                                            "    \"success\": true\n" +
                                            "  }\n" +
                                            "}"
                            )

                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "动态规则ID", example = "2")
            }
    )
    public ResponseResult queryDynamicRuleObjectById(@PathVariable Integer id) ;

    @Operation(
            summary = "查询多个动态规则",
            description = "根据条件查询多个动态规则",
            responses = {
                    @ApiResponse(
                            description = "返回查询到的多个动态规则",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            title = "ResponseResult和DynamicRule的组合模型",
                                            description = "ResponseResult的data内DynamicRule",
                                            anyOf = {ResponseResult.class, DynamicRuleObject.class})
                            ))},
            parameters = {@Parameter(name = "ids", description = "动态规则ID列表", example = "dynamicrule/query/5,11")}
    )
            @GetMapping("/query/{ids}")
    ResponseResult queryDynamicRuleObjectByIds(@PathVariable List<Integer> ids);

    @Operation(
            summary = "根据条件查询多个动态规则",
            description = "根据查询条件和页码等，查询多个对象并以列表返回",
            responses = {
                    @ApiResponse(
                            description = "返回多个动态规则",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            title = "ResponseResult和DynamicRule的组合模型",
                                            description = "ResponseResult的data内DynamicRule",
                                            anyOf = {ResponseResult.class, DynamicRuleObject.class}),
                                    examples ={
                                            @ExampleObject(
                                                    name = "返回多个动态规则",
                                                    value = "{\n" +
                                                            "  \"code\": 200,\n" +
                                                            "  \"message\": \"request succeed\",\n" +
                                                            "  \"data\": {\n" +
                                                            "    \"dynamic_rule_list\": [\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 5,\n" +
                                                            "        \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                                            "        \"template_id\": 27,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 11,\n" +
                                                            "        \"dynamic_rule_name\": \"动态规则测试\",\n" +
                                                            "        \"template_id\": 1,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 15,\n" +
                                                            "        \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                                            "        \"template_id\": 27,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 16,\n" +
                                                            "        \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                                            "        \"template_id\": 27,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 17,\n" +
                                                            "        \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                                            "        \"template_id\": 27,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 18,\n" +
                                                            "        \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                                            "        \"template_id\": 27,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 19,\n" +
                                                            "        \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                                            "        \"template_id\": 27,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 20,\n" +
                                                            "        \"dynamic_rule_name\": \"UpdateDynamicRule2\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"反射型DDOS攻击\",\n" +
                                                            "        \"template_id\": 27,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 23,\n" +
                                                            "        \"dynamic_rule_name\": \"动态规则测试\",\n" +
                                                            "        \"dynamic_rule_create_time\": \"2024-01-18T16:11:39\",\n" +
                                                            "        \"dynamic_rule_create_username\": \"mh\",\n" +
                                                            "        \"dynamic_rule_create_depart\": \"mh\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"test\",\n" +
                                                            "        \"template_id\": 1237,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      },\n" +
                                                            "      {\n" +
                                                            "        \"dynamic_rule_id\": 31,\n" +
                                                            "        \"dynamic_rule_name\": \"动态规则测试\",\n" +
                                                            "        \"dynamic_rule_create_time\": \"2024-01-19T22:39:08\",\n" +
                                                            "        \"dynamic_rule_create_username\": \"mh\",\n" +
                                                            "        \"dynamic_rule_source_system\": \"xxxx系统\",\n" +
                                                            "        \"dynamic_rule_event_type\": \"test\",\n" +
                                                            "        \"template_id\": 1237,\n" +
                                                            "        \"dynamic_rule_priority\": 1,\n" +
                                                            "        \"dynamic_rule_range\": \"北京\",\n" +
                                                            "        \"dynamic_rule_frequency\": 1\n" +
                                                            "      }\n" +
                                                            "    ],\n" +
                                                            "    \"success\": true,\n" +
                                                            "    \"dynamic_rule_total_num\": 11\n" +
                                                            "  }\n" +
                                                            "}",
                                                    description = "dynamic_rule_list:动态规则列表\n" +
                                                            "dynamic_rule_total_num:动态规则总数\n" +
                                                            "success：是否查询成功"

                                            )
                            }
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "name", description = "动态规则名称", example = "test"),
                    @Parameter(name = "id", description = "动态规则ID", example = "2"),
                    @Parameter(name = "source_system", description = "来源你系统", example = "BW系统"),
                    @Parameter(name = "creator", description = "创建人名称"),
                    @Parameter(name = "audit_status", description = "审核状态", example = "1"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页大小", example = "10")
            }
    )
    ResponseResult queryDynamicRuleObject(@RequestParam(value = "name", required = false) String dynamicRuleName,
                                          @RequestParam(value = "id", required = false) Integer dynamicRuleId,
                                          @RequestParam(value = "source_system", required = false) String protectObjectSourceSystem,
                                          @RequestParam(value = "creator", required = false) String creator,
                                            @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize);

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
                                                            "dynamic_rule_num": 3,
                                                            "dynamic_rule_used_num": 0,
                                                            "dynamic_rule_audit_num": 2,
                                                            "dynamic_rule_unaudit_num": 0
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
    ResponseResult getStaticRuleStatisticsData();
}