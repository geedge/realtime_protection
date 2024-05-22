package com.realtime.protection.server.dictionary;

import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "字典表API", description = "字典表模块所有接口")
public interface DictControllerApi {
    @Operation(
            summary = "查询字典表数据",
            description = "查询不同类型的字典",
            responses = {
                    @ApiResponse(
                            description = "返回要查询类型的字典结果",
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
                                                         "dictData": [
                                                           {
                                                             "dict_label": "反射型DDoS",
                                                             "dict_value": "reflectDDoS",
                                                             "dict_type": "event_type"
                                                           },
                                                           {
                                                             "dict_label": "洪泛型DDoS",
                                                             "dict_value": "floodDDoS",
                                                             "dict_type": "event_type"
                                                           },
                                                           {
                                                             "dict_label": "漏洞、网络后门",
                                                             "dict_value": "backdoor",
                                                             "dict_type": "event_type"
                                                           },
                                                           {
                                                             "dict_label": "APT",
                                                             "dict_value": "APT",
                                                             "dict_type": "event_type"
                                                           },
                                                           {
                                                             "dict_label": "钓鱼域名/URL、C2域名/URL、虚拟货币交易平台/URL等",
                                                             "dict_value": "URL",
                                                             "dict_type": "event_type"
                                                           }
                                                         ]
                                                       }
                                                     }
                                                    """,
                                            description = """
                                                    "dict_label": 字典的键，要展示在前端的
                                                    "dict_value": 字典的值，要传给后端的
                                                    "dict_type": 字典的类型
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "dictType", description = "字典的类型,事件类型字典是event_type;" +
                            "处置动作字典类型是task_act；处置局点字典类型是task_range", required = true, example = "event_type")
            }
    )
    @GetMapping(value = "/type/{dictType}")
    ResponseResult dictType(@PathVariable String dictType);
}

