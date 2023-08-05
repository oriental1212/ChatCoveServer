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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import cool.oriental.chatcove.vo.ChangePasswordInfo;
import cool.oriental.chatcove.vo.CheckByCaptchaInfo;
import cool.oriental.chatcove.vo.RegisterInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
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
    RedisTemplate<String, Object> RedisTemplate;

    @Override
    public Result<String> Register(RegisterInfo registerInfo) {
        long userId = new SnowflakeIdGenerator(1, 1).nextId();
        if(userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getEmail, registerInfo.getEmail())
                .or()
                .eq(UserInfo::getUsername, registerInfo.getUsername())
            ) != null){
            return Result.error("该邮箱已经注册用户，请不要重复注册");
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
                .setAvatar("default");
        try {
            userInfoMapper.insert(userInfo);
            userDetailMapper.insert(userDetail);
            StpUtil.login(userId);
            return Result.success("用户注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户注册业务异常");
            return Result.error("服务器异常，用户注册信息插入失败，请稍后重试");
        }
    }

    @Override
    public Result<String> LoginByAccount(String account, String password) {
        if(StpUtil.getLoginIdDefaultNull() != null){
            return Result.error("该用户已登录，禁止重复登录");
        }
        UserInfo userInfo = UserHasInfo(account);
        try {
            if(userInfo == null){
                return Result.error("用户未注册");
            }else{
                if(BCrypt.checkpw(password,userInfo.getPassword())){
                    StpUtil.login(userInfo.getId());
                    return Result.success(StpUtil.getTokenInfo().tokenValue);
                }else {
                    return Result.error("用户密码错误,请稍后重试");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("账号密码登录业务异常");
            return Result.error("服务器异常，用户登录失败，请稍后重试");
        }
    }

    @Override
    public Result<String> LoginByCaptcha(CheckByCaptchaInfo checkByCaptchaInfo) {
        if(StpUtil.getLoginIdDefaultNull() != null){
            return Result.error("该用户已登录，禁止重复登录");
        }
        UserInfo userInfo = UserHasInfo(checkByCaptchaInfo.getAccount());
        if(userInfo == null){
            return Result.error("不存在该用户");
        }
        try {
            Boolean flag = CheckCaptcha((checkByCaptchaInfo.getAccount() + checkByCaptchaInfo.getSendFlag()), checkByCaptchaInfo.getCaptcha());
            if(flag){
                StpUtil.login(userInfo.getId());
                return Result.success(StpUtil.getTokenInfo().tokenValue);
            }else {
                return Result.error("验证码错误，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("验证码登录业务异常");
            return Result.error("服务器异常，验证码登录失败，请稍后重试");
        }
    }

    @Override
    public Result<String> SendCaptcha(String account, String sendFlag) {
        UserInfo userInfo = UserHasInfo(account);
        if(userInfo == null){
            return Result.error("用户不存在，请先注册");
        }
        if(RedisTemplate.hasKey(account) == Boolean.TRUE){
            return Result.error("已经发送了验证码注意查收你的邮箱");
        }
        String captcha = new CaptchaGenerator().CaptchaCreate();
        // 发送验证码
        if(account.contains("@")){
            try {
                mailConfiguration.sendTemplateMail(account, captcha, EnumMail.EMAIL_CAPTCHA);
                RedisTemplate.opsForValue().set(account+sendFlag, captcha,5, TimeUnit.MINUTES);
                return Result.success("验证码发送成功，注意查收");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("邮箱验证码发送失败");
                if(Boolean.TRUE.equals(RedisTemplate.hasKey(account))){
                    RedisTemplate.delete(account+sendFlag);
                }
                return Result.error("服务器异常，发送邮箱验证码失败，请稍后重试");
            }
        }else {
            return Result.error("邮箱格式错误，请稍后再试");
        }
    }

    @Override
    public Result<String> FindPassword(CheckByCaptchaInfo checkByCaptchaInfo) {
        UserInfo userInfo = UserHasInfo(checkByCaptchaInfo.getAccount());
        if(userInfo == null){
            return Result.error("不存在该用户");
        }
        try {
            boolean flag = CheckCaptcha(checkByCaptchaInfo.getAccount() + checkByCaptchaInfo.getSendFlag(), checkByCaptchaInfo.getCaptcha());
            if(flag){
                RedisTemplate.opsForValue().set((checkByCaptchaInfo.getAccount()+"ChangePassword"), "1", 5, TimeUnit.MINUTES);
                return Result.success("用户验证码校验成功");
            }else {
                return Result.error("验证码错误，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("找回密码校验验证码业务异常");
            if(Boolean.TRUE.equals(RedisTemplate.hasKey(checkByCaptchaInfo.getAccount()+"ChangePassword"))){
                RedisTemplate.delete(checkByCaptchaInfo.getAccount()+"ChangePassword");
            }
        return Result.error("服务器异常，验证码校验失败，请稍后重试");
        }
    }

    @Override
    public Result<String> ChangePassword(ChangePasswordInfo changePasswordInfo) {
        UserInfo userInfo = UserHasInfo(changePasswordInfo.getAccount());
        if(userInfo == null){
            return Result.error("不存在该用户");
        }
        if(RedisTemplate.hasKey(changePasswordInfo.getAccount()+"ChangePassword") != Boolean.TRUE){
            return Result.error("修改密码前请先完成邮箱校验");
        }
        if(!changePasswordInfo.getPassword().equals(changePasswordInfo.getRepeatPassword())){
            return Result.error("重复密码不一致，请重新输入");
        }
        try {
            LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper
                    .eq(UserInfo::getEmail, changePasswordInfo.getAccount())
                    .set(UserInfo::getPassword, BCrypt.hashpw(changePasswordInfo.getPassword(),BCrypt.gensalt()));
            int updateFlag = userInfoMapper.update(null, wrapper);
            if(updateFlag>0){
                if(RedisTemplate.hasKey(changePasswordInfo.getAccount()+"ChangePassword") != Boolean.TRUE){
                    RedisTemplate.delete(changePasswordInfo.getAccount()+"ChangePassword");
                }
                return Result.success("修改密码成功");
            }else{
                return Result.error("修改密码失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("修改密码业务异常");
            return Result.error("服务器异常，修改密码失败，请稍后重试");
        }
    }

    @Override
    public Result<String> Logout() {
        long userId = StpUtil.getLoginIdAsLong();
        try {
            StpUtil.logout(userId);
            return Result.success("用户登出成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户登出业务异常");
            return Result.error("服务器异常，用户登出失败，请稍后重试");
        }
    }

    // 查看验证码是否存在和正确
    private Boolean CheckCaptcha(String key, String captcha){
        if(RedisTemplate.hasKey(key) != Boolean.TRUE){
            // 验证码不存在
            return Boolean.FALSE;
        }
        if(Objects.equals(RedisTemplate.opsForValue().get(key), captcha)){
            RedisTemplate.delete(key);
            return Boolean.TRUE;
        }else{
            // 验证码错误
            return Boolean.FALSE;
        }
    }

    // 根据用户账户判断用户是否存在
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
            log.error("查询用户异常，请稍后重试");
            return null;
        }
    }
}
