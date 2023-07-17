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
import cool.oriental.chatcove.vo.channel.*;
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
    @Resource
    private UserDetailMapper userDetailMapper;
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
                    new ChannelInfo()
                            .setId(channelId)
                            .setMasterId(StpUtil.getLoginIdAsLong())
                            .setAvatar(avatarUrl)
                            .setName(channelFontInfo.getChannelName())
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
                            .setDescription((channelFontInfo.getDescription().isEmpty()) ? channelFontInfo.getDescription() : null)
            );
            // 将频道主添加进频道人员
            UserDetail userDetail = userDetailMapper.selectOne(new LambdaQueryWrapper<UserDetail>().eq(UserDetail::getUserId, StpUtil.getLoginIdAsLong()));
            int insertFlag1 = channelUserMapper.insert(
                    new ChannelUser()
                            .setChannelId(channelId)
                            .setUserId(StpUtil.getLoginIdAsLong())
                            .setUserName(userDetail.getNickName())
                            .setRoleId(0)
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
                            .setActiveTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
            );
            if(insertFlag > 0 && insertFlag1 > 0){
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

    @Override
    public Result<String> CreateRole(RoleInfo roleInfo) {
        ChannelInfo channelInfo = channelInfoMapper.selectById(roleInfo.getChannelId());
        if(!channelInfo.getMasterId().equals(StpUtil.getLoginIdAsLong())){
            return Result.error("用户没有权限创建角色");
        }
        ChannelRole channelRoleOne = channelRoleMapper.selectOne(
                new LambdaQueryWrapper<ChannelRole>()
                        .eq(ChannelRole::getName,roleInfo.getName())
        );
        if(channelRoleOne != null){
            return Result.error("已存在相同名称的角色，请重试");
        }
        try {
            int insertFlag = channelRoleMapper.insert(new ChannelRole()
                    .setChannelId(roleInfo.getChannelId())
                    .setName(roleInfo.getName())
                    .setColor(roleInfo.getColor())
                    .setAuthority(roleInfo.getAuthority())
                    .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
            );
            if(insertFlag>0){
                InsertLog(roleInfo.getChannelId(), EnumChannelLog.ROLE_INSERT, "创建角色"+roleInfo.getName());
                return Result.success("角色创建成功");
            }else{
                return Result.error("服务器异常，创建角色失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建频道角色业务异常");
            return Result.error("服务器异常，创建角色失败，请稍后重试");
        }
    }

    @Override
    public Result<String> UpdateRole(RoleInfo roleInfo, Integer roleId) {
        ChannelRole channelRole = channelRoleMapper.selectById(roleId);
        if(channelRole == null){
            return Result.error("不存在该角色，请稍后再试");
        }
        // 鉴权后面写
        try {
            LambdaUpdateWrapper<ChannelRole> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ChannelRole::getId, roleId);
            if(!channelRole.getName().equals(roleInfo.getName())){
                updateWrapper.set(ChannelRole::getName, roleInfo.getName());
            }
            if(!channelRole.getColor().equals(roleInfo.getColor())){
                updateWrapper.set(ChannelRole::getColor, roleInfo.getColor());
            }
            if(!channelRole.getAuthority().equals(roleInfo.getAuthority())){
                updateWrapper.set(ChannelRole::getAuthority, roleInfo, roleInfo.getAuthority());
            }
            int updateFlag = channelRoleMapper.update(null, updateWrapper);
            if(updateFlag>0){
                InsertLog(roleInfo.getChannelId(), EnumChannelLog.ROLE_UPDATE, "更新角色信息");
                return Result.success("跟新角色成功");
            }else{
                return Result.error("服务器异常，更新角色失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新角色业务异常");
            return Result.error("服务器异常，更新角色失败，请稍后重试");
        }
    }

    @Override
    public Result<String> DeleteRole(Integer channelId, Integer roleId) {
        LambdaQueryWrapper<ChannelRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChannelRole::getId, roleId);
        ChannelRole channelRoleOne = channelRoleMapper.selectOne(queryWrapper);
        if(channelRoleOne == null){
            return Result.error("不存在该角色");
        }
        // 鉴权
        try {
            int deleteFlag = channelRoleMapper.delete(queryWrapper);
            if(deleteFlag>0){
                InsertLog(channelId, EnumChannelLog.ROLE_DELETE, "删除角色"+channelRoleOne.getName());
                return Result.success("删除角色成功");
            }else {
                return Result.error("服务器异常，删除角色失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除角色业务异常");
            return Result.error("服务器异常，删除角色失败，请稍后重试");
        }
    }

    @Override
    public Result<String> UploadEmoji(EmojiInfo emojiInfo) {
        // 鉴权...
        Boolean upload = new MinioTools().Upload(emojiInfo.getEmojiFile(), emojiInfo.getChannelId(), MinioEnum.CHANNEL_EMOJI_UPLOAD);
        if(upload.equals(Boolean.FALSE)){
            return Result.error("服务器异常，表情上传失败，请稍后重试");
        }
        String emojiUrl = minioServerUrl+"/"+"channel"+"/"+emojiInfo.getChannelId()+"/"+emojiInfo.getEmojiFile().getOriginalFilename()+"-"+ DateUtil.thisDayOfMonth();
        try {
            int insertFlag = channelEmojiMapper.insert(new ChannelEmoji()
                    .setChannelId(emojiInfo.getChannelId())
                    .setName(emojiInfo.getEmojiFile().getOriginalFilename()+"-"+DateUtil.thisDayOfMonth())
                    .setLink(emojiUrl)
                    .setUserId(StpUtil.getLoginIdAsLong())
                    .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
            );
            if(insertFlag>0){
                InsertLog(emojiInfo.getChannelId(), EnumChannelLog.EMOJI_INSERT, "上传图片"+emojiInfo.getEmojiFile().getName());
                return Result.success(emojiUrl);
            }else {
                return Result.error("服务器异常，表情上传失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传频道表情业务异常");
            return Result.error("服务器异常，表情上传失败，请稍后重试");
        }
    }

    @Override
    public Result<String> DeleteEmoji(Integer channelId, Integer emojiId) {
        // 鉴权。。。
        LambdaQueryWrapper<ChannelEmoji> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChannelEmoji::getId, emojiId);
        ChannelEmoji channelEmojiOne = channelEmojiMapper.selectOne(queryWrapper);
        if(channelEmojiOne == null){
            return Result.error("不存在该图片");
        }
        try {
            int deleteFlag = channelEmojiMapper.delete(queryWrapper);
            Boolean emojiDeleteFlag = new MinioTools().Delete(channelId, channelEmojiOne.getName(), MinioEnum.CHANNEL_EMOJI_DELETE);
            if (deleteFlag>0 && emojiDeleteFlag.equals(Boolean.TRUE)){
                InsertLog(channelId, EnumChannelLog.EMOJI_DELETE, "删除表情"+channelEmojiOne.getName());
                return Result.success("表情删除成功");
            }else{
                return Result.error("服务器异常，删除表情失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除频道表情业务失败");
            return Result.error("服务器异常，删除表情失败，请稍后重试");
        }
    }

    @Override
    public Result<String> CreateUser(Integer channelId, String nickName) {
        ChannelInfo channelInfoOne = channelInfoMapper.selectById(channelId);
        if(channelInfoOne == null){
            return Result.error("不存在该频道，请稍后重试");
        }
        try {
            int insertFlag = channelUserMapper.insert(new ChannelUser()
                    .setChannelId(channelId)
                    .setUserId(StpUtil.getLoginIdAsLong())
                    .setUserName(nickName)
                    .setRoleId(1)
                    .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
                    .setActiveTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
            );
            if(insertFlag>0){
                return Result.success("加入频道成功");
            }else{
                return Result.error("加入频道失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户加入频道业务异常");
            return Result.error("服务器异常，加入频道失败，请稍后重试");
        }
    }

    @Override
    public Result<String> DeleteUser(Integer channelId, Long userId) {
        // 鉴权。。。
        LambdaQueryWrapper<ChannelUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(ChannelUser::getUserId, userId)
                .eq(ChannelUser::getChannelId, channelId);
        ChannelUser channelUserOne = channelUserMapper.selectOne(queryWrapper);
        if(channelUserOne == null){
            return Result.error("不存在该用户");
        }
        try {
            int deleteFlag = channelUserMapper.delete(queryWrapper);
            if(deleteFlag>0){
                return InsertLog(channelId, EnumChannelLog.USER_DELETE, "删除用户"+channelUserOne.getUserName())?Result.success("删除用户成功"):Result.error("服务器异常,删除用户失败，请稍后重试");
            }else{
                return Result.error("服务器异常,删除用户失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除用户业务异常");
            return Result.error("服务器异常,删除用户失败，请稍后重试");
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
