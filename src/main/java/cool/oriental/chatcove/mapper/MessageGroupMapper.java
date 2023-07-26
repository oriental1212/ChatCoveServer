package cool.oriental.chatcove.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.oriental.chatcove.dto.ChannelMessage;
import cool.oriental.chatcove.entity.MessageGroup;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author oriental
 * @since 2023-06-25 01:23:32
 */
public interface MessageGroupMapper extends BaseMapper<MessageGroup> {
    List<ChannelMessage> GetChannelMessage(Integer channelId, Integer channelChildrenId);
    ChannelMessage GetChannelTextMessageOne(Long sendId, Integer messageGroupId);
}
