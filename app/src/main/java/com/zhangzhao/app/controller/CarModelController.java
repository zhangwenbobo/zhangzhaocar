package com.zhangzhao.app.controller;

import com.zhangzhao.app.base.BaseService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "CarModel",description = "车型")
@RestController
@RequestMapping("/app/carmodel")
public class CarModelController extends BaseService {

}
