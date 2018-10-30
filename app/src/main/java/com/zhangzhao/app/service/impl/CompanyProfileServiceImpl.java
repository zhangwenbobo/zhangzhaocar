package com.zhangzhao.app.service.impl;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.service.ActivityService;
import com.zhangzhao.app.service.CompanyProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司简介
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyProfileServiceImpl extends BaseService implements CompanyProfileService {

}
