package cool.oriental.chatcove.utils;

import cool.oriental.chatcove.configuration.mail.EnumMail;
import cool.oriental.chatcove.configuration.mail.MailConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Oriental
 * @Date: 2023-07-11-9:44
 * @Description: 发送验证码工具
 */

@Slf4j
public class SendCaptchaTools {
    @Resource
    private RedisTemplate<String, Object> RedisTemplate;
    @Resource
    private MailConfiguration mailConfiguration;
    public Boolean SendCaptcha(String userEmail, String sendFlag, int time){
        if(RedisTemplate.hasKey(userEmail+sendFlag) == Boolean.TRUE){
            return Boolean.FALSE;
        }
        String captcha = new CaptchaGenerator().CaptchaCreate();
        try {
            mailConfiguration.sendTemplateMail(userEmail, captcha, EnumMail.EMAIL_CAPTCHA);
            RedisTemplate.opsForValue().set(userEmail+sendFlag, captcha, time, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送邮箱验证码业务异常");
            RedisTemplate.delete(userEmail+sendFlag);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public Boolean CheckCaptcha(String userEmail, String sendFlag, String captcha){
        try {
            if(RedisTemplate.hasKey(userEmail+sendFlag) != Boolean.TRUE){
                // 验证码不存在
                return Boolean.FALSE;
            }
            if(Objects.equals(RedisTemplate.opsForValue().get(userEmail + sendFlag), captcha)){
                RedisTemplate.delete(userEmail+sendFlag);
                return Boolean.TRUE;
            }else {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("校验邮箱验证码业务异常");
            return Boolean.FALSE;
        }
    }
}
