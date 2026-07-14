import ai.chat2db.spi.DefaultSqlSyntaxHandler;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertValueMapping;
import ai.chat2db.community.domain.api.enums.parser.InsertValueMappingStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OracleDMLTest {

    @Test
    public void testInsertValueMappings() {
        String sql = """
                insert into user_info (
                    id,
                    name,
                    age,
                    created_at
                ) values (
                    1,
                    concat('A', 'B'),
                    18,
                    to_date('2026-05-19', 'YYYY-MM-DD')
                );
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.ORACLE);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(4, insertValueMappings.size());

        InsertValueMapping nameMapping = insertValueMappings.get(1);
        Assertions.assertEquals("name", nameMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("name", nameMapping.getColumnLastToken().getText());
        Assertions.assertEquals("concat", nameMapping.getValueFirstToken().getText());
        Assertions.assertEquals(")", nameMapping.getValueLastToken().getText());
        Assertions.assertEquals("(", nameMapping.getRowFirstToken().getText());
        Assertions.assertEquals(")", nameMapping.getRowLastToken().getText());
        Assertions.assertEquals(0, nameMapping.getRowIndex());
        Assertions.assertEquals(1, nameMapping.getColumnIndex());

        InsertValueMapping createdAtMapping = insertValueMappings.get(3);
        Assertions.assertEquals("created_at", createdAtMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("to_date", createdAtMapping.getValueFirstToken().getText());
        Assertions.assertEquals(")", createdAtMapping.getValueLastToken().getText());
        Assertions.assertEquals(0, createdAtMapping.getRowIndex());
        Assertions.assertEquals(3, createdAtMapping.getColumnIndex());
    }

    @Test
    public void testInsertValueMappingsReturnsUnmappedColumns() {
        String sql = """
                insert into user_info (id, name, age, email, phone) values (
                    1,
                    'Tom',
                    18,
                    'tom@example.com'
                );
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.ORACLE);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(5, insertValueMappings.size());

        InsertValueMapping unmappedColumn = insertValueMappings.get(4);
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_COLUMN, unmappedColumn.getMappingStatus());
        Assertions.assertEquals("phone", unmappedColumn.getColumnFirstToken().getText());
        Assertions.assertNull(unmappedColumn.getValueFirstToken());
        Assertions.assertEquals("(", unmappedColumn.getRowFirstToken().getText());
        Assertions.assertEquals(")", unmappedColumn.getRowLastToken().getText());
        Assertions.assertEquals(0, unmappedColumn.getRowIndex());
        Assertions.assertEquals(4, unmappedColumn.getColumnIndex());
    }

    @Test
    public void testInsertValueMappingsReturnsUnmappedValues() {
        String sql = """
                insert into user_info (id, name, age, email, phone) values (
                    1,
                    'Tom',
                    18,
                    'tom@example.com',
                    '188',
                    'extra1',
                    'extra2'
                );
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.ORACLE);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(7, insertValueMappings.size());

        InsertValueMapping firstUnmappedValue = insertValueMappings.get(5);
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, firstUnmappedValue.getMappingStatus());
        Assertions.assertNull(firstUnmappedValue.getColumnFirstToken());
        Assertions.assertEquals("'extra1'", firstUnmappedValue.getValueFirstToken().getText());
        Assertions.assertEquals("(", firstUnmappedValue.getRowFirstToken().getText());
        Assertions.assertEquals(")", firstUnmappedValue.getRowLastToken().getText());
        Assertions.assertEquals(0, firstUnmappedValue.getRowIndex());
        Assertions.assertEquals(5, firstUnmappedValue.getColumnIndex());

        InsertValueMapping secondUnmappedValue = insertValueMappings.get(6);
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, secondUnmappedValue.getMappingStatus());
        Assertions.assertEquals("'extra2'", secondUnmappedValue.getValueFirstToken().getText());
        Assertions.assertEquals(0, secondUnmappedValue.getRowIndex());
        Assertions.assertEquals(6, secondUnmappedValue.getColumnIndex());
    }

    @Test
    public void testInsertValueMappingsDoesNotReturnMappingsWhenEmptyValuesRowIsNotParsed() {
        String sql = "insert into user_info (id, name, age) values ();";
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.ORACLE);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(0, insertValueMappings.size());
    }

    @Test
    public void testSimpleInsertValueMappingsReturnsUnmappedValues() {
        String sql = """
                insert into user_info (id, name, age, email, phone) values (
                    1,
                    'Tom',
                    18,
                    'tom@example.com',
                    '188',
                    'extra1',
                    'extra2'
                );
                """;
        List<Statement> statements = DefaultSqlSyntaxHandler.simpleParserStatements(sql, DatabaseTypeEnum.ORACLE.name());
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(7, insertValueMappings.size());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(5).getMappingStatus());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(6).getMappingStatus());
    }

}
