package com.zhangzhao.web.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhangzhao.web.base.BaseService;

import io.swagger.annotations.Api;

@Api(tags = "UserCooperate", description = "用户搭档")
@RestController
@RequestMapping("/web/userCooperate")
@CrossOrigin
public class UserCooperateController extends BaseService {

}
