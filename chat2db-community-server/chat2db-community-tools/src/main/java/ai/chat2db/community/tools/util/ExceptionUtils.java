package ai.chat2db.community.tools.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ExceptionUtils {


    public static String getErrorInfoFromException(Throwable throwable) {
        Throwable root =  getRootCause(throwable);
        try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
            root.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            log.error("ErrorInfoFromException", e);
            return "ErrorInfoFromException";
        }
    }

    public static Throwable getRootCause(final Throwable throwable) {
        Throwable thr = org.apache.commons.lang3.exception.ExceptionUtils.getRootCause(throwable);
        return thr == null ? throwable : thr;
    }
}
