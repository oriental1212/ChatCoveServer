package cool.oriental.chatcove.service.impl;

/*
 * @Author: Oriental
 * @Date: 2023-07-04-15:10
 * @Description: 用户权限服务实现类
 */

import cn.hutool.core.date.DateUtil;
import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.entity.UserDetail;
import cool.oriental.chatcove.entity.UserInfo;
import cool.oriental.chatcove.mapper.UserDetailMapper;
import cool.oriental.chatcove.mapper.UserInfoMapper;
import cool.oriental.chatcove.service.AuthorityService;
import cool.oriental.chatcove.utils.SnowflakeIdGenerator;
import cool.oriental.chatcove.vo.RegisterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthorityServiceImpl implements AuthorityService {
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    UserDetailMapper userDetailMapper;

    @Override
    public Result<String> Register(RegisterInfo registerInfo) {
        long userId = new SnowflakeIdGenerator(1, 1).nextId();
        LocalDateTime createTime = DateUtil.parse(DateUtil.now()).toLocalDateTime();
        UserInfo userInfo = new UserInfo()
                .setId(userId)
                .setUsername(registerInfo.getUsername())
                .setPassword(registerInfo.getPassword())
                .setEmail(registerInfo.getEmail())
                .setRole("Default")
                .setStatus(true)
                .setCreateTime(createTime);
        UserDetail userDetail = new UserDetail()
                .setUserId(userId)
                .setNickName(registerInfo.getUsername())
                .setAvatar("null");
        try {
            userInfoMapper.insert(userInfo);
            userDetailMapper.insert(userDetail);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户注册信息插入失败");
            return Result.error("500","用户注册信息插入失败，请稍后重试");
        }
        return Result.success("用户注册成功");
    }
}
