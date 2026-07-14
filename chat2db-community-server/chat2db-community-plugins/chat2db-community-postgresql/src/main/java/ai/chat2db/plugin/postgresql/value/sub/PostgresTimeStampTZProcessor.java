package ai.chat2db.plugin.postgresql.value.sub;

import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Objects;


import static ai.chat2db.plugin.postgresql.constant.PostgresTimeStampTZProcessorConstants.*;
@Slf4j
public class PostgresTimeStampTZProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return EasyStringUtils.quoteString(dataValue.getValue());
    }


    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        Timestamp timestamp = dataValue.getTimestamp();
        if (Objects.isNull(timestamp)) {
            return null;
        }
        String timezone = dataValue.getTimezone();
        if (StringUtils.isBlank(timezone) || StringUtils.equalsAnyIgnoreCase(GET_TIMEZONE_ERROR,timezone)) {
            return timestamp.toString();
        }
        ZoneId zoneId = ZoneId.of(timezone);
        String zoneOffset = zoneId.getRules().getOffset(timestamp.toInstant()).getId();
        return timestamp + " " + zoneOffset;


    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        Timestamp timestamp = dataValue.getTimestamp();
        return EasyStringUtils.quoteString(timestamp.toString());
    }
}
