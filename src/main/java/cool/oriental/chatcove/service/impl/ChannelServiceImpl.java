package cool.oriental.chatcove.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.configuration.minio.MinioEnum;
import cool.oriental.chatcove.entity.*;
import cool.oriental.chatcove.mapper.*;
import cool.oriental.chatcove.service.ChannelService;
import cool.oriental.chatcove.utils.ChannelIdGenerator;
import cool.oriental.chatcove.utils.MinioTools;
import cool.oriental.chatcove.vo.channel.ChannelChildrenInfo;
import cool.oriental.chatcove.vo.channel.ChannelFontInfo;
import cool.oriental.chatcove.vo.channel.EnumChannelLog;
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
    private ChannelChildrenMapper channelChildrenMapper;
    @Resource
    private ChannelEmojiMapper channelEmojiMapper;
    @Resource
    private ChannelGroupMapper channelGroupMapper;
    @Resource
    private ChannelRoleMapper channelRoleMapper;
    @Resource
    private ChannelSettingMapper channelSettingMapper;
    @Resource
    private ChannelUserMapper channelUserMapper;
    @Value("${minio.url}")
    private String minioServerUrl;

    @Override
    public Result<String> CreateMasterChannel(ChannelFontInfo channelFontInfo) {
        Integer channelId = Integer.parseInt(ChannelIdGenerator.generateUniqueId());
        Boolean upload = new MinioTools().Upload(channelFontInfo.getAvatar(), channelId, MinioEnum.CHANNEL_AVATAR);
        if(!upload){
            return Result.error("服务器系统异常，创建频道服务失败，请稍后重试");
        }
        String avatarUrl =  minioServerUrl+"/"+"channel"+"/"+ channelId+"/"+"channelAvatar";
        try {
            // 创建主频道
            int insertFlag = channelInfoMapper.insert(
                    new cool.oriental.chatcove.entity.ChannelInfo()
                            .setId(channelId)
                            .setMasterId(StpUtil.getLoginIdAsLong())
                            .setAvatar(avatarUrl)
                            .setName(channelFontInfo.getChannelName())
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
                            .setDescription((channelFontInfo.getDescription().isEmpty()) ? channelFontInfo.getDescription() : null)
            );
            if(insertFlag > 0){
                // 添加日志
                Boolean flag = InsertLog(channelId, EnumChannelLog.CHANNEL_INSERT, "创建新的主频道");
                if(flag == Boolean.TRUE){
                    return Result.success(avatarUrl);
                }
                else{
                    return Result.error("创建主频道失败");
                }
            }
            else {
                return Result.error("创建主频道失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建主频道业务出错");
            if(channelInfoMapper.selectById(channelId) != null){
                channelInfoMapper.deleteById(channelId);
            }
            return Result.error("服务器异常，创建主频道失败，请稍后重试");
        }
    }

    @Override
    public Result<String> UpdateMasterChannel(ChannelFontInfo channelFontInfo, Integer channelId, Boolean updateAvatarFlag) {
        ChannelInfo channelInfo = channelInfoMapper.selectById(channelId);
        if(channelInfo == null){
            return Result.error("不存在该频道");
        }
        if(!channelInfo.getMasterId().equals(StpUtil.getLoginIdAsLong())){
            return Result.error("您不是频道主,没有权限更改");
        }
        try {
            // 更新主频道
            if(updateAvatarFlag == Boolean.TRUE){
                Boolean upload = new MinioTools().Upload(channelFontInfo.getAvatar(), channelId, MinioEnum.CHANNEL_AVATAR);
                if(!upload){
                    return Result.error("服务器系统异常，更新频道服务失败，请稍后重试");
                }
            }
            LambdaUpdateWrapper<ChannelInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ChannelInfo::getId,channelId);
            if(!channelInfo.getName().equals(channelFontInfo.getChannelName())){
                updateWrapper.set(ChannelInfo::getName, channelFontInfo.getChannelName());
            }
            if(!channelInfo.getDescription().equals(channelFontInfo.getDescription())){
                updateWrapper.set(ChannelInfo::getDescription, channelFontInfo.getDescription());
            }
            int updateFlag = channelInfoMapper.update(null, updateWrapper);
            if(updateFlag>0){
                // 添加日志
                Boolean flag = InsertLog(channelId, EnumChannelLog.CHANNEL_UPDATE, "更新频道信息");
                if(flag == Boolean.TRUE){
                    return Result.success("更新成功");
                }else{
                    return Result.error("更新失败，请稍后重试");
                }
            }
            else{
                return Result.error("更新失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新主频道信息业务出错");
            return Result.error("服务器异常，更新主频道失败，请稍后重试");
        }
    }

    @Override
    public Result<String> DeleteMasterChannel(Integer channelId) {
        ChannelInfo channelInfo = channelInfoMapper.selectById(channelId);
        if(!channelInfo.getMasterId().equals(StpUtil.getLoginIdAsLong())){
            return Result.error("你没有删除该频道的权限");
        }
        Boolean delete = new MinioTools().Delete(channelId, MinioEnum.CHANNEL_DELETE);
        if(delete == Boolean.TRUE){
            try {
                channelInfoMapper.deleteById(channelId);
                channelLogsMapper.delete(new LambdaQueryWrapper<ChannelLogs>().eq(ChannelLogs::getChannelId,channelId));
                channelChildrenMapper.delete(new LambdaQueryWrapper<ChannelChildren>().eq(ChannelChildren::getChannelId,channelId));
                channelEmojiMapper.delete(new LambdaQueryWrapper<ChannelEmoji>().eq(ChannelEmoji::getChannelId,channelId));
                channelGroupMapper.delete(new LambdaQueryWrapper<ChannelGroup>().eq(ChannelGroup::getChannelId,channelId));
                channelUserMapper.delete(new LambdaQueryWrapper<ChannelUser>().eq(ChannelUser::getChannelId,channelId));
                channelSettingMapper.delete(new LambdaQueryWrapper<ChannelSetting>().eq(ChannelSetting::getChannelId,channelId));
                channelRoleMapper.delete(new LambdaQueryWrapper<ChannelRole>().eq(ChannelRole::getChannelId,channelId));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("删除主频道业务出错");
                return Result.error("服务器异常，删除主频道失败，请稍后重试");
            }
            return Result.success();
        }else{
            return Result.error("删除主频道业务失败，请稍后重试");
        }
    }

    @Override
    public Result<String> CreateChildrenChannel(ChannelChildrenInfo channelChildrenInfo) {
        ChannelInfo channelInfo = channelInfoMapper.selectById(channelChildrenInfo.getMasterChannelId());
        if(!channelInfo.getMasterId().equals(StpUtil.getLoginIdAsLong())){
            return Result.error("您不是频道主,没有权限更改");
        }
        try {
            int insertFlag = channelChildrenMapper.insert(
                    new ChannelChildren()
                            .setChannelId(channelChildrenInfo.getMasterChannelId())
                            .setGroupId(channelChildrenInfo.getGroupId())
                            .setUserId(StpUtil.getLoginIdAsLong())
                            .setName(channelChildrenInfo.getChildrenChannelName())
                            .setDescription(channelChildrenInfo.getChildrenChannelDescription().isEmpty() ? channelChildrenInfo.getChildrenChannelDescription() : null)
                            .setType(channelChildrenInfo.getType())
                            .setSecret(channelChildrenInfo.getSecret())
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
            );
            if(insertFlag>0){
                InsertLog(channelChildrenInfo.getMasterChannelId(), EnumChannelLog.CHANNEL_INSERT, "创建子频道");
                return Result.success("创建子频道成功");
            }else{
                return Result.error("服务器异常，创建子频道业务失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建子频道业务异常");
            return Result.error("服务器异常，创建子频道业务失败，请稍后重试");
        }
    }

    @Override
    public Result<String> UpdateChildrenChannel(ChannelChildrenInfo channelChildrenInfo, Integer childrenChannelId) {
        ChannelChildren channelBasicChildren = channelChildrenMapper.selectOne(
                new LambdaQueryWrapper<ChannelChildren>()
                        .eq(ChannelChildren::getChannelId, channelChildrenInfo.getMasterChannelId())
                        .eq(ChannelChildren::getId, childrenChannelId)
        );
        if(channelBasicChildren == null){
            return Result.error("不存在该子频道");
        }
        if(!channelBasicChildren.getUserId().equals(StpUtil.getLoginIdAsLong())){
            return Result.error("你不是频道主没有权限修改");
        }
        try {
            // 设置更新条件
            LambdaUpdateWrapper<ChannelChildren> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ChannelChildren::getChannelId, channelChildrenInfo.getMasterChannelId());
            if (!channelBasicChildren.getGroupId().equals(channelChildrenInfo.getGroupId())){
                updateWrapper.set(ChannelChildren::getGroupId, channelChildrenInfo.getGroupId());
            }
            if (!channelBasicChildren.getName().equals(channelChildrenInfo.getChildrenChannelName())){
                updateWrapper.set(ChannelChildren::getName, channelChildrenInfo.getChildrenChannelName());
            }
            if (!channelBasicChildren.getDescription().equals(channelChildrenInfo.getChildrenChannelDescription())){
                updateWrapper.set(ChannelChildren::getDescription, channelChildrenInfo.getChildrenChannelDescription());
            }
            if (!channelBasicChildren.getType().equals(channelChildrenInfo.getType())){
                updateWrapper.set(ChannelChildren::getType, channelChildrenInfo.getType());
            }
            if (!channelBasicChildren.getSecret().equals(channelChildrenInfo.getSecret())){
                updateWrapper.set(ChannelChildren::getSecret, channelChildrenInfo.getSecret());
            }
            int updateFlag = channelChildrenMapper.update(null, updateWrapper);
            if(updateFlag>0){
                InsertLog(channelChildrenInfo.getMasterChannelId(), EnumChannelLog.CHANNEL_UPDATE, "更新子频道信息");
                return Result.success("更新子频道业务成功");
            }else{
                return Result.error("更新子频道信息失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新子频道业务异常");
            return Result.error("更新子频道信息失败，请稍后重试");
        }
    }

    @Override
    public Result<String> DeleteChildrenChannel(Integer channelId, Integer childrenChannelId) {
        LambdaQueryWrapper<ChannelChildren> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChannelChildren::getId, childrenChannelId);
        ChannelChildren channelBasicChildren = channelChildrenMapper.selectOne(queryWrapper);
        if(channelBasicChildren == null){
            return Result.error("不存在该频道");
        }
        if(!channelBasicChildren.getUserId().equals(StpUtil.getLoginIdAsLong())){
            return Result.error("你没有删除该频道的权限");
        }
        try {
            int deleteFlag = channelChildrenMapper.delete(queryWrapper);
            if(deleteFlag>0){
                InsertLog(channelId, EnumChannelLog.CHANNEL_UPDATE, "删除子频道"+channelBasicChildren.getName());
                return Result.success("删除子频道成功");
            }else{
                return Result.error("删除子频道失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除子频道业务出错");
            return Result.error("服务器异常，删除子频道失败，请稍后重试");
        }
    }

    private Boolean InsertLog(Integer channelId, EnumChannelLog enumChannelLog, String content){
        try {
            channelLogsMapper.insert(
                    new ChannelLogs()
                            .setChannelId(channelId)
                            .setUserId(StpUtil.getLoginIdAsLong())
                            .setType(enumChannelLog.getType())
                            .setContent(content)
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建日志服务异常，请稍后重试");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
