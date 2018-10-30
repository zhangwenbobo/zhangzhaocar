package com.zhangzhao.web.controller;

import com.zhangzhao.web.base.BaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = "OrderSupply",description = "订单")
@Controller
@RequestMapping("/web/ordersupply")
@CrossOrigin
public class OrderSupplyController extends BaseService {

    @GetMapping("/find/order/s/view")
    @ApiOperation(value = "订单页面", notes = "订单页面")
    public Object orderView() {
        return "order/order-list";
    }

    @ResponseBody
    @GetMapping("/find/order/s/list")
    @ApiOperation(value = "订单列表", notes = "订单列表")
    public Object orderList(@RequestParam(required = false,defaultValue = "1")Integer page,
                        @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false)Integer status) {
        return orderSupplyService.findAll(page,pageSize,keyword,status).toString();
    }
}
