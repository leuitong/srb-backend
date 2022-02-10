package com.example.srb.sms.service.Impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.example.srb.common.exception.Assert;
import com.example.srb.common.exception.BusinessException;
import com.example.srb.common.result.ResponseEnum;
import com.example.srb.sms.service.SmsService;
import com.example.srb.sms.utils.SmsProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Override
    public void send(String mobile, String templateCode, Map<String, Object> param) {
        //创建远程连接客户端对象
        DefaultProfile profile = DefaultProfile.getProfile(
                SmsProperties.REGION_Id,
                SmsProperties.KEY_ID,
                SmsProperties.KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        //创建远程连接的请求参数
//        CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("dysmsapi.aliyuncs.com");
//        request.setSysVersion("2017-05-25");
//        request.setSysAction("SendSms");
//        request.putQueryParameter("RegionId", SmsProperties.REGION_Id);
//        request.putQueryParameter("PhoneNumbers", mobile);
//        request.putQueryParameter("SignName", SmsProperties.SIGN_NAME);
//        request.putQueryParameter("TemplateCode", templateCode);
//
//        Gson gson = new Gson();
//        String jsonParam = gson.toJson(param);
//        request.putQueryParameter("TemplateParam", jsonParam);
//        try {
//            //使用客户端对象携带请求参数向远程阿里云服务器发起远程调用，并得到响应结果
//            CommonResponse response = client.getCommonResponse(request);
//            System.out.println("response.getData()：" + response.getData());
//        } catch (ServerException e) {
//            log.error("阿里云短信发送sdk调用失败:" + e.getErrCode() + ", " + e.getErrMsg());
//            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
////            e.printStackTrace();
//        } catch (ClientException e) {
//            log.error("阿里云短信发送sdk调用失败:" + e.getErrCode() + ", " + e.getErrMsg());
//            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
////            e.printStackTrace();
//        }
        SendSmsRequest request = new SendSmsRequest();
        request.setSignName(SmsProperties.SIGN_NAME);
        request.setTemplateCode(SmsProperties.TEMPLATE_CODE);
        request.setPhoneNumbers(mobile);
        Gson gson = new Gson();
        String jsonParam = gson.toJson(param);
        request.setTemplateParam(jsonParam);

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));

            //获取响应结果
            String code = response.getCode();
            String message = response.getMessage();
            log.info("code：" + code + "，message：" + message);

            //业务失败的处理
            Assert.notEquals("isv.BUSINESS_LIMIT_CONTROL", code, ResponseEnum.ALIYUN_SMS_LIMIT_CONTROL_ERROR);
            Assert.equals("OK", code, ResponseEnum.ALIYUN_SMS_ERROR);
        } catch (ServerException e) {
            log.error("阿里云短信发送sdk调用失败:" + e.getErrCode() + ", " + e.getErrMsg());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
        } catch (ClientException e) {
            log.error("阿里云短信发送sdk调用失败:" + e.getErrCode() + ", " + e.getErrMsg());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
        }
    }
}
