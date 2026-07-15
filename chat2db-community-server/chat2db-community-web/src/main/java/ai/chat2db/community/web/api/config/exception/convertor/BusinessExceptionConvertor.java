package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.util.I18nUtils;


public class BusinessExceptionConvertor implements IExceptionConvertor<BusinessException> {

    @Override
    public ActionResult convert(BusinessException exception) {
        return ActionResult.fail(exception.getCode(), I18nUtils.getMessage(exception.getCode(), exception.getArgs()),
            null);
    }
}
