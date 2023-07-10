package cool.oriental.chatcove.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.entity.FriendInfo;
import cool.oriental.chatcove.entity.FriendRequest;
import cool.oriental.chatcove.entity.UserInfo;
import cool.oriental.chatcove.mapper.FriendInfoMapper;
import cool.oriental.chatcove.mapper.FriendRequestMapper;
import cool.oriental.chatcove.mapper.UserInfoMapper;
import cool.oriental.chatcove.service.FriendsService;
import cool.oriental.chatcove.utils.FriendsStatus;
import cool.oriental.chatcove.vo.AllFriendsInfo;
import cool.oriental.chatcove.vo.FriendsRequestInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Oriental
 * @Date: 2023-07-09-14:29
 * @Description: 好友服务实现类
 */

@Service
@Slf4j
public class FriendsServiceImpl implements FriendsService {
    @Resource
    FriendInfoMapper friendInfoMapper;
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    FriendRequestMapper friendRequestMapper;

    @Override
    public Result<String> AddFriends(String friendsName) {
        LambdaQueryWrapper<UserInfo> userWrapper = new LambdaQueryWrapper<>();
        userWrapper
                .eq(UserInfo::getUsername,friendsName);
        UserInfo userInfo = userInfoMapper.selectOne(userWrapper);
        if(userInfo == null){
            return Result.error("该用户不存在");
        }
        // 添加好友关系表
        try {
            friendInfoMapper.insert(
                    new FriendInfo()
                            .setUserId(StpUtil.getLoginIdAsLong())
                            .setFriendId(userInfo.getId())
                            .setStatus(FriendsStatus.REQUESTING_STATUS.getStatus())
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
                            .setRemarkName(friendsName)
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加好友信息业务出错");
            return Result.error("服务器异常，添加好友失败，请稍后重试");
        }
        // 添加好友请求表
        FriendRequest friendRequest = new FriendRequest();
        try {
            friendRequest
                    .setSenderId(StpUtil.getLoginIdAsLong())
                    .setReceiverId(userInfo.getId())
                    .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime());
            friendRequestMapper.insert(friendRequest);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加好友请求业务出错");
            return Result.error("服务器异常，添加好友失败，请稍后重试");
        }
        return Result.success("发送好友请求成功");
    }

    @Override
    public Result<String> ConfirmFriendsRequest(Integer friendRequestId, String senderName) {
        LambdaQueryWrapper<FriendRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FriendRequest::getId,friendRequestId);
        FriendRequest friendRequest = friendRequestMapper.selectOne(queryWrapper);
        if(friendRequest == null){
            return Result.error("不存在该好友请求");
        }
        try {
            // 状态变为正常
            LambdaUpdateWrapper<FriendInfo> friendsInfoWrapper = new LambdaUpdateWrapper<>();
            friendsInfoWrapper
                    .eq(FriendInfo::getUserId, friendRequest.getSenderId())
                    .eq(FriendInfo::getFriendId, friendRequest.getReceiverId())
                    .set(FriendInfo::getStatus, FriendsStatus.DEFAULT_STATUS.getStatus());
            friendInfoMapper.update(null, friendsInfoWrapper);
            // 添加被动的好友关心信息表
            friendInfoMapper.insert(
                    new FriendInfo()
                            .setUserId(friendRequest.getReceiverId())
                            .setFriendId(friendRequest.getSenderId())
                            .setStatus(FriendsStatus.DEFAULT_STATUS.getStatus())
                            .setCreateTime(DateUtil.parse(DateUtil.now()).toLocalDateTime())
                            .setRemarkName(senderName)
            );
            // 删除好友请求信息表
            friendRequestMapper.deleteById(friendRequestId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("确认好友请求业务出错");
            return Result.error("服务器异常，确认好友请求失败，请稍后重试");
        }
        return Result.success("添加好友成功");
    }

    @Override
    public Result<String> DeleteFriends(Long friendId) {
        try {
            LambdaQueryWrapper<FriendInfo> wrapperByUser = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<FriendInfo> wrapperByFriend = new LambdaQueryWrapper<>();
            wrapperByUser
                    .eq(FriendInfo::getUserId, StpUtil.getLoginIdAsLong())
                    .eq(FriendInfo::getFriendId, friendId);
            wrapperByFriend
                    .eq(FriendInfo::getFriendId, StpUtil.getLoginIdAsLong())
                    .eq(FriendInfo::getUserId, friendId);
            friendInfoMapper.delete(wrapperByUser);
            friendInfoMapper.delete(wrapperByFriend);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除好友业务出错");
            return Result.error("服务器异常，删除好友失败，请稍后重试");
        }
        return Result.success("删除成功");
    }

    @Override
    public Result<String> BlacklistFriends(Long friendId) {
        LambdaUpdateWrapper<FriendInfo> friendWrapper = new LambdaUpdateWrapper<>();
        try {
            friendWrapper
                    .eq(FriendInfo::getFriendId, friendId)
                    .eq(FriendInfo::getUserId, StpUtil.getLoginIdAsLong())
                    .set(FriendInfo::getStatus, FriendsStatus.BLACK_STATUS.getStatus());
            friendInfoMapper.update(null, friendWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("将好友加入黑名单业务出错");
            return Result.error("服务器异常，将好友加入黑名单失败，请稍后重试");
        }
        return Result.success("加入黑名单成功");
    }

    @Override
    public Result<String> RemarkFriends(Long friendId, String remarkName) {
        try {
            LambdaUpdateWrapper<FriendInfo> friendWrapper = new LambdaUpdateWrapper<>();
            friendWrapper
                    .eq(FriendInfo::getUserId, StpUtil.getLoginIdAsLong())
                    .eq(FriendInfo::getFriendId, friendId)
                    .set(FriendInfo::getRemarkName, remarkName);
            friendInfoMapper.update(null, friendWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("重命名好友业务出错");
            return Result.error("服务器异常，重命名好友出错，请稍后重试");
        }
        return Result.success("好友重命名成功");
    }

    @Override
    public Result<List<AllFriendsInfo>> ShowAllFriends() {
        try {
            return Result.success(friendInfoMapper.ShowAllFriends(StpUtil.getLoginIdAsLong()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询好友业务出错");
            return Result.error("服务器异常，查询好友出错，请稍后重试");
        }
    }

    @Override
    public Result<List<FriendsRequestInfo>> GetFriendsRequest() {
        try {
            return Result.success(friendRequestMapper.GetFriendsRequest(StpUtil.getLoginIdAsLong()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取好友请求业务出错");
            return Result.error("服务器异常，获取好友请求出错，请稍后重试");
        }
    }
}
