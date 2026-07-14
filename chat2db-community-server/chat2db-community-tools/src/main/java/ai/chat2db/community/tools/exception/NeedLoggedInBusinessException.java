package ai.chat2db.community.tools.exception;
import ai.chat2db.community.tools.exception.BusinessException;
import lombok.Getter;
@Getter
public class NeedLoggedInBusinessException extends BusinessException {
    public NeedLoggedInBusinessException() {
        super("common.needLoggedIn");
    }
}
