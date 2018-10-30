package com.zhangzhao.app.service;

import com.zhangzhao.app.vo.QualityVo;
import com.zhangzhao.common.commonservice.CommonService;
import com.zhangzhao.common.dto.QualityDto;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.springframework.validation.BindingResult;

public interface QualityService extends CommonService {

    /**
     * 质量处理提交
     *
     * @param qualityDto
     * @param bindingResult
     * @return
     */
    StatusVoidVo saveProcessing(QualityDto qualityDto, BindingResult bindingResult);

    /**
     * 质量处理列表
     *
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    StatusVo<QualityVo> findAll(int page, int pageSize, Long id);

    /**
     * 质量处理详情
     *
     * @param id
     * @return
     */
    StatusOneVo<QualityVo> findById(Long id);
}
