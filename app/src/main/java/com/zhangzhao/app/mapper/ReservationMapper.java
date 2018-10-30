package com.zhangzhao.app.mapper;

import com.zhangzhao.app.vo.ReservationVo;
import com.zhangzhao.common.dto.ReservationDto;
import com.zhangzhao.common.entity.Reservation;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * 预约
 *
 * @author Administrator
 */
@Component
@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationVo beanToVo(Reservation reservation);

    Reservation dtoToBean(ReservationDto reservationDto);



}
