package com.realtime.protection.configuration.entity.dict;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class SysDictData {
    @JsonProperty("dict_label")
    @Schema(description = "字典标签")
    private String dictLabel;

    @JsonProperty("dict_value")
    @Schema(description = "字典值")
    private String dictValue;

    @JsonProperty("dict_type")
    @Schema(description = "字典类型")
    private String dictType;

    @JsonProperty("dict_remark")
    @Schema(description = "字典备注")
    private String dictRemark;

    @JsonProperty("department")
    @Schema(description = "字典数据对应可查看部门")
    private String department;


}
