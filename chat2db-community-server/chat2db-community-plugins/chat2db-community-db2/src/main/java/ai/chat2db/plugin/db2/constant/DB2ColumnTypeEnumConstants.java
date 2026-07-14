package ai.chat2db.plugin.db2.constant;

import ai.chat2db.spi.IColumnBuilder;
import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.ColumnType;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.spi.util.SqlUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public final class DB2ColumnTypeEnumConstants {

    public static final String SQL_ALTER_TABLE = "ALTER TABLE ";
    public static final String SQL_DROP_COLUMN = "DROP COLUMN ";
    public static final String SQL_RENAME_COLUMN = "RENAME COLUMN ";

    private DB2ColumnTypeEnumConstants() {
    }
}
