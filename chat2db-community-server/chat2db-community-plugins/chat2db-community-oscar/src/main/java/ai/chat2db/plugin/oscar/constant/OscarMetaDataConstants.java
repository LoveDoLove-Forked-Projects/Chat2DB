package ai.chat2db.plugin.oscar.constant;

import ai.chat2db.plugin.oscar.builder.OscarSqlBuilder;
import ai.chat2db.plugin.oscar.constant.OscarConstants;
import ai.chat2db.plugin.oscar.enums.type.OscarColumnTypeEnum;
import ai.chat2db.plugin.oscar.enums.type.OscarDefaultValueEnum;
import ai.chat2db.plugin.oscar.enums.type.OscarIndexTypeEnum;
import ai.chat2db.plugin.oscar.enums.type.OscarObjectTypeEnum;
import ai.chat2db.plugin.oscar.enums.type.OscarTypeAliasEnum;
import ai.chat2db.spi.ISqlBuilder;
import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.community.domain.api.model.form.FormConfig;
import ai.chat2db.community.domain.api.model.metadata.Function;
import ai.chat2db.community.domain.api.model.view.ModifyViewConfiguration;
import ai.chat2db.community.domain.api.model.metadata.PrimaryKey;
import ai.chat2db.community.domain.api.model.metadata.Procedure;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.model.metadata.TableIndexColumn;
import ai.chat2db.community.domain.api.model.metadata.TableMeta;
import ai.chat2db.community.domain.api.model.metadata.Trigger;
import ai.chat2db.spi.DefaultSQLExecutor;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ai.chat2db.plugin.oscar.enums.type.OscarSystemSchemaEnum;

public final class OscarMetaDataConstants {

    public static final String TABLE_TYPE = "TABLE";
    public static final String SYSTEM_TABLE_TYPE = "SYSTEM TABLE";
    public static final int INVALID_FRACTIONAL_DIGITS = -1;
    public static final List<String> SYSTEM_SCHEMAS = OscarSystemSchemaEnum.schemaNames();

    private OscarMetaDataConstants() {
    }
}
