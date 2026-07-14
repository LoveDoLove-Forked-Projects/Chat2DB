package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.util.ExceptionUtils;


public class ParamExceptionConvertor implements IExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail("common.paramError", exception.getMessage(), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
