package com.realtime.protection.configuration.entity.defense.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateNew {
    @JsonProperty("template_id")
    @Schema(description = "防御策略模板ID", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer templateId;

    @JsonProperty("template_name")
    @NotNull(message = "template_name字段不能为空。")
    @Schema(description = "防御策略模板名称", example = "APT攻击事件日常态")
    private String templateName;

    @JsonProperty("source_system")
    @NotNull(message = "source_system字段不能为空。")
    @Schema(description = "防御策略模板数据来源系统", example = "BW系统")
    private String sourceSystem;

    @JsonProperty("event_type")
    @NotNull(message = "事件类型字段不能为空。")
    @Schema(description = "防御策略模板事件类型", example = "APT攻击事件")
    private String eventType;

    @JsonProperty("protect_level")
    @NotNull(message = "防护等级字段不能为空。")
    @Max(value = 3)
    @Min(value = 1)
    @Schema(description = "防护等级：1代表日常态、2代表应急态、3代表紧急态", example = "1")
    private String protectLevel;

    @JsonProperty("description")
    @Schema(description = "对策略模板的文字描述。方便用户使用", example = "zd防护对象的全流量")
    private String description;

    @Schema(description = "该防护等级是否需要提取防护对象IP地址字段")
    private Boolean hasProtectObjectIP = false;

    @Schema(description = "该防护等级是否需要提取防护对象端口字段")
    private Boolean hasProtectObjectPort = false;

    @Schema(description = "该防护等级是否需要提取对端IP地址字段")
    private Boolean hasPeerIP = false;

    @Schema(description = "该防护等级是否需要提取对端端口字段")
    private Boolean hasPeerPort = false;

    @Schema(description = "该防护等级是否需要提取网络协议字段")
    private Boolean hasProtocol = false;

    @Schema(description = "该防护等级是否需要提取URL字段")
    private Boolean hasURL = false;

    @Schema(description = "该防护等级是否需要提取DNS")
    private Boolean hasDNS = false;

//    @Schema(description = "该防护等级防护对象ip地址是否作为目的地址")
//    private Boolean isFullFlow = false;
//
//    @Schema(description = "该防护等级防护对象ip地址是否作为源地址")
//    private Boolean isProtectObjectIPSrc = false;

    @Schema(description = "该防护等级防护对象ip地址是否作为目的地址")
    private Boolean isProtectObjectDst = false;

    @Schema(description = "该防护等级防护对象ip地址是否作为源地址")
    private Boolean isProtectObjectSrc = false;

    @JsonProperty("template_used_times")
    @Schema(description = "防御策略模板使用次数", example = "20", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer usedTimes;

    @JsonProperty("running_tasks")
    @Schema(description = "防御策略模板已运行的任务数量", example = "30", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer runningTasks;

    @JsonProperty("create_user_id")
    @Schema(description = "防御策略模板创建人ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer createUserId;

    @JsonProperty("create_user_name")
    @Schema(description = "防御策略模板创建人名称", example = "xxx", accessMode = Schema.AccessMode.READ_ONLY)
    private String createUsername;

    @JsonProperty("create_user_depart")
    @Schema(description = "防御策略模板创建人处室", example = "xxx", accessMode = Schema.AccessMode.READ_ONLY)
    private String createDepart;

    @JsonProperty("audit_status")
    @Schema(description = "防御策略模板审核状态（0为未审核，1为已退回，2为审核通过）", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private String auditStatus;

    @JsonProperty("template_display_id")
    @Schema(description = "策略模板展示id", accessMode = Schema.AccessMode.READ_ONLY)
    private String templateDisplayId;

    @JsonProperty("audit_user_name")
    @Schema(description = "审核用户名称", example = "user11", accessMode = Schema.AccessMode.READ_ONLY)
    private String auditUserName;
    @JsonProperty("audit_user_id")
    @Schema(description = "审核用户id", example = "11111", accessMode = Schema.AccessMode.READ_ONLY)
    private String auditUserId;
    @JsonProperty("audit_user_depart")
    @Schema(description = "审核用户部门", example = "部门1", accessMode = Schema.AccessMode.READ_ONLY)
    private String auditUserDepart;

    @JsonProperty("create_time")
    @Schema(description = "新建时间", example = "部门1", accessMode = Schema.AccessMode.READ_ONLY)
    private String createTime;

    @JsonProperty("update_time")
    @Schema(description = "更新时间", example = "部门1", accessMode = Schema.AccessMode.READ_ONLY)
    private String updateTime;

    @JsonProperty("effecive_time")
    @Schema(description = "日志表中有效开始时间", example = "", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime effeciveTime;

    @JsonProperty("expire_time")
    @Schema(description = "日志表中到期时间", example = "", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime expireTime;
}
