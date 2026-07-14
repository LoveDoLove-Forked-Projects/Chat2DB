package ai.chat2db.community.tools.exception;
import ai.chat2db.community.tools.exception.BusinessException;
import lombok.Getter;
@Getter
public class ParamBusinessException extends BusinessException {
    public ParamBusinessException() {
        super("common.paramError");
    }
    public ParamBusinessException(String paramString) {
        super("common.paramDetailError", new Object[] {paramString});
    }
}
