package ai.chat2db.plugin.sqlite.constant;

import ai.chat2db.spi.constant.SQLConstants;

import ai.chat2db.plugin.sqlite.SqliteMetaData;
import ai.chat2db.plugin.sqlite.enums.type.SqliteColumnTypeEnum;
import ai.chat2db.plugin.sqlite.enums.type.SqliteIndexTypeEnum;
import ai.chat2db.spi.ISQLIdentifierProcessor;
import ai.chat2db.spi.DefaultSqlBuilder;
import ai.chat2db.spi.model.request.PageLimitRequest;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import org.apache.commons.lang3.StringUtils;



public final class SqliteBuilderConstants {

    public static final String VALUE = " -- ";
    public static final String SQL_SELECT_ASTERISK_FROM_OPEN_PAREN = "select * from(";
    public static final String VALUE_CLOSE_PAREN_T_LIMIT = ") t LIMIT ";
    public static final String SQL_ALTER_TABLE = "ALTER TABLE ";
    public static final String SQL_CREATE = "CREATE ";
    public static final String SQL_CREATE_TABLE = "CREATE TABLE ";
    public static final String SQL_RENAME = "RENAME TO ";

    private SqliteBuilderConstants() {
    }
}
