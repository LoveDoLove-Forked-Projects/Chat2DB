package ai.chat2db.plugin.postgresql.constant;

import ai.chat2db.plugin.postgresql.enums.type.PostgreSQLColumnTypeEnum;
import ai.chat2db.plugin.postgresql.value.factory.PostgreSQLValueProcessorFactory;
import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.DefaultSQLExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Objects;



public final class PostgreSQLValueProcessorConstants {

    public static final String SQL_SHOW_TIMEZONE = "show timezone";
    public static final String GET_TIMEZONE_ERROR = "pgsql.getTimeZone.error";

    private PostgreSQLValueProcessorConstants() {
    }
}
