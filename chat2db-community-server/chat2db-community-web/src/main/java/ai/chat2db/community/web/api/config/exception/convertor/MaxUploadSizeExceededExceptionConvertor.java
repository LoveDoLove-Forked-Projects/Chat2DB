package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;

import ai.chat2db.community.tools.util.I18nUtils;
import ai.chat2db.community.tools.util.ExceptionUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


public class MaxUploadSizeExceededExceptionConvertor implements IExceptionConvertor<MaxUploadSizeExceededException> {

    @Override
    public ActionResult convert(MaxUploadSizeExceededException exception) {
        return ActionResult.fail("common.maxUploadSize", I18nUtils.getMessage("common.maxUploadSize"), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
