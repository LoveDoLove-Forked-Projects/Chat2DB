package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class OracleTimeStampLTZProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(dataValue.getValue(), dataValue.getScale());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        Timestamp timestamp = dataValue.getTimestamp();
        int scale = dataValue.getScale();
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        StringBuilder templateBuilder = new StringBuilder("yyyy-MM-dd HH:mm:ss");
        if (scale != 0) {
            templateBuilder.append(".");
            templateBuilder.append("S".repeat(scale));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(templateBuilder.toString());
        return localDateTime.format(formatter);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        Timestamp timestamp = dataValue.getTimestamp();
        int scale = dataValue.getScale();
        Instant instant = timestamp.toInstant();
        ZonedDateTime utcZonedDateTime = instant.atZone(ZoneId.of("UTC"));
        StringBuilder templateBuilder = new StringBuilder("yyyy-MM-dd HH:mm:ss");
        if (scale != 0) {
            templateBuilder.append(".");
            templateBuilder.append("S".repeat(scale));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(templateBuilder.toString());
        String formattedUtcTime = utcZonedDateTime.format(formatter);
        return wrap(formattedUtcTime, scale);
    }

    private String wrap(String value, int scale) {
        if (scale == 0) {
            return OracleDmlValueTemplate.wrapDate(value);
        }
        return OracleDmlValueTemplate.wrapTimestamp(value, scale);
    }
}
