package ai.chat2db.plugin.oceanbase.constant;

import ai.chat2db.plugin.oracle.OracleMetaData;
import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public final class OceanbaseOracleMetaDataConstants {

    public static final String TABLE_DDL_SQL = "select dbms_metadata.get_ddl('TABLE','%s','%s') as sql from dual";
    public static final String TABLE_COMMENT_SQL = "select owner, table_name, comments from ALL_TAB_COMMENTS where OWNER = '%s'  and TABLE_NAME = '%s'";
    public static final String TABLE_COLUMN_COMMENT_SQL = """
                                                           SELECT owner, table_name, column_name, comments
                                                           FROM all_col_comments
                                                           WHERE  owner = '%s' and table_name = '%s' and comments is not null""";
    public static final String TABLE_INDEX_DDL_SQL = """
                                                        SELECT DBMS_METADATA.GET_DDL('INDEX', '%s', '%s') AS ddl FROM dual
                                                      """;
    public static final String TABLE_INDEX_NAME_SQL = """
                                                      select
                                                        INDEX_NAME
                                                      from
                                                        SYS.ALL_INDEXES
                                                      where
                                                        OWNER = '%s'
                                                        and TABLE_NAME = '%s'
                                                      """;
    public static final String PU_INDEX_NAME_SQL = """
                                                    SELECT DISTINCT AC.INDEX_NAME
                                                    FROM ALL_CONSTRAINTS AC
                                                    WHERE  AC.OWNER = '%s' AND AC.TABLE_NAME = '%s'
                                                      AND AC.CONSTRAINT_TYPE IN ('P','U')""";

    private OceanbaseOracleMetaDataConstants() {
    }
}
