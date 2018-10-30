package com.zhangzhao.app.service;

import com.zhangzhao.common.commonservice.CommonService;
import com.zhangzhao.common.entity.PrecontractEvaluate;
import com.zhangzhao.common.vo.StatusVoidVo;

import java.util.List;

public interface PrecontractEvaluateService extends CommonService {


    /**
     * 预约评价
     *
     * @param precontractEvaluates
     * @return
     */
    StatusVoidVo saveList(List<PrecontractEvaluate> precontractEvaluates);




}
