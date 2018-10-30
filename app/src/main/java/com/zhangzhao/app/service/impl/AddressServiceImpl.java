package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.ActivityService;
import com.zhangzhao.app.service.AddressService;
import com.zhangzhao.app.vo.AddressVo;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.dto.AddressDto;
import com.zhangzhao.common.entity.Address;
import com.zhangzhao.common.entity.BankCard;
import com.zhangzhao.common.vo.StatusVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 地址
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AddressServiceImpl extends BaseService implements AddressService {

    @Override
    public StatusVo findAll(Integer page, Integer pageSize, String... p) {
        StatusVo vo=new StatusVo();
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.Direction.DESC, "createTime");
        Page<Address> all = addressRepository.findByUserId(getUser().getId(), pageable);
        List<AddressVo> collect = all.getContent().parallelStream().map(addressMapper::beanToVo).collect(Collectors.toList());
        vo.success(collect);
        return vo;
    }

    @Override
    public StatusVoidVo saveOrUp(AddressDto addressDto, BindingResult bindingResult) {
        StatusVoidVo vo = new StatusVoidVo();
        if (bindingResult.hasErrors()) {
            vo.fail(ErrorCode.PARAMETER_ERROR, bindingResult.getFieldError().getDefaultMessage());
        } else {
            Address address = addressMapper.dtoToBean(addressDto);
            address.setUserId(getUser().getId());
            addressRepository.saveAndFlush(address);
            vo.success();
        }
        return vo;
    }
}
