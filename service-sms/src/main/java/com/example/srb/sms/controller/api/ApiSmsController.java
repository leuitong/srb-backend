package com.example.srb.sms.controller.api;

import com.example.srb.common.exception.Assert;
import com.example.srb.common.result.R;
import com.example.srb.common.result.ResponseEnum;
import com.example.srb.common.utils.RandomUtils;
import com.example.srb.common.utils.RegexValidateUtils;
import com.example.srb.sms.client.CoreUserInfoClient;
import com.example.srb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {
    @Resource
    private SmsService smsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CoreUserInfoClient coreUserInfoClient;

    @ApiOperation("短信发送")
    @GetMapping("/send/{mobile}")
    public R send(
            @ApiParam(value = "手机号", required = true)
            @PathVariable String mobile){
        //校验手机号吗不能为空
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //是否是合法的手机号码
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        //判断手机号是否已经注册
        boolean result = coreUserInfoClient.checkMobile(mobile);
        log.info("result = " + result);
        Assert.isTrue(!result, ResponseEnum.MOBILE_EXIST_ERROR);

        String code = RandomUtils.getFourBitRandom();
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        //smsService.send(mobile, SmsProperties.TEMPLATE_CODE, param);

        //将验证码存入redis
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, code, 5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }

}
