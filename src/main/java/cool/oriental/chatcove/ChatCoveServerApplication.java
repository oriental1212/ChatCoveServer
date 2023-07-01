package cool.oriental.chatcove;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cool.oriental.chatcove.mapper")
public class ChatCoveServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatCoveServerApplication.class, args);
    }

}
