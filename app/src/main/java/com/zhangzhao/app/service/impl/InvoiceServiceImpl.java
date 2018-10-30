package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.ActivityService;
import com.zhangzhao.app.service.InvoiceService;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.dto.InvoiceDto;
import com.zhangzhao.common.entity.Invoice;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

/**
 * 发票
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InvoiceServiceImpl extends BaseService implements InvoiceService {

    @Override
    public StatusVoidVo saveInvoice(InvoiceDto invoiceDto, BindingResult bindingResult) {
        StatusVoidVo vo=new StatusVoidVo();
        if (bindingResult.hasErrors()){
            vo.fail(ErrorCode.PARAMETER_ERROR, bindingResult.getFieldError().getDefaultMessage());
        }else {
            Invoice invoice = invoiceMapper.dtoToBean(invoiceDto);
            invoice.setUserId(getUser().getId());
            invoiceRepository.saveAndFlush(invoice);
            vo.success();
        }
        return vo;
    }
}
