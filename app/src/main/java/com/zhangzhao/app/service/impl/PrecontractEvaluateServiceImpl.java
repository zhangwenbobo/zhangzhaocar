package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.PrecontractEvaluateService;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.dto.PrecontractEvaluateDto;
import com.zhangzhao.common.entity.PrecontractEvaluate;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * 预约评价
 *
 * @author Administrator
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PrecontractEvaluateServiceImpl extends BaseService implements PrecontractEvaluateService {

    @Override
    public StatusVoidVo saveList(List<PrecontractEvaluate> precontractEvaluates) {
        StatusVoidVo vo = new StatusVoidVo();
        precontractEvaluateRepository.saveAll(precontractEvaluates);
        vo.success();
        return vo;
    }

}
