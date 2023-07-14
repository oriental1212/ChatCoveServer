package cool.oriental.chatcove.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.configuration.minio.MinioConfiguration;
import cool.oriental.chatcove.configuration.minio.MinioEnum;
import cool.oriental.chatcove.entity.ChannelInfo;
import cool.oriental.chatcove.mapper.ChannelInfoMapper;
import cool.oriental.chatcove.mapper.ChannelLogsMapper;
import cool.oriental.chatcove.service.ChannelService;
import cool.oriental.chatcove.utils.MinioTools;
import cool.oriental.chatcove.vo.channel.CreateChannelInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: Oriental
 * @Date: 2023-07-11-10:52
 * @Description: 频道服务实现类
 */

@Service
@Slf4j
public class ChannelServiceImpl implements ChannelService {
    @Resource
    private ChannelInfoMapper channelInfoMapper;
    @Resource
    private ChannelLogsMapper channelLogsMapper;
    @Resource
    private MinioConfiguration minioConfiguration;
    @Value("${minio.url}")
    private String minioServerUrl;

    @Override
    public Result<String> CreateMasterChannel(CreateChannelInfo createChannelInfo) {
        Boolean upload = new MinioTools().Upload(createChannelInfo.getAvatar(), createChannelInfo.getChannelName(), MinioEnum.CHANNEL_AVATAR);
        if(!upload){
            return Result.error("服务器系统异常，创建频道服务失败，请稍后重试");
        }
        String avatarUrl =  minioServerUrl+"/"+"channel"+"/"+createChannelInfo.getChannelName()+"/"+"channelAvatar";
        // 创建主频道
        try {
            channelInfoMapper.insert(
                    new ChannelInfo()
                            .setMasterId(StpUtil.getLoginIdAsLong())
                            .setAvatar(avatarUrl)
                            .setName(createChannelInfo.getChannelName())
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
                            .setDescription((createChannelInfo.getDescription().isEmpty())? createChannelInfo.getDescription(): null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建主频道业务出错");
            return Result.error("服务器异常，创建主频道失败，请稍后重试");
        }
        // 添加日志
        return Result.success(avatarUrl);
    }
}
