package cool.oriental.chatcove.configuration.exception;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-9:38
 * @Description: 基础的异常接口类
 */
public interface BaseErrorInfoInterface {
    // 获取错误码
    String getResultCode();
    // 错误描述
    String getResultMsg();
}
