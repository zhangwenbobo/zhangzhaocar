package com.zhangzhao.app.controller;

import com.alibaba.fastjson.JSONArray;
import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.common.entity.PrecontractEvaluate;
import com.zhangzhao.common.vo.StatusVoidVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "PrecontractEvaluate", description = "预约评价")
@RestController
@RequestMapping("/app/precontractEvaluate")
public class PrecontractEvaluateController extends BaseService {

    @ApiOperation(value = "1 预约评论", notes = "预约评论", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/q/precontract/evaluate", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVoidVo saveBean(@ApiParam(value = "评论jsonarray", required = true) @RequestParam String context) {
        StatusVoidVo vo = new StatusVoidVo();
        try {
            List<PrecontractEvaluate> precontractEvaluates = JSONArray.parseArray(context, PrecontractEvaluate.class);
            precontractEvaluates = precontractEvaluates.parallelStream().map(o -> o.setUser(getUser())).collect(Collectors.toList());
            return precontractEvaluateService.saveList(precontractEvaluates);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

}
