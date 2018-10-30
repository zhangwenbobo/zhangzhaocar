package com.zhangzhao.web.service;

import com.zhangzhao.common.commonservice.CommonService;
import com.zhangzhao.common.vo.StatusPageVo;

public interface OrderSupplyService extends CommonService {

    StatusPageVo findAll(Integer page, Integer pageSize,String keyword,Integer status);
}
