package com.zhangzhao.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ReservationDto {
    @ApiModelProperty
    private Long id;
    @NotNull(message = "省不能为空")
    @ApiModelProperty(value = "省")
    private String province;
    @NotNull(message = "区不能为空")
    @ApiModelProperty(value = "区")
    private String district;
    @NotNull(message = "市不能为空")
    @ApiModelProperty(value = "市")
    private String city;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "预约时间", required = true)
    private Date reservationTime;
    @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}", message = "手机号码错误")
    @ApiModelProperty(value = "手机号", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "预约费用", required = true)
    private double ordermoney;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;
    @NotNull(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址")
    private String detailedAddress;
}
