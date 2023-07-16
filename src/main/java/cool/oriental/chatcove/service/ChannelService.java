package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.vo.channel.ChannelChildrenInfo;
import cool.oriental.chatcove.vo.channel.ChannelFontInfo;

/**
 * @Author: Oriental
 * @Date: 2023-07-11-10:52
 * @Description: 频道服务接口
 */
public interface ChannelService {
    public Result<String> CreateMasterChannel(ChannelFontInfo channelFontInfo);
    Result<String> UpdateMasterChannel(ChannelFontInfo channelFontInfo, Integer channelId, Boolean updateAvatarFlag);
    Result<String> DeleteMasterChannel(Integer channelId);
    Result<String> CreateChildrenChannel(ChannelChildrenInfo channelChildrenInfo);
    Result<String> UpdateChildrenChannel(ChannelChildrenInfo channelChildrenInfo, Integer childrenChannelId);
    Result<String> DeleteChildrenChannel(Integer channelId, Integer childrenChannelId);
}
