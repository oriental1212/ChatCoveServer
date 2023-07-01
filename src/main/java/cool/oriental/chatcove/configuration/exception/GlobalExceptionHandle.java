package cool.oriental.chatcove.configuration.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-9:01
 * @Description: 全局异常处理类
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {
    /*
    * @Description: 处理自定义的业务异常
    * @param: ChaiCoveException
    * @return: Result
    * @Author: Oriental
    * @Date: 2023/7/1
    */
    @ExceptionHandler(value = ChatCoveException.class)
    @ResponseBody
    public <T> Result<T> ChatCoveExceptionHandler(ChatCoveException err){
        log.error("发生业务异常，原因：{}", err.getErrorMsg());
        return Result.error(err.getErrorCode(),err.getErrorMsg());
    }

    /*
    * @Description: 处理空指针异常
    * @Param: NullPointerException
    * @return: Result
    * @Author: Oriental
    * @Date: 2023/7/1
    */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public <T> Result<T> NullPointerExceptionHandler(NullPointerException e){
        log.error("发生空指针的异常",e);
        return Result.error(EnumException.BODY_NOT_MATCH);
    }
}
