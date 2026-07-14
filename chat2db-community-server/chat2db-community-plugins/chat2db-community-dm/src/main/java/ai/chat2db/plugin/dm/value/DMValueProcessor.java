package ai.chat2db.plugin.dm.value;

import ai.chat2db.plugin.dm.value.factory.DMValueProcessorFactory;
import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class DMValueProcessor extends DefaultValueProcessor {

    private static final Logger log = LoggerFactory.getLogger(DMValueProcessor.class);


    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (value instanceof String emptyStr) {
            if (StringUtils.isBlank(emptyStr)) {
                return emptyStr;
            }
        }
        return convertJDBCValueByType(dataValue);
    }


    @Override
    public String getJdbcSqlValueString(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
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
            DefaultValueProcessor valueProcessor = DMValueProcessorFactory.getValueProcessor(dataValue.getDateTypeName());
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
            DefaultValueProcessor valueProcessor = DMValueProcessorFactory.getValueProcessor(type);
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
            valueProcessor = DMValueProcessorFactory.getValueProcessor(type);
            if (Objects.nonNull(valueProcessor)) {
                return valueProcessor.convertJDBCValueStrByType(dataValue);
            }
        } catch (Exception e) {
            log.warn("convertJDBCValueStrByType error", e);
            return super.convertJDBCValueStrByType(dataValue);
        }
        return super.convertJDBCValueStrByType(dataValue);
    }
}
