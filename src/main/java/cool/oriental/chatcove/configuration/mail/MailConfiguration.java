package cool.oriental.chatcove.configuration.mail;

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

import javax.annotation.Resource;
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

    public void sendTemplateMail(String receiverEmail, EnumMail enumMail){
        switch (enumMail){
            case CAPTCHA -> {
                CaptchaTemplate(receiverEmail);
            }
            case NORMAL -> {
                normalTemplate();
            }
        }
    }
    /*
     * @Description: 验证码邮箱发送
     * @Param: captcha
     * @return: null
     * @Author: Oriental
     * @Date: 2023/7/2
     */
    public void CaptchaTemplate(String receiverEmail){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(sender);  //发送人邮箱
            messageHelper.setTo(receiverEmail); //接收人邮箱
            messageHelper.setSubject("找回密码");   //邮件标题
            // 使用Thymeleaf模板
            Context context = new Context();
            HashMap<String, Object> map = new HashMap<>();
            /*
            * 放入一些参数
            * map.put("xxx",xxx);
            * */
            context.setVariables(map);
            String emailContent = templateEngine.process("findPassword", context);

            messageHelper.setText(emailContent);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送邮件失败：{}",e.getMessage());
            e.printStackTrace();
        }
        return;
    }

    /*
    * @Description: 普通邮箱发送
    * @Param: receiverEmail
    * @return: null
    * @Author: Oriental
    * @Date: 2023/7/2
    */
    public void normalTemplate(){
        return;
    }
}
