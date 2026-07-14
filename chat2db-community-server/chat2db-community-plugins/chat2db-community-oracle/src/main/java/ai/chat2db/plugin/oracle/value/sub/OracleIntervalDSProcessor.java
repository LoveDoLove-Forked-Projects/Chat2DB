package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;


public class OracleIntervalDSProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return OracleDmlValueTemplate.wrapIntervalDayToSecond(dataValue.getValue(), dataValue.getPrecision(), dataValue.getScale());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getStringValue();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return OracleDmlValueTemplate.wrapIntervalDayToSecond(convertJDBCValueByType(dataValue), dataValue.getPrecision(), dataValue.getScale());
    }

}
