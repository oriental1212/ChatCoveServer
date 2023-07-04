package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.vo.RegisterInfo;

/**
 * @Author: Oriental
 * @Date: 2023-07-04-15:10
 * @Description: 用户权限服务接口
 */

public interface AuthorityService {
    Result<String> Register(RegisterInfo registerInfo);
}
