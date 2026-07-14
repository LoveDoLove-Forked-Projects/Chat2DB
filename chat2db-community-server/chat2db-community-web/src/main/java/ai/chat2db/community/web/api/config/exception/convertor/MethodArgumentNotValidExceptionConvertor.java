package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;

import ai.chat2db.community.web.api.util.ExceptionConvertorUtils;
import ai.chat2db.community.tools.util.ExceptionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;


public class MethodArgumentNotValidExceptionConvertor implements IExceptionConvertor<MethodArgumentNotValidException> {

    @Override
    public ActionResult convert(MethodArgumentNotValidException exception) {
        String message = ExceptionConvertorUtils.buildMessage(exception.getBindingResult());
        return ActionResult.fail("common.paramError", message, ExceptionUtils.getErrorInfoFromException(exception));
    }
}
