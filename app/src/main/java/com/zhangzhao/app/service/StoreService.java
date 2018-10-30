package com.zhangzhao.app.service;

import com.zhangzhao.app.vo.StoreVo;
import com.zhangzhao.common.commonservice.CommonService;
import com.zhangzhao.common.entity.Store;
import com.zhangzhao.common.vo.StatusVo;

public interface StoreService extends CommonService {

    /**
     * 门店列表
     *
     * @return
     */
    StatusVo<StoreVo> findAll(Store store);
}
