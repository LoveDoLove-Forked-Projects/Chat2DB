package ai.chat2db.community.web.api.util;

import java.util.List;

import ai.chat2db.community.tools.constant.SymbolConstant;
import ai.chat2db.community.tools.util.I18nUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;


public class ExceptionConvertorUtils {


    public static String buildMessage(BindingResult result) {
        List<ObjectError> errors = result.getAllErrors();
        if (CollectionUtils.isEmpty(errors)) {
            return null;
        }

        int index = 1;
        StringBuilder msg = new StringBuilder();
        msg.append(I18nUtils.getMessage("common.paramCheckError"));
        for (ObjectError e : errors) {
            msg.append(index++);
            msg.append(SymbolConstant.DOT);
            if (e instanceof FieldError fieldError) {
                msg.append(fieldError.getField());
                msg.append(" : ");
            }
            msg.append(e.getDefaultMessage());
            msg.append(SymbolConstant.SEMICOLON);
        }
        return msg.toString();
    }
}
