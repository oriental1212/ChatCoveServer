package cool.oriental.chatcove.configuration.exception;

import lombok.AllArgsConstructor;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-9:40
 * @Description: 自定义异常枚举类
 */
@AllArgsConstructor
public enum EnumException implements BaseErrorInfoInterface{
    // 数据操作错误定义
    SUCCESS("200","成功"),
    BODY_NOT_MATCH("400","请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH("401","请求的数字签名不匹配!"),
    NOT_FOUND("404", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    SERVER_BUSY("503","服务器正忙，请稍后再试!");

    // 错误码
    private final String resultCode;
    // 错误描述
    private final String resultMsg;

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
