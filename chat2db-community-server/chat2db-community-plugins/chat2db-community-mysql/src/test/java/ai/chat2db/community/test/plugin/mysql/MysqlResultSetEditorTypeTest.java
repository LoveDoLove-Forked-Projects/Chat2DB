package ai.chat2db.community.test.plugin.mysql;

import ai.chat2db.plugin.mysql.MysqlMetaData;
import ai.chat2db.community.domain.api.enums.plugin.ResultSetEditorTypeEnum;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MysqlResultSetEditorTypeTest {

    private final MysqlMetaData mysqlMetaData = new MysqlMetaData();

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
}
