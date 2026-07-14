package test;

import ai.chat2db.spi.DefaultSqlSyntaxHandler;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertValueMapping;
import ai.chat2db.community.domain.api.enums.parser.InsertValueMappingStatusEnum;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

public class PgsqlInsertTest {

    @Test
    public void testInsertValueMappings() {
        String sql = """
                insert into user_info (
                    id,
                    name,
                    age
                ) values
                    (1, 'Tom', 18),
                    (2, concat('A', 'B'), 20);
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.POSTGRESQL);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(6, insertValueMappings.size());

        InsertValueMapping firstNameMapping = insertValueMappings.get(1);
        Assertions.assertEquals("name", firstNameMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("name", firstNameMapping.getColumnLastToken().getText());
        Assertions.assertEquals("'Tom'", firstNameMapping.getValueFirstToken().getText());
        Assertions.assertEquals("'Tom'", firstNameMapping.getValueLastToken().getText());
        Assertions.assertEquals("(", firstNameMapping.getRowFirstToken().getText());
        Assertions.assertEquals(")", firstNameMapping.getRowLastToken().getText());
        Assertions.assertEquals(0, firstNameMapping.getRowIndex());
        Assertions.assertEquals(1, firstNameMapping.getColumnIndex());

        InsertValueMapping secondNameMapping = insertValueMappings.get(4);
        Assertions.assertEquals("name", secondNameMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("concat", secondNameMapping.getValueFirstToken().getText());
        Assertions.assertEquals(")", secondNameMapping.getValueLastToken().getText());
        Assertions.assertEquals("(", secondNameMapping.getRowFirstToken().getText());
        Assertions.assertEquals(")", secondNameMapping.getRowLastToken().getText());
        Assertions.assertEquals(1, secondNameMapping.getRowIndex());
        Assertions.assertEquals(1, secondNameMapping.getColumnIndex());
    }

    @Test
    public void testInsertSelectDoesNotProduceValueMappings() {
        String sql = "insert into user_info (id, name) select id, name from old_user_info;";
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.POSTGRESQL);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        Statement statement = statements.get(0);
        Assertions.assertEquals(0, statement.getInsertValueMappings().size());
        Assertions.assertTrue(hasTableIdentifier(statement, "user_info"));
    }

    @Test
    public void testInsertOnConflictProducesValueMappings() {
        String sql = """
                insert into user_info (id, name) values (1, 'Tom')
                on conflict (id) do nothing;
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.POSTGRESQL);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        Statement statement = statements.get(0);
        List<InsertValueMapping> insertValueMappings = statement.getInsertValueMappings();
        Assertions.assertEquals(2, insertValueMappings.size());
        Assertions.assertEquals("id", insertValueMappings.get(0).getColumnFirstToken().getText());
        Assertions.assertEquals("1", insertValueMappings.get(0).getValueFirstToken().getText());
        Assertions.assertEquals("name", insertValueMappings.get(1).getColumnFirstToken().getText());
        Assertions.assertEquals("'Tom'", insertValueMappings.get(1).getValueFirstToken().getText());
        Assertions.assertTrue(hasTableIdentifier(statement, "user_info"));
    }

    @Test
    public void testInsertValueMappingsReturnsUnmappedColumnsAndValues() {
        String sql = """
                insert into user_info (id, name, age, email, phone) values
                    (1, 'Tom', 18, 'tom@example.com'),
                    (2, 'Jerry', 20, 'jerry@example.com', '188', 'extra1', 'extra2');
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.POSTGRESQL);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(12, insertValueMappings.size());

        InsertValueMapping unmappedColumn = insertValueMappings.get(4);
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_COLUMN, unmappedColumn.getMappingStatus());
        Assertions.assertEquals("phone", unmappedColumn.getColumnFirstToken().getText());
        Assertions.assertNull(unmappedColumn.getValueFirstToken());
        Assertions.assertEquals("(", unmappedColumn.getRowFirstToken().getText());
        Assertions.assertEquals(")", unmappedColumn.getRowLastToken().getText());
        Assertions.assertEquals(0, unmappedColumn.getRowIndex());
        Assertions.assertEquals(4, unmappedColumn.getColumnIndex());

        InsertValueMapping firstUnmappedValue = insertValueMappings.get(10);
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, firstUnmappedValue.getMappingStatus());
        Assertions.assertNull(firstUnmappedValue.getColumnFirstToken());
        Assertions.assertEquals("'extra1'", firstUnmappedValue.getValueFirstToken().getText());
        Assertions.assertEquals("(", firstUnmappedValue.getRowFirstToken().getText());
        Assertions.assertEquals(")", firstUnmappedValue.getRowLastToken().getText());
        Assertions.assertEquals(1, firstUnmappedValue.getRowIndex());
        Assertions.assertEquals(5, firstUnmappedValue.getColumnIndex());

        InsertValueMapping secondUnmappedValue = insertValueMappings.get(11);
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, secondUnmappedValue.getMappingStatus());
        Assertions.assertEquals("'extra2'", secondUnmappedValue.getValueFirstToken().getText());
        Assertions.assertEquals(1, secondUnmappedValue.getRowIndex());
        Assertions.assertEquals(6, secondUnmappedValue.getColumnIndex());
    }

    @Test
    public void testInsertValueMappingsDoesNotReturnMappingsWhenEmptyValuesRowIsNotParsed() {
        String sql = "insert into user_info (id, name, age) values ();";
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.POSTGRESQL);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(0, insertValueMappings.size());
    }

    @Test
    public void testSimpleInsertValueMappingsReturnsUnmappedColumnsAndValues() {
        String sql = """
                insert into user_info (id, name, age, email, phone) values
                    (1, 'Tom', 18, 'tom@example.com'),
                    (2, 'Jerry', 20, 'jerry@example.com', '188', 'extra1', 'extra2');
                """;
        List<Statement> statements = DefaultSqlSyntaxHandler.simpleParserStatements(sql, DatabaseTypeEnum.POSTGRESQL.name());
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(12, insertValueMappings.size());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_COLUMN, insertValueMappings.get(4).getMappingStatus());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(10).getMappingStatus());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(11).getMappingStatus());
    }

    private boolean hasTableIdentifier(Statement statement, String tableName) {
        return statement.getIdentifiers().stream()
                .filter(identifier -> Objects.equals(IdentifierTypeEnum.TABLE.name(), identifier.getIdentifierType()))
                .map(Identifier::getIdentifierName)
                .anyMatch(tableName::equals);
    }
}
