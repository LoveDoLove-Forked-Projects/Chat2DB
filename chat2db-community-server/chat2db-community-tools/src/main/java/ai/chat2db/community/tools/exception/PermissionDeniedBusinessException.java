package ai.chat2db.community.tools.exception;
import ai.chat2db.community.tools.exception.BusinessException;
import lombok.Getter;
@Getter
public class PermissionDeniedBusinessException extends BusinessException {
    public PermissionDeniedBusinessException() {
        super("common.permissionDenied");
    }
}
