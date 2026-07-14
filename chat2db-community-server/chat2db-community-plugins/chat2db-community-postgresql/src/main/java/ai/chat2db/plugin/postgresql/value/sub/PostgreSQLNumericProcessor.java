package ai.chat2db.plugin.postgresql.value.sub;

import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;


public class PostgreSQLNumericProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return super.convertSQLValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getBigDecimalString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return dataValue.getBigDecimalString();
    }
}
