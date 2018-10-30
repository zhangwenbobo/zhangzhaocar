package com.zhangzhao.common.repository;

import com.zhangzhao.common.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 收货地址
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {

    Optional<Address> findByUsed(Long aLong);

    Page<Address> findByUserId(Long id,Pageable pageable);
}
