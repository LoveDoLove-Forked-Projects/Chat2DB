package ai.chat2db.spi;

import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Types;
import java.util.Objects;


public class DefaultValueProcessor implements IValueProcessor {

    @Override
    public String getSqlValueString(SQLDataValue dataValue) {
        if (Objects.isNull(dataValue.getValue())) {
            return "NULL";
        }
        return convertSQLValueByType(dataValue);

    }


    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        return convertJDBCValueByType(dataValue);
    }


    @Override
    public String getJdbcSqlValueString(JDBCDataValue dataValue) {
        return convertJDBCValueStrByType(dataValue);
    }

    public String convertSQLValueByType(SQLDataValue dataValue) {
        return getString(dataValue.getValue());
    }


    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return switch (dataValue.getSqlType()) {
            case Types.CLOB, Types.NCLOB -> dataValue.getClobString();
            case Types.LONGVARCHAR, Types.LONGNVARCHAR -> dataValue.getCharsetString();
            case Types.BLOB, Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY -> dataValue.getBinaryDataString();
            default -> dataValue.getString();
        };
    }


    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        String value = dataValue.getString();
        if (value == null) {
            return "NULL";
        }
        return getString(value);

    }

    protected boolean isNumber(String value) {
        return NumberUtils.isCreatable(value);
    }

    private String getString(String value) {
        return EasyStringUtils.escapeAndQuoteString(value);
    }

    @Override
    public boolean isStringDataType(String dataType) {
        return false;
    }
}
