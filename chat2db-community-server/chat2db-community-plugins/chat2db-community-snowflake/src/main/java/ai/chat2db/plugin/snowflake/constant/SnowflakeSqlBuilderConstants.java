package ai.chat2db.plugin.snowflake.constant;

import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.plugin.snowflake.enums.type.SnowflakeColumnTypeEnum;
import ai.chat2db.plugin.snowflake.enums.type.SnowflakeIndexTypeEnum;
import ai.chat2db.spi.DefaultSqlBuilder;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import org.apache.commons.lang3.StringUtils;


public final class SnowflakeSqlBuilderConstants {

    public static final String VALUE_LOCAL_SQL_PART = " \n";
    public static final String SQL_AUTO_INCREMENT_EQUAL = "AUTO_INCREMENT=";
    public static final String SQL_ALTER_TABLE = "ALTER TABLE ";
    public static final String SQL_COMMENT = " COMMENT='";
    public static final String SQL_CREATE_TABLE = "CREATE TABLE ";
    public static final String SQL_RENAME = "RENAME TO ";
    public static final String SQL_SET_COMMENT = "set COMMENT=";

    private SnowflakeSqlBuilderConstants() {
    }
}
