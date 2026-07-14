package ai.chat2db.plugin.postgresql.value.sub;

import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;

import java.sql.Timestamp;


public class PostgresTimeStampProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return EasyStringUtils.quoteString(dataValue.getValue());
    }


    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        Timestamp timestamp = dataValue.getTimestamp();
        return timestamp.toString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        Timestamp timestamp = dataValue.getTimestamp();
        return EasyStringUtils.quoteString(timestamp.toString());
    }
}
