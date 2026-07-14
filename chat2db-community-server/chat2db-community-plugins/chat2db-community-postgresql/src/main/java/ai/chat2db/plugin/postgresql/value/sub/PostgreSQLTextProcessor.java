package ai.chat2db.plugin.postgresql.value.sub;

import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PostgreSQLTextProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return EasyStringUtils.escapeAndQuoteString(dataValue.getValue());
    }


    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getCharsetString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return EasyStringUtils.escapeAndQuoteString(dataValue.getStringValue());
    }

}
