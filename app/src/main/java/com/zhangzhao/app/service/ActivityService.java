package com.zhangzhao.app.service;

import com.zhangzhao.app.vo.ActivityVo;
import com.zhangzhao.common.commonservice.CommonService;
import com.zhangzhao.common.vo.StatusOneVo;

public interface ActivityService extends CommonService {

    StatusOneVo<ActivityVo> details(Long id);
}
