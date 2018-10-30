package com.zhangzhao.app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderSupplyDetailVo {

    @ApiModelProperty
    private String id;

    @ApiModelProperty(value = "订单编号")
    private String orderNumber;

    @ApiModelProperty(value = "订单价格")
    private String orderPrcie;

    @ApiModelProperty(value = "商品总价")
    private String goodPrcie;

    @ApiModelProperty(value = "状态 -2取消 -1-删除 1-待付款 2-待发货 3-待收货 4-待评价 5-已完成 6-退款 7-退货退款")
    private String status;

    @ApiModelProperty(value = "运费")
    private String freightPrcie;

    @ApiModelProperty(value = "积分")
    private String integral;

    @ApiModelProperty(value = "补发物流")
    private String reissue;

    @ApiModelProperty(value = "类型 1-不参与优惠 2-参与优惠")
    private String type;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "预安装")
    private Date preInstallationTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上门安装")
    private Date storeInstallationTime;

    @ApiModelProperty(value = "门店")
    private StoreVo store;

    @ApiModelProperty(value = "发票")
    private InvoiceVo invoice;

    @ApiModelProperty(value = "地址")
    private AddressVo address;

    @ApiModelProperty(value = "详细")
    private List<OrderDetailsVo> orderDetails;
}
