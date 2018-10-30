package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.QualityService;
import com.zhangzhao.app.vo.QualityVo;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.dto.QualityDto;
import com.zhangzhao.common.entity.Quality;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 质量处理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class QualityServiceImpl extends BaseService implements QualityService {
    /**
     * 质量处理提交
     *
     * @param qualityDto
     * @param bindingResult
     * @return
     */
    @Override
    public StatusVoidVo saveProcessing(QualityDto qualityDto, BindingResult bindingResult) {
        StatusVoidVo vo = new StatusVoidVo();
        if (bindingResult.hasErrors()) {
            vo.fail(ErrorCode.PARAMETER_ERROR, bindingResult.getFieldError().getDefaultMessage());
        }
        Quality quality = qualityMapper.dtoToBean(qualityDto);
        qualityRepository.saveAndFlush(quality);
        vo.success();
        return vo;
    }

    /**
     * 质量列表
     *
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    @Override
    public StatusVo<QualityVo> findAll(int page, int pageSize, Long id) {
        StatusVo<QualityVo> vo = new StatusVo<QualityVo>();
        PageRequest pageable = new PageRequest(page, pageSize, Sort.Direction.DESC, "createTime");
        Page<Quality> pageContent = qualityRepository.findAll(new Specification<Quality>() {
            @Override
            public Predicate toPredicate(Root<Quality> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                Quality quality = new Quality();
                if (quality.getStatus() > 0) {
                    if (quality.getStatus() == 1) {//1 待跟进 2 待处理 3已处理
                        list.add(root.in(Quality.Status.FOLLOW));
                    } else if (quality.getStatus() == 2) {
                        list.add(root.in(Quality.Status.PROCESS));
                    } else if (quality.getStatus() == 3) {
                        list.add(root.in(Quality.Status.PROCESSED));
                    }
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        }, pageable);
        List<QualityVo> collect = pageContent.getContent().parallelStream().map(qualityMapper::beanToVo).collect(Collectors.toList());
        vo.success(collect);

        return vo;
    }


    /**
     * 质量详情
     *
     * @param id
     * @return
     */
    @Override
    public StatusOneVo<QualityVo> findById(Long id) {
        StatusOneVo<QualityVo> vo = new StatusOneVo<>();
        Quality quality = qualityRepository.getOne(id);
        QualityVo qualityVo = qualityMapper.beanToVo(quality);
        vo.success(qualityVo);
        return vo;
    }

}
