package com.example.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.srb.core.pojo.entity.UserLoginRecord;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author Tong
 * @since 2022-01-24
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    List<UserLoginRecord> listTop50(Long userId);
}
