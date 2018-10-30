package com.zhangzhao.web.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhangzhao.web.base.BaseService;

import io.swagger.annotations.Api;

@Api(tags = "GoodsCommodity", description = "商品信息")
@RestController
@RequestMapping("/web/goodsCommodity")
@CrossOrigin
public class GoodsCommodityController extends BaseService {

}
