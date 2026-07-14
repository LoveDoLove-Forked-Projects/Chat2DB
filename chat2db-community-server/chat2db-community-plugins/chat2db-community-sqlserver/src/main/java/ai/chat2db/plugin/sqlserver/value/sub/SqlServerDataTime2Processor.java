package ai.chat2db.plugin.sqlserver.value.sub;

import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import org.apache.commons.lang3.StringUtils;

public class SqlServerDataTime2Processor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return StringUtils.wrap(dataValue.getValue(), "'");
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getStringValue();

    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return StringUtils.wrap(convertJDBCValueByType(dataValue), "'");
    }
}
