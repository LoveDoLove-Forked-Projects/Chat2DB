package ai.chat2db.plugin.postgresql.value.sub;

import ai.chat2db.plugin.postgresql.value.template.PostgreSQLDmlValueTemplate;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;


public class PostgreSQLBitProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return PostgreSQLDmlValueTemplate.wrapBit(dataValue.getValue());
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getStringValue();
    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return PostgreSQLDmlValueTemplate.wrapBit(dataValue.getStringValue());
    }
}
