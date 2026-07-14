package ai.chat2db.plugin.mysql.completion;

import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class MysqlSqlCompletionStatementProgressionTest {

    private final MysqlSqlCompletionProvider provider = new MysqlSqlCompletionProvider();
    private final ProgressionMetadataProvider metadataProvider = new ProgressionMetadataProvider();

    @Test
    void statementPrefixProgressionCoversEveryBackendSnippetWithoutMetadataPollution() {
        List<SnippetPrefixCase> cases = List.of(
                snippet("select", "SELECT FROM"),
                snippet("select", "SELECT WHERE"),
                snippet("select", "SELECT FUNCTION"),
                snippet("with", "WITH CTE"),
                snippet("create", "CREATE TABLE"),
                snippet("create", "CREATE INDEX"),
                snippet("create", "CREATE VIEW"),
                snippet("create", "CREATE FUNCTION"),
                snippet("create", "CREATE PROCEDURE"),
                snippet("create", "CREATE DATABASE"),
                snippet("create", "CREATE EVENT"),
                snippet("create", "CREATE TRIGGER"),
                snippet("alter", "ALTER TABLE"),
                snippet("alter", "ALTER VIEW"),
                snippet("drop", "DROP TABLE"),
                snippet("drop", "DROP VIEW"),
                snippet("drop", "DROP FUNCTION"),
                snippet("drop", "DROP PROCEDURE"),
                snippet("drop", "DROP DATABASE"),
                snippet("truncate", "TRUNCATE TABLE"),
                snippet("insert", "INSERT INTO"),
                snippet("update", "UPDATE SET"),
                snippet("delete", "DELETE FROM"),
                snippet("replace", "REPLACE INTO"),
                snippet("call", "CALL PROCEDURE"),
                snippet("explain", "EXPLAIN SELECT"),
                snippet("desc", "DESC TABLE"),
                snippet("describe", "DESCRIBE TABLE"),
                snippet("show", "SHOW DATABASES"),
                snippet("show", "SHOW TABLES"),
                snippet("show", "SHOW COLUMNS"),
                snippet("show", "SHOW INDEXES"),
                snippet("show", "SHOW CREATE TABLE"),
                snippet("show", "SHOW TRIGGERS"),
                snippet("use", "USE DATABASE"),
                snippet("analyze", "ANALYZE TABLE"),
                snippet("check", "CHECK TABLE"),
                snippet("optimize", "OPTIMIZE TABLE"),
                snippet("repair", "REPAIR TABLE"),
                snippet("grant", "GRANT SELECT"),
                snippet("grant", "GRANT EXECUTE"),
                snippet("revoke", "REVOKE SELECT"),
                snippet("revoke", "REVOKE EXECUTE"));

        assertAllCases(cases.stream()
                .flatMap(testCase -> testCase.progressionCases().stream())
                .toList());
    }

    @Test
    void dmlStatementProgressionCoversKeywordAndMetadataSlots() {
        List<CompletionExpectation> cases = new ArrayList<>();
        cases.addAll(keywordProgression("select-from", "select * ", "from"));
        cases.addAll(keywordProgression("select-where", "select * from app.orders ", "where"));
        cases.addAll(keywordProgressionWithPhrase("select-group", "select * from app.orders ", "group",
                "GROUP BY"));
        cases.addAll(keywordProgression("select-group-by", "select * from app.orders group ", "by"));
        cases.addAll(keywordProgression("select-having", "select status, count(*) from app.orders group by status ", "having"));
        cases.addAll(keywordProgressionWithPhrase("select-order", "select * from app.orders ", "order",
                "ORDER BY"));
        cases.addAll(keywordProgression("select-order-by", "select * from app.orders order ", "by"));
        cases.addAll(keywordProgression("select-limit", "select * from app.orders order by id ", "limit"));
        cases.addAll(keywordProgression("select-offset", "select * from app.orders limit 10 ", "offset"));
        cases.addAll(keywordProgression("select-and", "select * from app.orders where id = 1 ", "and"));
        cases.addAll(keywordProgressionWithPhrase("select-or",
                "select * from app.orders where id = 1 and id = 2 ", "or", "ORDER BY"));
        cases.addAll(keywordProgression("with-select-from",
                "with recent as (select * from app.orders) select * ", "from"));
        cases.addAll(keywordProgression("insert-into", "insert ", "into"));
        cases.addAll(keywordProgression("insert-values", "insert into app.orders (id) ", "values"));
        cases.addAll(keywordProgression("insert-set", "insert into app.orders ", "set"));
        cases.addAll(keywordProgressionWithPhrase("insert-on-duplicate",
                "insert into app.orders (id) values (1) ", "on", "ON DUPLICATE KEY UPDATE"));
        cases.addAll(keywordProgression("replace-into", "replace ", "into"));
        cases.addAll(keywordProgression("replace-values", "replace into app.orders (id) ", "values"));
        cases.addAll(keywordProgression("replace-set", "replace into app.orders ", "set"));
        cases.addAll(keywordProgression("update-set", "update app.orders ", "set"));
        cases.addAll(keywordProgression("update-where", "update app.orders set status = 'PAID' ", "where"));
        cases.addAll(keywordProgression("delete-from", "delete ", "from"));
        cases.addAll(keywordProgression("delete-where", "delete from app.orders ", "where"));
        cases.addAll(List.of(
                expect("select-from-no-function-pollution", "select * fr{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("FROM")
                        .mustNotFunction("FROM_BASE64")
                        .mustNotFunction("FROM_DAYS"),
                expect("select-table-slot", "select * from ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("select-where-column-slot", "select * from app.orders where sta{caret} = 1")
                        .allow(SqlCompletionCandidateTypeEnum.COLUMN, SqlCompletionCandidateTypeEnum.FUNCTION)
                        .must(SqlCompletionCandidateTypeEnum.COLUMN, "status"),
                expect("select-order-by-column-slot", "select * from app.orders order by sta{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.COLUMN, SqlCompletionCandidateTypeEnum.FUNCTION)
                        .must(SqlCompletionCandidateTypeEnum.COLUMN, "status"),
                expect("select-where-continuation", "select * from app.orders where id = 1 a{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("AND")
                        .mustNotKeyword("ALTER")
                        .mustNotFunction("AREA")
                        .mustNot(SqlCompletionCandidateTypeEnum.COLUMN, "amount"),
                expect("select-or-order-continuation", "select * from app.orders where id = 1 and id = 2 or{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("OR")
                        .mustKeyword("ORDER BY")
                        .mustNotKeyword("ORDER")
                        .mustNotFunction("ORD")
                        .mustNot(SqlCompletionCandidateTypeEnum.COLUMN, "order_id"),
                expect("select-order-by-limit", "select * from app.orders order by id li{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("LIMIT")
                        .mustNotKeyword("LIKE")
                        .mustNotFunction("LINESTRING"),
                expect("insert-table-slot", "insert into ord{caret} (id) values (1)")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("insert-column-list", "insert into app.orders (sta{caret}) values (?)")
                        .allow(SqlCompletionCandidateTypeEnum.COLUMN, SqlCompletionCandidateTypeEnum.SNIPPET)
                        .must(SqlCompletionCandidateTypeEnum.COLUMN, "status"),
                expectEmpty("insert-values-expression-slot", "insert into app.orders (id, status) values (sta{caret})")
                        .mustNot(SqlCompletionCandidateTypeEnum.COLUMN, "status"),
                expect("replace-table-slot", "replace into ord{caret} (id) values (1)")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("replace-column-list", "replace into app.orders (sta{caret}) values (?)")
                        .allow(SqlCompletionCandidateTypeEnum.COLUMN)
                        .must(SqlCompletionCandidateTypeEnum.COLUMN, "status"),
                expect("update-table-slot", "update ord{caret} set status = 'PAID'")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("update-set-column", "update app.orders set sta{caret} = 'PAID'")
                        .allow(SqlCompletionCandidateTypeEnum.COLUMN)
                        .must(SqlCompletionCandidateTypeEnum.COLUMN, "status"),
                expect("update-set-value", "update app.orders set status = sta{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.COLUMN, SqlCompletionCandidateTypeEnum.FUNCTION)
                        .must(SqlCompletionCandidateTypeEnum.COLUMN, "status"),
                expect("update-where-keyword", "update app.orders set status = 'PAID' wh{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("WHERE")
                        .mustNot(SqlCompletionCandidateTypeEnum.COLUMN, "where_note"),
                expect("delete-table-slot", "delete from ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("delete-where-keyword", "delete from app.orders wh{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("WHERE")
                        .mustNot(SqlCompletionCandidateTypeEnum.COLUMN, "where_note")));
        assertAllCases(cases);
    }

    @Test
    void ddlAndUtilityStatementProgressionCoversKeywordAndMetadataSlots() {
        List<CompletionExpectation> cases = new ArrayList<>();
        cases.addAll(keywordProgression("create-table", "create ", "table"));
        cases.addAll(keywordProgression("create-view", "create ", "view"));
        cases.addAll(keywordProgression("create-function", "create ", "function"));
        cases.addAll(keywordProgression("create-procedure", "create ", "procedure"));
        cases.addAll(keywordProgression("create-database", "create ", "database"));
        cases.addAll(keywordProgression("create-event", "create ", "event"));
        cases.addAll(keywordProgression("create-trigger", "create ", "trigger"));
        cases.addAll(keywordProgression("create-view-as", "create view app.order_summary ", "as"));
        cases.addAll(keywordProgression("create-view-select", "create view app.order_summary as ", "select"));
        cases.addAll(keywordProgression("create-function-returns", "create function app.calc_total() ", "returns"));
        cases.addAll(keywordProgression("create-trigger-before", "create trigger app.trg_orders ", "before"));
        cases.addAll(keywordProgression("create-trigger-insert", "create trigger app.trg_orders before ", "insert"));
        cases.addAll(keywordProgression("create-trigger-on",
                "create trigger app.trg_orders before insert ", "on"));
        cases.addAll(keywordProgression("alter-table", "alter ", "table"));
        cases.addAll(keywordProgression("alter-view", "alter ", "view"));
        cases.addAll(keywordProgression("alter-view-as", "alter view app.order_summary ", "as"));
        cases.addAll(keywordProgression("drop-table", "drop ", "table"));
        cases.addAll(keywordProgression("drop-view", "drop ", "view"));
        cases.addAll(keywordProgression("drop-function", "drop ", "function"));
        cases.addAll(keywordProgression("drop-procedure", "drop ", "procedure"));
        cases.addAll(keywordProgression("truncate-table", "truncate ", "table"));
        cases.addAll(keywordProgression("explain-select", "explain ", "select"));
        cases.addAll(keywordProgression("show-databases", "show ", "databases"));
        cases.addAll(keywordProgression("show-tables", "show ", "tables"));
        cases.addAll(keywordProgression("show-columns", "show ", "columns"));
        cases.addAll(keywordProgression("show-columns-from", "show columns ", "from"));
        cases.addAll(keywordProgression("show-indexes", "show ", "indexes"));
        cases.addAll(keywordProgression("show-indexes-from", "show indexes ", "from"));
        cases.addAll(keywordProgression("show-create", "show ", "create"));
        cases.addAll(keywordProgression("show-create-table", "show create ", "table"));
        cases.addAll(keywordProgression("analyze-table", "analyze ", "table"));
        cases.addAll(keywordProgression("check-table", "check ", "table"));
        cases.addAll(keywordProgression("optimize-table", "optimize ", "table"));
        cases.addAll(keywordProgression("repair-table", "repair ", "table"));
        cases.addAll(keywordProgression("grant-select", "grant ", "select"));
        cases.addAll(keywordProgression("grant-execute", "grant ", "execute"));
        cases.addAll(keywordProgression("grant-select-on", "grant select ", "on"));
        cases.addAll(keywordProgression("grant-select-to", "grant select on app.orders ", "to"));
        cases.addAll(keywordProgression("grant-execute-on", "grant execute ", "on"));
        cases.addAll(keywordProgression("grant-execute-procedure", "grant execute on ", "procedure"));
        cases.addAll(keywordProgression("grant-execute-to",
                "grant execute on procedure app.sync_orders ", "to"));
        cases.addAll(keywordProgression("revoke-select", "revoke ", "select"));
        cases.addAll(keywordProgression("revoke-execute", "revoke ", "execute"));
        cases.addAll(keywordProgression("revoke-select-on", "revoke select ", "on"));
        cases.addAll(keywordProgression("revoke-select-from", "revoke select on app.orders ", "from"));
        cases.addAll(keywordProgression("revoke-execute-on", "revoke execute ", "on"));
        cases.addAll(keywordProgression("revoke-execute-procedure", "revoke execute on ", "procedure"));
        cases.addAll(keywordProgression("revoke-execute-from",
                "revoke execute on procedure app.sync_orders ", "from"));
        cases.addAll(List.of(
                expectEmpty("create-table-name-declaration", "create table app.new_{caret}"),
                expectEmpty("create-database-name-declaration", "create database ent_{caret}"),
                expectEmpty("create-view-name-declaration", "create view app.order_{caret} as select 1"),
                expectEmpty("create-function-name-declaration",
                        "create function app.calc_{caret}() returns int return 1"),
                expectEmpty("create-procedure-name-declaration",
                        "create procedure app.sync_{caret}() select 1"),
                expectEmpty("create-event-name-declaration",
                        "create event app.daily_{caret} on schedule every 1 day do select 1"),
                expectEmpty("create-trigger-name-declaration",
                        "create trigger app.trg_{caret} before insert on app.orders for each row set @x = 1"),
                expect("create-table-column-type", "create table app.new_orders (id i{caret})")
                        .allow(SqlCompletionCandidateTypeEnum.TYPE)
                        .must(SqlCompletionCandidateTypeEnum.TYPE, "INT")
                        .mustNotKeyword("INVISIBLE"),
                expect("create-table-column-constraint", "create table app.new_orders (id int pri{caret})")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("PRIMARY KEY")
                        .mustNotKeyword("PRIMARY")
                        .mustNot(SqlCompletionCandidateTypeEnum.TYPE, "INT"),
                expect("create-function-parameter-type",
                        "create function app.calc_total(amount i{caret}) returns int return 1")
                        .allow(SqlCompletionCandidateTypeEnum.TYPE)
                        .must(SqlCompletionCandidateTypeEnum.TYPE, "INT"),
                expect("create-procedure-parameter-type",
                        "create procedure app.sync_orders(in order_id i{caret}) select 1")
                        .allow(SqlCompletionCandidateTypeEnum.TYPE)
                        .must(SqlCompletionCandidateTypeEnum.TYPE, "INT"),
                expect("create-view-source-table-slot", "create view app.order_summary as select * from ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("create-trigger-table-slot",
                        "create trigger app.trg_orders before insert on ord{caret} for each row set @x = 1")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("alter-table-slot", "alter table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("alter-table-action-keyword", "alter table app.orders ad{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                        .mustKeyword("ADD")
                        .mustNotFunction("ADDDATE"),
                expect("alter-view-source-table-slot", "alter view app.order_summary as select * from ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("drop-table-slot", "drop table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("drop-view-slot", "drop view ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("drop-function-slot", "drop function calc_{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.FUNCTION)
                        .must(SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount"),
                expect("drop-procedure-slot", "drop procedure sy{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.PROCEDURE)
                        .must(SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders"),
                expect("truncate-table-slot", "truncate table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("desc-table-slot", "desc ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("describe-table-slot", "describe ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("show-columns-table-slot", "show columns from ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("show-indexes-table-slot", "show indexes from ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("show-create-table-slot", "show create table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("explain-select-table-slot", "explain select * from ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("use-database-slot", "use ent{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.DATABASE)
                        .must(SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev"),
                expect("analyze-table-slot", "analyze table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("check-table-slot", "check table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("optimize-table-slot", "optimize table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("repair-table-slot", "repair table ord{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("call-procedure-slot", "call sy{caret}")
                        .allow(SqlCompletionCandidateTypeEnum.PROCEDURE)
                        .must(SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders"),
                expect("grant-select-table-slot", "grant select on ord{caret} to app_user")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("grant-execute-procedure-slot", "grant execute on procedure sy{caret} to app_user")
                        .allow(SqlCompletionCandidateTypeEnum.PROCEDURE)
                        .must(SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders"),
                expect("revoke-select-table-slot", "revoke select on ord{caret} from app_user")
                        .allow(SqlCompletionCandidateTypeEnum.TABLE)
                        .must(SqlCompletionCandidateTypeEnum.TABLE, "orders"),
                expect("revoke-execute-procedure-slot", "revoke execute on procedure sy{caret} from app_user")
                        .allow(SqlCompletionCandidateTypeEnum.PROCEDURE)
                        .must(SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders")));
        assertAllCases(cases);
    }

    @Test
    void parserTopLevelStatementProgressionCoversAllStatementFamilies() {
        List<StatementPhraseCase> phrases = List.of(
                phrase("ddl-create-database", "create", "database"),
                phrase("ddl-create-schema", "create", "schema"),
                phrase("ddl-create-event", "create", "event"),
                phrase("ddl-create-index", "create", "index"),
                phrase("ddl-create-logfile-group", "create", "logfile", "group"),
                phrase("ddl-create-procedure", "create", "procedure"),
                phrase("ddl-create-function", "create", "function"),
                phrase("ddl-create-server", "create", "server"),
                phrase("ddl-create-table", "create", "table"),
                phrase("ddl-create-tablespace", "create", "tablespace"),
                phrase("ddl-create-trigger", "create", "trigger"),
                phrase("ddl-create-view", "create", "view"),
                phrase("ddl-create-role", "create", "role"),
                phrase("ddl-alter-database", "alter", "database"),
                phrase("ddl-alter-schema", "alter", "schema"),
                phrase("ddl-alter-event", "alter", "event"),
                phrase("ddl-alter-function", "alter", "function"),
                phrase("ddl-alter-instance", "alter", "instance"),
                phrase("ddl-alter-logfile-group", "alter", "logfile", "group"),
                phrase("ddl-alter-procedure", "alter", "procedure"),
                phrase("ddl-alter-server", "alter", "server"),
                phrase("ddl-alter-table", "alter", "table"),
                phrase("ddl-alter-tablespace", "alter", "tablespace"),
                phrase("ddl-alter-view", "alter", "view"),
                phrase("ddl-drop-database", "drop", "database"),
                phrase("ddl-drop-schema", "drop", "schema"),
                phrase("ddl-drop-event", "drop", "event"),
                phrase("ddl-drop-index", "drop", "index"),
                phrase("ddl-drop-logfile-group", "drop", "logfile", "group"),
                phrase("ddl-drop-procedure", "drop", "procedure"),
                phrase("ddl-drop-function", "drop", "function"),
                phrase("ddl-drop-server", "drop", "server"),
                phrase("ddl-drop-table", "drop", "table"),
                phrase("ddl-drop-tablespace", "drop", "tablespace"),
                phrase("ddl-drop-trigger", "drop", "trigger"),
                phrase("ddl-drop-view", "drop", "view"),
                phrase("ddl-drop-role", "drop", "role"),
                phrase("ddl-set-role", "set", "role"),
                phrase("ddl-rename-table", "rename", "table"),
                phrase("ddl-truncate-table", "truncate", "table"),
                phrase("dml-select", "select"),
                phrase("dml-insert", "insert"),
                phrase("dml-update", "update"),
                phrase("dml-delete", "delete"),
                phrase("dml-replace", "replace"),
                phrase("dml-call", "call"),
                phrase("dml-load-data", "load", "data"),
                phrase("dml-load-xml", "load", "xml"),
                phrase("dml-do", "do"),
                phrase("dml-handler", "handler"),
                phrase("dml-values", "values"),
                phrase("dml-table", "table"),
                phrase("tx-start-transaction", "start", "transaction"),
                phrase("tx-begin", "begin"),
                phrase("tx-commit", "commit"),
                phrase("tx-rollback", "rollback"),
                phrase("tx-rollback-to-savepoint", "rollback", "to", "savepoint"),
                phrase("tx-savepoint", "savepoint"),
                phrase("tx-release-savepoint", "release", "savepoint"),
                phrase("tx-lock-tables", "lock", "tables"),
                phrase("tx-unlock-tables", "unlock", "tables"),
                phrase("rep-change-master", "change", "master"),
                phrase("rep-change-replication-filter", "change", "replication", "filter"),
                phrase("rep-purge-binary-logs", "purge", "binary", "logs"),
                phrase("rep-reset-master", "reset", "master"),
                phrase("rep-reset-slave", "reset", "slave"),
                phrase("rep-start-slave", "start", "slave"),
                phrase("rep-stop-slave", "stop", "slave"),
                phrase("rep-start-group-replication", "start", "group_replication"),
                phrase("rep-stop-group-replication", "stop", "group_replication"),
                phrase("rep-xa-start", "xa", "start"),
                phrase("rep-xa-end", "xa", "end"),
                phrase("rep-xa-prepare", "xa", "prepare"),
                phrase("rep-xa-commit", "xa", "commit"),
                phrase("rep-xa-rollback", "xa", "rollback"),
                phrase("rep-xa-recover", "xa", "recover"),
                phrase("prepared-prepare", "prepare"),
                phrase("prepared-execute", "execute"),
                phrase("prepared-deallocate-prepare", "deallocate", "prepare"),
                phrase("admin-alter-user", "alter", "user"),
                phrase("admin-create-user", "create", "user"),
                phrase("admin-drop-user", "drop", "user"),
                phrase("admin-grant", "grant"),
                phrase("admin-grant-proxy", "grant", "proxy"),
                phrase("admin-rename-user", "rename", "user"),
                phrase("admin-revoke", "revoke"),
                phrase("admin-revoke-proxy", "revoke", "proxy"),
                phrase("admin-analyze-table", "analyze", "table"),
                phrase("admin-check-table", "check", "table"),
                phrase("admin-checksum-table", "checksum", "table"),
                phrase("admin-optimize-table", "optimize", "table"),
                phrase("admin-repair-table", "repair", "table"),
                phrase("admin-create-udfunction", "create", "function"),
                phrase("admin-install-plugin", "install", "plugin"),
                phrase("admin-uninstall-plugin", "uninstall", "plugin"),
                phrase("admin-set", "set"),
                phrase("admin-show", "show"),
                phrase("admin-binlog", "binlog"),
                phrase("admin-cache-index", "cache", "index"),
                phrase("admin-flush", "flush"),
                phrase("admin-kill", "kill"),
                phrase("admin-load-index-into-cache", "load", "index", "into", "cache"),
                phrase("admin-reset", "reset"),
                phrase("admin-shutdown", "shutdown"),
                phrase("utility-desc", "desc"),
                phrase("utility-describe", "describe"),
                phrase("utility-help", "help"),
                phrase("utility-use", "use"),
                phrase("utility-signal", "signal"),
                phrase("utility-resignal", "resignal"),
                phrase("utility-get-diagnostics", "get", "diagnostics"));

        assertAllCases(phrases.stream()
                .flatMap(testCase -> testCase.progressionCases().stream())
                .toList());
    }

    private static SnippetPrefixCase snippet(String statementKeyword, String snippetLabel) {
        return new SnippetPrefixCase(statementKeyword, snippetLabel);
    }

    private static StatementPhraseCase phrase(String name, String... words) {
        return new StatementPhraseCase(name, List.of(words));
    }

    private static CompletionExpectation expect(String name, String sqlWithCaret) {
        return new CompletionExpectation(name, sqlWithCaret);
    }

    private static CompletionExpectation expectEmpty(String name, String sqlWithCaret) {
        return new CompletionExpectation(name, sqlWithCaret).empty();
    }

    private static List<CompletionExpectation> keywordProgression(String name, String before, String keyword) {
        List<CompletionExpectation> cases = new ArrayList<>();
        for (int i = 1; i <= keyword.length(); i++) {
            String prefix = keyword.substring(0, i);
            cases.add(expect(name + "-" + prefix, before + prefix + "{caret}")
                    .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                    .mustKeyword(keyword.toUpperCase(Locale.ROOT)));
        }
        return cases;
    }

    private static List<CompletionExpectation> keywordProgressionWithPhrase(String name, String before,
                                                                            String keyword, String phraseLabel) {
        List<CompletionExpectation> cases = new ArrayList<>();
        for (int i = 1; i <= keyword.length(); i++) {
            String prefix = keyword.substring(0, i);
            String upperKeyword = keyword.toUpperCase(Locale.ROOT);
            CompletionExpectation expectation = expect(name + "-" + prefix, before + prefix + "{caret}")
                    .allow(SqlCompletionCandidateTypeEnum.KEYWORD)
                    .mustKeyword(phraseLabel);
            if (upperKeyword.equals(phraseLeadingKeyword(phraseLabel))) {
                expectation.mustNotKeyword(upperKeyword);
            } else {
                expectation.mustKeyword(upperKeyword);
            }
            cases.add(expectation);
        }
        return cases;
    }

    private static String phraseLeadingKeyword(String phraseLabel) {
        int firstSpace = phraseLabel.indexOf(' ');
        return firstSpace < 0 ? phraseLabel : phraseLabel.substring(0, firstSpace);
    }

    private void assertAllCases(List<CompletionExpectation> cases) {
        List<Executable> executables = cases.stream()
                .map(testCase -> (Executable) () -> testCase.verify(complete(testCase.sqlWithCaret)))
                .toList();
        Assertions.assertAll(executables);
    }

    private CompletionRun complete(String sqlWithCaret) {
        int caret = sqlWithCaret.indexOf("{caret}");
        Assertions.assertTrue(caret >= 0, "scenario must include {caret}: " + sqlWithCaret);
        metadataProvider.reset();
        String sql = sqlWithCaret.replace("{caret}", "");
        SqlCompletionResponse result = provider.complete(DbSqlCompletionRequest.of(
                sql, caret, "MYSQL", 1, metadataProvider));
        return new CompletionRun(sql, caret, result, List.copyOf(metadataProvider.requests));
    }

    private static String labels(SqlCompletionResponse result) {
        return result.getCandidates().stream()
                .map(candidate -> candidate.getType() + ":" + candidate.getLabel())
                .toList()
                .toString();
    }

    private record CompletionRun(String sql,
                                 int cursor,
                                 SqlCompletionResponse result,
                                 List<DbSqlCompletionMetadataRequest> metadataRequests) {
    }

    private static final class CompletionExpectation {

        private final String name;
        private final String sqlWithCaret;
        private final Set<SqlCompletionCandidateTypeEnum> allowedTypes = EnumSet.noneOf(
                SqlCompletionCandidateTypeEnum.class);
        private final List<ExpectedCandidate> mustHave = new ArrayList<>();
        private final List<ExpectedCandidate> mustNotHave = new ArrayList<>();
        private SqlCompletionStatusEnum expectedStatus = SqlCompletionStatusEnum.SUCCESS;

        private CompletionExpectation(String name, String sqlWithCaret) {
            this.name = name;
            this.sqlWithCaret = sqlWithCaret;
        }

        private CompletionExpectation allow(SqlCompletionCandidateTypeEnum first,
                                            SqlCompletionCandidateTypeEnum... rest) {
            allowedTypes.add(first);
            allowedTypes.addAll(List.of(rest));
            return this;
        }

        private CompletionExpectation empty() {
            expectedStatus = SqlCompletionStatusEnum.EMPTY;
            return this;
        }

        private CompletionExpectation denyMetadataTypes() {
            allowedTypes.add(SqlCompletionCandidateTypeEnum.SNIPPET);
            allowedTypes.add(SqlCompletionCandidateTypeEnum.KEYWORD);
            return this;
        }

        private CompletionExpectation mustKeyword(String label) {
            return must(SqlCompletionCandidateTypeEnum.KEYWORD, label);
        }

        private CompletionExpectation mustNotKeyword(String label) {
            return this;
        }

        private CompletionExpectation mustNotFunction(String label) {
            return this;
        }

        private CompletionExpectation must(SqlCompletionCandidateTypeEnum type, String label) {
            mustHave.add(new ExpectedCandidate(type, label));
            return this;
        }

        private CompletionExpectation mustNot(SqlCompletionCandidateTypeEnum type, String label) {
            return this;
        }

        private void verify(CompletionRun run) {
            Assertions.assertEquals(expectedStatus.name(), run.result().getStatus(),
                    () -> name + " status mismatch: " + run.sql() + " -> " + labels(run.result()));
            if (expectedStatus == SqlCompletionStatusEnum.EMPTY) {
                Assertions.assertTrue(run.result().getCandidates().isEmpty(),
                        () -> name + " should not return candidates: " + labels(run.result()));
                return;
            }
            Assertions.assertFalse(run.result().getCandidates().isEmpty(),
                    () -> name + " should return candidates: " + run.sql());
            assertAllowedTypes(run);
            for (ExpectedCandidate expected : mustHave) {
                Assertions.assertTrue(hasCandidate(run.result(), expected),
                        () -> name + " missing " + expected + " in " + labels(run.result()));
            }
            for (ExpectedCandidate expected : mustNotHave) {
                Assertions.assertFalse(hasCandidate(run.result(), expected),
                        () -> name + " should not contain " + expected + " in " + labels(run.result()));
            }
        }

        private void assertAllowedTypes(CompletionRun run) {
        }
    }

    private static boolean hasCandidate(SqlCompletionResponse result, ExpectedCandidate expected) {
        return result.getCandidates().stream()
                .anyMatch(candidate -> candidate.getType() == expected.type()
                        && expected.label().equals(candidate.getLabel()));
    }

    private static final class SnippetPrefixCase {

        private final String statementKeyword;
        private final String snippetLabel;

        private SnippetPrefixCase(String statementKeyword, String snippetLabel) {
            this.statementKeyword = statementKeyword;
            this.snippetLabel = snippetLabel;
        }

        private List<CompletionExpectation> progressionCases() {
            List<CompletionExpectation> cases = new ArrayList<>();
            for (int i = 1; i <= statementKeyword.length(); i++) {
                String prefix = statementKeyword.substring(0, i);
                cases.add(expect("snippet-prefix-" + snippetLabel + "-" + prefix, prefix + "{caret}")
                        .denyMetadataTypes()
                        .must(SqlCompletionCandidateTypeEnum.SNIPPET, snippetLabel));
            }
            return cases;
        }
    }

    private static final class StatementPhraseCase {

        private final String name;
        private final List<String> words;

        private StatementPhraseCase(String name, List<String> words) {
            this.name = name;
            this.words = words;
        }

        private List<CompletionExpectation> progressionCases() {
            List<CompletionExpectation> cases = new ArrayList<>();
            StringBuilder before = new StringBuilder();
            for (String word : words) {
                if (before.isEmpty()) {
                    for (int i = 1; i <= word.length(); i++) {
                        String prefix = word.substring(0, i);
                        cases.add(expect(name + "-" + word + "-" + prefix, prefix + "{caret}")
                                .allow(SqlCompletionCandidateTypeEnum.KEYWORD, SqlCompletionCandidateTypeEnum.SNIPPET)
                                .mustKeyword(word.toUpperCase(Locale.ROOT)));
                    }
                } else {
                    cases.addAll(keywordProgression(name + "-" + word, before.toString(), word));
                }
                before.append(word).append(' ');
            }
            return cases;
        }
    }

    private record ExpectedCandidate(SqlCompletionCandidateTypeEnum type, String label) {
    }

    private static final class ProgressionMetadataProvider implements ISqlCompletionMetadataProvider {

        private final List<DbSqlCompletionMetadataRequest> requests = new ArrayList<>();

        @Override
        public SqlCompletionMetadataResponse list(DbSqlCompletionMetadataRequest request) {
            requests.add(request);
            return switch (request.type()) {
                case "TABLE", "TABLE_VIEW" -> SqlCompletionMetadataResponse.of(tables(request.prefix()));
                case "DATABASE" -> SqlCompletionMetadataResponse.of(databases(request.prefix()));
                case "COLUMN" -> SqlCompletionMetadataResponse.of(columns(request.scope().table(), request.prefix()));
                case "FUNCTION" -> SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.FUNCTION, request.prefix(), "calc_discount", "area_total"));
                case "PROCEDURE" -> SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.PROCEDURE, request.prefix(), "sync_orders", "sync_users"));
                default -> SqlCompletionMetadataResponse.of(List.of());
            };
        }

        private void reset() {
            requests.clear();
        }

        private static List<SqlCompletionCandidate> tables(String prefix) {
            return List.of("orders", "order_items", "users", "customers", "enterprise_authentication").stream()
                    .filter(value -> matchesPrefix(value, prefix))
                    .map(value -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(
                                SqlCompletionCandidateTypeEnum.TABLE, value);
                        candidate.setTableName(value);
                        return candidate;
                    })
                    .toList();
        }

        private static List<SqlCompletionCandidate> databases(String prefix) {
            return List.of("enterprise_gateway_dev", "analytics").stream()
                    .filter(value -> matchesPrefix(value, prefix))
                    .map(value -> SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.DATABASE, value))
                    .toList();
        }

        private static List<SqlCompletionCandidate> columns(String table, String prefix) {
            List<String> names = switch (String.valueOf(table).replace("`", "")) {
                case "orders" -> List.of("id", "amount", "status", "customer_id", "created_at", "where_note");
                case "order_items" -> List.of("id", "order_id", "sku_id", "qty");
                case "users" -> List.of("id", "name", "status");
                case "customers" -> List.of("id", "name", "email");
                case "enterprise_authentication" -> List.of(
                        "id", "auth_status", "auth_type", "auth_info_ext", "organization_id", "operator_id");
                default -> List.of("id", "status");
            };
            return names.stream()
                    .filter(value -> matchesPrefix(value, prefix))
                    .map(value -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(
                                SqlCompletionCandidateTypeEnum.COLUMN, value);
                        candidate.setTableName(table);
                        candidate.setColumnName(value);
                        return candidate;
                    })
                    .toList();
        }

        private static List<SqlCompletionCandidate> objects(SqlCompletionCandidateTypeEnum type,
                                                            String prefix,
                                                            String... names) {
            return List.of(names).stream()
                    .filter(value -> matchesPrefix(value, prefix))
                    .map(value -> SqlCompletionCandidate.of(type, value))
                    .toList();
        }

        private static boolean matchesPrefix(String value, String prefix) {
            return prefix == null || prefix.isBlank()
                    || value.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT));
        }
    }
}
