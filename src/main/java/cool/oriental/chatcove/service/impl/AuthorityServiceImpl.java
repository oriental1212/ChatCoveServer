package cool.oriental.chatcove.service.impl;

/*
 * @Author: Oriental
 * @Date: 2023-07-04-15:10
 * @Description: 用户权限服务实现类
 */

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.configuration.mail.EnumMail;
import cool.oriental.chatcove.configuration.mail.MailConfiguration;
import cool.oriental.chatcove.entity.UserDetail;
import cool.oriental.chatcove.entity.UserInfo;
import cool.oriental.chatcove.mapper.UserDetailMapper;
import cool.oriental.chatcove.mapper.UserInfoMapper;
import cool.oriental.chatcove.service.AuthorityService;
import cool.oriental.chatcove.utils.CaptchaGenerator;
import cool.oriental.chatcove.utils.SnowflakeIdGenerator;
import cool.oriental.chatcove.vo.LoginByCaptchaInfo;
import cool.oriental.chatcove.vo.LoginInfo;
import cool.oriental.chatcove.vo.RegisterInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthorityServiceImpl implements AuthorityService {
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    UserDetailMapper userDetailMapper;
    @Resource
    MailConfiguration mailConfiguration;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<String> Register(RegisterInfo registerInfo) {
        long userId = new SnowflakeIdGenerator(1, 1).nextId();
        if(userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getEmail, registerInfo.getEmail())) != null){
            return Result.error("400","该邮箱已经注册用户，请不要重复注册");
        }
        LocalDateTime createTime = DateUtil.parse(DateUtil.now()).toLocalDateTime();
        UserInfo userInfo = new UserInfo()
                .setId(userId)
                .setUsername(registerInfo.getUsername())
                .setPassword(BCrypt.hashpw(registerInfo.getPassword(),BCrypt.gensalt()))
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
            StpUtil.login(userId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户注册信息插入失败");
            return Result.error("500","服务器异常，用户注册信息插入失败，请稍后重试");
        }
        return Result.success("用户注册成功");
    }

    @Override
    public Result<String> LoginDefault(LoginInfo loginInfo) {
        UserInfo userInfo = UserHasInfo(loginInfo.getAccount());
        try {
            if(userInfo == null){
                return Result.error("用户未注册");
            }
            if(BCrypt.checkpw(loginInfo.getPassword(),userInfo.getPassword())){
                return Result.success("用户登录成功");
            }else {
                return Result.error("用户密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("校验用户密码失败");
            return Result.error("服务器异常，用户登录失败，请稍后重试");
        }
    }


    @Override
    public Result<String> SendCaptcha(String account) {
        UserInfo userInfo = UserHasInfo(account);
        if(userInfo == null){
            return Result.error("用户不存在，请先注册");
        }
        if(Boolean.TRUE.equals(redisTemplate.hasKey(account))){
            return Result.error("已经发送了验证码注意查收你的邮箱");
        }
        String captcha = new CaptchaGenerator().CaptchaCreate();
        // 邮箱处理
        if(account.contains("@")){
            try {
                mailConfiguration.sendTemplateMail(account, captcha, EnumMail.EMAIL_CAPTCHA);
                redisTemplate.opsForValue().set(account,captcha,5, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("邮箱验证码发送失败");
                if(Boolean.TRUE.equals(redisTemplate.hasKey(account))){
                    redisTemplate.delete(account);
                }
                return Result.error("服务器异常，获取邮箱验证码失败，请稍后重试");
            }
        }
        // 手机处理
        else{
            return Result.success("发送手机验证码");
        }
        return Result.success("验证码发送成功，注意查收");
    }

    @Override
    public Result<String> LoginByCaptcha(LoginByCaptchaInfo loginByCaptchaInfo) {
        return null;
    }

    private UserInfo UserHasInfo(String account){
        LambdaQueryWrapper<UserInfo> userInfoWrapper = new LambdaQueryWrapper<>();
        UserInfo userInfo;
        if(account.contains("@")){
            userInfoWrapper.eq(UserInfo::getEmail,account);
        }else{
            userInfoWrapper.eq(UserInfo::getUsername,account);
        }
        try {
            userInfo = userInfoMapper.selectOne(userInfoWrapper);
            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询用户是否存在数据库失败，请稍后重试");
        }
        return null;
    }
}
