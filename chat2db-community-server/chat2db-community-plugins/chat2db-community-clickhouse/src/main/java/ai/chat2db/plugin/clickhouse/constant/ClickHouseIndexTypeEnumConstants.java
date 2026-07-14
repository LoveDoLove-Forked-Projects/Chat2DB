package ai.chat2db.plugin.clickhouse.constant;

import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.IndexType;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.model.metadata.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;


public final class ClickHouseIndexTypeEnumConstants {

    public static final String SQL_DROP_INDEX = "DROP INDEX `";

    private ClickHouseIndexTypeEnumConstants() {
    }
}
