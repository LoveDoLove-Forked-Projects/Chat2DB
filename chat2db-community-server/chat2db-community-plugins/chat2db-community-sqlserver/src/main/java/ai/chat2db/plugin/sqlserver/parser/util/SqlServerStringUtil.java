package ai.chat2db.plugin.sqlserver.parser.util;

import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.spi.util.SqlStringUtil;

public class SqlServerStringUtil {
    public static String removeQuote(String sql) {
        return SqlStringUtil.removeQuote(sql, DatabaseTypeEnum.SQLSERVER.name());
    }
}
