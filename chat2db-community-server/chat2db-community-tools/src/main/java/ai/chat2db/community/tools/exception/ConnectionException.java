package ai.chat2db.community.tools.exception;

import ai.chat2db.community.tools.exception.BusinessException;
import lombok.Getter;

@Getter
public class ConnectionException extends BusinessException {


    public ConnectionException() {
        this("connection.error");
    }

    public ConnectionException(String code) {
        this(code, null);
    }

    public ConnectionException(String code, Object[] args) {
        super(code,args);
    }

    public ConnectionException(String code, Object[] args, Throwable throwable) {
        super(code,args, throwable);
    }
}
