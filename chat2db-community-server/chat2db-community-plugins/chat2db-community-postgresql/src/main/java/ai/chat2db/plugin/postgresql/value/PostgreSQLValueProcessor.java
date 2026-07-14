package ai.chat2db.plugin.postgresql.value;

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


import static ai.chat2db.plugin.postgresql.constant.PostgreSQLValueProcessorConstants.*;
public class PostgreSQLValueProcessor extends DefaultValueProcessor {



    private static final Logger log = LoggerFactory.getLogger(PostgreSQLValueProcessor.class);


    private String timezone = null;

    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String emptyStr) {
            if (StringUtils.isBlank(emptyStr)) {
                return emptyStr;
            }
        }
        String type = dataValue.getType();
        if (StringUtils.equalsIgnoreCase(PostgreSQLColumnTypeEnum.TIMESTAMPTZ.name(), type)
                && StringUtils.isBlank(timezone)
                && !StringUtils.equalsIgnoreCase(GET_TIMEZONE_ERROR, timezone)
        ) {
            Connection connection = Chat2DBContext.getConnection();
            timezone = DefaultSQLExecutor.getInstance().execute(connection, SQL_SHOW_TIMEZONE, (resultSet) -> {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                } else {
                    return GET_TIMEZONE_ERROR;
                }
            });
            log.info("Get timezone: {}", timezone);
        }
        dataValue.setTimezone(timezone);
        return convertJDBCValueByType(dataValue);
    }


    @Override
    public String getJdbcSqlValueString(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            return "NULL";
        }
        if (value instanceof String stringValue) {
            if (StringUtils.isBlank(stringValue)) {
                return EasyStringUtils.quoteString(stringValue);
            }
        }
        return convertJDBCValueStrByType(dataValue);
    }

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        try {
            DefaultValueProcessor valueProcessor = PostgreSQLValueProcessorFactory.getValueProcessor(dataValue.getDateTypeName().toUpperCase());
            if (Objects.nonNull(valueProcessor)) {
                return valueProcessor.convertSQLValueByType(dataValue);
            }
        } catch (Exception e) {
            log.warn("convertSQLValueByType error", e);
            return super.convertSQLValueByType(dataValue);
        }
        return super.convertSQLValueByType(dataValue);
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        String type = dataValue.getType();
        try {
            DefaultValueProcessor valueProcessor = PostgreSQLValueProcessorFactory.getValueProcessor(type.toUpperCase());
            if (Objects.nonNull(valueProcessor)) {
                return valueProcessor.convertJDBCValueByType(dataValue);
            }
        } catch (Exception e) {
            log.warn("convertJDBCValueByType error", e);
            return super.convertJDBCValueByType(dataValue);
        }
        return super.convertJDBCValueByType(dataValue);

    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        String type = dataValue.getType();
        DefaultValueProcessor valueProcessor;
        try {
            valueProcessor = PostgreSQLValueProcessorFactory.getValueProcessor(type.toUpperCase());
            if (Objects.nonNull(valueProcessor)) {
                return valueProcessor.convertJDBCValueStrByType(dataValue);
            }
        } catch (Exception e) {
            log.warn("convertJDBCValueStrByType error", e);
            return super.convertJDBCValueStrByType(dataValue);
        }
        return super.convertJDBCValueStrByType(dataValue);
    }


    @Override
    public boolean isStringDataType(String dataType) {
        return StringUtils.equalsAnyIgnoreCase(dataType, PostgreSQLColumnTypeEnum.TEXT.name(),
                                               PostgreSQLColumnTypeEnum.CHARACTER.name(),
                                               PostgreSQLColumnTypeEnum.VARCHAR.name());
    }
}
