package com.zhangzhao.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class CouponVo {

    @ApiModelProperty
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型 1-优惠券 2-代金券")
    private String type;

    @ApiModelProperty(value = "金额")
    private double price;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "到期时间")
    private Date endTime;

    @ApiModelProperty(value = "指定商品ID")
    private String goodId;

    @ApiModelProperty(value = "0-未使用 1-已使用 2-已过期")
    private String status;

    @ApiModelProperty(value = "指定用户ID")
    private String userId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
