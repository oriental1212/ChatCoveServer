package cool.oriental.chatcove.configuration.mail;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * @Author: Oriental
 * @Date: 2023-07-02-11:21
 * @Description: Mail的配置类
 * @Password: JVOEXUUTLQQPUASV
 */

@Data
@Slf4j
@Component
public class MailConfiguration {
    @Resource
    private JavaMailSenderImpl javaMailSender;
    @Resource
    private TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String sender;

    public void sendTemplateMail(String receiverEmail, Object message,EnumMail enumMail){
        switch (enumMail){
            case EMAIL_CAPTCHA -> CaptchaTemplate(receiverEmail,message);
            case NORMAL -> normalTemplate();
        }
    }
    /*
     * @Description: 验证码邮箱发送
     * @Param: captcha
     * @return: null
     * @Author: Oriental
     * @Date: 2023/7/2
     */
    public void CaptchaTemplate(String receiverEmail, Object message){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(sender,"ChatCove");  //发送人邮箱
            messageHelper.setTo(receiverEmail); //接收人邮箱
            messageHelper.setSubject("找回密码");   //邮件标题
            // 使用Thymeleaf模板
            Context context = new Context();
            HashMap<String, Object> map = new HashMap<>();
            map.put("captcha",message);
            context.setVariables(map);
            String emailContent = templateEngine.process("SendCaptcha", context);
            messageHelper.setText(emailContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送邮件失败：{}",e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            log.error("设置发件人失败");
            e.printStackTrace();
        }
    }

    /*
    * @Description: 普通邮箱发送
    * @Param: receiverEmail
    * @return: null
    * @Author: Oriental
    * @Date: 2023/7/2
    */
    public void normalTemplate(){
    }
}
