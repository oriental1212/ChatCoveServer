package cool.oriental.chatcove.utils;

import java.util.Random;

/**
 * @Author: Oriental
 * @Date: 2023-07-05-15:15
 * @Description: 验证码生成器
 */
public class CaptchaGenerator {
    public String CaptchaCreate(){
        return Integer.toHexString(new Random().nextInt()).substring(0, 6);
    }
}
