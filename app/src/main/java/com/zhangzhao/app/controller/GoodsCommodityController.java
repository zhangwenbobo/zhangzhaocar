package com.zhangzhao.app.controller;

import com.zhangzhao.app.vo.GoodsCommodityDetailsVo;
import com.zhangzhao.app.vo.GoodsCommodityVo;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.zhangzhao.app.base.BaseService;

import io.swagger.annotations.Api;

@Api(tags = "GoodsCommodity", description = "商品信息")
@RestController
@RequestMapping("/app/goodsCommodity")
public class GoodsCommodityController extends BaseService {

    @GetMapping("/s/goods/commodity/list")
    @ApiOperation(value = "1 首页热销商品", notes = "首页热销商品",produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<GoodsCommodityVo> findNoyesHotsell(){
        return goodsCommodityService.findNoyesHotsell();
    }

    @GetMapping("/s/goods/commodity/details/id")
    @ApiOperation(value = "2 商品详情", notes = "商品详情",produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusOneVo<GoodsCommodityDetailsVo> details(@ApiParam(value = "商品id", required = true) @RequestParam Long id){
        return goodsCommodityService.details(id);
    }

    @GetMapping("/s/goods/search/list")
    @ApiOperation(value = "3 商品/分类搜索", notes = "商品/分类搜索",produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<GoodsCommodityVo> findAll(@ApiParam(value="当前页")@RequestParam(required = false,defaultValue = "1")Integer page,
                                              @ApiParam(value="页数")@RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                              @ApiParam(value="关键字")@RequestParam(required = false)String keyword,
                                              @ApiParam(value="价格排序 0-降序 1-升序")@RequestParam(required = false,defaultValue = "0")Integer priceDesc,
                                              @ApiParam(value="销量排序 0-降序 1-升序")@RequestParam(required = false,defaultValue = "0")Integer dealDesc,
                                              @ApiParam(value="二级分类id")@RequestParam(required = false)Integer two,
                                              @ApiParam(value="三级分类id")@RequestParam(required = false)Integer classification,
                                              @ApiParam(value="花纹")@RequestParam(required = false)String figure){
        return goodsCommodityService.search(page,pageSize,keyword,priceDesc,dealDesc,two,classification,figure);
    }
}
