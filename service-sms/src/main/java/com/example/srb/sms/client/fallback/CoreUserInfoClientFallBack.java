package com.example.srb.sms.client.fallback;

import com.example.srb.sms.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoreUserInfoClientFallBack implements CoreUserInfoClient {
    @Override
    public boolean checkMobile(String mobile) {
        log.error("远程服务调用失败，服务熔断");
        return false;
    }
}
