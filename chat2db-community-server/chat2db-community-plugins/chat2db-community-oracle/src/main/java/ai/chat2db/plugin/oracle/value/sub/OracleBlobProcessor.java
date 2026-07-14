package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class OracleBlobProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        String value = dataValue.getValue();
        if (value.startsWith("0x")) {
            return EasyStringUtils.quoteString(value.substring(2));
        } else {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                boolean isDigit = (c >= '0' && c <= '9');
                boolean isUpperCaseHex = (c >= 'A' && c <= 'F');
                boolean isLowerCaseHex = (c >= 'a' && c <= 'f');
                if (!isDigit && !isUpperCaseHex && !isLowerCaseHex) {
                    return EasyStringUtils.quoteString(dataValue.getBlobHexString());
                }
            }
            return EasyStringUtils.quoteString(value);
        }
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getBlobString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return EasyStringUtils.quoteString(dataValue.getBlobHexString());
    }
}
