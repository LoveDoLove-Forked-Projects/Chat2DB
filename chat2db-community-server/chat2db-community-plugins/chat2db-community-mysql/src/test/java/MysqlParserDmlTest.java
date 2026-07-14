import ai.chat2db.spi.DefaultSqlSyntaxHandler;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertValueMapping;
import ai.chat2db.community.domain.api.enums.parser.InsertValueMappingStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MysqlParserDmlTest {

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
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.MYSQL);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(6, insertValueMappings.size());

        InsertValueMapping firstNameMapping = insertValueMappings.get(1);
        Assertions.assertEquals("name", firstNameMapping.getColumnFirstToken().getText());
        Assertions.assertEquals("'Tom'", firstNameMapping.getValueFirstToken().getText());
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
    public void testInsertValueMappingsReturnsUnmappedColumnsAndValues() {
        String sql = """
                insert into user_info (id, name, age, email, phone) values
                    (1, 'Tom', 18, 'tom@example.com'),
                    (2, 'Jerry', 20, 'jerry@example.com', '188', 'extra1', 'extra2');
                """;
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.MYSQL);
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
    public void testInsertValueMappingsReturnsUnmappedColumnsForEmptyValuesRow() {
        String sql = "insert into user_info (id, name, age) values ();";
        SqlParserResponse sqlParserResult = DefaultSqlSyntaxHandler.parserStatements(sql, DatabaseTypeEnum.MYSQL);
        List<Statement> statements = sqlParserResult.getStatements();
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(3, insertValueMappings.size());
        for (int i = 0; i < insertValueMappings.size(); i++) {
            InsertValueMapping mapping = insertValueMappings.get(i);
            Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_COLUMN, mapping.getMappingStatus());
            Assertions.assertNull(mapping.getValueFirstToken());
            Assertions.assertEquals(0, mapping.getRowIndex());
            Assertions.assertEquals(i, mapping.getColumnIndex());
            Assertions.assertEquals("(", mapping.getRowFirstToken().getText());
            Assertions.assertEquals(")", mapping.getRowLastToken().getText());
        }
    }

    @Test
    public void testSimpleInsertValueMappingsReturnsUnmappedColumnsAndValues() {
        String sql = """
                insert into user_info (id, name, age, email, phone) values
                    (1, 'Tom', 18, 'tom@example.com'),
                    (2, 'Jerry', 20, 'jerry@example.com', '188', 'extra1', 'extra2');
                """;
        List<Statement> statements = DefaultSqlSyntaxHandler.simpleParserStatements(sql, DatabaseTypeEnum.MYSQL.name());
        Assertions.assertEquals(1, statements.size());

        List<InsertValueMapping> insertValueMappings = statements.get(0).getInsertValueMappings();
        Assertions.assertEquals(12, insertValueMappings.size());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_COLUMN, insertValueMappings.get(4).getMappingStatus());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(10).getMappingStatus());
        Assertions.assertEquals(InsertValueMappingStatusEnum.UNMAPPED_VALUE, insertValueMappings.get(11).getMappingStatus());
    }
}
