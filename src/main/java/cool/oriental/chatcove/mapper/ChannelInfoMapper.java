package cool.oriental.chatcove.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.oriental.chatcove.dto.ChannelByUserList;
import cool.oriental.chatcove.entity.ChannelInfo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author oriental
 * @since 2023-06-25 01:23:32
 */
public interface ChannelInfoMapper extends BaseMapper<ChannelInfo> {
    List<ChannelByUserList> GetChannelList(Long userId);
}
