package com.example.srb.core.controller.admin;


import com.example.srb.common.result.R;
import com.example.srb.core.pojo.entity.UserLoginRecord;
import com.example.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author Tong
 * @since 2022-01-24
 */
@RestController
@RequestMapping("/admin/core/userLoginRecord")
@Slf4j
@CrossOrigin
@Api(tags = "会员登录日志")
public class AdminUserLoginRecordController {
    @Resource
    private UserLoginRecordService userLoginRecordService;

    @GetMapping("/listTop50/{userId}")
    @ApiOperation("查询登录日志")
    public R listTop50(
            @ApiParam(value = "用户id", required = true)
            @PathVariable Long userId) {
        List<UserLoginRecord> list = userLoginRecordService.listTop50(userId);
        return R.ok().data("list", list);
    }
}

