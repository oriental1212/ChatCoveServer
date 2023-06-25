package cool.oriental.chatcove.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cool.oriental.chatcove.entity.UserInfo;
import cool.oriental.chatcove.mapper.UserInfoMapper;
import cool.oriental.chatcove.service.UserInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author oriental
 * @since 2023-06-25 01:23:32
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
