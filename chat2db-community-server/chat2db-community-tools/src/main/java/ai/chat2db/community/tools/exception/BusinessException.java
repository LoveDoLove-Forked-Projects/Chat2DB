package ai.chat2db.community.tools.exception;

import lombok.Data;


@Data
public class BusinessException extends RuntimeException {


    private String code;


    private Object[] args;

    public BusinessException() {
        this("common.businessError");
    }

    public BusinessException(String code) {
        this(code, null);
    }

    public BusinessException(String code, Object[] args) {
        super(code);
        this.code = code;
        this.args = args;
    }

    public BusinessException(String code, Object[] args, Throwable throwable) {
        super(code, throwable);
        this.code = code;
        this.args = args;
    }

}
