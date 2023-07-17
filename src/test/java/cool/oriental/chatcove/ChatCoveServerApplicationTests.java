package cool.oriental.chatcove;

import cn.hutool.core.date.DateUtil;
import cool.oriental.chatcove.utils.ChannelIdGenerator;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class ChatCoveServerApplicationTests {
    @Resource
    private RedisTemplate<String, Object> RedisTemplate;
    @Test
    void contextLoads(){
        // SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
        // for (int i = 0; i < 20; i++) {
        //     long id = idGenerator.nextId();
        //     System.out.println("生成的ID: " + id);
        System.out.println(DateUtil.parse(DateUtil.now()).toLocalDateTime());
        for (int i = 0; i < 10; i++) {
            String s = ChannelIdGenerator.generateUniqueId();
            System.out.println(s);
        }

        System.out.println("=====================");
        System.out.println(DateUtil.thisDayOfMonth());
    }

}
