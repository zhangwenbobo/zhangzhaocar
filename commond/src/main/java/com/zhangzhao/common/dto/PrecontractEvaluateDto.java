package com.zhangzhao.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class PrecontractEvaluateDto {

    @ApiModelProperty
    private int id;

    @NotNull(message = "星级不能为空")
    @ApiModelProperty(value = "'星级")

    private String starLevel;
    @ApiModelProperty(value = "内容")

    private String content;
    @ApiModelProperty(value = "图片")
    private String img;

    @ApiModelProperty(value = "匿名")
    private String anonymity;

    @ApiModelProperty(value = "预约ID")
    private Long anonymityId;

}
