package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;


public class OracleAnyDataProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return dataValue.getValue();
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {

        return "SYS.ANYDATA";
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return "SYS.ANYDATA";
    }


}
