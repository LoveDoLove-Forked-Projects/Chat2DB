import ai.chat2db.spi.DefaultSqlSyntaxHandler;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertValueMapping;
import ai.chat2db.community.domain.api.enums.parser.InsertValueMappingStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SqlServerDMLStatementTest {

    @Test
    public void testInsertValueMappings() {
        String sql = """
                insert into dbo.user_info (
                    id,
                    [name],
                    age
                ) values
                    (1, N'Tom', 18),
                    (2, CONCAT(N'A', N'B'), 20);
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.SQLSERVER);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(6, insertValueMappings.size());

        InsertValueMapping firstIdMapping = insertValueMappings.get(0);
        Assertions.assertEquals("id", firstIdMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("id", firstIdMapping.getColumnLastToken().getText());
        Assertions.assertEquals("1", firstIdMapping.getValueFirstToken().getText());
        Assertions.assertEquals("1", firstIdMapping.getValueLastToken().getText());
        Assertions.assertEquals("(", firstIdMapping.getRowFirstToken().getText());
        Assertions.assertEquals(")", firstIdMapping.getRowLastToken().getText());
        Assertions.assertEquals(0, firstIdMapping.getRowIndex());
        Assertions.assertEquals(0, firstIdMapping.getColumnIndex());

        InsertValueMapping firstNameMapping = insertValueMappings.get(1);
        Assertions.assertEquals("[name]", firstNameMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("[name]", firstNameMapping.getColumnLastToken().getText());
        Assertions.assertEquals("N'Tom'", firstNameMapping.getValueFirstToken().getText());
        Assertions.assertEquals("N'Tom'", firstNameMapping.getValueLastToken().getText());
        Assertions.assertEquals("(", firstNameMapping.getRowFirstToken().getText());
        Assertions.assertEquals(")", firstNameMapping.getRowLastToken().getText());
        Assertions.assertEquals(0, firstNameMapping.getRowIndex());
        Assertions.assertEquals(1, firstNameMapping.getColumnIndex());

        InsertValueMapping secondNameMapping = insertValueMappings.get(4);
        Assertions.assertEquals("[name]", secondNameMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("[name]", secondNameMapping.getColumnLastToken().getText());
        Assertions.assertEquals("CONCAT", secondNameMapping.getValueFirstToken().getText());
        Assertions.assertEquals(")", secondNameMapping.getValueLastToken().getText());
        Assertions.assertEquals("(", secondNameMapping.getRowFirstToken().getText());
        Assertions.assertEquals(")", secondNameMapping.getRowLastToken().getText());
        Assertions.assertEquals(1, secondNameMapping.getRowIndex());
        Assertions.assertEquals(1, secondNameMapping.getColumnIndex());

        InsertValueMapping secondAgeMapping = insertValueMappings.get(5);
        Assertions.assertEquals("age", secondAgeMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("20", secondAgeMapping.getValueFirstToken().getText());
        Assertions.assertEquals(1, secondAgeMapping.getRowIndex());
        Assertions.assertEquals(2, secondAgeMapping.getColumnIndex());
    }

    @Test
    public void testInsertValueMappingsReturnsUnmappedColumnsAndValues() {
        String sql = """
                insert into dbo.user_info (id, [name], age, email, phone) values
                    (1, N'Tom', 18, N'tom@example.com'),
                    (2, N'Jerry', 20, N'jerry@example.com', N'188', N'extra1', N'extra2');
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.SQLSERVER);
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
        Assertions.assertEquals("N'extra1'", firstUnmappedValue.getValueFirstToken().getText());
        Assertions.assertEquals("(", firstUnmappedValue.getRowFirstToken().getText());
        Assertions.assertEquals(")", firstUnmappedValue.getRowLastToken().getText());
        Assertions.assertEquals(1, firstUnmappedValue.getRowIndex());
        Assertions.assertEquals(5, firstUnmappedValue.getColumnIndex());

        InsertValueMapping secondUnmappedValue = insertValueMappings.get(11);
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, secondUnmappedValue.getMappingStatus());
        Assertions.assertEquals("N'extra2'", secondUnmappedValue.getValueFirstToken().getText());
        Assertions.assertEquals(1, secondUnmappedValue.getRowIndex());
        Assertions.assertEquals(6, secondUnmappedValue.getColumnIndex());
    }

    @Test
    public void testInsertValueMappingsDoesNotReturnMappingsWhenEmptyValuesRowIsNotParsed() {
        String sql = "insert into dbo.user_info (id, [name], age) values ();";
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.SQLSERVER);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(0, insertValueMappings.size());
    }

    @Test
    public void testSimpleInsertValueMappingsReturnsUnmappedColumnsAndValues() {
        String sql = """
                insert into dbo.user_info (id, [name], age, email, phone) values
                    (1, N'Tom', 18, N'tom@example.com'),
                    (2, N'Jerry', 20, N'jerry@example.com', N'188', N'extra1', N'extra2');
                """;
        List<Statement> statements = DefaultSqlSyntaxHandler.simpleParserStatements(sql, DatabaseTypeEnum.SQLSERVER.name());
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(12, insertValueMappings.size());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_COLUMN, insertValueMappings.get(4).getMappingStatus());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(10).getMappingStatus());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(11).getMappingStatus());
    }

}
