package com.example.srb.core.controller.admin;

import com.example.srb.common.exception.Assert;
import com.example.srb.common.result.R;
import com.example.srb.common.result.ResponseEnum;
import com.example.srb.core.pojo.entity.IntegralGrade;
import com.example.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "积分等级管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/core/integralGrade")
@Slf4j
public class AdminIntegralGradeController {
    @Resource
    private IntegralGradeService integralGradeService;

    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public R listAll() {
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("查询成功");  // 技巧，串联语法
    }

    @ApiOperation(value = "根据id删除记录", notes = "逻辑删除")
    @DeleteMapping("/remove/{id}")
    public R removeById(
            @ApiParam(value = "数据id", example = "100", required = true)
            @PathVariable Long id) {
        boolean b = integralGradeService.removeById(id);
        if (b) {
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    @ApiOperation(value = "新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象")
            @RequestBody  IntegralGrade integralGrade) {
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        boolean save = integralGradeService.save(integralGrade);
        if (save) {
            return R.ok().message("保存成功");
        }else {
            return R.error().message("保存失败");
        }
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "根据id查询记录")
    public R getById(
            @ApiParam(value = "数据id")
            @PathVariable Integer id){
        IntegralGrade byId = integralGradeService.getById(id);
        if (byId != null) {
            return R.ok().message("查询成功").data("record",byId);
        }else {
            return R.error().message("不存在该记录");
        }
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新积分等级")
    public R updateById(
            @ApiParam(value = "积分对象")
            @RequestBody  IntegralGrade integralGrade){
        boolean b = integralGradeService.updateById(integralGrade);
        if (b) {
            return R.ok().message("更新成功");
        }else {
            return R.error().message("更新失败");
        }
    }

}
