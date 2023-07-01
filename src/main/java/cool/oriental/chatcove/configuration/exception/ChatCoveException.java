package cool.oriental.chatcove.configuration.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-9:00
 * @Description: 自定义异常
 */
@Getter
@Setter
public class ChatCoveException extends RuntimeException {
    protected String errorCode;
    protected String errorMsg;

    public ChatCoveException(){
        super();
    }
    public ChatCoveException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultCode());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    public ChatCoveException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getResultCode(), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    public ChatCoveException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public ChatCoveException(String errorCode, String errorMsg) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ChatCoveException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
