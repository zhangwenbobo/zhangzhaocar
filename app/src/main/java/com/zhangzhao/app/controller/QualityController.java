package com.zhangzhao.app.controller;

import com.zhangzhao.app.base.BaseService;
import com.zhangzhao.app.vo.QualityVo;
import com.zhangzhao.common.dto.QualityDto;
import com.zhangzhao.common.vo.StatusOneVo;
import com.zhangzhao.common.vo.StatusVo;
import com.zhangzhao.common.vo.StatusVoidVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Quality", description = "质量处理")
@RestController
@RequestMapping("/app/quality")
public class QualityController extends BaseService {

    @PostMapping("/q/save/quality/processing")
    @ApiOperation(value = "1 质量处理提交", notes = "质量处理提交", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVoidVo saveProcessing(@ApiParam(value = "请求参数封装") @RequestBody @Valid QualityDto qualityDto, BindingResult bindingResult) {
        return qualityService.saveProcessing(qualityDto, bindingResult);

    }

    @GetMapping("/q/select/quality/processing")
    @ApiOperation(value = "2 问题记录查询", notes = "质量处理查询", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusVo<QualityVo> slideshowImgList(
            @ApiParam("当前页") @RequestParam(required = false, defaultValue = "1") int page,
            @ApiParam("每页总数") @RequestParam(required = false, defaultValue = "10") int pageSize) {

        return qualityService.findAll(page, pageSize, getUser().getId());
    }

    @GetMapping(value = "/q/quality/processing/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "3 问题处理详情", notes = "问题处理详情", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusOneVo<QualityVo> qualityDetails(@ApiParam(value = "问题处理详情id", required = true) @RequestParam Long id) {
        return qualityService.findById(id);
    }
}
