package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.OrderSupplyService;
import com.zhangzhao.app.vo.GoodsCommodityVo;
import com.zhangzhao.app.vo.OrderSupplyDetailVo;
import com.zhangzhao.app.vo.OrderSupplyVo;
import com.zhangzhao.app.vo.PlaceOrderVo;
import com.zhangzhao.common.constant.ErrorCode;
import com.zhangzhao.common.dto.OrderDetailsDto;
import com.zhangzhao.common.dto.OrderSupplyDto;
import com.zhangzhao.common.entity.*;
import com.zhangzhao.common.util.NumberUtil;
import com.zhangzhao.common.util.UtilDate;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderSupplyServiceImpl extends BaseService implements OrderSupplyService {

    public static final int A = 30;
    public static final int B = 50;

    @Override
    public StatusOneVo<PlaceOrderVo> placeOrder(Long id, int type, String installationType, int amount, List<Long> ids) {
        PlaceOrderVo orderVo = new PlaceOrderVo();
        List<GoodsCommodityVo> list = new ArrayList<>();
        int acount = amount;
        List<MasterTime> all = masterTimeRepository.findAll();
        if (id != null) {
            Optional<GoodsCommodity> optional = goodsCommodityRepository.findById(id);
            if (optional.isPresent()) {
                GoodsCommodity goods = optional.get();
                if (goods.getInventory() >= amount) {
                    GoodsCommodityVo goodsCommodityVo = calculationPrice(goods, amount, orderVo,all.get(0));
//                    orderVo.setIntegral(String.valueOf(goods.getIntegral()));//积分
                    list.add(goodsCommodityVo);
                }
            }
        } else if (ids != null) {
            List<ShoppingCart> carts = shoppingCartRepository.findAllById(ids);
            List<GoodsCommodity> commodities = goodsCommodityRepository.findAllById(carts.parallelStream().map(o -> o.getGoodId()).collect(Collectors.toList()));
            for (GoodsCommodity o : commodities) {
                for (ShoppingCart c : carts) {
                    if (o.getId() == c.getGoodId().longValue() && o.getInventory() >= c.getAmount()) {
                        o.setInventory(c.getAmount());
                        o.setType(c.getType());
                        o.setInstallationType(c.getInstallationType());
                        acount += c.getAmount();
                    }
                }
            }
            List<GoodsCommodityVo> collect = commodities.parallelStream().map(o -> calculationPrice(o, o.getInventory(), orderVo,all.get(0))).collect(Collectors.toList());
            list.addAll(collect);
        }
        orderVo.setList(list);
        Optional<Address> address = getUser().getAddresses().parallelStream().filter(o -> o.getUsed() == 1).findFirst();
        orderVo.setAddress(address.isPresent() ? addressMapper.beanToVo(address.get()) : null);

        StatusOneVo<PlaceOrderVo> vo = new StatusOneVo<>();
        vo.success(orderVo);
        return vo;
    }

    @Override
    public StatusOneVo saveOrder(OrderSupplyDto orderSupplyDto, BindingResult bindingResult) {
        StatusOneVo vo = new StatusOneVo();
        if (bindingResult.hasErrors()) {
            vo.fail(ErrorCode.PARAMETER_ERROR, bindingResult.getFieldError().getDefaultMessage());
        } else {
            String orderNum = UtilDate.getOrderNum();
            OrderSupply orderSupply = orderSupplyMapper.dtoToBean(orderSupplyDto);
            orderSupply.setOrderNumber(orderNum);
            orderSupply.setPaymentPrcie(orderSupply.getOrderPrcie());
            orderSupply.setStatus(OrderSupply.Status.PENDING_PAYMENT.getStatus());
            orderSupply.setUser(getUser());
            Optional<Address> optional = addressRepository.findById(orderSupply.getAddressId());
            if(optional.isPresent()){
                BeanUtils.copyProperties(optional.get(),orderSupply,"id","createTime");
            }
            Optional<Store> storeOptional = storeRepository.findById(orderSupplyDto.getStoreId());
            if (storeOptional.isPresent()){
                orderSupply.setStore(storeOptional.get());
            }
            List<Long> ids = orderSupplyDto.getOrderDetails().parallelStream().map(o -> o.getId()).collect(Collectors.toList());
            List<GoodsCommodity> list = goodsCommodityRepository.findAllById(ids);
            List<OrderDetails> detailsList = new ArrayList<>();
            for (GoodsCommodity good : list) {
                OrderDetails orderDetails = orderDetailsMapper.goodToOrderDetails(good);
                for (OrderDetailsDto details : orderSupplyDto.getOrderDetails()) {
                    if (good.getId() == details.getId() && good.getInventory() >= details.getAmount()) {
                        orderDetails.setGoodsCommodityId(good.getId());
                        orderDetails.setOrderNumber(orderNum);
                        orderDetails.setPrice(details.getPrcie());
                        orderDetails.setAmount(details.getAmount());
                        orderDetails.setProperty(details.getProperty());
                        orderDetails.setCouponId(details.getCouponId());
                        orderDetails.setPaymentPrcie(details.getPrcie());
                        break;
                    }
                }
                detailsList.add(orderDetails);
            }
            double val=list.stream().mapToDouble(GoodsCommodity::getPrice).sum();
            orderSupply.setType(1);
            AtomicBoolean fag= new AtomicBoolean(false);
            if (val==orderSupply.getGoodPrcie()){
                orderSupplyDto.getOrderDetails().forEach(o->{
                    if (o.getCouponId()!=null){
                        fag.set(true);
                    }
                });
            }
            if (fag.get()){
                orderSupply.setType(2);
            }
            orderSupply.setOrderDetails(detailsList);
            List<String> types = orderSupplyDto.getOrderDetails().stream().filter(o -> "0".equals(o.getInstallationType()) || "2".equals(o.getInstallationType())).map(o -> o
                    .getInstallationType()).collect(Collectors.toList());
            if (types.size()>0){
                StringBuilder sb = new StringBuilder();
                List<Services> services = servicesRepository.findByTypeIn(types.stream().map(Integer::valueOf).collect(Collectors.toList()));
                for (String type: types){
                    for (Services services1 : services){
                        if (type.equals(String.valueOf(services1.getType()))){
                            orderNum = UtilDate.getOrderNum();
                            Reservation reservation = new Reservation();
                            reservation.setReservationNumber(orderNum);
                            reservation.setProvince(optional.get().getProvince());
                            reservation.setDistrict(optional.get().getDistrict());
                            reservation.setCity(optional.get().getCity());
                            reservation.setDetailedAddress(optional.get().getDetailed());
                            reservation.setPhoneNumber(optional.get().getPhone());
                            reservation.setUserId(getUser().getId());
                            reservation.setAppointmentTime(orderSupplyDto.getPreInstallationTime());
                            reservation.setOrderMoney(services1.getServicePrice());
                            reservation.setSubsidyPrice(services1.getSubsidyPrice());
                            if (type.equals("0")){
                                reservation.setYesnoShopnote(1);
                                reservation.setStore(storeOptional.get());
                                reservation.setAppointmentTime(orderSupplyDto.getStoreInstallationTime());
                            }
                            reservation.setStatus(Reservation.Status.Waiting_list.getStatus());
                            reservationRepository.saveAndFlush(reservation);
                            sb.append(orderNum+",");
                            break;
                        }
                    }
                }
                orderSupply.setReservations(sb.length()>0?sb.substring(0,sb.length()-1):"");
            }
            OrderSupply order = orderSupplyRepository.saveAndFlush(orderSupply);
            vo.success(order.getId());
        }
        return vo;
    }

    @Override
    public OrderSupply findById(Long id) {
        return orderSupplyRepository.findById(id).orElseThrow(()->new RuntimeException("id是"+id+"的订单为空"));
    }

    @Override
    public OrderSupply findOrderByNumber(String orderNumber) {
        return orderSupplyRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public StatusVoidVo updateStatus(Long id, int status) {
        StatusVoidVo vo=new StatusVoidVo();
        if (status==4){
            orderSupplyRepository.updateStatusAndCompleteTime(id,status);
        }else {
            orderSupplyRepository.updateStatus(id,status);
        }
        vo.success();
        return vo;
    }

    @Async
    public void upIS(List<OrderDetails> detailsList) {
        detailsList.forEach(o -> goodsCommodityRepository.upIS(o.getId(), o.getAmount(), o.getAmount()));
    }

    @Override
    public StatusVo findAll(Integer page, Integer pageSize, String... p) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.Direction.DESC,"createTime");
        Page<OrderSupply> all = orderSupplyRepository.findAll(new Specification<OrderSupply>() {
            @Override
            public Predicate toPredicate(Root<OrderSupply> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank(p[0])) {
                    list.add(criteriaBuilder.equal(root.get("status"), p[0]));
                }
                list.add(criteriaBuilder.equal(root.get("userId"), getUser().getId()));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        }, pageable);
        List<OrderSupplyVo> collect = all.getContent().parallelStream().map(orderSupplyMapper::beanToVo).collect(Collectors.toList());
        StatusVo<OrderSupplyVo> vo = new StatusVo<>();
        vo.success(collect);
        return vo;
    }

    @Override
    public StatusOneVo findOne(Long id) {
        StatusOneVo vo = new StatusOneVo();
        Optional<OrderSupply> optional = orderSupplyRepository.findById(id);
        if (optional.isPresent()){
            OrderSupplyDetailVo orderSupplyDetailVo = orderSupplyMapper.beanToOrderSupplyDetailVo(optional.get());
            if (optional.get().getStore()!=null){
               orderSupplyDetailVo.setStore(storeMapper.beanToVo(optional.get().getStore()));
            }
            if (optional.get().getInvoiceId()>0){
                Optional<Invoice> invoiceOptional = invoiceRepository.findById(optional.get().getInvoiceId());
                if (invoiceOptional.isPresent()){
                    orderSupplyDetailVo.setInvoice(invoiceMapper.beanToVo(invoiceOptional.get()));
                }
            }
            if (optional.get().getAddressId()>0){
                Optional<Address> addressOptional = addressRepository.findById(optional.get().getAddressId());
                if (addressOptional.isPresent()){
                    orderSupplyDetailVo.setAddress(addressMapper.beanToVo(addressOptional.get()));
                }
            }
            vo.success(orderSupplyDetailVo);
        }
        return vo;
    }

    public GoodsCommodityVo calculationPrice(GoodsCommodity goods, int amount, PlaceOrderVo orderVo, MasterTime masterTime) {
        if ((getUser().getMember() == 0 || getUser().getMember() == 1) && goods.getDiscount() == 0) {//会员
            double discount = 0;
            if ((amount <= A && getUser().getAmount() == 0) || getUser().getAmount() > 0) {
                if (goods.getPromotionPrice()>0){
                    discount=goods.getPromotionPrice();
                }else {
                    discount = goods.getPrice();
                }
            } else if (amount > A && amount <= B) {
                discount = goods.getMemberXprcie();
            } else if (amount > B) {
                discount = goods.getMemberYprcie();
            }
            goods.setPromotionPrice(NumberUtil.formatDouble(discount));
            if (getUser().getMember() == 0){
                orderVo.setIntegral(orderVo.getIntegral()+goods.getIntegral());
            }else {
                orderVo.setIntegral(orderVo.getIntegral()+(int) (goods.getIntegral()*amount*masterTime.getTimes()));
            }
            if (getUser().getMember()==1){
                goods.setIntegral((int)(goods.getIntegral()*amount*masterTime.getTimes()));
            }else {
                goods.setIntegral(goods.getIntegral()*amount);
            }
        } else if (getUser().getMember() == 2) {
            UserCooperate cooperate = getUser().getUserCooperate();
            if (cooperate.getComplete() == 1) {
                if (goods.getPromotionPrice()>0){
                    goods.setPromotionPrice(NumberUtil.formatDouble(goods.getPromotionPrice() - cooperate.getPreferentialPriceZ()));
                }else {
                    goods.setPromotionPrice(NumberUtil.formatDouble(goods.getPrice() - cooperate.getPreferentialPriceZ()));
                }
            } else {
                if (goods.getPromotionPrice()>0){
                    goods.setPromotionPrice(NumberUtil.formatDouble(goods.getPromotionPrice() + goods.getCooperatePrcie()));
                }else {
                    goods.setPromotionPrice(NumberUtil.formatDouble(goods.getPrice() + goods.getCooperatePrcie()));
                }
            }
            goods.setIntegral(goods.getIntegral()*amount);
            orderVo.setIntegral(orderVo.getIntegral()+goods.getIntegral());
        }
        GoodsCommodityVo goodsCommodityVo = goodsCommodityMapper.beanToVo(goods);
        goodsCommodityVo.setInventory(String.valueOf(amount));
        if (!goods.getInstallationType().equals("1")) {
            orderVo.setInstallationFee(NumberUtil.formatDouble(orderVo.getInstallationFee() + goods.getInstallationFee() * amount));
        }
        if (goods.getFreeShipping() > amount) {
            orderVo.setFreight(NumberUtil.formatDouble(orderVo.getFreight() + goods.getFreightPrcie() * amount));
        }
        return goodsCommodityVo;
    }
}
