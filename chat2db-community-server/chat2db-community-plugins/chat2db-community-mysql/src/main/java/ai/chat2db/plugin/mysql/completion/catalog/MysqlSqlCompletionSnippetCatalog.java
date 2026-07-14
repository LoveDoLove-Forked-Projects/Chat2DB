package ai.chat2db.plugin.mysql.completion.catalog;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionSnippetSlotTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionSnippetSpec;
import java.util.List;


public final class MysqlSqlCompletionSnippetCatalog {

    private static final List<SqlCompletionSnippetSpec> PHRASES = List.of(
            phrase(MySqlLexer.SELECT, "select from", """
                    SELECT
                        *
                    FROM
                        $1;
                    """,
                    "Select data from a table"),
            phrase(MySqlLexer.SELECT, "select where",
                    """
                    SELECT
                        *
                    FROM
                        $1
                    WHERE
                        $2;
                    """,
                    "Select data from a table with a filter"),
            phrase(MySqlLexer.SELECT, "select function", """
                    SELECT $1;
                    """,
                    "Call a function in a select statement",
                    List.of(SqlCompletionSnippetSlotTypeEnum.SELECT_FUNCTION)),
            phrase(MySqlLexer.WITH, "with cte",
                    """
                    WITH $1 AS (
                        SELECT
                            *
                        FROM
                            $2
                    )
                    SELECT
                        *
                    FROM
                        $1;
                    """,
                    "Query with a common table expression"),
            phrase(MySqlLexer.CREATE, "create table", """
                    CREATE TABLE $1 (
                        $2
                    );
                    """,
                    "Create a table"),
            phrase(MySqlLexer.CREATE, "create index", """
                    CREATE INDEX $1 ON $2 ($3);
                    """,
                    "Create an index"),
            phrase(MySqlLexer.CREATE, "create view",
                    """
                    CREATE VIEW $1 AS
                    SELECT
                        *
                    FROM
                        $2;
                    """,
                    "Create a view"),
            phrase(MySqlLexer.CREATE, "create function",
                    """
                    CREATE FUNCTION $1($2)
                    RETURNS $3
                    RETURN $4;
                    """,
                    "Create a function"),
            phrase(MySqlLexer.CREATE, "create procedure",
                    """
                    CREATE PROCEDURE $1($2)
                    BEGIN
                        $3
                    END;
                    """,
                    "Create a procedure"),
            phrase(MySqlLexer.CREATE, "create database", """
                    CREATE DATABASE $1;
                    """,
                    "Create a database"),
            phrase(MySqlLexer.CREATE, "create event",
                    """
                    CREATE EVENT $1
                    ON SCHEDULE EVERY $2
                    DO
                        $3;
                    """,
                    "Create an event"),
            phrase(MySqlLexer.CREATE, "create trigger",
                    """
                    CREATE TRIGGER $1
                    $2 ON $3
                    FOR EACH ROW
                    BEGIN
                        $4
                    END;
                    """,
                    "Create a trigger"),
            phrase(MySqlLexer.ALTER, "alter table",
                    """
                    ALTER TABLE $1
                        $2;
                    """,
                    "Alter a table"),
            phrase(MySqlLexer.ALTER, "alter view",
                    """
                    ALTER VIEW $1 AS
                    SELECT
                        *
                    FROM
                        $2;
                    """,
                    "Alter a view"),
            phrase(MySqlLexer.DROP, "drop table", """
                    DROP TABLE $1;
                    """,
                    "Drop a table"),
            phrase(MySqlLexer.DROP, "drop view", """
                    DROP VIEW $1;
                    """,
                    "Drop a view"),
            phrase(MySqlLexer.DROP, "drop function", """
                    DROP FUNCTION $1;
                    """,
                    "Drop a function"),
            phrase(MySqlLexer.DROP, "drop procedure", """
                    DROP PROCEDURE $1;
                    """,
                    "Drop a procedure"),
            phrase(MySqlLexer.DROP, "drop database", """
                    DROP DATABASE $1;
                    """,
                    "Drop a database"),
            phrase(MySqlLexer.TRUNCATE, "truncate table", """
                    TRUNCATE TABLE $1;
                    """,
                    "Truncate a table"),
            phrase(MySqlLexer.INSERT, "insert into", """
                    INSERT INTO $1 ($2)
                    VALUES ($3);
                    """,
                    "Insert data into a table",
                    List.of(SqlCompletionSnippetSlotTypeEnum.INSERT_COLUMN_LIST)),
            phrase(MySqlLexer.UPDATE, "update set",
                    """
                    UPDATE $1
                    SET
                        $2
                    WHERE
                        $3;
                    """,
                    "Update rows in a table"),
            phrase(MySqlLexer.DELETE, "delete from", """
                    DELETE FROM $1
                    WHERE
                        $2;
                    """,
                    "Delete rows from a table"),
            phrase(MySqlLexer.REPLACE, "replace into",
                    """
                    REPLACE INTO $1 ($2)
                    VALUES ($3);
                    """,
                    "Replace data into a table"),
            phrase(MySqlLexer.CALL, "call procedure", """
                    CALL $1;
                    """,
                    "Call a procedure",
                    List.of(SqlCompletionSnippetSlotTypeEnum.CALL_PROCEDURE)),
            phrase(MySqlLexer.EXPLAIN, "explain select",
                    """
                    EXPLAIN SELECT
                        *
                    FROM
                        $1;
                    """,
                    "Explain a select statement"),
            phrase(MySqlLexer.DESC, "desc table", """
                    DESC $1;
                    """,
                    "Describe a table"),
            phrase(MySqlLexer.DESCRIBE, "describe table", """
                    DESCRIBE $1;
                    """,
                    "Describe a table"),
            phrase(MySqlLexer.SHOW, "show databases", """
                    SHOW DATABASES;
                    """,
                    "Show databases"),
            phrase(MySqlLexer.SHOW, "show tables", """
                    SHOW TABLES;
                    """,
                    "Show tables"),
            phrase(MySqlLexer.SHOW, "show columns", """
                    SHOW COLUMNS FROM $1;
                    """,
                    "Show table columns"),
            phrase(MySqlLexer.SHOW, "show indexes", """
                    SHOW INDEXES FROM $1;
                    """,
                    "Show table indexes"),
            phrase(MySqlLexer.SHOW, "show create table", """
                    SHOW CREATE TABLE $1;
                    """,
                    "Show table DDL"),
            phrase(MySqlLexer.SHOW, "show triggers", """
                    SHOW TRIGGERS;
                    """,
                    "Show triggers"),
            phrase(MySqlLexer.SHOW, "show procedure status", """
                    SHOW PROCEDURE STATUS;
                    """,
                    "Show procedure status"),
            phrase(MySqlLexer.USE, "use database", """
                    USE $1;
                    """,
                    "Use a database"),
            phrase(MySqlLexer.ANALYZE, "analyze table", """
                    ANALYZE TABLE $1;
                    """,
                    "Analyze a table"),
            phrase(MySqlLexer.CHECK, "check table", """
                    CHECK TABLE $1;
                    """,
                    "Check a table"),
            phrase(MySqlLexer.OPTIMIZE, "optimize table", """
                    OPTIMIZE TABLE $1;
                    """,
                    "Optimize a table"),
            phrase(MySqlLexer.REPAIR, "repair table", """
                    REPAIR TABLE $1;
                    """,
                    "Repair a table"),
            phrase(MySqlLexer.GRANT, "grant select",
                    """
                    GRANT SELECT ON $1.$2 TO $3;
                    """,
                    "Grant select privilege"),
            phrase(MySqlLexer.GRANT, "grant execute",
                    """
                    GRANT EXECUTE ON PROCEDURE $1.$2 TO $3;
                    """,
                    "Grant execute privilege"),
            phrase(MySqlLexer.REVOKE, "revoke select",
                    """
                    REVOKE SELECT ON $1.$2 FROM $3;
                    """,
                    "Revoke select privilege"),
            phrase(MySqlLexer.REVOKE, "revoke execute",
                    """
                    REVOKE EXECUTE ON PROCEDURE $1.$2 FROM $3;
                    """,
                    "Revoke execute privilege"),
            routinePhrase(MySqlLexer.BEGIN, "begin end", """
                    BEGIN
                        $1
                    END\
                    """,
                    "Begin an inner routine block"));

    private MysqlSqlCompletionSnippetCatalog() {
    }

    public static List<SqlCompletionSnippetSpec> phrases() {
        return PHRASES;
    }

    private static SqlCompletionSnippetSpec phrase(int tokenType, String label, String insertText, String detail) {
        return phrase(tokenType, label, insertText, detail, List.of());
    }

    private static SqlCompletionSnippetSpec phrase(int tokenType,
                                                   String label,
                                                   String insertText,
                                                   String detail,
                                                   List<SqlCompletionSnippetSlotTypeEnum> slots) {
        return new SqlCompletionSnippetSpec(tokenType, label, List.of(), insertText, detail, 40,
                slots);
    }

    private static SqlCompletionSnippetSpec routinePhrase(int tokenType,
                                                          String label,
                                                          String insertText,
                                                          String detail) {
        return new SqlCompletionSnippetSpec(tokenType, label, List.of(), insertText, detail, 40,
                List.of());
    }
}
