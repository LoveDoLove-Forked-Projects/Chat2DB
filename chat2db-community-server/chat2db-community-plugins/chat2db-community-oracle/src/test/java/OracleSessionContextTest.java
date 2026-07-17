import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.spi.DefaultSqlSyntaxHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class OracleSessionContextTest {

    @Test
    void currentSchemaIsTheOnlyAlterSessionSettingClassifiedAsSchemaSwitch() {
        assertSchemaSwitch(DefaultSqlSyntaxHandler.parserStatements(
                "ALTER SESSION SET CURRENT_SCHEMA = APP", DatabaseTypeEnum.ORACLE).getStatements());
        assertSchemaSwitch(DefaultSqlSyntaxHandler.simpleParserStatements(
                "ALTER SESSION SET CURRENT_SCHEMA = APP", DatabaseTypeEnum.ORACLE.name()));

        assertNotSchemaSwitch(DefaultSqlSyntaxHandler.parserStatements(
                "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'", DatabaseTypeEnum.ORACLE).getStatements());
        assertNotSchemaSwitch(DefaultSqlSyntaxHandler.simpleParserStatements(
                "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'", DatabaseTypeEnum.ORACLE.name()));
    }

    private static void assertSchemaSwitch(List<Statement> statements) {
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(SqlTypeEnum.SET_SCHEMA.name(), statement.getType());
        assertEquals(1, statement.getIdentifiers().size());
        String schemaName = statement.getIdentifiers().get(0).getIdentifierSchema();
        if (schemaName == null) {
            schemaName = statement.getIdentifiers().get(0).getIdentifierName();
        }
        assertEquals("APP", schemaName);
    }

    private static void assertNotSchemaSwitch(List<Statement> statements) {
        assertEquals(1, statements.size());
        assertNotEquals(SqlTypeEnum.SET_SCHEMA.name(), statements.get(0).getType());
    }
}
