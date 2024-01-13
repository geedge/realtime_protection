package com.realtime.protection.server.whitelist;

import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "白名单API", description = "白名单模块所有接口")
public interface WhiteListControllerApi {

    @Operation(
            summary = "新增白名单",
            description = "新增一个白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回新增对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "白名单信息")
    )
    ResponseResult newWhitelistObject(@RequestBody WhiteListObject object);

    @Operation(
            summary = "批量导入白名单",
            description = "以Excel文件方式批量导入白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回导入结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Excel文件")
    )
    ResponseResult uploadFile(MultipartFile uploadFile) throws IOException;

    @Operation(
            summary = "下载白名单模板",
            description = "下载白名单模板文件",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回下载结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            }
    )
    void downloadTemplate(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "查询白名单",
            description = "按页和搜索内容查询白名单相关信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回查询到的所有白名单",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "whiteobj_name", description = "白名单名称"),
                    @Parameter(name = "whiteobj_id", description = "白名单ID"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页查询个数", example = "10")
            }
    )
    ResponseResult queryWhiteListObject(@RequestParam(value = "whiteobj_name", required = false) String whiteListName,
                                        @RequestParam(value = "whiteobj_id", required = false) Integer whiteListId,
                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize);

    @Operation(
            summary = "查询单个白名单",
            description = "根据白名单ID查询单个白名单的所有详细信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回查询到的单个白名单",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单ID", example = "2")
            }
    )
    ResponseResult queryWhiteListObjectById(@PathVariable Integer id);

    @Operation(
            summary = "删除白名单",
            description = "删除一个白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单id")
            }
    )
    ResponseResult deleteWhiteListObjectById(@PathVariable Integer id);

    @Operation(
            summary = "批量删除白名单",
            description = "批量删除白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "ids", description = "白名单id数组")
            }
    )
    ResponseResult deleteWhiteListObjects(@PathVariable List<Integer> whiteListObjIds);

    @Operation(
            summary = "修改白名单",
            description = "修改白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回修改对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单id")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "白名单信息")

    )
    ResponseResult updateWhiteListObject(@PathVariable Integer id,
                                         @RequestBody WhiteListObject object);

    @Operation(
            summary = "修改白名单审核状态",
            description = "修改白名单审核状态",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回修改对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单id"),
                    @Parameter(name = "auditStatus", description = "白名单审核状态（0为未审核，1为已退回，2为审核通过）", example = "2")
            }
    )
    ResponseResult updateWhiteListObjectAuditStatus(@PathVariable Integer id,
                                                    @PathVariable Integer auditStatus);


}
