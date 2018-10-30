package com.zhangzhao.app.mapper;

import com.zhangzhao.app.vo.ActivityVo;
import com.zhangzhao.app.vo.CarModelVo;
import com.zhangzhao.common.entity.Activity;
import com.zhangzhao.common.entity.CarModel;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * 车型
 */
@Component
@Mapper(componentModel = "spring")
public interface CarModelMapper {

    CarModelVo beanToVo(CarModel carModel);
}
