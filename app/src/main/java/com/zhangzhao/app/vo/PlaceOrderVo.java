package com.zhangzhao.app.vo;

import com.zhangzhao.common.entity.Address;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PlaceOrderVo {

    @ApiModelProperty
    private String id;

    @ApiModelProperty(value = "安装费")
    private double installationFee;

    @ApiModelProperty(value = "运费")
    private double freight;

    @ApiModelProperty(value = "积分")
    private Integer integral;

    @ApiModelProperty(value = "默认地址")
    private AddressVo address;

    @ApiModelProperty(value = "购买的商品")
    private List<GoodsCommodityVo> list;
}
