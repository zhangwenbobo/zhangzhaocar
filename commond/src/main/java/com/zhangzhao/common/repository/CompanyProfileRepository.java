package com.zhangzhao.common.repository;

import com.zhangzhao.common.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 公司简介
 */
@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long>, JpaSpecificationExecutor<CompanyProfile> {

}
