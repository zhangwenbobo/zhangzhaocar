package com.zhangzhao.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderDetailsVo {

    @ApiModelProperty
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "图片")
    private String img;

    @ApiModelProperty(value = "价格")
    private String price;

    @ApiModelProperty(value = "支付价格")
    private double paymentPrcie;

    @ApiModelProperty(value = "数量")
    private String amount;

    @ApiModelProperty(value = "积分")
    private String integral;

    @ApiModelProperty(value = "商品id")
    private String goodsCommodityId;

    @ApiModelProperty(value = "胎号")
    private String fetalNumber;

    @ApiModelProperty(value = "型号")
    private String model;
}
