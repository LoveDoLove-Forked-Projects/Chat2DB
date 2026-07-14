package ai.chat2db.plugin.mysql.parser.util;

import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.spi.util.SqlStringUtil;

public class MysqlStringUtil {

    public static String removeQuote(String text) {
        return SqlStringUtil.removeQuote(text, DatabaseTypeEnum.MYSQL.name());
    }

    public static String removeQuoteAndEscape(String text) {
        return SqlStringUtil.removeQuoteAndEscape(text, DatabaseTypeEnum.MYSQL.name());
    }
}
