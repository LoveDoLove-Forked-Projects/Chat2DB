package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.util.ExceptionUtils;


public class DefaultExceptionConvertor implements IExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        String message = exception.getMessage() == null ? "" : exception.getMessage();
        String errorCode = message + " "+exception.getClass().getSimpleName();
        return ActionResult.fail(errorCode, null, ExceptionUtils.getErrorInfoFromException(exception));
    }

}
