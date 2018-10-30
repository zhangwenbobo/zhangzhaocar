package com.zhangzhao.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MasterTimeVo {
	@ApiModelProperty
	private String id;

	@ApiModelProperty(value = "简介内容")
	private String profile;

	@ApiModelProperty(value = "协议")
	private String agreement;

	@ApiModelProperty(value = "客服")
	private String customer;
}
