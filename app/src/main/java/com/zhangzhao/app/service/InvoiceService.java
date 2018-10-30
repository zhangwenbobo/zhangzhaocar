package com.zhangzhao.app.service;

import com.zhangzhao.common.commonservice.CommonService;
import com.zhangzhao.common.dto.InvoiceDto;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.springframework.validation.BindingResult;

public interface InvoiceService extends CommonService {

    StatusVoidVo saveInvoice(InvoiceDto invoiceDto, BindingResult bindingResult);
}
