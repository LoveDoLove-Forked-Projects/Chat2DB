package ai.chat2db.plugin.db2.constant;

import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.IndexType;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.model.metadata.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;


public final class DB2IndexTypeEnumConstants {

    public static final String SQL_ALTER_TABLE = "ALTER TABLE ";
    public static final String SQL_ALTER_TABLE_2 = "ALTER TABLE \"";
    public static final String SQL_COMMENT_CONSTRAINT = "COMMENT ON CONSTRAINT";
    public static final String SQL_COMMENT_INDEX = "COMMENT ON INDEX";
    public static final String SQL_CREATE_INDEX = "CREATE INDEX ";
    public static final String SQL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX ";
    public static final String SQL_DROP_INDEX = "DROP INDEX ";
    public static final String SQL_DROP_PRIMARY_KEY = " DROP PRIMARY KEY";
    public static final String SQL_ON = " ON \"";

    private DB2IndexTypeEnumConstants() {
    }
}
