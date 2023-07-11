package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.exception.Result;

/**
 * @Author: Oriental
 * @Date: 2023-07-11-10:52
 * @Description: 频道服务接口
 */
public interface ChannelService {
    public Result<String> CreateMasterChannel();
}
