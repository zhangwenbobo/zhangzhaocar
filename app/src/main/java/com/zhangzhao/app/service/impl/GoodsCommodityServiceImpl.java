package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.vo.*;
import com.zhangzhao.common.entity.Activity;
import com.zhangzhao.common.entity.Collect;
import com.zhangzhao.common.entity.GoodsCommodity;
import com.zhangzhao.common.entity.Properties;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.GoodsCommodityService;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  商品信息
 * 
 * @author Administrator
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsCommodityServiceImpl extends BaseService implements GoodsCommodityService {

    @Override
    public StatusVo<GoodsCommodityVo> findNoyesHotsell() {
        Pageable pageable = new PageRequest(0, 6, Sort.Direction.DESC, "createTime");
        Page<GoodsCommodity> page = goodsCommodityRepository.findByNoyesHotsell("1", pageable);
        List<GoodsCommodityVo> collect = page.getContent().parallelStream().map(goodsCommodityMapper::beanToVo).collect(Collectors.toList());
        StatusVo<GoodsCommodityVo> vo=new StatusVo<>();
        vo.success(collect);
        return vo;
    }

    @Override
    public StatusOneVo<GoodsCommodityDetailsVo> details(Long id) {
        StatusOneVo<GoodsCommodityDetailsVo> vo=new StatusOneVo<>();
        Optional<GoodsCommodity> optional = goodsCommodityRepository.findById(id);
        if (optional.isPresent()){
            GoodsCommodityDetailsVo goodsCommodityDetailsVo = goodsCommodityMapper.beanToDetailsVo(optional.get());
            List<GoodsCommodity> series = goodsCommodityRepository.findBySeries(optional.get().getSeries());
            List<List<ProperstiesVo>> collect = series.parallelStream().filter(o -> !o.getId().equals(optional.get().getId())).map
                    (GoodsCommodity::getProperties).map(goodsCommodityMapper::beanToGoodsPropersieVo).collect(Collectors.toList());
            List<ProperstiesVo> list = collect.stream().flatMap(o -> Arrays.stream(o.toArray(new ProperstiesVo[]{}))).distinct().collect(Collectors.toList());
            goodsCommodityDetailsVo.setPropersieVoList(list);
            Activity activity = activityRepository.findByGoodIdAndStartTimeLessThanEqualAndEndTimeAfter(optional.get().getId(), new Date(), new Date());
            if (activity!=null){
                ActivityVo activityVo = activityMapper.beanToVo(activity);
                goodsCommodityDetailsVo.setActivityVo(activityVo);
                goodsCommodityDetailsVo.setPromotionPrice(String.valueOf(activity.getPrice()));
            }
            if (getUser()!=null){
                int i = collectRepository.countByUserIdAndGoodsCommodity_Id(getUser().getId(), id);
                 goodsCommodityDetailsVo.setCollect(String.valueOf(i));
            }
            vo.success(goodsCommodityDetailsVo);
        }
        return vo;
    }

    @Override
    public StatusVo<GoodsCommodityVo> search(Integer page,Integer pageSize,String keyword, Integer priceDesc, Integer dealDesc,Integer two,Integer classification,String figure) {
        StatusVo<GoodsCommodityVo> vo=new StatusVo<>();
        Sort sort = new Sort(Sort.Direction.DESC, "price");
        if (priceDesc==1){
            sort = new Sort(Sort.Direction.ASC, "price");
        }
        if (dealDesc==0){
            sort.and(new Sort(Sort.Direction.DESC, "sales"));
        }else {
            sort.and(new Sort(Sort.Direction.ASC, "sales"));
        }
        Pageable pageable = PageRequest.of(page-1, pageSize, sort);
        Page<GoodsCommodity> all = goodsCommodityRepository.findAll(new Specification<GoodsCommodity>() {
            @Override
            public Predicate toPredicate(Root<GoodsCommodity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank(keyword)) {
                    Predicate name = criteriaBuilder.like(root.get("name"), "%" + keyword + "%");
                    Predicate figure = criteriaBuilder.like(root.get("figure"), "%" + keyword + "%");
                    Predicate model = criteriaBuilder.like(root.get("model"), "%" + keyword + "%");
                    list.add(criteriaBuilder.or(name, figure, model));
                }else if (two != null){
                    list.add(criteriaBuilder.equal(root.get("twoClassId"),two));
                }else if (classification!=null){
                    list.add(criteriaBuilder.equal(root.get("threeClassId"),classification));
                }
                if (StringUtils.isNotBlank(figure)) {
                    list.add(criteriaBuilder.equal(root.get("figure"),figure));
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        }, pageable);
        List<GoodsCommodityVo> collect = all.getContent().parallelStream().map(goodsCommodityMapper::beanToVo).collect(Collectors.toList());
        vo.success(collect);
        return vo;
    }

    @Override
    public List<GoodsCommodity> findAllById(List<Long> ids) {
        return goodsCommodityRepository.findAllById(ids);
    }
}
