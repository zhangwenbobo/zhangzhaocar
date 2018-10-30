package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.ReservationService;
import com.zhangzhao.app.vo.ReservationOrderDetailsVo;
import com.zhangzhao.app.vo.ReservationVo;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.dto.ReservationDto;
import com.zhangzhao.common.dto.ReservationsDto;
import com.zhangzhao.common.entity.Reservation;
import com.zhangzhao.common.entity.User;
import com.zhangzhao.common.util.UtilDate;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预约
 *
 * @author Administrator
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReservationServiceImpl extends BaseService implements ReservationService {

    /**
     * 预约申请下单
     *
     * @param reservationDto
     * @return
     */
    @Override
    public StatusVoidVo save(ReservationDto reservationDto, BindingResult bindingResult) {
        StatusVoidVo vo = new StatusVoidVo();
        if (bindingResult.hasErrors()) {
            vo.fail(ErrorCode.PARAMETER_ERROR, bindingResult.getFieldError().getDefaultMessage());
            return vo;
        } else {
            try {
                String orderNum = UtilDate.getOrderNum();
                Reservation reservation = reservationMapper.dtoToBean(reservationDto);
                Optional<User> user = userRepository.findById(reservationDto.getId());
                reservation.setReservationNumber(orderNum);
                if (!user.isPresent()) {
                    vo.fail(ErrorCode.USER_ERROR, "用户不存在");
                    return vo;
                }
                reservation.setProvince(reservation.getProvince());
                reservation.setDistrict(reservation.getDistrict());
                reservation.setCity(reservation.getCity());
                reservation.setDetailedAddress(reservation.getDetailedAddress());
                reservation.setAppointmentTime(reservation.getAppointmentTime());
                reservation.setPhoneNumber(reservation.getPhoneNumber());
                reservation.setRemark(reservation.getRemark());
                reservation.setStatus(Reservation.Status.Waiting_list.getStatus());
                reservation.setOrderMoney(reservation.getOrderMoney());
                reservation.setUserId(getUser().getId());
                reservationRepository.saveAndFlush(reservation);
            } catch (Exception e) {
                vo.fail(ErrorCode.SYSTEM_ERROR, "下单失败");
                return vo;
            }
            vo.success();
            return vo;
        }
    }

    /**
     * 修改预约地址
     *
     * @param reservationsDto
     * @return
     */
    @Override
    public StatusVoidVo updateAddress(ReservationsDto reservationsDto) {
        StatusVoidVo vo = new StatusVoidVo();
        try {

            reservationRepository.updateAddress(reservationsDto.getId(), reservationsDto.getProvince(),
                    reservationsDto.getDistrict(), reservationsDto.getCity(),
                    reservationsDto.getDetailedAddress());
        } catch (Exception e) {
            e.printStackTrace();
            vo.fail(ErrorCode.SYSTEM_ERROR, "修改地址失败");
        }
        vo.success();
        return vo;
    }

    /**
     * 预约订单状态
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public StatusVoidVo updateStatus(Long id, int status) {
        StatusVoidVo vo = new StatusVoidVo();
        reservationRepository.updateStatus(id, status);
        vo.success();
        return vo;
    }

    @Override
    public StatusVoidVo updatestatusOk(Long id, int statusOk) {
        StatusVoidVo vo = new StatusVoidVo();
        Reservation reservation = new Reservation();
        reservationRepository.updatestatusOk(id, statusOk);
        if (statusOk == 1) {
            vo.fail(ErrorCode.PARAMETER_ERROR, "请先用户确认");
        } else if (statusOk == 2) {
            vo.fail(ErrorCode.PARAMETER_ERROR, "等待师傅确认");
        } else if (statusOk == 3) {
            reservation.setStatus(statusOk);
            vo.success();
        }
        return vo;
    }

    /**
     * 查询订单ID
     *
     * @param id
     * @return
     */
    @Override
    public Reservation findById(Long id) {

        return reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("预约订单为空" + id));
    }

    /**
     * 查询订单编号
     *
     * @param reservationNumber
     * @return
     */
    @Override
    public Reservation findByreservationNumber(String reservationNumber) {
        return reservationRepository.findByreservationNumber(reservationNumber);
    }

    /**
     * 预约订单列表
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public StatusVo<ReservationVo> findAll(int page, int pageSize, Long id, int type, int status) {
        StatusVo<ReservationVo> vo = new StatusVo<>();
        PageRequest pageable = new PageRequest(page, pageSize, Sort.Direction.DESC, "createTime");
        Page<Reservation> all = reservationRepository.findAll(new Specification<Reservation>() {
            @Override
            public Predicate toPredicate(Root<Reservation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (type > 0) {
                    if (type == 1) {//1是2否到店单
                        list.add(root.in(Reservation.yesnoShopnote.YES));
                    } else if (type == 2) {
                        list.add(root.in(Reservation.yesnoShopnote.NO));
                    }
                }
                if (status > 0) {//状态
                    if (status == 1) {
                        list.add(root.in(Reservation.Status.Waiting_list));
                    } else if (status == 2) {
                        list.add(root.in(Reservation.Status.For_service));
                    } else if (status == 3) {
                        list.add(root.in(Reservation.Status.in_service));
                    } else if (status == 4) {
                        list.add(root.in(Reservation.Status.to_be_evaluated));
                    } else if (status == 5) {
                        list.add(root.in(Reservation.Status.have_been_evaluated));
                    }
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        }, pageable);
        List<ReservationVo> collect = all.getContent().parallelStream().map(reservationMapper::beanToVo).collect(Collectors.toList());
        vo.success(collect);
        return vo;
    }

    @Override
    public StatusVo<ReservationVo> findAll2(int page, int pageSize, Long id, int status) {
        StatusVo<ReservationVo> vo = new StatusVo<>();
        PageRequest request = new PageRequest(page, pageSize, Sort.Direction.DESC, "createTime");
        Page<Reservation> all = reservationRepository.findAll(new Specification<Reservation>() {
            @Override
            public Predicate toPredicate(Root<Reservation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (status == 1) {
                    list.add(root.in(Reservation.Status.Waiting_list));
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        }, request);
        List<ReservationVo> collect = all.getContent().parallelStream().map(reservationMapper::beanToVo).collect(Collectors.toList());
        vo.success(collect);
        return vo;
    }


    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    @Override
    public Reservation findOrderById(Long orderId) {
        return reservationRepository.getOne(orderId);
    }

    /**
     * 删除已完成订单
     *
     * @param id
     * @return
     */
    @Override
    public StatusVoidVo deleteOrder(Long id) {
        StatusVoidVo vo = new StatusVoidVo();
        try {
            reservationRepository.deleteById(id);

        } catch (Exception e) {
            vo.fail(ErrorCode.SYSTEM_ERROR, "删除错误");
        }
        vo.success();
        return vo;
    }

    /**
     * 订单保存
     *
     * @param orderId
     * @return
     */
    @Override
    public StatusOneVo<ReservationOrderDetailsVo> findOrderId(Long orderId) {
        StatusOneVo<ReservationOrderDetailsVo> vo = new StatusOneVo<>();
        Reservation one = reservationRepository.getOne(orderId);
        ReservationOrderDetailsVo reservationOrderDetailsVo = reservationOrderDetailsMapper.beanToVo(one);
        vo.success(reservationOrderDetailsVo);

        return vo;
    }

    private Date getNeedTime(int hour, int minute, int second, int addDay, int... args) {
        Calendar calendar = Calendar.getInstance();
        if (addDay != 0) {
            calendar.add(Calendar.DATE, addDay);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        if (args.length == 1) {
            calendar.set(Calendar.MILLISECOND, args[0]);
        }
        return calendar.getTime();
    }
}
