package com.example.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.srb.common.exception.Assert;
import com.example.srb.common.result.ResponseEnum;
import com.example.srb.common.utils.MD5;
import com.example.srb.core.mapper.UserAccountMapper;
import com.example.srb.core.mapper.UserInfoMapper;
import com.example.srb.core.pojo.entity.UserAccount;
import com.example.srb.core.pojo.entity.UserInfo;
import com.example.srb.core.pojo.vo.RegisterVO;
import com.example.srb.core.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Tong
 * @since 2022-01-24
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountMapper userAccountMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {
        // 判断用户是否已经注册
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile", registerVO.getMobile());
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        //userInfo.setHeadImg("");
        // 脱敏
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        // 插入用户信息记录 user_info
        baseMapper.insert(userInfo);

        // 插入用户账户记录 user_account
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }
}
