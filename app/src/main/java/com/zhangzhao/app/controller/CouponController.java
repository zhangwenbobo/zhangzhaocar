package com.zhangzhao.app.controller;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.vo.CouponVo;
import com.zhangzhao.app.vo.ShoppingCartVo;
import com.zhangzhao.common.entity.Coupon;
import com.zhangzhao.common.vo.StatusVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Coupon",description = "优惠券")
@RestController
@RequestMapping("/app/coupon")
public class CouponController extends BaseService {

    @GetMapping("/coupon/s/list")
    @ApiOperation(value = "1 用户优惠券列表", notes = "用户优惠券列表",produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<CouponVo> findAll(){
        StatusVo<CouponVo> vo=new StatusVo<>();
        List<CouponVo> collect = getUser().getCoupons().parallelStream().filter(o -> o.getStatus() == 0 || o.getStatus() == 1).map(couponMapper::beanToVo).collect(Collectors.toList());
        vo.success(collect);
        return vo;
    }

    @PostMapping("/coupon/s/update/status")
    @ApiOperation(value = "2 修改优惠券状态", notes = "修改优惠券状态", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVoidVo upStatus(@ApiParam(value = "优惠券id", required = true) @RequestParam Long id,
                                 @ApiParam(value = "状态 0-未使用 1-使用", required = true) @RequestParam Integer status) {
        return couponService.upStatus(id,status);
    }

    @GetMapping("/coupon/s/detail/id")
    @ApiOperation(value = "3 用户优惠券详情", notes = "用户优惠券详情",produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<CouponVo> detail(@ApiParam(value = "优惠券id", required = true) @RequestParam Long id){
        StatusVo<CouponVo> vo=new StatusVo<>();
        List<CouponVo> collect = getUser().getCoupons().parallelStream().filter(o -> o.getStatus() == 0 || o.getStatus() == 1).map(couponMapper::beanToVo).collect(Collectors.toList());
        vo.success(collect);
        return vo;
    }
}
