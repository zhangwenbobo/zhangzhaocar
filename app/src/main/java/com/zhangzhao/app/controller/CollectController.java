package com.zhangzhao.app.controller;

import com.zhangzhao.app.vo.GoodsCommodityVo;
import com.zhangzhao.common.vo.StatusVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.zhangzhao.app.base.BaseService;

import io.swagger.annotations.Api;

@Api(tags = "Collect", description = "收藏")
@RestController
@RequestMapping("/app/collect")
public class CollectController extends BaseService {

    @GetMapping("/collect/s/goods/list")
    @ApiOperation(value = "1 我的收藏", notes = "我的收藏",produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<GoodsCommodityVo> collects(@ApiParam(value="当前页")@RequestParam(required = false,defaultValue = "1")Integer page,
                                               @ApiParam(value="页数")@RequestParam(required = false,defaultValue = "10")Integer pageSize){
        return collectService.findAll(page,pageSize);
    }
}
