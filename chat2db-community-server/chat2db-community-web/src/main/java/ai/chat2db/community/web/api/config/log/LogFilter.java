package ai.chat2db.community.web.api.config.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


public class LogFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        String message = event.getMessage();
        String logname = event.getLoggerName();
        if (message.contains("password")) {
            return FilterReply.DENY;
        }
        if (logname.contains("liquibase")) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
