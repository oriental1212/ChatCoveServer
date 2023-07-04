package cool.oriental.chatcove.configuration.authority;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-16:17
 * @Description: SaToken的配置文件
 */

@Configuration
public class SaTokenConfiguration {
    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
        SaTokenConfig config = new SaTokenConfig();
        config
                .setTokenName("ChatCoveToken")
                .setTimeout(24 * 60 * 60)
                .setActivityTimeout(-1)
                .setIsConcurrent(false)
                .setIsShare(false)
                .setIsLog(false)
                .setIsReadCookie(false)
                .setIsLog(true)
                .setLogLevel("error")
                .setJwtSecretKey("chatcoveismyfavoritewebsite")
                .setTokenStyle("simple-uuid");
        return config;
    }
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
