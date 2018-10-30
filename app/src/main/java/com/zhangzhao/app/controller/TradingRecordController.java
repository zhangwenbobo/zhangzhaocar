package com.zhangzhao.app.controller;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.vo.TradingRecordVo;
import com.zhangzhao.common.entity.TradingRecord;
import com.zhangzhao.common.vo.StatusOneVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "TradingRecord", description = "交易记录")
@RestController
@RequestMapping("/app/tradingrecord")
public class TradingRecordController extends BaseService {


    @ApiOperation(value = "1 扣款记录", notes = "扣款记录", produces = "application/json")
    @GetMapping("/q/find/id")
    public StatusOneVo<TradingRecordVo> queryTransactionrecord(TradingRecord tradingRecord) {
        return tradingRecordService.findById(tradingRecord);
    }

}
