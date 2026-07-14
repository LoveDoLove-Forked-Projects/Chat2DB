package ai.chat2db.plugin.snowflake.constant;

import ai.chat2db.spi.IColumnBuilder;
import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.ColumnType;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public final class SnowflakeColumnTypeEnumConstants {

    public static final String SQL_COMMENT = "COMMENT '";
    public static final String SQL_DROP_COLUMN = "DROP COLUMN ";
    public static final String SQL_SET_DEFAULT = "SET DEFAULT ";
    public static final String SQL_SET_DEFAULT_2 = "SET DEFAULT '";
    public static final String SQL_SET_DEFAULT_3 = "SET DEFAULT ''";
    public static final String SQL_SET_DEFAULT_NULL = "SET DEFAULT NULL";

    private SnowflakeColumnTypeEnumConstants() {
    }
}
