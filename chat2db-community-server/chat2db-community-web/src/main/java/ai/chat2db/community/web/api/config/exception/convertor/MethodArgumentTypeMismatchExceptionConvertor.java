package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;

import ai.chat2db.community.tools.util.I18nUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


public class MethodArgumentTypeMismatchExceptionConvertor
    implements IExceptionConvertor<MethodArgumentTypeMismatchException> {

    @Override
    public ActionResult convert(MethodArgumentTypeMismatchException exception) {
        return ActionResult.fail("common.paramError", I18nUtils.getMessage("common.paramError"), null);
    }
}
