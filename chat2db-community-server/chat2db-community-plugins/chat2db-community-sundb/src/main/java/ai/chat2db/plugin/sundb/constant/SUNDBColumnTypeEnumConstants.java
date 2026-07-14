package ai.chat2db.plugin.sundb.constant;

import ai.chat2db.spi.IColumnBuilder;
import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.ColumnType;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public final class SUNDBColumnTypeEnumConstants {

    public static final String SQL_ALTER_COLUMN = "ALTER COLUMN ";
    public static final String SQL_ALTER_TABLE = "ALTER TABLE ";
    public static final String SQL_COMMENT = "COMMENT '";
    public static final String SQL_RENAME_COLUMN = "RENAME COLUMN ";
    public static final String SQL_SET_UNUSED_COLUMN = "SET UNUSED COLUMN ";

    private SUNDBColumnTypeEnumConstants() {
    }
}
