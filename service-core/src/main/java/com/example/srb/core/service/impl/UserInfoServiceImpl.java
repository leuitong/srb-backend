package com.example.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.srb.base.utils.JwtUtils;
import com.example.srb.common.exception.Assert;
import com.example.srb.common.result.ResponseEnum;
import com.example.srb.common.utils.MD5;
import com.example.srb.core.mapper.UserAccountMapper;
import com.example.srb.core.mapper.UserInfoMapper;
import com.example.srb.core.mapper.UserLoginRecordMapper;
import com.example.srb.core.pojo.entity.UserAccount;
import com.example.srb.core.pojo.entity.UserInfo;
import com.example.srb.core.pojo.entity.UserLoginRecord;
import com.example.srb.core.pojo.query.UserInfoQuery;
import com.example.srb.core.pojo.vo.LoginVO;
import com.example.srb.core.pojo.vo.RegisterVO;
import com.example.srb.core.pojo.vo.UserInfoVO;
import com.example.srb.core.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        UserInfoVO userInfoVO = new UserInfoVO();
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();

        // 用户是否存在
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper
                .eq("mobile", mobile)
                .eq("user_type", userType);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        // 密码是否正确
        Assert.equals(userInfo.getPassword(), MD5.encrypt(password), ResponseEnum.LOGIN_PASSWORD_ERROR);

        // 用户是否被禁用
        Assert.equals(userInfo.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);

        // 记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        // 生成Token
        String token = JwtUtils.createToken(userInfo.getId(), mobile);

        // 组装VO
        userInfoVO.setToken(token);
        userInfoVO.setMobile(mobile);
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setUserType(userType);
        userInfoVO.setNickName(userInfo.getNickName());

        // 返回用户信息对象
        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {
        if (userInfoQuery == null) {
            return baseMapper.selectPage(pageParam, null);
        }
        String mobile = userInfoQuery.getMobile();
        Integer userType = userInfoQuery.getUserType();
        Integer status = userInfoQuery.getStatus();

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
//        if (StringUtils.isNotBlank(mobile)) {
//            userInfoQueryWrapper.eq("mobile", mobile);
//        }
//        if (userType != null) {
//            userInfoQueryWrapper.eq("user_type", userType);
//        }
//        if (status != null) {
//            userInfoQueryWrapper.eq("status", status);
//        }
        userInfoQueryWrapper
                .eq(StringUtils.isNotBlank(mobile), "mobile", mobile)
                .eq(userType != null, "user_type", userType)
                .eq(status != null, "status", status);
        return baseMapper.selectPage(pageParam, userInfoQueryWrapper);
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobile(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile", mobile);
        Integer integer = baseMapper.selectCount(userInfoQueryWrapper);
        return integer > 0;
    }
}
