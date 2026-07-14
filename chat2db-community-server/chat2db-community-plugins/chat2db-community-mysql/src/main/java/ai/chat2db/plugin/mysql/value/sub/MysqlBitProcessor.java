package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.plugin.mysql.value.template.MysqlDmlValueTemplate;
import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Function;


public class MysqlBitProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        String value = dataValue.getValue().toLowerCase();
        if (Objects.equals("true", value.toLowerCase())) {
            return MysqlDmlValueTemplate.wrapBit("1");
        }
        if (Objects.equals("false", value.toLowerCase())) {
            return MysqlDmlValueTemplate.wrapBit("0");
        }
        if (StringUtils.isBlank(value)) {
            return "NULL";
        }
        return MysqlDmlValueTemplate.wrapBit(value);
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return getValue(dataValue, s -> s);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return getValue(dataValue, this::wrap);
    }

    private String getValue(JDBCDataValue dataValue, Function<String, String> function) {
        int precision = dataValue.getPrecision();
        if (precision == 1) {
            return String.valueOf(dataValue.getInt());
        }
        byte[] bytes = dataValue.getBytes();
        return function.apply(EasyStringUtils.getBitString(bytes, precision));
    }

    private String wrap(String value) {
        return MysqlDmlValueTemplate.wrapBit(value);
    }
}
