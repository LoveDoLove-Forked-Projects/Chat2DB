package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import org.apache.commons.lang3.StringUtils;

public class MysqlDoubleProcessor  extends DefaultValueProcessor{

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        String value = dataValue.getValue();
        if (isNumber(value)) {
            return value;
        }else {
            return StringUtils.wrap(value,"'");
        }
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return String.valueOf(dataValue.getDouble());
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return convertJDBCValueByType(dataValue);
    }


}
