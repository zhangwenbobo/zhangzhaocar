package com.zhangzhao.web.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class GoodsCommodityVo {
	@ApiModelProperty
	private String id;

	@ApiModelProperty(value = "标题")
	private String name;

	@ApiModelProperty(value = "副标题")
	private String viceName;

	@ApiModelProperty(value = "主图")
	private String img;

	@ApiModelProperty(value = "价格")
	private double price;

	@ApiModelProperty(value = "搭档价")
	private double cooperatePrcie;

	@ApiModelProperty(value = "会员价")
	private double memberYprcie;

	@ApiModelProperty(value = "会员价")
	private double memberXprcie;

	@ApiModelProperty(value = "1-热销")
	private String noyesHotsell;

	@ApiModelProperty(value = "促销价")
	private String promotionPrice;

	@ApiModelProperty(value = "状态 0-正常 1-删除")
	private int status;

	@ApiModelProperty(value = "时间")
	private Date createTime;

	@ApiModelProperty(value = "库存")
	private int inventory;

	@ApiModelProperty(value = "安装类型 0-到店安装 1-无需安装 2-上门安装")
	private String installationType;

	@ApiModelProperty(value = "安装价")
	private double installationFee;

	@ApiModelProperty(value = "属性")
	private String property;

}
