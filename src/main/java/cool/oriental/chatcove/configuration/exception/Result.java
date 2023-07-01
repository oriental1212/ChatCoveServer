package cool.oriental.chatcove.configuration.exception;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-8:59
 * @Description: 统一数据结果返回类
 */
@Data
@Accessors(chain = true)
public class Result<T>{
    // 响应代码
    private String code;
    // 响应消息
    private String  message;
    // 响应结果
    private T result;

    public Result(){};
    public Result(BaseErrorInfoInterface errorInfo){
        this.code = errorInfo.getResultCode();
        this.message = errorInfo.getResultMsg();
    };
    // 成功
    public static <T> Result<T> success(){
        return success(null);
    }
    public static <T> Result<T> success(T data){
        return new Result<T>()
                .setCode(EnumException.SUCCESS.getResultCode())
                .setMessage(EnumException.SUCCESS.getResultMsg())
                .setResult(data);
    }

    // 失败
    public static <T> Result<T> error(BaseErrorInfoInterface errorInfo){
        return new Result<T>()
                .setCode(errorInfo.getResultCode())
                .setMessage(errorInfo.getResultMsg())
                .setResult(null);
    }

    public static <T> Result<T> error(String code, String message){
        return new Result<T>()
                .setCode(code)
                .setMessage(message)
                .setResult(null);
    }
    public static <T> Result<T> error(String message){
        return new Result<T>()
                .setCode("-1")
                .setMessage(message)
                .setResult(null);
    }
}
