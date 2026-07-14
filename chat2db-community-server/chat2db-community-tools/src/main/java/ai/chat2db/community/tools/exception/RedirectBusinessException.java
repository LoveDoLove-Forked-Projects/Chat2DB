package ai.chat2db.community.tools.exception;
import ai.chat2db.community.tools.exception.BusinessException;
import lombok.Getter;
@Getter
public class RedirectBusinessException extends BusinessException {
    private final String redirect;
    public RedirectBusinessException(String redirect) {
        super("common.redirect");
        this.redirect = redirect;
    }
}
