package cool.oriental.chatcove.configuration.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-11:20
 * @Description: Swagger的配置文件
 */

@Configuration
@OpenAPIDefinition
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI().info(new Info()
                .title("ChatCove")
                .description("ChatCove的接口文档")
                .version("v1.0.o")
        );
    }
}
