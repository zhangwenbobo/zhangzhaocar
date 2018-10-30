package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.WalletService;
import com.zhangzhao.app.vo.UserWalletVo;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.entity.OrderSupply;
import com.zhangzhao.common.entity.TradingRecord;
import com.zhangzhao.common.entity.User;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 我的钱包
 *
 * @author Administrator
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WalletServiceImpl extends BaseService implements WalletService {
    /**
     * 我的钱包
     *
     * @param id
     * @return
     */
    @Override
    public StatusOneVo<UserWalletVo> findWallet(Long id) {
        StatusOneVo<UserWalletVo> vo = new StatusOneVo<>();
        User user = userRepository.getOne(id);
        UserWalletVo userWalletVo = userMapper.userUserWalletVo(user);
        vo.success(userWalletVo);

        return vo;
    }

    /**
     * 提现
     *
     * @param id
     * @param balance
     * @return
     */
    @Override
    public StatusVoidVo updateBalance(Long id, double balance, String cashAccount, String cardNumber) {
        StatusVoidVo vo = new StatusVoidVo();
        try {
            User user = userRepository.getOne(id);
            if (user != null && user.getWallet().getBalance() >= balance) {
                walletRepository.updateBalance(id, balance);
                TradingRecord tradingRecord = new TradingRecord();
                tradingRecord.setMoney(balance);
                tradingRecord.setCashAccount(cashAccount);
                tradingRecord.setCardNumber(cardNumber);
                tradingRecordRepository.save(tradingRecord);
                vo.success();
            } else {
                vo.fail(ErrorCode.ACCOUNT_ERROR, "错误,提现超出");
            }

        } catch (Exception e) {
            e.printStackTrace();
            vo.fail(ErrorCode.SYSTEM_ERROR, "提现失败");
        }
        return vo;
    }

    @Override
    public StatusVoidVo payment(long orderId, double paymentPrice) {
        StatusVoidVo vo = new StatusVoidVo();
        User user = getUser();
        if (user != null && user.getWallet().getBalance() >= paymentPrice) {
            walletRepository.updateBalance(user.getId(), paymentPrice);
            orderSupplyRepository.updateStatus(orderId,OrderSupply.Status.PENDING_DELIVERY.getStatus());
            TradingRecord tradingRecord = new TradingRecord();
            tradingRecord.setType(TradingRecord.Type.EXPEND.getType());
            tradingRecord.setMoney(paymentPrice);
            tradingRecord.setUserId(user.getId());
            tradingRecord.setStatus(TradingRecord.Status.SUCCESSD.getStatus());
            tradingRecordRepository.save(tradingRecord);
            vo.success();
        } else {
            vo.fail(ErrorCode.ACCOUNT_ERROR, "余额不足");
        }
        return vo;
    }
}
