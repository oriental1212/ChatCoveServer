package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.dto.ChannelLogList;
import cool.oriental.chatcove.vo.channel.ChannelChildrenInfo;
import cool.oriental.chatcove.vo.channel.ChannelFontInfo;
import cool.oriental.chatcove.vo.channel.EmojiInfo;
import cool.oriental.chatcove.vo.channel.RoleInfo;

import java.util.List;

/**
 * @Author: Oriental
 * @Date: 2023-07-11-10:52
 * @Description: 频道服务接口
 */
public interface ChannelService {
    Result<String> CreateMasterChannel(ChannelFontInfo channelFontInfo);
    Result<String> UpdateMasterChannel(ChannelFontInfo channelFontInfo, Integer channelId, Boolean updateAvatarFlag);
    Result<String> DeleteMasterChannel(Integer channelId);
    Result<String> CreateChildrenChannel(ChannelChildrenInfo channelChildrenInfo);
    Result<String> UpdateChildrenChannel(ChannelChildrenInfo channelChildrenInfo, Integer childrenChannelId);
    Result<String> DeleteChildrenChannel(Integer channelId, Integer childrenChannelId);
    Result<String> CreateRole(RoleInfo roleInfo);
    Result<String> UpdateRole(RoleInfo roleInfo, Integer roleId);
    Result<String> DeleteRole(Integer channelId, Integer roleId);
    Result<String> UploadEmoji(EmojiInfo emojiInfo);
    Result<String> DeleteEmoji(Integer channelId, Integer emojiId);
    Result<String> CreateUser(Integer channelId, String nickName);
    Result<String> ChangeUserName(Integer channelId, String remarkNickName);
    Result<String> DeleteUser(Integer channelId, Long userId);
    Result<String> ExitChannel(Integer channelId);
    Result<List<ChannelLogList>> GetChannelLog(Integer channelId);
}
