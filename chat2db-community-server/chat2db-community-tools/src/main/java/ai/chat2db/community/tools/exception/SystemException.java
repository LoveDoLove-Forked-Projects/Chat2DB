package ai.chat2db.community.tools.exception;

import lombok.Data;


@Data
public class SystemException extends RuntimeException {


    private String code;


    private Object[] args;

    public SystemException() {
        this("common.systemError");
    }

    public SystemException(String code) {
        this(code, null);
    }

    public SystemException(String code, Object[] args) {
        super(code);
        this.code = code;
        this.args = args;
    }

    public SystemException(String code, Object[] args, Throwable throwable) {
        super(code, throwable);
        this.code = code;
        this.args = args;
    }
}
