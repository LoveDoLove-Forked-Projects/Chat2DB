package ai.chat2db.community.start.test.sqlUtil;

import ai.chat2db.spi.DefaultSqlSyntaxHandler;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.model.parser.info.ColumnInfo;
import ai.chat2db.community.domain.api.model.parser.info.TableInfo;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.util.SqlUtils;
import com.alibaba.druid.DbType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SqlUtilTest {

    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";

    @Test
    void mysqlTestValidTable() {
        String sql = """
                 select * from test_all_column;
                select test.test_all_column.* from test_all_column;
                select A.a,b as B from test.abcd as A;
                select test.class.size , position as P from test.class;
                             SELECT u.id, u.name, o.order_id, o.amount
                             FROM users u
                             INNER JOIN orders o ON u.id = o.user_id
                             WHERE o.amount > 100;

                             SELECT u.id, u.name, o.total
                                          FROM users u, LATERAL (
                                              SELECT SUM(amount) AS total FROM orders WHERE orders.user_id = u.id
                                          ) o;

                                          select co(a(t)) as b from a;
                             """;

        List<Statement> statements = DefaultSqlSyntaxHandler.validTableStatements(sql, DatabaseTypeEnum.MYSQL.name());
        assertNotNull(statements);
        for (Statement statement : statements) {
            System.out.println(CYAN + "SQL: " + RESET + statement.getSql());
            System.out.println(GREEN + "SQL Type: " + RESET + statement.getType());
            Map<TableInfo, String> tableAliasMap = statement.getTableAliasMap();
            System.out.println("====================================table==========================================");
            for (Map.Entry<TableInfo, String> tableInfoAliasEntry : tableAliasMap.entrySet()) {
                TableInfo key = tableInfoAliasEntry.getKey();
                String value = tableInfoAliasEntry.getValue();
                System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                System.out.println("  " + BLUE + "Alias: " + RESET + value);
            }
            Map<ColumnInfo, String> columnAliasMap = statement.getColumnAliasMap();
            System.out.println("====================================column==========================================");

            for (Map.Entry<ColumnInfo, String> columnInfoStringEntry : columnAliasMap.entrySet()) {
                ColumnInfo key = columnInfoStringEntry.getKey();
                String value = columnInfoStringEntry.getValue();
                System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                System.out.println("  " + BLUE + "Column: " + RESET + key.getColumn());
                System.out.println("  " + BLUE + "Alias: " + RESET + value);
                System.out.println("=========================================================================================");

            }
            System.out.println("=========================================================================================");
        }

    }

    @Test
    void mysqlTestValidTable2() {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDbType(DatabaseTypeEnum.MYSQL.name());
        Chat2DBContext.putContext(connectInfo);

        String sql = """
                 select * from test_all_column;
                select test.test_all_column.* from test_all_column;
                select A.a,b as B from test.abcd as A;
                select test.class.size , position as P from test.class;
                             SELECT u.id, u.name, o.order_id, o.amount
                             FROM users u
                             INNER JOIN orders o ON u.id = o.user_id
                             WHERE o.amount > 100;

                             SELECT u.id, u.name, o.total
                                          FROM users u, LATERAL (
                                              SELECT SUM(amount) AS total FROM orders WHERE orders.user_id = u.id
                                          ) o;

                                          select co(a(t)) as b from a;
                             """;

        List<SimpleSqlStatement> simpleSqlStatements = SqlUtils.parseAndValidTableStatements(sql, DbType.mysql, DatabaseTypeEnum.MYSQL.name());
        for (SimpleSqlStatement statement : simpleSqlStatements) {
            System.out.println(RESET + "SQL: " + statement.getSql());
            System.out.println(CYAN + "SQL Type: " + statement.getSqlType());

            if (statement.getTables() != null && !statement.getTables().isEmpty()) {
                System.out.println(RED + "Tables: ");
                for (SimpleSqlStatement.SimpleTable table : statement.getTables()) {
                    System.out.println("  " + GREEN + "Database: " + table.getDatabaseName());
                    System.out.println("  " + GREEN + "Schema: " + table.getSchemaName());
                    System.out.println("  " + GREEN + "Table: " + table.getTableName());
                    System.out.println("  " + GREEN + "DatasourceId: " + table.getDatasourceId());
                    System.out.println("  " + GREEN + "Alias: " + table.getAlias());

                    if (table.getColumns() != null && !table.getColumns().isEmpty()) {
                        System.out.println("  " + BLUE + "Columns: ");
                        for (SimpleSqlStatement.Column column : table.getColumns()) {
                            System.out.println("    " + "Table: " + column.getTableName());
                            System.out.println("    " + "Column: " + column.getColumnName());
                            System.out.println("    " + "Alias: " + column.getAlias());
                        }
                    }
                }
            }
            System.out.println("=====================================");
        }


    }

    @AfterEach
    void tearDown() {
        Chat2DBContext.removeContext();
    }


    @Test
    void oracleTestValidTable() {
        String sql = """
                select * from USER_SALARY;
                -- Create the CUSTOMER_ORDERS table
                CREATE TABLE CUSTOMER_ORDERS (
                    ORDER_ID NUMBER PRIMARY KEY,
                    CUSTOMER_NAME VARCHAR2(100),
                    ORDER_DATE DATE,
                    TOTAL_AMOUNT NUMBER(10, 2)
                );

                -- Insert data
                INSERT INTO CUSTOMER_ORDERS VALUES (1001, 'Alice', SYSDATE - 10, 5000);
                INSERT INTO CUSTOMER_ORDERS VALUES (1002, 'Bob', SYSDATE - 5, 3000);
                INSERT INTO CUSTOMER_ORDERS VALUES (1004, 'Charlie', SYSDATE - 2, 7000);

                -- Drop the previously created table
                DROP TABLE EMPLOYEE_PROJECTS;
                DROP TABLE PROJECTS;
                DROP TABLE EMPLOYEES;
                DROP TABLE DEPARTMENTS;

                -- Commit the transaction
                COMMIT;
                                             """;

        List<Statement> statements = DefaultSqlSyntaxHandler.validTableStatements(sql, DatabaseTypeEnum.ORACLE.name());
        assertNotNull(statements);
        for (Statement statement : statements) {
            System.out.println(CYAN + "SQL: " + RESET + statement.getSql());
            System.out.println(GREEN + "SQL Type: " + RESET + statement.getType());
            Map<TableInfo, String> tableAliasMap = statement.getTableAliasMap();
            System.out.println("====================================table==========================================");
            if (tableAliasMap != null) {
                for (Map.Entry<TableInfo, String> tableInfoAliasEntry : tableAliasMap.entrySet()) {
                    TableInfo key = tableInfoAliasEntry.getKey();
                    String value = tableInfoAliasEntry.getValue();
                    System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                    System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                    System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                    System.out.println("  " + BLUE + "Alias: " + RESET + value);
                }
            }
            System.out.println("====================================column==========================================");
            Map<ColumnInfo, String> columnAliasMap = statement.getColumnAliasMap();
            if (columnAliasMap != null) {
                for (Map.Entry<ColumnInfo, String> columnInfoStringEntry : columnAliasMap.entrySet()) {
                    ColumnInfo key = columnInfoStringEntry.getKey();
                    String value = columnInfoStringEntry.getValue();
                    System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                    System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                    System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                    System.out.println("  " + BLUE + "Column: " + RESET + key.getColumn());
                    System.out.println("  " + BLUE + "Alias: " + RESET + value);
                    System.out.println("=========================================================================================");

                }
                System.out.println("=========================================================================================");
            }
        }

    }


    @Test
    void pgsqlTestValidTable() {
        String sql = """
                 select * from test_all_column;
                select test.dbo.test_all_column.* from test_all_column;
                select A.a,b as B from test.abcd as A;
                select test.class.size , position as P from test.class;
                             SELECT u.id, u.name, o.order_id, o.amount
                             FROM users u
                             INNER JOIN orders o ON u.id = o.user_id
                             WHERE o.amount > 100;


                                          select co(a(t)) as b from a;

                                          WITH recent_orders AS (
                                              SELECT id, customer_id, total_price
                                              FROM orders
                                              WHERE order_date > CURRENT_DATE - INTERVAL '7 days'
                                          )
                                          SELECT * FROM recent_orders;
                             """;

        List<Statement> statements = DefaultSqlSyntaxHandler.validTableStatements(sql, DatabaseTypeEnum.POSTGRESQL.name());
        assertNotNull(statements);
        for (Statement statement : statements) {
            System.out.println(CYAN + "SQL: " + RESET + statement.getSql());
            System.out.println(GREEN + "SQL Type: " + RESET + statement.getType());
            Map<TableInfo, String> tableAliasMap = statement.getTableAliasMap();
            System.out.println("====================================table==========================================");
            for (Map.Entry<TableInfo, String> tableInfoAliasEntry : tableAliasMap.entrySet()) {
                TableInfo key = tableInfoAliasEntry.getKey();
                String value = tableInfoAliasEntry.getValue();
                System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                System.out.println("  " + BLUE + "Alias: " + RESET + value);
            }
            Map<ColumnInfo, String> columnAliasMap = statement.getColumnAliasMap();
            System.out.println("====================================column==========================================");

            for (Map.Entry<ColumnInfo, String> columnInfoStringEntry : columnAliasMap.entrySet()) {
                ColumnInfo key = columnInfoStringEntry.getKey();
                String value = columnInfoStringEntry.getValue();
                System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                System.out.println("  " + BLUE + "Column: " + RESET + key.getColumn());
                System.out.println("  " + BLUE + "Alias: " + RESET + value);
                System.out.println("=========================================================================================");

            }
            System.out.println("=========================================================================================");
        }

    }

    @Test
    void sqlserverTestValidTable() {
        String sql = """
                select * from test_all_column;
                select test.dbo.test_all_column.* from test_all_column;
                select A.a,b as B from test.abcd as A;
                select test.class.size , position as P from test.class;
                             SELECT u.id, u.name, o.order_id, o.amount
                             FROM users u
                             INNER JOIN orders o ON u.id = o.user_id
                             WHERE o.amount > 100;


                                          select co(a(t)) as b from a;

                                          WITH recent_orders AS (
                                              SELECT id, customer_id, total_price
                                              FROM orders

                                          )
                                          SELECT * FROM recent_orders;


                             """;

        List<Statement> statements = DefaultSqlSyntaxHandler.validTableStatements(sql, DatabaseTypeEnum.SQLSERVER.name());
        assertNotNull(statements);
        for (Statement statement : statements) {
            System.out.println(CYAN + "SQL: " + RESET + statement.getSql());
            System.out.println(GREEN + "SQL Type: " + RESET + statement.getType());
            Map<TableInfo, String> tableAliasMap = statement.getTableAliasMap();
            System.out.println("====================================table==========================================");
            for (Map.Entry<TableInfo, String> tableInfoAliasEntry : tableAliasMap.entrySet()) {
                TableInfo key = tableInfoAliasEntry.getKey();
                String value = tableInfoAliasEntry.getValue();
                System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                System.out.println("  " + BLUE + "Alias: " + RESET + value);
            }
            Map<ColumnInfo, String> columnAliasMap = statement.getColumnAliasMap();
            System.out.println("====================================column==========================================");

            for (Map.Entry<ColumnInfo, String> columnInfoStringEntry : columnAliasMap.entrySet()) {
                ColumnInfo key = columnInfoStringEntry.getKey();
                String value = columnInfoStringEntry.getValue();
                System.out.println("  " + BLUE + "Database: " + RESET + key.getDatabase());
                System.out.println("  " + BLUE + "Schema: " + RESET + key.getSchema());
                System.out.println("  " + BLUE + "Table: " + RESET + key.getTable());
                System.out.println("  " + BLUE + "Column: " + RESET + key.getColumn());
                System.out.println("  " + BLUE + "Alias: " + RESET + value);
                System.out.println("=========================================================================================");

            }
            System.out.println("=========================================================================================");
        }

    }
}
