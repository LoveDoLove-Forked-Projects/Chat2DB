package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.util.I18nUtils;


public class DefaultExceptionConvertor implements IExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail(I18nUtils.DEFAULT_MESSAGE_CODE,
                I18nUtils.getMessage(I18nUtils.DEFAULT_MESSAGE_CODE), null);
    }

}
