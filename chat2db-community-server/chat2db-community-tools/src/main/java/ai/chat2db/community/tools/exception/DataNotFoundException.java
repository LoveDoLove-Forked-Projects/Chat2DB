package ai.chat2db.community.tools.exception;
import ai.chat2db.community.tools.exception.BusinessException;
import lombok.Getter;
@Getter
public class DataNotFoundException extends BusinessException {
    public DataNotFoundException() {
        super("common.dataNotFound");
    }
}
