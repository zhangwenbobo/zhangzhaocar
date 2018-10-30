package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.WithdrawService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提现
 *
 * @author Administrator
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WithdrawServiceImpl extends BaseService implements WithdrawService {
    ///**
    // * 提现
    // *
    // * @param id
    // * @param type
    // * @param balance
    // * @return
    // */
    //@Override
    //public StatusVoidVo updateBalance(Long id, double balance, int type) {
    //    StatusVoidVo vo = new StatusVoidVo();
    //    try {
    //        User user = userRepository.getOne(id);
    //        if (user != null && user.getWallet().getBalance() >= balance) {
    //            walletRepository.updateBalance(id, balance);
    //            TradingRecord tradingRecord = new TradingRecord();
    //            tradingRecord.setUser(user);
    //            tradingRecord.setMoney(balance);
    //            tradingRecordRepository.save(tradingRecord);
    //            vo.success();
    //        } else {
    //            vo.fail(ErrorCode.ACCOUNT_ERROR, "错误,提现超出");
    //
    //        }
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //        vo.fail(ErrorCode.SYSTEM_ERROR, "提现失败");
    //    }
    //    return vo;
    //}
}
