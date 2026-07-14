package ai.chat2db.community.web.api.config.exception.convertor;

import ai.chat2db.community.tools.wrapper.result.ActionResult;


public interface IExceptionConvertor<T extends Throwable> {


    ActionResult convert(T exception);
}
