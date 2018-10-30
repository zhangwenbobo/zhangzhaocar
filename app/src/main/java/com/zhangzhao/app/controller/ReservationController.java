package com.zhangzhao.app.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.common.collect.Maps;
import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.config.WechatProperties;
import com.zhangzhao.app.util.StringUtil;
import com.zhangzhao.app.vo.ReservationOrderDetailsVo;
import com.zhangzhao.app.vo.ReservationVo;
import com.zhangzhao.app.vo.WeiXinOrderVo;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.dto.ReservationDto;
import com.zhangzhao.common.dto.ReservationsDto;
import com.zhangzhao.common.entity.*;
import com.zhangzhao.common.util.NumberUtil;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

@Slf4j
@Api(tags = "Reservation", description = "预约")
@RestController
@RequestMapping("/app/reservation")
public class ReservationController extends BaseService {
    @Autowired
    private WxPayService wxService;

    @PostMapping("/save/q/order")
    @ApiOperation(value = "1 预约申请", notes = "预约申请", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVoidVo save(@ApiParam(value = "请求参数封装") @RequestBody @Valid ReservationDto reservationDto, BindingResult bindingResult) {
        return reservationService.save(reservationDto, bindingResult);
    }

    @GetMapping("/update/q/address")
    @ApiOperation(value = "2 更改预约地址", notes = "更改预约地址", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVoidVo updateAddress(@ApiParam(value = "请求参数封装") @RequestBody @Valid ReservationsDto reservationsDto) {
        return reservationService.updateAddress(reservationsDto);
    }


    @PostMapping("/supply/q/order")
    @ApiOperation(value = "3 预约微信统一下单", notes = "预约微信统一下单", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusOneVo<WeiXinOrderVo> reservationOrder(HttpServletRequest request, @ApiParam(value = "预约编号", required = true) @RequestParam Long orderId) {
        StatusOneVo<WeiXinOrderVo> vo = new StatusOneVo<>();
        try {
            Reservation reservation = reservationService.findById(orderId);
            WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
            wxPayUnifiedOrderRequest.setDeviceInfo(WechatProperties.DEVICE_INFO);
            wxPayUnifiedOrderRequest.setNonceStr(StringUtil.getRandomStr(32));
            wxPayUnifiedOrderRequest.setOutTradeNo(reservation.getReservationNumber());
            wxPayUnifiedOrderRequest.setTotalFee((int) (reservation.getOrderMoney() * 100));
            wxPayUnifiedOrderRequest.setSpbillCreateIp(request.getRemoteAddr());
            wxPayUnifiedOrderRequest.setNotifyUrl(wxService.getConfig().getNotifyUrl());
            wxPayUnifiedOrderRequest.setTradeType(WxPayConstants.TradeType.APP);

            WxPayUnifiedOrderResult unifiedOrder = this.wxService.unifiedOrder(wxPayUnifiedOrderRequest);
            if (unifiedOrder.getResultCode().equals(WxPayConstants.RefundStatus.SUCCESS) &&
                    unifiedOrder.getReturnCode().equals(WxPayConstants.RefundStatus.SUCCESS)) {
                String appid = unifiedOrder.getAppid();
                String prepayId = unifiedOrder.getPrepayId();
                String timeStamp = StringUtil.getTimeStamp();
                String nonceStr = StringUtil.getRandomStr(20);
                SortedMap<String, Object> signMap = Maps.newTreeMap();
                signMap.put("appId", appid);
                signMap.put("partnerId", unifiedOrder.getMchId());
                signMap.put("prepayId", prepayId);
                signMap.put("packages", "sign=WXPay");
                signMap.put("nonceStr", nonceStr);
                signMap.put("timeStamp", timeStamp);
                signMap.put("sign", wxService.getConfig().getMchKey());
                String sign = StringUtil.getSign(signMap);
                signMap.put("sign", sign);
                WeiXinOrderVo weiXinOrderVo = (WeiXinOrderVo) StringUtil.mapToObject(signMap, WeiXinOrderVo.class);
                vo.success(weiXinOrderVo);

            } else {
                vo.fail(ErrorCode.WECHAT_PAY_ERROR, unifiedOrder.getReturnMsg() + "微信下单失败" + orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo.fail(ErrorCode.WECHAT_PAY_ERROR, "微信下单失败" + orderId);
        }


        return vo;

    }

    @GetMapping(value = "/q/notify/weixin/pay", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "4 预约下单微信支付回调", notes = "预约下单微信支付回调", produces = MediaType.APPLICATION_JSON_VALUE)
    public String notifyWeiXinPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String xmlStr = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        WxPayOrderNotifyResult notifyResult = wxService.parseOrderNotifyResult(xmlStr);
        Map<String, Object> return_data = new HashMap<String, Object>(2);
        if (!notifyResult.getResultCode().equals(WxPayConstants.RefundStatus.SUCCESS)) {
            return_data.put("return_code", "FAIL");
            return_data.put("return_msg", "return_code错误");
        } else {
            try {
                Integer totalFee = notifyResult.getTotalFee();
                String outTradeNo = notifyResult.getOutTradeNo();
                Double v = Double.valueOf(totalFee / 100);
                Reservation reservation = reservationService.findByreservationNumber(outTradeNo);
                return_data.put("return_code", "SUCCESS");
                return_data.put("return_msg", "OK");
                if (reservation.getStatus() > 1) {
                    return StringUtil.mapToxml(return_data);
                } else if (reservation.getStatus() == 0) {
                    notifys(reservation);
                }

                reservationService.updateStatus(reservation.getId(), Reservation.Status.For_service.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("回调错误:" + notifyResult.getReturnMsg());
            }
        }
        return StringUtil.mapToxml(return_data);
    }

    @GetMapping(value = "/q/order/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "5 预约订单列表", notes = "预约订单列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<ReservationVo> slideshowImgList(
            @ApiParam("当前页") @RequestParam(required = false, defaultValue = "1") int page,
            @ApiParam("每页总数") @RequestParam(required = false, defaultValue = "10") int pageSize,
            @ApiParam("是否到店订单类型1:是 2不是") @RequestParam(defaultValue = "0", required = false) int type,
            @ApiParam("状态 1-待接单 2-待服务 3-服务中 4-待评价 5-已完成") @RequestParam(required = false, defaultValue = "1") int status) {

        return reservationService.findAll(page, pageSize, getUser().getId(), type, status);
    }

    @GetMapping(value = "/q/order/details/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "6 预约订单详情/到店订单详情", notes = "预约订单详情", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusOneVo<ReservationOrderDetailsVo> orderDetails(@ApiParam(value = "订单id", required = true) @RequestParam Long orderId) {
        return reservationService.findOrderId(orderId);

    }


    @GetMapping(value = "/q/delete/details/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "7 删除已完成订单", notes = "删除已完成订单", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVoidVo deleteOrder(@ApiParam(value = "删除已完成ID", required = true) @RequestParam Long id) {
        return reservationService.deleteOrder(id);
    }


    @GetMapping("/q/find/Waitinglist/all")
    @ApiOperation(value = "8 待接单订单列表", notes = "待接单订单列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<ReservationVo> findWaitingList(@ApiParam("当前页") @RequestParam(required = false, defaultValue = "1") int page,
                                                   @ApiParam("每页总数") @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                   @ApiParam("状态1:待接单") @RequestParam(required = false, defaultValue = "1") int status) {
        return reservationService.findAll2(page, pageSize, getUser().getId(), status);

    }

    @ApiOperation(value = "9 立即抢单", notes = "立即抢单", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/q/update/status/order")
    public StatusVoidVo statusOrder(@ApiParam(value = "订单id", required = true) @RequestParam Long id,
                                    @ApiParam(value = "状态 2:待服务 3:服务中 5:已完成", required = true) @RequestParam(defaultValue = "2") int status) {
        return reservationService.updateStatus(id, status);
    }

    @ApiOperation(value = "10 订单确认", notes = "订单确认", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/q/update/status/order/ok")
    public StatusVoidVo statusOrderOk(@ApiParam(value = "订单id", required = true) @RequestParam Long id,
                                      @ApiParam(value = "状态 1:用户确认 2:师傅确认 3:双方确认", required = true) @RequestParam(defaultValue = "0") int statusOk) {
        return reservationService.updatestatusOk(id, statusOk);
    }


    /**
     * 预约订单计算方式
     *
     * @param reservation
     */
    public void notifys(Reservation reservation) {

        Optional<User> optional = userRepository.findById(reservation.getUserId());
        try {
            Wallet wallet = new Wallet();
            double reservationPrice = 0;//预约下单支付价格
            User user = optional.get();
            Services services = new Services();
            if (optional.isPresent()) {
                if (user.getMember() == 0 && user.getMember() == 3) {
                    reservationPrice += NumberUtil.formatDouble(services.getServicePrice() - services.getSubsidyPrice());

                    wallet.setBalance(reservationPrice + services.getSubsidyPrice());

                } else if (user.getMember() == 2) {
                    if (!(user.getGratisTime() == 0)) {
                        int i = user.getGratisTime() - 1;
                        user.setGratisTime(i);
                        reservationPrice += 0;
                    } else {
                        reservationPrice += NumberUtil.formatDouble(services.getServicePrice() - services.getSubsidyPrice());
                        wallet.setBalance(reservationPrice + services.getSubsidyPrice());
                    }
                }
            }
            TradingRecord tradingRecord = new TradingRecord();
            tradingRecord.setType(TradingRecord.Type.EXPEND.getType());
            tradingRecord.setMoney(reservationPrice);
            tradingRecord.setUserId(user.getId());
            tradingRecordService.saveBean(tradingRecord);
            orderSupplyService.updateStatus(reservation.getId(), Reservation.Status.Waiting_list.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}