package cool.oriental.chatcove.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.entity.UserDetail;
import cool.oriental.chatcove.entity.UserInfo;
import cool.oriental.chatcove.mapper.UserDetailMapper;
import cool.oriental.chatcove.mapper.UserInfoMapper;
import cool.oriental.chatcove.service.SettingService;
import cool.oriental.chatcove.utils.SendCaptchaTools;
import cool.oriental.chatcove.vo.ChangeUserSetting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: Oriental
 * @Date: 2023-07-10-17:00
 * @Description: 用户设置服务实现类
 */

@Service
@Slf4j
public class SettingServiceImpl implements SettingService {
    @Resource
    UserDetailMapper userDetailMapper;
    @Resource
    UserInfoMapper userInfoMapper;
    @Override
    public Result<String> ChangeUserSetting(ChangeUserSetting changeUserSetting) {
        try {
            LambdaUpdateWrapper<UserDetail> userDetailWrapper = new LambdaUpdateWrapper<>();
            userDetailWrapper
                    .eq(UserDetail::getUserId, StpUtil.getLoginIdAsLong())
                    .set(UserDetail::getNickName, changeUserSetting.getNickName())
                    .set(UserDetail::getAvatar, changeUserSetting.getAvatar())
                    .set(UserDetail::getSummary, changeUserSetting.getSummery())
                    .set(UserDetail::getGender, changeUserSetting.getGender())
                    .set(UserDetail::getBirthday, changeUserSetting.getBirthday())
                    .set(UserDetail::getLocation, changeUserSetting.getLocation());
            userDetailMapper.update(null, userDetailWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户修改个人设置业务出错");
            return Result.error("服务器异常，用户修改个人设置失败，请稍后重试");
        }
        return Result.success("修改个人设置成功");
    }

    @Override
    public Result<String> SendCaptchaToChangPassword() {
        try {
            UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getId, StpUtil.getLoginIdAsLong()));
            SendCaptchaTools sendCaptchaTools = new SendCaptchaTools();
            Boolean changePassword = sendCaptchaTools.SendCaptcha(userInfo.getEmail(), "changePassword", 2);
            if (changePassword){
                return Result.success("发送验证码成功");
            }else{
                return Result.error("验证码已存在，请查收邮箱");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户修改个人密码发送验证码业务出错");
            return Result.error("服务器异常，验证码发送，请稍后重试");
        }
    }

    @Override
    public Result<String> ChangePassword(String password, String captcha) {
        if(captcha == null || password == null){
            return Result.error("参数错误");
        }
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getId, StpUtil.getLoginIdAsLong()));
        if(new SendCaptchaTools().CheckCaptcha(userInfo.getEmail(), "changePassword", captcha)){
            try {
                userInfoMapper.update(
                        null,
                        new LambdaUpdateWrapper<UserInfo>()
                                .eq(UserInfo::getId, StpUtil.getLoginIdAsLong())
                                .set(UserInfo::getPassword, password)
                );
            } catch (Exception e) {
                e.printStackTrace();
                log.error("修改个人密码业务出错");
                return Result.error("服务器异常，修改密码失败，请稍后重试");
            }
            return Result.success("修改成功");
        }else{
            return Result.error("验证码校验出错或者不存在");
        }
    }
}
