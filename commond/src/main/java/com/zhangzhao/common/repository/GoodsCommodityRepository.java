package com.zhangzhao.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zhangzhao.common.entity.GoodsCommodity;

import java.util.List;

/**
 * 商品信息管理
 *
 * @author Administrator
 */
@Repository
public interface GoodsCommodityRepository
        extends JpaRepository<GoodsCommodity, Long>, JpaSpecificationExecutor<GoodsCommodity> {

    Page<GoodsCommodity> findByNoyesHotsell(String noyesHotsell, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query(value = "update goods_commodity set inventory = inventory-:inventory,sales = sales+:sales where id=:id", nativeQuery = true)
    void upIS(@Param("id") Long id, @Param("inventory") int inventory, @Param("sales") int sales);

    List<GoodsCommodity> findBySeries(String series);

    List<GoodsCommodity> findByIdIn(Long ids);
}
