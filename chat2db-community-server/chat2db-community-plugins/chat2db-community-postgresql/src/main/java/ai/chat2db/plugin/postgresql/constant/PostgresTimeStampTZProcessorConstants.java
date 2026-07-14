package ai.chat2db.plugin.postgresql.constant;

import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Objects;



public final class PostgresTimeStampTZProcessorConstants {

    public static final String GET_TIMEZONE_ERROR = "pgsql.getTimeZone.error";

    private PostgresTimeStampTZProcessorConstants() {
    }
}
