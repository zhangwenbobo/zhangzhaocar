package com.zhangzhao.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GoodsCommodityVo {

	@ApiModelProperty
	private String id;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "图片")
	private String img;

	@ApiModelProperty(value = "价格")
	private String price;

	@ApiModelProperty(value = "属性")
	private String property;

	@ApiModelProperty(value = "促销价")
	private String promotionPrice;

	@ApiModelProperty(value = "积分")
	private String integral;

	@ApiModelProperty(value = "销量")
	private String sales;

	@ApiModelProperty(value = "数量")
	private String inventory;
}
