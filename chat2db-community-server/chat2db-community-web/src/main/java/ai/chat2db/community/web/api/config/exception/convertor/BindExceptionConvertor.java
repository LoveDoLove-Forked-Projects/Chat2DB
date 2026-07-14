package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.web.api.util.ExceptionConvertorUtils;
import ai.chat2db.community.tools.util.ExceptionUtils;
import org.springframework.validation.BindException;


public class BindExceptionConvertor implements IExceptionConvertor<BindException> {

    @Override
    public ActionResult convert(BindException exception) {
        String message = ExceptionConvertorUtils.buildMessage(exception.getBindingResult());
        return ActionResult.fail("common.paramError", message, ExceptionUtils.getErrorInfoFromException(exception));
    }
}
