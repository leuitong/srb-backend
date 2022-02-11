package com.example.srb.core.service;

import com.example.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.srb.core.pojo.vo.RegisterVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Tong
 * @since 2022-01-24
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);
}
