package com.zhangzhao.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RankingVo {

    @ApiModelProperty(value = "用户头像")
    private String icon;

    @ApiModelProperty(value = "昵称")
    private String name;
}
