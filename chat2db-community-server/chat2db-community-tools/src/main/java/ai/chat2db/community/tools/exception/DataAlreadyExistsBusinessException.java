package ai.chat2db.community.tools.exception;
import ai.chat2db.community.tools.exception.BusinessException;
import lombok.Getter;
@Getter
public class DataAlreadyExistsBusinessException extends BusinessException {
    public DataAlreadyExistsBusinessException() {
        super("common.dataAlreadyExists");
    }
    public DataAlreadyExistsBusinessException(String key, Object value) {
        super("common.dataAlreadyExistsWithParam", new Object[] {key, value});
    }
}
