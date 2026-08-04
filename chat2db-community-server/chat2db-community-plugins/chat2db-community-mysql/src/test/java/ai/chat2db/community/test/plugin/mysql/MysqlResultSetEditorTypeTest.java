package ai.chat2db.community.test.plugin.mysql;

import ai.chat2db.plugin.mysql.MysqlMetaData;
import ai.chat2db.plugin.mysql.MysqlPlugin;
import ai.chat2db.community.domain.api.enums.plugin.ResultSetEditorTypeEnum;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.result.ResultSetEditorMetadata;
import ai.chat2db.spi.IDbMetaData;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MysqlResultSetEditorTypeTest {

    private final MysqlMetaData mysqlMetaData = new MysqlMetaData();
    private final IDbMetaData mysqlPluginMetaData = new MysqlPlugin().getDbMetaData();

    @Test
    void resolvesMysqlTemporalTypesFromTypeName() {
        assertEquals(ResultSetEditorTypeEnum.DATE,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("DATE", Types.DATE)));
        assertEquals(ResultSetEditorTypeEnum.TIME,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("TIME", Types.TIME)));
        assertEquals(ResultSetEditorTypeEnum.DATETIME,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("DATETIME", Types.TIMESTAMP)));
        assertEquals(ResultSetEditorTypeEnum.TIMESTAMP,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("TIMESTAMP", Types.TIMESTAMP)));
        assertEquals(ResultSetEditorTypeEnum.DATETIME,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("datetime(6)", Types.TIMESTAMP)));
        assertEquals(ResultSetEditorTypeEnum.TIMESTAMP,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("timestamp(6)", Types.TIMESTAMP)));
    }

    @Test
    void fallsBackToTextForOtherMysqlTypes() {
        assertEquals(ResultSetEditorTypeEnum.TEXT,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("VARCHAR", Types.VARCHAR)));
        assertEquals(ResultSetEditorTypeEnum.TEXT,
                ResultSetEditorTypeEnum.from(mysqlMetaData.resolveResultSetEditorType("DATETIMEOFFSET", null)));
    }

    @Test
    void resolvesMysqlEnumOptionsFromColumnMetadata() {
        TableColumn column = TableColumn.builder()
                .name("status")
                .columnType("ENUM")
                .dataType(Types.VARCHAR)
                .value("'draft','needs,review','can\\'t','a\\\\b','(nested)',''")
                .build();

        ResultSetEditorMetadata metadata = mysqlPluginMetaData.resolveResultSetEditorMetadata(column);

        assertEquals(ResultSetEditorTypeEnum.SELECT.getCode(), metadata.getEditorType());
        assertEquals(List.of("draft", "needs,review", "can't", "a\\b", "(nested)", ""),
                metadata.getEditorOptions().stream().map(option -> option.getValue()).toList());
        assertEquals(metadata.getEditorOptions().stream().map(option -> option.getValue()).toList(),
                metadata.getEditorOptions().stream().map(option -> option.getLabel()).toList());
    }

    @Test
    void leavesSetNonEnumAndMalformedEnumOnTheirExistingEditorType() {
        TableColumn setColumn = TableColumn.builder()
                .columnType("SET")
                .dataType(Types.VARCHAR)
                .value("'one','two'")
                .build();
        TableColumn varcharColumn = TableColumn.builder()
                .columnType("VARCHAR")
                .dataType(Types.VARCHAR)
                .build();
        TableColumn malformedEnum = TableColumn.builder()
                .name("broken_status")
                .columnType("ENUM")
                .dataType(Types.VARCHAR)
                .value("'one','two")
                .build();

        assertEquals(ResultSetEditorTypeEnum.TEXT.getCode(),
                mysqlPluginMetaData.resolveResultSetEditorMetadata(setColumn).getEditorType());
        assertEquals(ResultSetEditorTypeEnum.TEXT.getCode(),
                mysqlPluginMetaData.resolveResultSetEditorMetadata(varcharColumn).getEditorType());
        ResultSetEditorMetadata malformedMetadata = mysqlPluginMetaData.resolveResultSetEditorMetadata(malformedEnum);
        assertEquals(ResultSetEditorTypeEnum.TEXT.getCode(), malformedMetadata.getEditorType());
        assertEquals(List.of(), malformedMetadata.getEditorOptions());
    }

    @Test
    void mysqlPluginOptsInButMysqlCompatibilitySubclassesRemainDisabled() {
        MysqlMetaData compatibilityDialect = new MysqlMetaData() {
        };
        TableColumn enumColumn = TableColumn.builder()
                .columnType("ENUM")
                .dataType(Types.VARCHAR)
                .value("'one','two'")
                .build();

        assertTrue(mysqlPluginMetaData.supportsResultSetEditorOptions());
        assertFalse(compatibilityDialect.supportsResultSetEditorOptions());
        assertEquals(ResultSetEditorTypeEnum.TEXT.getCode(),
                compatibilityDialect.resolveResultSetEditorMetadata(enumColumn).getEditorType());
        assertEquals(List.of(), compatibilityDialect.resolveResultSetEditorMetadata(enumColumn).getEditorOptions());
    }
}
