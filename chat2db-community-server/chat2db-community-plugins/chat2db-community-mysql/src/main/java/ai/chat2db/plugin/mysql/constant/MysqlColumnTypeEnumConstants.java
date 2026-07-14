package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.spi.IColumnBuilder;
import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.ColumnType;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.spi.util.SqlUtils;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



public final class MysqlColumnTypeEnumConstants {

    public static final int DEFAULT_DECIMAL_COLUMN_SIZE = 10;

    private MysqlColumnTypeEnumConstants() {
    }
}
