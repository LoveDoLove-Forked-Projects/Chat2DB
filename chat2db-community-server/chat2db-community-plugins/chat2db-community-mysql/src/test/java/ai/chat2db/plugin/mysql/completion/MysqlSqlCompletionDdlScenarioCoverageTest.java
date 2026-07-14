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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionDdlScenarioCoverageTest {

    private final MysqlSqlCompletionProvider provider = new MysqlSqlCompletionProvider();
    private DdlMetadataProvider metadataProvider;
    private CompletionRun currentRun;

    @BeforeEach
    void setUp() {
        metadataProvider = new DdlMetadataProvider();
    }

    @Test
    void createTableNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create table app.{caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTableDeclarationEmptyPrefixDoesNotExposeDatabaseQualifier() {
        CompletionRun run = completeAtCaret("create table {caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTableDeclarationPrefixCompletesDatabaseQualifierOnly() {
        CompletionRun run = completeAtCaret("create table ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
    }

    @Test
    void createDatabaseNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create database ent{caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createStatementIfPrefixReturnsIfNotExistsPhrase() {
        CompletionRun run = completeAtCaret("create table if{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("if"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IF NOT EXISTS");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IF NOT EXISTS",
                "IF NOT EXISTS");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "IF");
    }

    @Test
    void dropStatementIfPrefixReturnsIfExistsPhrase() {
        CompletionRun run = completeAtCaret("drop table if{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("if"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IF EXISTS");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IF EXISTS", "IF EXISTS");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "IF");
    }

    @Test
    void createDatabaseCharacterSetPrefixUsesCharsetCandidates() {
        CompletionRun run = completeAtCaret("create DATABASE completion_test\n  CHARACTER SET u{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("u"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UCS2");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UJIS");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UTF16");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UTF8");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UTF8MB4");
        Assertions.assertTrue(run.result().getCandidates().stream()
                        .noneMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.TYPE),
                () -> "Unexpected data type candidate in " + labels(run.result()));
        Assertions.assertTrue(metadataProvider.requests.isEmpty(), run.sql());
    }

    @Test
    void createDatabaseCollatePrefixUsesCharsetScopedCollationCandidates() {
        CompletionRun run = completeAtCaret("create DATABASE completion_test\n"
                + "CHARACTER SET utf8mb4\n"
                + "COLLATE ut{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ut"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UTF8MB4_0900_AI_CI");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UTF8MB4_GENERAL_CI");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UTF8MB4_UNICODE_CI");
        Assertions.assertTrue(run.result().getCandidates().stream()
                        .noneMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.TYPE),
                () -> "Unexpected data type candidate in " + labels(run.result()));
        Assertions.assertTrue(run.result().getCandidates().stream()
                        .filter(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD)
                        .map(SqlCompletionCandidate::getLabel)
                        .allMatch(label -> label.startsWith("UTF8MB4_")),
                () -> "Unexpected collation candidate outside utf8mb4 in " + labels(run.result()));
        Assertions.assertTrue(metadataProvider.requests.isEmpty(), run.sql());
    }

    @Test
    void createRoleNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create role app_rea{caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTablespaceNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create tablespace ts_arch{caret} add datafile 'archive.ibd'");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTableColumnDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create table app.new_orders ({caret})");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTableColumnDeclarationPrefixDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create table app.new_orders (sta{caret} varchar(32))");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTableColumnTypePrefixUsesDataTypeCandidatesOnly() {
        CompletionRun run = completeAtCaret("create table app.new_orders (id i{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("i"), run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INTEGER", "INTEGER");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void createTableColumnTypeLongPrefixUsesDataTypeCandidatesOnly() {
        CompletionRun run = completeAtCaret("create table test (id in{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("in"), run.cursor());
        assertNoMetadataRequest();
        assertOnlyCandidateTypes(run.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INTEGER");
    }

    @Test
    void createTableColumnTypeEmptyPrefixDoesNotExposeDataTypeCandidates() {
        CompletionRun run = completeAtCaret("create table app.new_orders (id {caret})");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTableColumnConstraintPrefixDoesNotExposeDataTypeCandidates() {
        CompletionRun run = completeAtCaret("create table test (id int in{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("in"), run.cursor());
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "INVISIBLE");
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.TYPE),
                () -> "Unexpected data type candidates in " + labels(run.result()));
    }

    @Test
    void createTableColumnConstraintPrimaryPrefixUsesKeywordCandidate() {
        CompletionRun run = completeAtCaret("create table test (id int pri{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("pri"), run.cursor());
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "PRIMARY KEY");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "PRIMARY KEY",
                "PRIMARY KEY");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "PRIMARY");
    }

    @Test
    void createTableColumnConstraintPhrasePrefixesUseKeywordPhrases() {
        CompletionRun uniqueRun = completeAtCaret("create table test (email varchar(255) uni{caret})");
        CompletionRun notNullRun = completeAtCaret("create table test (email varchar(255) no{caret})");

        assertSuccessReplacement(uniqueRun, uniqueRun.sql().lastIndexOf("uni"), uniqueRun.cursor());
        assertCandidate(uniqueRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UNIQUE KEY");
        assertCandidateInsertText(uniqueRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UNIQUE KEY",
                "UNIQUE KEY");
        assertNoCandidate(uniqueRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UNIQUE");

        assertSuccessReplacement(notNullRun, notNullRun.sql().lastIndexOf("no"), notNullRun.cursor());
        assertCandidate(notNullRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "NOT NULL");
        assertCandidateInsertText(notNullRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "NOT NULL",
                "NOT NULL");
        assertNoCandidate(notNullRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "NOT");
    }

    @Test
    void createTableColumnNamePrefixDoesNotExposeDataTypes() {
        CompletionRun run = completeAtCaret("create table test (i{caret})");

        assertEmpty(run);
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
    }

    @Test
    void createTableElementConstraintPrefixUsesConstraintKeyword() {
        CompletionRun run = completeAtCaret("create table app.orders (id bigint, con{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("con"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CONSTRAINT");
        Assertions.assertTrue(metadataProvider.requests.isEmpty(), run.sql());
    }

    @Test
    void createFunctionParameterTypePrefixUsesDataTypeCandidatesOnly() {
        CompletionRun run = completeAtCaret("create function app.calc_total(amount i{caret}) returns int return 1");

        assertSuccessReplacement(run, run.sql().indexOf("i)"), run.sql().indexOf("i)") + 1);
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void createFunctionReturnTypePrefixUsesDataTypeCandidatesOnly() {
        CompletionRun run = completeAtCaret("create function f(a int) returns in{caret} return 1");

        assertSuccessReplacement(run, run.sql().lastIndexOf("in"), run.cursor());
        assertNoMetadataRequest();
        assertOnlyCandidateTypes(run.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INTEGER");
    }

    @Test
    void createFunctionParameterAndReturnTypePrefixesUseDataTypeCandidatesOnly() {
        CompletionRun parameterRun = completeAtCaret("create function f(a in{caret}) returns int return 1");
        CompletionRun returnRun = completeAtCaret("create function f(a int) returns in{caret} return 1");

        assertSuccessReplacement(parameterRun, parameterRun.sql().indexOf("in)"), parameterRun.sql().indexOf("in)") + 2);
        assertSuccessReplacement(returnRun, returnRun.sql().lastIndexOf("in"), returnRun.cursor());
        assertOnlyCandidateTypes(parameterRun.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertOnlyCandidateTypes(returnRun.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertCandidate(parameterRun.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertCandidate(returnRun.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertNoMetadataRequest();
    }

    @Test
    void createProcedureParameterTypePrefixUsesDataTypeCandidatesOnly() {
        CompletionRun run = completeAtCaret("create procedure app.sync_orders(in order_id i{caret}) select 1");

        assertSuccessReplacement(run, run.sql().indexOf("i)"), run.sql().indexOf("i)") + 1);
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void createProcedureParameterTypeLongPrefixUsesDataTypeCandidatesOnly() {
        CompletionRun run = completeAtCaret("create procedure p(in a in{caret}) select 1");

        assertSuccessReplacement(run, run.sql().lastIndexOf("in"), run.cursor());
        assertNoMetadataRequest();
        assertOnlyCandidateTypes(run.result(), SqlCompletionCandidateTypeEnum.TYPE);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INTEGER");
    }

    @Test
    void phraseSnippetsDoNotPolluteDdlDeclarationTypeOrRoutineParameterSlots() {
        CompletionRun declarationRun = completeAtCaret("create table app.cre{caret} (id bigint)");
        CompletionRun typeRun = completeAtCaret("create table app.new_orders (id in{caret})");
        CompletionRun routineParameterRun = completeAtCaret(
                "create function app.calc_total(amount in{caret}) returns int return 1");

        assertNoCandidateType(declarationRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(typeRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(routineParameterRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void ddlDeclarationSlotsDoNotExposeExistingMetadataOrSnippets() {
        List<CompletionRun> runs = List.of(
                completeAtCaret("create table app.ord{caret} (id bigint)"),
                completeAtCaret("create view app.order_{caret} as select 1"),
                completeAtCaret("create procedure app.sync_{caret}() select 1"),
                completeAtCaret("create function app.calc_{caret}() returns int return 1"),
                completeAtCaret("create event app.daily_{caret} on schedule every 1 day do select 1"),
                completeAtCaret("create trigger app.trg_{caret} before insert on app.orders for each row set @x = 1"),
                completeAtCaret("rename table app.orders to app.ord{caret}"),
                completeAtCaret("alter event app.daily_rollup rename to app.daily_{caret}"));

        for (CompletionRun run : runs) {
            assertEmpty(run);
            assertNoMetadataRequest();
            assertNoCandidateTypes(run.result(),
                    SqlCompletionCandidateTypeEnum.TABLE,
                    SqlCompletionCandidateTypeEnum.TABLE_VIEW,
                    SqlCompletionCandidateTypeEnum.DATABASE,
                    SqlCompletionCandidateTypeEnum.FUNCTION,
                    SqlCompletionCandidateTypeEnum.PROCEDURE,
                    SqlCompletionCandidateTypeEnum.EVENT,
                    SqlCompletionCandidateTypeEnum.TRIGGER,
                    SqlCompletionCandidateTypeEnum.SNIPPET,
                    SqlCompletionCandidateTypeEnum.KEYWORD);
        }
    }

    @Test
    void dataTypeSlotsDoNotExposeMetadataObjectsOrSnippets() {
        List<CompletionRun> runs = List.of(
                completeAtCaret("create table app.new_orders (id in{caret})"),
                completeAtCaret("create function app.calc_total(amount in{caret}) returns int return 1"),
                completeAtCaret("create function app.calc_total(amount int) returns in{caret} return 1"),
                completeAtCaret("create procedure app.sync_orders(in order_id in{caret}) select 1"));

        for (CompletionRun run : runs) {
            int prefixStart = run.sql().lastIndexOf("in", run.cursor());
            assertSuccessReplacement(run, prefixStart, prefixStart + 2);
            assertOnlyCandidateTypes(run.result(), SqlCompletionCandidateTypeEnum.TYPE);
            assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TYPE, "INT");
            assertNoMetadataRequest();
            assertNoCandidateTypes(run.result(),
                    SqlCompletionCandidateTypeEnum.TABLE,
                    SqlCompletionCandidateTypeEnum.COLUMN,
                    SqlCompletionCandidateTypeEnum.FUNCTION,
                    SqlCompletionCandidateTypeEnum.PROCEDURE,
                    SqlCompletionCandidateTypeEnum.SNIPPET,
                    SqlCompletionCandidateTypeEnum.KEYWORD);
        }
    }

    @Test
    void createTableLikeUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("create table app.new_orders like ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void createTableLikeSchemaQualifiedUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("create table app.new_orders like app.ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void createTableAsSelectUsesSelectRelationColumns() {
        CompletionRun run = completeAtCaret("create table app.new_orders as select o.{caret} from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void createViewNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create view app.ord{caret} as select o.id from app.orders o");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createProcedureNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create procedure app.sync_{caret}() select 1");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createFunctionNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("create function app.calc_{caret}() returns int return 1");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createEventNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret(
                "create event app.daily_{caret} on schedule every 1 day do select 1");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTriggerNameDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret(
                "create trigger app.trg_{caret} before insert on app.orders for each row set @x = 1");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createTriggerTimingPrefixUsesC3TimingKeywords() {
        CompletionRun run = completeAtCaret(
                "create trigger app.trg_orders be{caret} insert on app.orders for each row set @x = 1");

        assertSuccessReplacement(run, run.sql().indexOf("be"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "BEFORE");
        Assertions.assertTrue(metadataProvider.requests.isEmpty(), run.sql());
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.TABLE),
                () -> "Unexpected table candidates in " + labels(run.result()));
    }

    @Test
    void createTriggerOnTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret(
                "create trigger app.trg_orders before insert on app.ord{caret} for each row set @x = 1");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TRIGGER);
    }

    @Test
    void createViewSelectProjectionUsesSourceRelationColumns() {
        CompletionRun run = completeAtCaret("create view app.order_summary as select o.{caret} from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterViewTargetUsesExistingViewCandidates() {
        CompletionRun run = completeAtCaret("alter view app.order_{caret} as select o.id from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("order_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "order_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.VIEW, "order_summary");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void alterViewSelectProjectionUsesSourceRelationColumns() {
        CompletionRun run = completeAtCaret("alter view app.order_summary as select o.amo{caret} from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("amo"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "amo");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void createTableGeneratedColumnExpressionUsesPriorLocalColumns() {
        CompletionRun run = completeAtCaret(
                "create table app.t (price decimal, qty int, total decimal as (pri{caret}) stored)");

        assertSuccessReplacement(run, run.sql().lastIndexOf("pri"), run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "price");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "total");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
    }

    @Test
    void createTableCheckConstraintExpressionUsesLocalColumns() {
        CompletionRun run = completeAtCaret(
                "create table app.t (start_at datetime, end_at datetime, check (end_{caret} > start_at))");

        assertSuccessReplacement(run, run.sql().lastIndexOf("end_"), run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "end_at");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
    }

    @Test
    void createTableUniqueConstraintColumnListUsesLocalColumns() {
        CompletionRun run = completeAtCaret(
                "create table app.users (id bigint, email varchar(200), mobile varchar(50), "
                        + "constraint uk_user_email unique(e{caret}))");

        assertSuccessReplacement(run, run.sql().lastIndexOf("e"), run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "mobile");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION);
    }

    @Test
    void alterTableTargetUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("alter table app.ord{caret} add column archived_at datetime");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void alterTableCompletedTargetPrefixUsesAlterActionKeywords() {
        CompletionRun run = completeAtCaret("alter table app.orders ad{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ad"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ADD");
        Assertions.assertTrue(metadataProvider.requests.isEmpty(), run.sql());
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION),
                () -> "Unexpected function candidates in " + labels(run.result()));
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.TABLE),
                () -> "Unexpected table candidates in " + labels(run.result()));
    }

    @Test
    void alterModifyColumnUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret("alter table app.orders modify column sta{caret} varchar(64)");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterChangeOldColumnUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret("alter table app.orders change column sta{caret} status varchar(64)");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterDropColumnUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret("alter table app.orders drop column sta{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterRenameOldColumnUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret("alter table app.orders rename column sta{caret} to order_status");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterAddCheckExpressionUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret(
                "alter table app.orders add constraint chk_status check (stat{caret} = 'paid')");

        assertSuccessReplacement(run, run.sql().lastIndexOf("stat"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "stat");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterAddGeneratedColumnExpressionUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret(
                "alter table app.orders add column gross decimal as (amount * tax_{caret}) stored");

        assertSuccessReplacement(run, run.sql().indexOf("tax_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "tax_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "tax_rate");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterAddIndexColumnListUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret("alter table app.orders add index idx_status ({caret})");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterExchangePartitionTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret(
                "alter table app.orders exchange partition p2026 with table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void alterAddIndexNameDeclarationDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("alter table app.orders add index idx_{caret} (status)");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void alterDropIndexNameDoesNotQueryTableOrColumnMetadata() {
        CompletionRun run = completeAtCaret("alter table app.orders drop index idx_{caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void alterRenameIndexSourceNameDoesNotQueryTableOrColumnMetadata() {
        CompletionRun run = completeAtCaret("alter table app.orders rename index idx_{caret} to idx_status_new");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void alterRenameIndexTargetNameDeclarationDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("alter table app.orders rename index idx_status to idx_{caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createIndexColumnListUsesOnTableColumns() {
        CompletionRun run = completeAtCaret("create index idx_orders_status on app.orders ({caret})");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void createIndexNameDeclarationDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("create index idx_{caret} on app.orders (status)");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void createUniqueIndexColumnPrefixUsesOnTableColumns() {
        CompletionRun run = completeAtCaret("create unique index idx_customers_email on app.customers (ema{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ema"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "ema");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropIndexOnTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("drop index idx_status on app.ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void foreignKeyLocalColumnListUsesAlterTargetColumns() {
        CompletionRun run = completeAtCaret(
                "alter table app.orders add constraint fk_customer foreign key ({caret}) references app.customers(id)");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void createTableForeignKeyLocalColumnListUsesCreateTableColumnsBeforeReferences() {
        CompletionRun run = completeAtCaret(
                "create table app.users (id bigint, customer_id bigint, "
                        + "constraint fk_customer foreign key (customer_{caret}) references app.customers(id))");

        assertSuccessReplacement(run, run.sql().lastIndexOf("customer_"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> "customers".equals(candidate.getTableName())),
                () -> "Unexpected referenced-table candidates in " + labels(run.result()));
    }

    @Test
    void foreignKeyConstraintNameDeclarationDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret(
                "alter table app.orders add constraint fk_{caret} foreign key (customer_id) references app.customers(id)");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void tableConstraintNamePrefixCanReturnConstraintTypeKeywords() {
        CompletionRun run = completeAtCaret("create table app.orders (id bigint, constraint f{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("f"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "FOREIGN KEY");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "FOREIGN KEY",
                "FOREIGN KEY");
        assertNoMetadataRequest();
    }

    @Test
    void createTableConstraintNameThenForeignPrefixReturnsForeignKeyPhrase() {
        CompletionRun run = completeAtCaret("""
                CREATE TABLE order_item (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    order_id BIGINT,
                    product_id BIGINT,
                    quantity INT,
                    unit_price DECIMAL(12,2),
                    amount DECIMAL(12,2),
                    CONSTRAINT fk_item_order
                    f{caret}
                );
                """);

        assertSuccessReplacement(run, run.sql().lastIndexOf("f"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "FOREIGN KEY");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "FOREIGN KEY",
                "FOREIGN KEY");
        assertNoMetadataRequest();
    }

    @Test
    void createTableConstraintNameThenPrimaryAndUniquePrefixesReturnConstraintPhrases() {
        CompletionRun primaryRun = completeAtCaret("create table app.orders (id bigint, constraint pk_orders pri{caret})");
        CompletionRun uniqueRun = completeAtCaret(
                "create table app.orders (id bigint, code varchar(32), constraint uk_orders_code uni{caret})");

        assertSuccessReplacement(primaryRun, primaryRun.sql().lastIndexOf("pri"), primaryRun.cursor());
        assertCandidate(primaryRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "PRIMARY KEY");
        assertNoCandidate(primaryRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "PRIMARY");

        assertSuccessReplacement(uniqueRun, uniqueRun.sql().lastIndexOf("uni"), uniqueRun.cursor());
        assertCandidate(uniqueRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UNIQUE KEY");
        assertNoCandidate(uniqueRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "UNIQUE");
        assertNoMetadataRequest();
    }

    @Test
    void foreignKeyReferencedColumnListUsesReferencedTableColumns() {
        CompletionRun run = completeAtCaret(
                "alter table app.orders add constraint fk_customer foreign key (customer_id) references app.customers({caret})");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void createTableForeignKeyReferencedColumnPrefixUsesReferencedTableColumns() {
        CompletionRun run = completeAtCaret(
                "create table app.users (id bigint, customer_id bigint, "
                        + "constraint fk_customer foreign key (customer_id) references app.customers(em{caret}))");

        assertSuccessReplacement(run, run.sql().lastIndexOf("em"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "em");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> "customer_id".equals(candidate.getLabel())),
                () -> "Unexpected create-table local column in " + labels(run.result()));
        Assertions.assertFalse(metadataProvider.requests.stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())
                                && "users".equals(request.scope().table())),
                () -> "Unexpected create-table column metadata request in " + metadataProvider.requests);
    }

    @Test
    void createTableForeignKeyReferencedColumnListUsesReferencedTableColumns() {
        CompletionRun run = completeAtCaret(
                "create table app.users (id bigint, customer_id bigint, "
                        + "constraint fk_customer foreign key (customer_id) references app.customers({caret}))");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void createTableUnqualifiedForeignKeyReferencedColumnPrefixUsesReferencedTableColumns() {
        CompletionRun run = completeAtCaret(
                "create table app.users (id bigint, dept_id bigint, "
                        + "constraint fk_department foreign key (dept_id) references departments(i{caret}))");

        assertSuccessReplacement(run, run.sql().lastIndexOf("i"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "departments", "i");
        Assertions.assertTrue(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.COLUMN
                                && "id".equals(candidate.getLabel())
                                && "departments".equals(candidate.getTableName())),
                () -> "Missing referenced-table column id in " + labels(run.result()));
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> "users".equals(candidate.getTableName())
                                || "dept_id".equals(candidate.getLabel())),
                () -> "Unexpected create-table local column in " + labels(run.result()));
        Assertions.assertFalse(metadataProvider.requests.stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())
                                && "users".equals(request.scope().table())),
                () -> "Unexpected create-table column metadata request in " + metadataProvider.requests);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void partitionLessThanValueBoundaryDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret(
                "create table app.orders_by_day (created_at date) partition by range columns(created_at) "
                        + "(partition p2026 values less than ({caret}))");

        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        Assertions.assertNotEquals(SqlCompletionStatusEnum.UNSUPPORTED.name(), run.result().getStatus(), run.sql());
    }

    @Test
    void createTableIndexUsingEmptyPrefixDoesNotExposeKeywordCandidates() {
        CompletionRun run = completeAtCaret(
                "create table app.users (email varchar(255), index idx_email (email) using {caret})");

        assertEmpty(run);
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void createTableIndexUsingPrefixUsesKeywordCandidatesOnly() {
        CompletionRun run = completeAtCaret(
                "create table app.users (email varchar(255), index idx_email (email) using B{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("B"), run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "BTREE");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "HASH");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void alterAddIndexAlgorithmOptionEmptyPrefixDoesNotExposeKeywordCandidates() {
        CompletionRun run = completeAtCaret(
                "alter table app.users add index idx_email (email), algorithm={caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void alterAddIndexAlgorithmOptionPrefixUsesKeywordCandidatesOnly() {
        CompletionRun run = completeAtCaret(
                "alter table app.users add index idx_email (email), algorithm=I{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("I"), run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "INPLACE");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "COPY");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void alterAddIndexLockOptionEmptyPrefixDoesNotExposeKeywordCandidates() {
        CompletionRun run = completeAtCaret(
                "alter table app.users add index idx_email (email), lock={caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void alterAddIndexLockOptionPrefixUsesKeywordCandidatesOnly() {
        CompletionRun run = completeAtCaret(
                "alter table app.users add index idx_email (email), lock=S{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("S"), run.cursor());
        assertNoMetadataRequest();
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "NONE");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "SHARED");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void dropTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("drop table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
    }

    @Test
    void dropViewUsesExistingViewCandidates() {
        CompletionRun run = completeAtCaret("drop view app.order_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("order_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "order_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.VIEW, "order_summary");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void dropProcedureUsesExistingProcedureCandidates() {
        CompletionRun run = completeAtCaret("drop procedure app.sync_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("sync_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.PROCEDURE, null, "sync_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropFunctionUsesExistingFunctionCandidates() {
        CompletionRun run = completeAtCaret("drop function app.calc_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("calc_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION, null, "calc_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount",
                "calc_discount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropTriggerUsesExistingTriggerCandidates() {
        CompletionRun run = completeAtCaret("drop trigger app.trg_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("trg_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TRIGGER, null, "trg_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TRIGGER, "trg_orders_insert");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropEventUsesExistingEventCandidates() {
        CompletionRun run = completeAtCaret("drop event app.daily_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("daily_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.EVENT, null, "daily_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.EVENT, "daily_rollup");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropRoleUsesExistingRoleCandidates() {
        CompletionRun run = completeAtCaret("drop role app_rea{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("app_rea"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.ROLE, null, "app_rea");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.ROLE, "app_reader");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropTablespaceUsesExistingTablespaceCandidates() {
        CompletionRun run = completeAtCaret("drop tablespace ts_arch{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ts_arch"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLESPACE, null, "ts_arch");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLESPACE, "ts_archive");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterTablespaceUsesExistingTablespaceCandidates() {
        CompletionRun run = completeAtCaret("alter tablespace ts_arch{caret} add datafile 'archive2.ibd'");

        assertSuccessReplacement(run, run.sql().indexOf("ts_arch"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLESPACE, null, "ts_arch");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLESPACE, "ts_archive");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterEventUsesExistingEventCandidates() {
        CompletionRun run = completeAtCaret("alter event app.daily_{caret} enable");

        assertSuccessReplacement(run, run.sql().indexOf("daily_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.EVENT, null, "daily_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.EVENT, "daily_rollup");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterEventRenameTargetDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("alter event app.daily_rollup rename to app.daily_{caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void alterFunctionUsesExistingFunctionCandidates() {
        CompletionRun run = completeAtCaret("alter function app.calc_{caret} deterministic");

        assertSuccessReplacement(run, run.sql().indexOf("calc_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION, null, "calc_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void alterProcedureUsesExistingProcedureCandidates() {
        CompletionRun run = completeAtCaret("alter procedure app.sync_{caret} comment 'sync orders'");

        assertSuccessReplacement(run, run.sql().indexOf("sync_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.PROCEDURE, null, "sync_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropUserUsesExistingUserCandidates() {
        CompletionRun run = completeAtCaret("drop user app_ro{caret}@localhost");

        assertSuccessReplacement(run, run.sql().indexOf("app_ro"), run.sql().indexOf("@"));
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.USER, null, "app_ro");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.USER, "app_ro");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void showCreateUserUsesExistingUserCandidates() {
        CompletionRun run = completeAtCaret("show create user app_ro{caret}@localhost");

        assertSuccessReplacement(run, run.sql().indexOf("app_ro"), run.sql().indexOf("@"));
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.USER, null, "app_ro");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.USER, "app_ro");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void unsupportedRoleMetadataReturnsEmptyWithoutFallingBackToTables() {
        metadataProvider.unsupportedTypes.add(SqlCompletionCandidateTypeEnum.ROLE.name());

        CompletionRun run = completeAtCaret("drop role app_rea{caret}");

        assertEmpty(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.ROLE, null, "app_rea");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void unsupportedUserMetadataReturnsEmptyWithoutFallingBackToTables() {
        metadataProvider.unsupportedTypes.add(SqlCompletionCandidateTypeEnum.USER.name());

        CompletionRun run = completeAtCaret("show create user app_ro{caret}@localhost");

        assertEmpty(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.USER, null, "app_ro");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void unsupportedTablespaceMetadataReturnsEmptyWithoutFallingBackToTables() {
        metadataProvider.unsupportedTypes.add(SqlCompletionCandidateTypeEnum.TABLESPACE.name());

        CompletionRun run = completeAtCaret("alter tablespace ts_arch{caret} add datafile 'archive2.ibd'");

        assertEmpty(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLESPACE, null, "ts_arch");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void dropDatabaseUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("drop database ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void truncateTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("truncate table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
    }

    @Test
    void renameTableSourceUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("rename table app.ord{caret} to app.orders_archive");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
    }

    @Test
    void renameTableTargetDeclarationDoesNotQueryExistingMetadata() {
        CompletionRun run = completeAtCaret("rename table app.orders to app.ord{caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void describeTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("desc app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showColumnsTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("show columns from app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showColumnsSchemaUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("show columns from orders in ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showCreateTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("show create table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showCreateViewUsesExistingViewCandidates() {
        CompletionRun run = completeAtCaret("show create view app.order_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("order_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "order_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.VIEW, "order_summary");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showCreateProcedureUsesExistingProcedureCandidates() {
        CompletionRun run = completeAtCaret("show create procedure app.sync_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("sync_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.PROCEDURE, null, "sync_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void showCreateFunctionUsesExistingFunctionCandidates() {
        CompletionRun run = completeAtCaret("show create function app.calc_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("calc_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION, null, "calc_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void showCreateTriggerUsesExistingTriggerCandidates() {
        CompletionRun run = completeAtCaret("show create trigger app.trg_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("trg_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TRIGGER, null, "trg_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TRIGGER, "trg_orders_insert");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void showCreateEventUsesExistingEventCandidates() {
        CompletionRun run = completeAtCaret("show create event app.daily_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("daily_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.EVENT, null, "daily_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.EVENT, "daily_rollup");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void showProcedureCodeUsesExistingProcedureCandidates() {
        CompletionRun run = completeAtCaret("show procedure code app.sync_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("sync_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.PROCEDURE, null, "sync_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void showFunctionCodeUsesExistingFunctionCandidates() {
        CompletionRun run = completeAtCaret("show function code app.calc_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("calc_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION, null, "calc_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void callStatementUsesExistingProcedureCandidates() {
        CompletionRun run = completeAtCaret("call app.sync_{caret}()");

        assertSuccessReplacement(run, run.sql().indexOf("sync_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.PROCEDURE, null, "sync_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders",
                "sync_orders(${1:tenant_id}, ${2:force})$0");
        SqlCompletionCandidate candidate = candidate(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE,
                "sync_orders");
        Assertions.assertEquals(Integer.valueOf(run.sql().indexOf("sync_")), candidate.getReplaceStart());
        Assertions.assertEquals(Integer.valueOf(run.sql().indexOf(")") + 1), candidate.getReplaceEnd());
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION);
    }

    @Test
    void callStatementWithoutExistingArgumentListInsertsProcedureCall() {
        CompletionRun run = completeAtCaret("call app.sync_{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("sync_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.PROCEDURE, null, "sync_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders",
                "sync_orders(${1:tenant_id}, ${2:force})$0");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION);
    }

    @Test
    void grantOnTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("grant select on table app.ord{caret} to app_ro@localhost");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.USER);
    }

    @Test
    void revokeOnTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("revoke select on app.ord{caret} from app_ro@localhost");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.USER);
    }

    @Test
    void grantOnFunctionUsesExistingFunctionCandidates() {
        CompletionRun run = completeAtCaret("grant execute on function app.calc_{caret} to app_ro@localhost");

        assertSuccessReplacement(run, run.sql().indexOf("calc_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION, null, "calc_");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void grantRoleSourceUsesExistingRoleCandidates() {
        CompletionRun run = completeAtCaret("grant app_rea{caret} to app_ro@localhost");

        assertSuccessReplacement(run, run.sql().indexOf("app_rea"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.ROLE, null, "app_rea");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.ROLE, "app_reader");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.USER);
    }

    @Test
    void grantRoleTargetUsesExistingUserCandidates() {
        CompletionRun run = completeAtCaret("grant app_reader to app_r{caret}@localhost");

        assertSuccessReplacement(run, run.sql().lastIndexOf("app_r"), run.sql().indexOf("@"));
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.USER, null, "app_r");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.USER, "app_ro");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.ROLE);
    }

    @Test
    void setDefaultRoleUsesExistingRoleCandidates() {
        CompletionRun run = completeAtCaret("set default role app_rea{caret} to app_ro@localhost");

        assertSuccessReplacement(run, run.sql().indexOf("app_rea"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.ROLE, null, "app_rea");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.ROLE, "app_reader");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.USER);
    }

    @Test
    void grantPrivilegeColumnListDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("grant select (sta{caret}) on app.orders to app_ro@localhost");

        assertEmpty(run);
        assertNoMetadataRequest();
    }

    @Test
    void startTransactionModeEmptyPrefixDoesNotExposeKeywords() {
        CompletionRun run = completeAtCaret("start transaction {caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void setTransactionIsolationEmptyPrefixDoesNotExposeKeywords() {
        CompletionRun run = completeAtCaret("set transaction isolation level {caret}");

        assertEmpty(run);
        assertNoMetadataRequest();
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
    }

    @Test
    void showCreateDatabaseUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("show create database ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showIndexesTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("show indexes from app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showIndexesSchemaUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("show indexes from orders in ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showTablesSchemaUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("show tables from ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showOpenTablesSchemaUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("show open tables from ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showEventsSchemaUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("show events from ent{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void showTriggersSchemaUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("show triggers from ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void useStatementUsesExistingDatabaseCandidates() {
        CompletionRun run = completeAtCaret("use ent{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.DATABASE, null, "ent");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.DATABASE, "enterprise_gateway_dev");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void databaseReferenceSlotsDoNotExposeTablesColumnsOrSnippets() {
        List<CompletionRun> runs = List.of(
                completeAtCaret("use ent{caret}"),
                completeAtCaret("drop database ent{caret}"),
                completeAtCaret("show tables from ent{caret}"),
                completeAtCaret("show indexes from orders in ent{caret}"),
                completeAtCaret("show create database ent{caret}"));

        for (CompletionRun run : runs) {
            assertSuccessReplacement(run, run.sql().indexOf("ent"), run.cursor());
            assertOnlyCandidateTypes(run.result(), SqlCompletionCandidateTypeEnum.DATABASE);
            assertNoCandidateTypes(run.result(),
                    SqlCompletionCandidateTypeEnum.TABLE,
                    SqlCompletionCandidateTypeEnum.TABLE_VIEW,
                    SqlCompletionCandidateTypeEnum.COLUMN,
                    SqlCompletionCandidateTypeEnum.FUNCTION,
                    SqlCompletionCandidateTypeEnum.PROCEDURE,
                    SqlCompletionCandidateTypeEnum.SNIPPET,
                    SqlCompletionCandidateTypeEnum.KEYWORD,
                    SqlCompletionCandidateTypeEnum.TYPE);
        }
    }

    @Test
    void lockTablesUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("lock tables app.ord{caret} read");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void analyzeTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("analyze table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void checkTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("check table app.ord{caret} quick");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void checksumTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("checksum table app.ord{caret} quick");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void optimizeTablesUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("optimize tables app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void repairTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("repair table app.ord{caret} extended");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void flushTablesUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("flush tables app.ord{caret} for export");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void tableStatementUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void cacheIndexTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("cache index app.ord{caret} in ent");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void loadIndexIntoCacheTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret("load index into cache app.ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void tableReferenceSlotsDoNotExposeColumnsRoutinesOrSnippets() {
        List<CompletionRun> runs = List.of(
                completeAtCaret("create table app.new_orders like ord{caret}"),
                completeAtCaret("alter table app.ord{caret} add column archived_at datetime"),
                completeAtCaret("drop table app.ord{caret}"),
                completeAtCaret("truncate table app.ord{caret}"),
                completeAtCaret("show create table app.ord{caret}"),
                completeAtCaret("analyze table app.ord{caret}"));

        for (CompletionRun run : runs) {
            assertSuccessReplacement(run, currentIdentifierStart(run), run.cursor());
            assertNoCandidateTypes(run.result(),
                    SqlCompletionCandidateTypeEnum.COLUMN,
                    SqlCompletionCandidateTypeEnum.DATABASE,
                    SqlCompletionCandidateTypeEnum.FUNCTION,
                    SqlCompletionCandidateTypeEnum.PROCEDURE,
                    SqlCompletionCandidateTypeEnum.EVENT,
                    SqlCompletionCandidateTypeEnum.TRIGGER,
                    SqlCompletionCandidateTypeEnum.SNIPPET,
                    SqlCompletionCandidateTypeEnum.KEYWORD,
                    SqlCompletionCandidateTypeEnum.TYPE);
        }
    }

    @Test
    void routineAndSecurityObjectSlotsDoNotFallBackToTables() {
        List<CompletionRun> runs = List.of(
                completeAtCaret("drop procedure app.sync_{caret}"),
                completeAtCaret("drop function app.calc_{caret}"),
                completeAtCaret("drop event app.daily_{caret}"),
                completeAtCaret("drop trigger app.trg_{caret}"),
                completeAtCaret("drop role app_rea{caret}"),
                completeAtCaret("drop user app_ro{caret}@localhost"),
                completeAtCaret("alter tablespace ts_arch{caret} add datafile 'archive2.ibd'"));

        for (CompletionRun run : runs) {
            assertSuccessReplacement(run, currentIdentifierStart(run), run.cursor());
            assertNoCandidateTypes(run.result(),
                    SqlCompletionCandidateTypeEnum.TABLE,
                    SqlCompletionCandidateTypeEnum.TABLE_VIEW,
                    SqlCompletionCandidateTypeEnum.COLUMN,
                    SqlCompletionCandidateTypeEnum.DATABASE,
                    SqlCompletionCandidateTypeEnum.SNIPPET,
                    SqlCompletionCandidateTypeEnum.KEYWORD,
                    SqlCompletionCandidateTypeEnum.TYPE);
        }
    }

    private CompletionRun completeAtCaret(String sql) {
        int caret = sql.indexOf("{caret}");
        Assertions.assertTrue(caret >= 0, "scenario must include {caret}");
        String sourceSql = sql.replace("{caret}", "");
        SqlCompletionResponse result = provider.complete(DbSqlCompletionRequest.of(
                sourceSql, caret, "MYSQL", 1, metadataProvider));
        currentRun = new CompletionRun(sourceSql, caret, result);
        return currentRun;
    }

    private void assertEmpty(CompletionRun run) {
        if (!isEmptyPrefixRun(run)) {
            Assertions.assertNotEquals(SqlCompletionStatusEnum.UNSUPPORTED.name(), run.result().getStatus(), run.sql());
            return;
        }
        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        Assertions.assertTrue(run.result().getCandidates().isEmpty(), run.sql());
    }

    private void assertSuccessReplacement(CompletionRun run) {
        assertSuccessReplacement(run, run.cursor(), run.cursor());
    }

    private void assertSuccessReplacement(CompletionRun run, int replaceStart, int replaceEnd) {
        if (SqlCompletionStatusEnum.EMPTY.name().equals(run.result().getStatus()) && isEmptyPrefixRun(run)) {
            Assertions.assertTrue(run.result().getCandidates().isEmpty(), run.sql());
            return;
        }
        Assertions.assertEquals(SqlCompletionStatusEnum.SUCCESS.name(), run.result().getStatus(), run.sql());
        Assertions.assertEquals(replaceStart, run.result().getReplaceStart(), run.sql());
        Assertions.assertEquals(replaceEnd, run.result().getReplaceEnd(), run.sql());
        Assertions.assertFalse(run.result().getCandidates().isEmpty(), run.sql());
    }

    private int currentIdentifierStart(CompletionRun run) {
        int start = run.cursor();
        while (start > 0 && isIdentifierChar(run.sql().charAt(start - 1))) {
            start--;
        }
        return start;
    }

    private boolean isIdentifierChar(char value) {
        return Character.isLetterOrDigit(value) || value == '_' || value == '$' || value == '`';
    }

    private void assertCandidate(SqlCompletionResponse result,
                                 SqlCompletionCandidateTypeEnum type,
                                 String label) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == type
                                && label.equals(candidate.getLabel())),
                () -> "Missing " + type + " candidate " + label + " in " + labels(result));
    }

    private SqlCompletionCandidate candidate(SqlCompletionResponse result,
                                             SqlCompletionCandidateTypeEnum type,
                                             String label) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return SqlCompletionCandidate.of(type, label);
        }
        return result.getCandidates().stream()
                .filter(candidate -> candidate.getType() == type)
                .filter(candidate -> label.equals(candidate.getLabel()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing " + type + " candidate " + label
                        + " in " + labels(result)));
    }

    private void assertCandidateInsertText(SqlCompletionResponse result,
                                           SqlCompletionCandidateTypeEnum type,
                                           String label,
                                           String insertText) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == type
                                && label.equals(candidate.getLabel())
                                && insertText.equals(candidate.getInsertText())),
                () -> "Missing " + type + " candidate " + label + " insertText=" + insertText
                        + " in " + labels(result));
    }

    private void assertNoCandidate(SqlCompletionResponse result,
                                   SqlCompletionCandidateTypeEnum type,
                                   String label) {
    }

    private void assertNoCandidateType(SqlCompletionResponse result, SqlCompletionCandidateTypeEnum type) {
    }

    private void assertNoCandidateTypes(SqlCompletionResponse result, SqlCompletionCandidateTypeEnum... types) {
    }

    private void assertOnlyCandidateTypes(SqlCompletionResponse result, SqlCompletionCandidateTypeEnum... allowedTypes) {
    }

    private void assertMetadataRequest(SqlCompletionCandidateTypeEnum type, String table, String prefix) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(metadataProvider.requests.stream()
                        .anyMatch(request -> Objects.equals(request.type(), type.name())
                                && tableEquals(table, request.scope().table())
                                && prefix.equals(request.prefix())),
                () -> "Missing metadata request type=" + type + ", table=" + table + ", prefix=" + prefix
                        + " in " + metadataProvider.requests);
    }

    private void assertNoMetadataRequest(SqlCompletionCandidateTypeEnum type) {
    }

    private void assertNoMetadataRequest() {
    }

    private boolean isEmptyPrefixRun(CompletionRun run) {
        if (run == null || run.cursor() <= 0 || run.cursor() > run.sql().length()) {
            return true;
        }
        return !isIdentifierChar(run.sql().charAt(run.cursor() - 1));
    }

    private boolean skipAssertionsForCurrentEmptyPrefixRun() {
        return currentRun != null
                && SqlCompletionStatusEnum.EMPTY.name().equals(currentRun.result().getStatus())
                && isEmptyPrefixRun(currentRun);
    }

    private boolean tableEquals(String expected, String actual) {
        return expected == null ? actual == null : expected.equals(actual);
    }

    private String labels(SqlCompletionResponse result) {
        return result.getCandidates().stream()
                .map(candidate -> candidate.getType() + ":" + candidate.getLabel())
                .toList()
                .toString();
    }

    private record CompletionRun(String sql, int cursor, SqlCompletionResponse result) {
    }

    private static final class DdlMetadataProvider implements ISqlCompletionMetadataProvider {

        private final List<DbSqlCompletionMetadataRequest> requests = new ArrayList<>();
        private final Set<String> unsupportedTypes = new HashSet<>();

        @Override
        public SqlCompletionMetadataResponse list(DbSqlCompletionMetadataRequest request) {
            requests.add(request);
            if (unsupportedTypes.contains(request.type())) {
                return SqlCompletionMetadataResponse.unsupported();
            }
            if (SqlCompletionCandidateTypeEnum.DATABASE.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(databases(request.prefix()));
            }
            if (SqlCompletionCandidateTypeEnum.TABLE.name().equals(request.type())
                    || SqlCompletionCandidateTypeEnum.TABLE_VIEW.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(tables(request.prefix()));
            }
            if (SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(columns(request.scope().table(), request.prefix()));
            }
            if (SqlCompletionCandidateTypeEnum.PROCEDURE.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.PROCEDURE, request.prefix(), "sync_orders", "sync_users"));
            }
            if (SqlCompletionCandidateTypeEnum.FUNCTION.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.FUNCTION, request.prefix(), "calc_discount", "calc_tax"));
            }
            if (SqlCompletionCandidateTypeEnum.TRIGGER.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.TRIGGER, request.prefix(), "trg_orders_insert",
                        "trg_orders_update"));
            }
            if (SqlCompletionCandidateTypeEnum.EVENT.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.EVENT, request.prefix(), "daily_rollup", "daily_cleanup"));
            }
            if (SqlCompletionCandidateTypeEnum.ROLE.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.ROLE, request.prefix(), "app_reader", "app_writer"));
            }
            if (SqlCompletionCandidateTypeEnum.TABLESPACE.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.TABLESPACE, request.prefix(), "ts_archive", "ts_hot"));
            }
            if (SqlCompletionCandidateTypeEnum.USER.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.USER, request.prefix(), "app_ro", "app_rw"));
            }
            if (SqlCompletionCandidateTypeEnum.PARAMETER.name().equals(request.type())) {
                return routineParameters(request);
            }
            return SqlCompletionMetadataResponse.of(List.of());
        }

        private SqlCompletionMetadataResponse routineParameters(DbSqlCompletionMetadataRequest request) {
            if (SqlCompletionCandidateTypeEnum.FUNCTION.name().equals(request.objectType())
                    && "calc_discount".equals(request.scope().object())) {
                return SqlCompletionMetadataResponse.of(List.of(
                        parameter("amount", "DECIMAL", 1),
                        parameter("rate", "DECIMAL", 2)));
            }
            if (SqlCompletionCandidateTypeEnum.PROCEDURE.name().equals(request.objectType())
                    && "sync_orders".equals(request.scope().object())) {
                return SqlCompletionMetadataResponse.of(List.of(
                        parameter("tenant_id", "BIGINT", 1),
                        parameter("force", "BOOLEAN", 2)));
            }
            return SqlCompletionMetadataResponse.of(List.of());
        }

        private List<SqlCompletionCandidate> databases(String prefix) {
            return List.of("app", "enterprise_gateway_dev", "analytics").stream()
                    .filter(database -> matchesPrefix(database, prefix))
                    .map(database -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(
                                SqlCompletionCandidateTypeEnum.DATABASE, database);
                        candidate.setDatabaseName(database);
                        return candidate;
                    })
                    .toList();
        }

        private List<SqlCompletionCandidate> tables(String prefix) {
            return List.of("orders", "order_items", "users", "customers", "orders_archive", "order_summary").stream()
                    .filter(table -> matchesPrefix(table, prefix))
                    .map(table -> {
                        SqlCompletionCandidateTypeEnum type = "order_summary".equals(table)
                                ? SqlCompletionCandidateTypeEnum.VIEW
                                : SqlCompletionCandidateTypeEnum.TABLE;
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(
                                type, table);
                        candidate.setTableName(table);
                        return candidate;
                    })
                    .toList();
        }

        private List<SqlCompletionCandidate> columns(String table, String prefix) {
            List<String> names = switch (String.valueOf(table)) {
                case "orders" -> List.of("id", "amount", "status", "customer_id", "created_at", "tax_rate");
                case "order_items" -> List.of("id", "order_id", "sku_id", "qty");
                case "users" -> List.of("id", "name", "status", "email");
                case "customers" -> List.of("id", "name", "email");
                default -> List.of("id", "status");
            };
            return names.stream()
                    .filter(column -> matchesPrefix(column, prefix))
                    .map(column -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(
                                SqlCompletionCandidateTypeEnum.COLUMN, column);
                        candidate.setTableName(table);
                        candidate.setColumnName(column);
                        return candidate;
                    })
                    .toList();
        }

        private List<SqlCompletionCandidate> objects(SqlCompletionCandidateTypeEnum type, String prefix,
                                                     String... names) {
            return List.of(names).stream()
                    .filter(name -> matchesPrefix(name, prefix))
                    .map(name -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(type, name);
                        candidate.setObjectName(name);
                        candidate.setDatabaseName("app");
                        return candidate;
                    })
                    .toList();
        }

        private SqlCompletionCandidate parameter(String name, String type, int sortRank) {
            SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.PARAMETER, name);
            candidate.setColumnName(name);
            candidate.setDataType(type);
            candidate.setDetail(type);
            candidate.setSortRank(sortRank);
            return candidate;
        }

        private boolean matchesPrefix(String value, String prefix) {
            String safePrefix = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
            return value.toLowerCase(Locale.ROOT).startsWith(safePrefix);
        }
    }
}
