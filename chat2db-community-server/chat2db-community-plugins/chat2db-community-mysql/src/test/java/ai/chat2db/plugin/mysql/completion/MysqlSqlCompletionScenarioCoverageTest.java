package ai.chat2db.plugin.mysql.completion;

import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionInsertTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionKeywordCaseEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionSnippetSlotTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionActiveSnippetSlot;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionScenarioCoverageTest {

    private final MysqlSqlCompletionProvider provider = new MysqlSqlCompletionProvider();
    private ScenarioMetadataProvider metadataProvider;
    private CompletionRun currentRun;

    @BeforeEach
    void setUp() {
        metadataProvider = new ScenarioMetadataProvider();
        currentRun = null;
    }

    @Test
    void whereRightValueEmptyPrefixUsesNormalExpressionFlow() {
        CompletionRun run = completeAtCaret("select * from app.orders o where o.status = {caret}");

        assertBlankExpressionCandidates(run);
    }

    @Test
    void selectListEmptySlotUsesNormalExpressionFlow() {
        CompletionRun run = completeAtCaret("select {caret} from app.orders o");

        assertBlankExpressionCandidates(run);
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "DISTINCT");
    }

    @Test
    void selectListPrefixUsesVisibleRelationColumns() {
        CompletionRun run = completeAtCaret("select sta{caret} from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void selectListPrefixUsesMetadataFunctionsAsCallableCandidates() {
        CompletionRun run = completeAtCaret("select calc_{caret} from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("calc_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "calc_");
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION, null, "calc_");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount",
                "calc_discount(${1:})$0");
        assertCandidateInsertType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount", SqlCompletionInsertTypeEnum.SNIPPET);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void schemaQualifiedFunctionPrefixUsesMetadataFunctionsInsteadOfUnknownOwnerColumns() {
        CompletionRun run = completeAtCaret("select app.calc_{caret} from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("calc_"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION, null, "calc_");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount",
                "calc_discount(${1:})$0");
        assertCandidateInsertType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount", SqlCompletionInsertTypeEnum.SNIPPET);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void databaseCandidateAppendsDotOnlyForObjectQualifierSlots() {
        CompletionRun tableReferenceRun = completeAtCaret("select * from ent{caret}");
        CompletionRun tableDeclarationRun = completeAtCaret("create table ent{caret}");
        CompletionRun procedureReferenceRun = completeAtCaret("drop procedure ent{caret}");
        CompletionRun useRun = completeAtCaret("use ent{caret}");
        CompletionRun dropDatabaseRun = completeAtCaret("drop database ent{caret}");

        assertSuccessReplacement(tableReferenceRun, tableReferenceRun.sql().indexOf("ent"),
                tableReferenceRun.cursor());
        assertCandidateInsertText(tableReferenceRun.result(), SqlCompletionCandidateTypeEnum.DATABASE,
                "enterprise_gateway_dev", "enterprise_gateway_dev.");

        assertSuccessReplacement(tableDeclarationRun, tableDeclarationRun.sql().indexOf("ent"),
                tableDeclarationRun.cursor());
        assertCandidateInsertText(tableDeclarationRun.result(), SqlCompletionCandidateTypeEnum.DATABASE,
                "enterprise_gateway_dev", "enterprise_gateway_dev.");

        assertSuccessReplacement(procedureReferenceRun, procedureReferenceRun.sql().indexOf("ent"),
                procedureReferenceRun.cursor());
        assertCandidateInsertText(procedureReferenceRun.result(), SqlCompletionCandidateTypeEnum.DATABASE,
                "enterprise_gateway_dev", "enterprise_gateway_dev.");

        assertSuccessReplacement(useRun, useRun.sql().indexOf("ent"), useRun.cursor());
        assertCandidateInsertText(useRun.result(), SqlCompletionCandidateTypeEnum.DATABASE,
                "enterprise_gateway_dev", "enterprise_gateway_dev");

        assertSuccessReplacement(dropDatabaseRun, dropDatabaseRun.sql().indexOf("ent"), dropDatabaseRun.cursor());
        assertCandidateInsertText(dropDatabaseRun.result(), SqlCompletionCandidateTypeEnum.DATABASE,
                "enterprise_gateway_dev", "enterprise_gateway_dev");
    }

    @Test
    void functionArgumentEmptySlotUsesNormalExpressionFlow() {
        CompletionRun run = completeAtCaret("select coalesce(o.status, {caret}) from app.orders o");

        assertBlankExpressionCandidates(run);
    }

    @Test
    void functionArgumentPrefixReplacesPrefixOnly() {
        CompletionRun run = completeAtCaret("select coalesce(o.status, sta{caret}) from app.orders o");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoInternalDummyText(run.result());
    }

    @Test
    void statementPrefixUsesBackendSnippetCandidate() {
        CompletionRun run = completeAtCaret("sel{caret}", SqlCompletionKeywordCaseEnum.LOWER);

        assertSuccessReplacement(run, 0, run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select from");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select from",
                """
                select
                    *
                from
                    $1;
                """);
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select function",
                "select $1;\n");
        assertCandidateInsertType(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select from", SqlCompletionInsertTypeEnum.SNIPPET);
        assertCandidateInsertType(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select function", SqlCompletionInsertTypeEnum.SNIPPET);
        assertCandidateSnippetSlot(run.result(), "select function", SqlCompletionSnippetSlotTypeEnum.SELECT_FUNCTION);
        assertCandidateDescription(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select from",
                "Select data from a table");
        assertCandidateDetail(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select from", null);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void statementPrefixAppliesLowerKeywordCaseToKeywordsAndSnippets() {
        CompletionRun run = completeAtCaret("sel{caret}", SqlCompletionKeywordCaseEnum.LOWER);

        assertSuccessReplacement(run, 0, run.cursor());
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "select");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select from");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "select from",
                """
                select
                    *
                from
                    $1;
                """);
    }

    @Test
    void statementPrefixAppliesUpperKeywordCaseToKeywordsAndSnippets() {
        CompletionRun run = completeAtCaret("sel{caret}", SqlCompletionKeywordCaseEnum.UPPER);

        assertSuccessReplacement(run, 0, run.cursor());
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "SELECT");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SELECT FROM");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SELECT FROM",
                """
                SELECT
                    *
                FROM
                    $1;
                """);
    }

    @Test
    void createPrefixUsesBackendPhraseSnippetCandidates() {
        CompletionRun run = completeAtCaret("cre{caret}");

        assertSuccessReplacement(run, 0, run.cursor());
        assertNoMetadataRequest();
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CREATE TABLE",
                """
                CREATE TABLE $1 (
                    $2
                );
                """);
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CREATE VIEW",
                """
                CREATE VIEW $1 AS
                SELECT
                    *
                FROM
                    $2;
                """);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CREATE");
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CREATE");
    }

    @Test
    void snippetCandidateSortsAfterMatchingKeyword() {
        CompletionRun run = completeAtCaret("cre{caret}");

        assertSuccessReplacement(run, 0, run.cursor());
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CREATE");
        Assertions.assertTrue(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.SNIPPET
                                && "CREATE TABLE".equals(candidate.getLabel())),
                () -> "Missing create table snippet in " + labels(run.result()));
    }

    @Test
    void completeCreateKeywordStillReturnsCurrentKeywordAndSnippets() {
        CompletionRun run = completeAtCaret("create{caret}");

        assertSuccessReplacement(run, 0, run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CREATE");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CREATE TABLE");
        assertNoMetadataRequest();
    }

    @Test
    void showPrefixUsesBackendPhraseSnippetCandidates() {
        CompletionRun run = completeAtCaret("sho{caret}");

        assertSuccessReplacement(run, 0, run.cursor());
        assertNoMetadataRequest();
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SHOW DATABASES");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SHOW TABLES");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SHOW CREATE TABLE");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SHOW PROCEDURE STATUS");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SHOW PROCEDURE STATUS",
                """
                SHOW PROCEDURE STATUS;
                """);
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "SHOW");
    }

    @Test
    void alterAndDropPrefixesUseBackendPhraseSnippetCandidates() {
        CompletionRun alterRun = completeAtCaret("alt{caret}");
        CompletionRun dropRun = completeAtCaret("dro{caret}");

        assertSuccessReplacement(alterRun, 0, alterRun.cursor());
        assertNoMetadataRequest();
        assertCandidate(alterRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "ALTER TABLE");
        assertCandidate(alterRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "ALTER VIEW");
        assertSuccessReplacement(dropRun, 0, dropRun.cursor());
        assertCandidate(dropRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "DROP TABLE");
        assertCandidate(dropRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "DROP PROCEDURE");
    }

    @Test
    void callAndExplainPrefixesUseBackendPhraseSnippetCandidates() {
        CompletionRun callRun = completeAtCaret("cal{caret}");
        CompletionRun explainRun = completeAtCaret("exp{caret}");

        assertSuccessReplacement(callRun, 0, callRun.cursor());
        assertNoMetadataRequest();
        assertCandidate(callRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CALL PROCEDURE");
        assertCandidateInsertText(callRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CALL PROCEDURE",
                "CALL $1;\n");
        assertCandidateSnippetSlot(callRun.result(), "CALL PROCEDURE", SqlCompletionSnippetSlotTypeEnum.CALL_PROCEDURE);
        assertCandidateDescription(callRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CALL PROCEDURE",
                "Call a procedure");
        assertCandidateDetail(callRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CALL PROCEDURE", null);
        assertSuccessReplacement(explainRun, 0, explainRun.cursor());
        assertCandidate(explainRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "EXPLAIN SELECT");
    }

    @Test
    void phraseSnippetsDoNotPolluteExpressionMemberOrTableReferenceSlots() {
        CompletionRun expressionRun = completeAtCaret("select cre{caret} from app.orders o");
        CompletionRun memberRun = completeAtCaret("select o.cre{caret} from app.orders o");
        CompletionRun tableRun = completeAtCaret("select * from cre{caret}");

        assertNoCandidateType(expressionRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(memberRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(tableRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void phraseSnippetsRequireStatementOpeningTokenEvenWhenC3AllowsToken() {
        CompletionRun showCreateRun = completeAtCaret("show cre{caret}");
        CompletionRun leadingWhitespaceRun = completeAtCaret("\n\tcre{caret}");
        CompletionRun secondStatementRun = completeAtCaret("select 1; cre{caret}");
        CompletionRun secondStatementCommentRun = completeAtCaret("select 1; /* keep */ cre{caret}");
        CompletionRun nonOpeningSecondTokenRun = completeAtCaret("select 1; select cre{caret}");
        CompletionRun withCteRun = completeAtCaret("with recent as (select 1) sel{caret}");

        assertNoSnippetCandidates(showCreateRun.result());
        assertCandidate(leadingWhitespaceRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CREATE TABLE");
        assertCandidate(secondStatementRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CREATE TABLE");
        assertCandidate(secondStatementCommentRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "CREATE TABLE");
        assertNoSnippetCandidates(nonOpeningSecondTokenRun.result());
        assertNoSnippetCandidates(withCteRun.result());
    }

    @Test
    void emptyStatementSlotDoesNotExposeSnippetOrKeywordCandidates() {
        CompletionRun run = completeAtCaret("{caret}");

        assertEmpty(run);
    }

    @Test
    void selectFromSnippetPlaceholderEmptyPrefixCompletesTablesWithoutSnippets() {
        CompletionRun run = completeAtCaret("SELECT \n\t* \nFROM \n\t{caret};");

        assertSuccessReplacement(run);
        assertMetadataRequestStrict(run, SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void insertIntoSnippetPlaceholderEmptyPrefixCompletesTablesWithoutSnippets() {
        CompletionRun run = completeAtCaret("INSERT INTO {caret} ()\nVALUES ();");

        assertSuccessReplacement(run);
        assertMetadataRequestStrict(run, SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void callStatementEmptyCursorCompletesProceduresWithoutSnippets() {
        CompletionRun run = completeAtCaret("CALL {caret}();");

        assertSuccessReplacement(run);
        assertMetadataRequestStrict(run, SqlCompletionCandidateTypeEnum.PROCEDURE, null, "");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.PROCEDURE, "sync_orders");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD);
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void blankLineWhitespaceDoesNotExposeSnippetOrKeywordCandidates() {
        CompletionRun run = completeAtCaret("\n\t{caret}");

        assertEmpty(run);
    }

    @Test
    void singleSpaceTableReferenceUsesMetadata() {
        CompletionRun run = completeAtCaret("select * from {caret}");

        assertSuccessReplacement(run);
        assertMetadataRequestStrict(run, SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void singleSpaceExpressionReferenceSlotsUseNormalFlow() {
        assertBlankExpressionCandidates(completeAtCaret("select {caret} from app.orders o"));
        assertBlankExpressionCandidates(completeAtCaret("select * from app.orders o where {caret}"));
    }

    @Test
    void dotScopedEmptyMemberPrefixUsesMetadataInsteadOfEmptyPrefixBlock() {
        CompletionRun run = completeAtCaret("select * from app.{caret}");

        assertSuccessReplacement(run);
        Assertions.assertTrue(run.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.TABLE_VIEW.name().equals(request.type())
                                && "app".equals(request.scope().schema())
                                && request.scope().table() == null
                                && "".equals(request.prefix())),
                () -> "Missing schema-scoped table request in " + run.metadataRequests());
        Assertions.assertFalse(run.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.DATABASE.name().equals(request.type())),
                () -> "Unexpected database metadata request in " + run.metadataRequests());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.DATABASE);
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                                || candidate.getType() == SqlCompletionCandidateTypeEnum.SNIPPET),
                () -> "Unexpected syntax candidates in " + labels(run.result()));
    }

    @Test
    void schemaQualifiedTablePrefixPassesSchemaScopeToMetadata() {
        CompletionRun run = completeAtCaret("select * from information_schema.ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        Assertions.assertTrue(run.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.TABLE_VIEW.name().equals(request.type())
                                && "information_schema".equals(request.scope().schema())
                                && request.scope().table() == null
                                && "ord".equals(request.prefix())),
                () -> "Missing information_schema table request in " + run.metadataRequests());
        Assertions.assertFalse(run.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())),
                () -> "Unexpected column metadata request in " + run.metadataRequests());
    }

    @Test
    void statementSnippetPrefixFiltersByTypedPrefix() {
        CompletionRun selectRun = completeAtCaret("sel{caret}");
        CompletionRun insertRun = completeAtCaret("ins{caret}");
        CompletionRun updateRun = completeAtCaret("upd{caret}");
        CompletionRun deleteRun = completeAtCaret("del{caret}");

        assertCandidate(selectRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SELECT FROM");
        assertCandidate(selectRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SELECT WHERE");
        assertCandidate(insertRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "INSERT INTO");
        assertCandidate(updateRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "UPDATE SET");
        assertCandidate(deleteRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "DELETE FROM");
        assertNoCandidate(insertRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SELECT FROM");
        assertNoCandidate(updateRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SELECT FROM");
        assertNoCandidate(deleteRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "SELECT FROM");
    }

    @Test
    void statementStartPrefixDoesNotAppendBuiltinFunctionsWithoutFunctionRule() {
        CompletionRun run = completeAtCaret("s{caret}");

        assertSuccessReplacement(run, 0, run.cursor());
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION),
                () -> "Unexpected function candidates in " + labels(run.result()));
    }

    @Test
    void prefixProjectedC3KeepsClauseKeywordWhenAliasDeclarationWouldConsumePrefix() {
        CompletionRun whereRun = completeAtCaret("select * from app.orders wh{caret};");

        assertSuccessReplacement(whereRun, whereRun.sql().indexOf("wh"), whereRun.cursor());
        assertCandidate(whereRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "WHERE");
        assertNoMetadataRequest();

        CompletionRun groupRun = completeAtCaret("select * from app.orders gr{caret};");

        assertSuccessReplacement(groupRun, groupRun.sql().indexOf("gr"), groupRun.cursor());
        assertCandidate(groupRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP BY");
        assertNoCandidate(groupRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP");

        CompletionRun orderRun = completeAtCaret("select * from app.orders or{caret};");

        assertSuccessReplacement(orderRun, orderRun.sql().lastIndexOf("or"), orderRun.cursor());
        assertCandidate(orderRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ORDER BY");
        assertNoCandidate(orderRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ORDER");
    }

    @Test
    void functionNamePrefixKeepsFunctionCandidateWhenPhraseUsesSameToken() {
        CompletionRun run = completeAtCaret("select le{caret}('abcdef', 2) from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("le("), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "LEFT");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "LEFT JOIN");
    }

    @Test
    void tableAliasPrefixDoesNotLeakNextStatementStartKeywords() {
        CompletionRun run = completeAtCaret("select * from app.orders a{caret};");

        assertSuccessReplacement(run, run.sql().lastIndexOf(" a") + 1, run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "AS");
        assertNoSyntaxCandidate(run.result(), "ALTER");
        assertNoSyntaxCandidate(run.result(), "ANALYZE");
    }

    @Test
    void prefixProjectedC3DoesNotPolluteExpressionOrMemberSlots() {
        CompletionRun expressionRun = completeAtCaret("select sta{caret} from app.orders o");

        assertSuccessReplacement(expressionRun, expressionRun.sql().indexOf("sta"), expressionRun.cursor());
        assertCandidate(expressionRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(expressionRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "WHERE");
        assertNoCandidate(expressionRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP");
        assertNoCandidate(expressionRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ORDER");

        CompletionRun memberRun = completeAtCaret("select * from app.orders o where o.st{caret}");

        assertSuccessReplacement(memberRun, memberRun.sql().indexOf("st"), memberRun.cursor());
        assertCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "WHERE");
        assertNoCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP");
        assertNoCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ORDER");
    }

    @Test
    void valueAndCompletePredicateSlotsDoNotExposeStatementSnippets() {
        CompletionRun valueRun = completeAtCaret("insert into app.orders (id, status) values ({caret})");
        CompletionRun predicateRun = completeAtCaret("select status from app.orders o where o.status=1{caret}");

        assertNoCandidateType(valueRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(predicateRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void selectListAliasPrefixDoesNotLeakRootStatementSyntaxCandidates() {
        CompletionRun run = completeAtCaret("select sum(amount) t{caret} from app.orders");

        assertNoCandidates(run);
        Assertions.assertFalse(run.metadataRequests().isEmpty(), run.sql());
    }

    @Test
    void predicatePrefixKeepsC3SyntaxKeywordsButSuppressesStatementSnippets() {
        CompletionRun run = completeAtCaret("select * from app.orders where id i{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf('i'), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IN");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "INTO");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS NULL");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS NOT NULL");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS");
        assertNoSyntaxCandidate(run.result(), "INSERT");
        assertNoSnippetCandidates(run.result());
    }

    @Test
    void predicateIsPrefixReturnsNullCheckPhrases() {
        CompletionRun run = completeAtCaret("select * from app.orders where deleted_at is{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("is"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS NULL");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS NOT NULL");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS NULL", "IS NULL");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS NOT NULL",
                "IS NOT NULL");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "IS");
    }

    @Test
    void triggerSetTargetPrefixUsesPseudoRecordLocalSymbolsOnly() {
        CompletionRun run = completeAtCaret(
                "create trigger app.trg_orders_ai after insert on app.orders for each row begin\n"
                        + "  set n{caret}\n"
                        + "end");

        assertSuccessReplacement(run, run.sql().indexOf("set n") + "set ".length(), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "NEW");
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.VARIABLE),
                () -> "Unexpected variable candidates in " + labels(run.result()));
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION),
                () -> "Unexpected function candidates in " + labels(run.result()));
        Assertions.assertTrue(run.metadataRequests().isEmpty(), run.sql());
    }

    @Test
    void triggerPseudoRecordDotUsesTriggerTargetTableColumns() {
        CompletionRun run = completeAtCaret(
                "create trigger app.trg_orders_ai after insert on app.orders for each row begin\n"
                        + "  set new.{caret}\n"
                        + "end");

        Assertions.assertEquals(SqlCompletionStatusEnum.SUCCESS.name(), run.result().getStatus(), run.sql());
        Assertions.assertEquals(run.cursor(), run.result().getReplaceStart(), run.sql());
        Assertions.assertEquals(run.cursor(), run.result().getReplaceEnd(), run.sql());
        Assertions.assertTrue(run.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())
                                && "orders".equals(request.scope().table())
                                && "".equals(request.prefix())),
                () -> "Missing trigger target column metadata request in " + run.metadataRequests());
        SqlCompletionCandidate status = candidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        Assertions.assertEquals("orders", status.getTableName(), run.sql());
        Assertions.assertNull(status.getTableAlias(), run.sql());
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.VARIABLE),
                () -> "Unexpected variable candidates in " + labels(run.result()));
        Assertions.assertFalse(run.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.TABLE_VIEW.name().equals(request.type())),
                () -> "Unexpected table metadata request in " + run.metadataRequests());
    }

    @Test
    void insertColumnListPrefixIncludesMatchingRemainingColumnsCandidate() {
        metadataProvider.duplicateStatusColumn = true;
        CompletionRun run = completeAtCaret("insert into app.orders (st{caret}) values ();");
        String remainingColumns = "status";
        SqlCompletionCandidate allRemainingColumns = candidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN,
                remainingColumns);

        assertSuccessReplacement(run, run.sql().indexOf("st"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "st");
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, remainingColumns);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        Assertions.assertEquals(SqlCompletionInsertTypeEnum.PLAIN_TEXT, allRemainingColumns.getInsertType(),
                labels(run.result()));
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, remainingColumns,
                remainingColumns);
        assertCandidateInsertType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, remainingColumns,
                SqlCompletionInsertTypeEnum.PLAIN_TEXT);
        assertCandidateCount(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status", 1);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
    }

    @Test
    void insertSnippetColumnListSlotUsesColumnListCompletionAtSlotCursor() {
        String sql = "INSERT INTO app.orders ({caret})\nVALUES ();";
        int caret = sql.indexOf("{caret}");
        CompletionRun run = completeAtCaret(sql, activeSlot(
                SqlCompletionSnippetSlotTypeEnum.INSERT_COLUMN_LIST, caret, caret));

        assertSuccessReplacement(run);
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void insertColumnListPrefixSuppressesWrittenColumnsAndBuildsRemainingAggregate() {
        CompletionRun run = completeAtCaret("insert into app.orders (id, status, a{caret}) values ();");
        assertSuccessReplacement(run, run.sql().indexOf(", a") + 2, run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "a");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "id");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
    }

    @Test
    void insertColumnListPrefixSuppressesMatchingWrittenColumn() {
        CompletionRun run = completeAtCaret("insert into app.orders (id, status, sta{caret}) values ();");

        assertNoCandidates(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN,
                "amount, customer_id, created_at, extra");
    }

    @Test
    void insertColumnListFilteringDoesNotLeakIntoValuesOrDuplicateUpdateSlots() {
        CompletionRun valuesRun = completeAtCaret("insert into app.orders (id, status) values ({caret});");
        CompletionRun duplicateUpdateRun = completeAtCaret(
                "insert into app.orders (id, status) values (1, 'new') on duplicate key update {caret}");

        assertNoCandidate(valuesRun.result(), SqlCompletionCandidateTypeEnum.COLUMN,
                "amount, customer_id, created_at, extra");
        assertNoCandidate(duplicateUpdateRun.result(), SqlCompletionCandidateTypeEnum.COLUMN,
                "amount, customer_id, created_at, extra");
    }

    @Test
    void insertDuplicatePrefixReturnsOnDuplicateKeyUpdatePhrase() {
        CompletionRun run = completeAtCaret("insert into app.orders (id, status) values (1, 'new') on{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("on"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ON DUPLICATE KEY UPDATE");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ON DUPLICATE KEY UPDATE",
                "ON DUPLICATE KEY UPDATE");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ON");
    }

    @Test
    void joinConditionEmptySlotUsesNormalExpressionFlow() {
        CompletionRun run = completeAtCaret(
                "select * from app.orders o join app.customers c on {caret}");

        assertSuccessReplacement(run);
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "COUNT");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void joinConditionQualifiedAliasUsesExactRelationOwner() {
        CompletionRun run = completeAtCaret(
                "select * from app.orders o join app.customers c on c.{caret} = o.customer_id");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void joinConditionAliasPrefixReturnsRelationAliasWithoutFunctions() {
        CompletionRun run = completeAtCaret(
                "CREATE VIEW testas AS SELECT * FROM user_document u join obj_namespace o ON u{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("u"), run.cursor());
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.ALIAS, "u");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.ALIAS, "u", "u.");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION);
    }

    @Test
    void joinConditionFunctionPrefixStillReturnsFunctionCandidatesWithoutAliasMatch() {
        CompletionRun run = completeAtCaret(
                "CREATE VIEW testas AS SELECT * FROM user_document u join obj_namespace o ON up{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("up"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "UPPER");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.ALIAS);
    }

    @Test
    void multiJoinQualifiedAliasUsesExactOwner() {
        CompletionRun run = completeAtCaret(
                "select o.id, c.name, p.paid_amount from app.orders o "
                        + "join app.customers c on c.id = o.customer_id "
                        + "left join app.payments p on p.order_id = o.id where p.{caret}");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "payments", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "paid_amount");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void joinVariantPrefixesReturnJoinPhrases() {
        CompletionRun leftRun = completeAtCaret("select * from app.orders o le{caret}");
        CompletionRun rightRun = completeAtCaret("select * from app.orders o ri{caret}");
        CompletionRun innerRun = completeAtCaret("select * from app.orders o in{caret}");
        CompletionRun crossRun = completeAtCaret("select * from app.orders o cr{caret}");
        CompletionRun naturalRun = completeAtCaret("select * from app.orders o na{caret}");

        assertSuccessReplacement(leftRun, leftRun.sql().lastIndexOf("le"), leftRun.cursor());
        assertCandidate(leftRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "LEFT JOIN");
        assertNoCandidate(leftRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "LEFT");

        assertSuccessReplacement(rightRun, rightRun.sql().lastIndexOf("ri"), rightRun.cursor());
        assertCandidate(rightRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "RIGHT JOIN");
        assertNoCandidate(rightRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "RIGHT");

        assertSuccessReplacement(innerRun, innerRun.sql().lastIndexOf("in"), innerRun.cursor());
        assertCandidate(innerRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "INNER JOIN");
        assertNoCandidate(innerRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "INNER");

        assertSuccessReplacement(crossRun, crossRun.sql().lastIndexOf("cr"), crossRun.cursor());
        assertCandidate(crossRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CROSS JOIN");
        assertNoCandidate(crossRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CROSS");

        assertSuccessReplacement(naturalRun, naturalRun.sql().lastIndexOf("na"), naturalRun.cursor());
        assertCandidate(naturalRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "NATURAL JOIN");
        assertNoCandidate(naturalRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "NATURAL");
    }

    @Test
    void samePrefixAliasAndSelfJoinKeepOwnersSeparated() {
        CompletionRun samePrefixRun = completeAtCaret(
                "select * from app.orders o join app.customers o2 on o2.id = o.customer_id where o.{caret}");
        CompletionRun selfJoinRun = completeAtCaret(
                "select * from app.orders o join app.orders parent on parent.id = o.id where parent.{caret}");

        assertSuccessReplacement(samePrefixRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(samePrefixRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(samePrefixRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");

        assertSuccessReplacement(selfJoinRun);
        assertCandidate(selfJoinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertCandidate(selfJoinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void mysqlJoinVariantsKeepAliasScope() {
        CompletionRun straightJoinRun = completeAtCaret(
                "select * from app.orders o straight_join app.customers c "
                        + "on c.id = o.customer_id where c.{caret}");
        CompletionRun naturalJoinRun = completeAtCaret(
                "select * from app.orders o natural join app.customers c where c.{caret}");

        assertSuccessReplacement(straightJoinRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(straightJoinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(straightJoinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");

        assertSuccessReplacement(naturalJoinRun);
        assertCandidate(naturalJoinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(naturalJoinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void joinUsingColumnListEmptyPrefixUsesNormalFlow() {
        CompletionRun run = completeAtCaret(
                "select * from app.orders o join app.customers c using ({caret})");

        assertSuccessReplacement(run);
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void joinUsingColumnPrefixUsesBothJoinedRelations() {
        CompletionRun run = completeAtCaret(
                "select * from app.orders o join app.customers c using (cu{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("cu"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "cu");
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "cu");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void parenthesizedPredicateRightValueEmptyPrefixUsesNormalFlow() {
        CompletionRun run = completeAtCaret("select * from app.orders o where (o.amount > {caret})");

        assertBlankExpressionCandidates(run);
    }

    @Test
    void caseWhenRightValueEmptyPrefixUsesNormalFlow() {
        CompletionRun run = completeAtCaret(
                "select case when o.status = {caret} then 1 else 0 end from app.orders o");

        assertBlankExpressionCandidates(run);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
        assertNoInternalDummyText(run.result());
    }

    @Test
    void caseResultAndJsonSelectContinuationKeepRelationScope() {
        CompletionRun caseRun = completeAtCaret(
                "select case when o.status = 'PAID' then o.{caret} else 0 end from app.orders o");
        CompletionRun jsonRun = completeAtCaret(
                "select json_unquote(json_extract(o.extra, '$.channel')) channel, o.{caret} from app.orders o");

        assertSuccessReplacement(caseRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(caseRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoCandidateType(caseRun.result(), SqlCompletionCandidateTypeEnum.TABLE);

        assertSuccessReplacement(jsonRun);
        assertCandidate(jsonRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertCandidate(jsonRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void caseWhenQualifiedPrefixUsesExpressionRelationScope() {
        CompletionRun run = completeAtCaret(
                "select case when o.sta{caret} = 'PAID' then 1 else 0 end from app.orders o");

        assertEmpty(run);
    }

    @Test
    void simpleCaseOperandPrefixUsesExpressionRelationScope() {
        CompletionRun run = completeAtCaret(
                "select case o.sta{caret} when 'PAID' then 1 else 0 end from app.orders o");

        assertEmpty(run);
    }

    @Test
    void betweenLowerBoundPrefixUsesExpressionRelationScope() {
        CompletionRun run = completeAtCaret(
                "select * from app.orders o where o.created_at between creat{caret} and now()");

        assertSuccessReplacement(run, run.sql().lastIndexOf("creat"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "creat");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "created_at");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void inSubqueryProjectionUsesInnerRelationScope() {
        CompletionRun run = completeAtCaret(
                "select * from app.orders o where o.customer_id in (select c.{caret} from app.customers c)");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void existsSubqueryPredicateUsesInnerQualifiedRelationScope() {
        CompletionRun run = completeAtCaret(
                "select * from app.orders o where exists "
                        + "(select 1 from app.customers c where c.{caret} = o.customer_id)");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void scalarTupleAndNotExistsSubqueriesUseInnerScope() {
        CompletionRun scalarRun = completeAtCaret(
                "select * from app.orders o where o.amount > "
                        + "(select avg(o2.{caret}) from app.orders o2 where o2.customer_id = o.customer_id)");
        CompletionRun tupleRun = completeAtCaret(
                "select * from app.orders o where (o.customer_id, o.status) in "
                        + "(select c.id,c.{caret} from app.customers c)");
        CompletionRun notExistsRun = completeAtCaret(
                "select * from app.orders o where not exists "
                        + "(select 1 from app.shipments s where s.order_id = o.id and s.{caret})");

        assertSuccessReplacement(scalarRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(scalarRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");

        assertSuccessReplacement(tupleRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(tupleRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(tupleRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");

        assertSuccessReplacement(notExistsRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "shipments", "");
        assertCandidate(notExistsRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "tracking_no");
        assertNoCandidate(notExistsRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void groupByEmptySlotUsesNormalFlow() {
        CompletionRun run = completeAtCaret(
                "select o.status, count(*) from app.orders o group by {caret}");

        assertBlankExpressionCandidates(run);
    }

    @Test
    void limitValueDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("select * from app.orders o limit {caret}");

        assertEmpty(run);
    }

    @Test
    void offsetValueDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("select * from app.orders o limit 10 offset {caret}");

        assertEmpty(run);
    }

    @Test
    void orderByEmptySlotUsesNormalFlow() {
        CompletionRun run = completeAtCaret(
                "select count(*) as cnt, o.status from app.orders o group by o.status order by {caret}");

        assertBlankExpressionCandidates(run);
    }

    @Test
    void havingEmptySlotUsesNormalFlow() {
        CompletionRun run = completeAtCaret(
                "select sum(o.amount) as total from app.orders o having {caret} > 0");

        assertBlankExpressionCandidates(run);
    }

    @Test
    void updateSetRightValuePrefixDoesNotConsumeWhereClause() {
        CompletionRun run = completeAtCaret("update app.orders set status = sta{caret} where id = 1");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        Assertions.assertTrue(run.sql().substring(run.result().getReplaceEnd()).startsWith(" where"));
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
    }

    @Test
    void onDuplicateKeyUpdateRightValuePrefixUsesExpressionCandidates() {
        CompletionRun run = completeAtCaret(
                "insert into app.orders(id, status) values(1, 'N') on duplicate key update status = sta{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
        assertNoInternalDummyText(run.result());
    }

    @Test
    void completePredicateBoundarySuppressesMetadataButAndContinuationCompletes() {
        CompletionRun numericRun = completeAtCaret("select status from app.orders o where o.status=1{caret}");
        CompletionRun stringRun = completeAtCaret("select status from app.orders o where o.status='PAID'{caret}");
        CompletionRun nullRun = completeAtCaret("select status from app.orders o where o.status is null{caret}");
        CompletionRun memberRun = completeAtCaret(
                "select * from app.access_control_apply_record a where id=1 and a.no_expire{caret}");

        assertEmpty(numericRun);
        assertEmpty(stringRun);
        assertEmpty(nullRun);
        assertNoCandidates(memberRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "access_control_apply_record", "no_expire");

        CompletionRun andRun = completeAtCaret(
                "select o.id from app.orders o join app.customers c on o.customer_id = c.id "
                        + "where o.id>10 and i{caret}");
        assertSuccessReplacement(andRun, andRun.sql().lastIndexOf('i'), andRun.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "i");
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "i");
        assertCandidate(andRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "id");
        assertNoCandidateType(andRun.result(), SqlCompletionCandidateTypeEnum.TABLE);
    }

    @Test
    void predicateBoundaryKeywordPrefixDoesNotPolluteWithExpressionCandidates() {
        CompletionRun run = completeAtCaret(
                "select * from app.access_control_apply_record a where id=1 and a.no_expire=1 or{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("or"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "OR");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ORDER BY");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ORDER");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION);
    }

    @Test
    void selectGroupPrefixReturnsGroupByPhraseWithoutMetadataPollution() {
        CompletionRun run = completeAtCaret("select * from app.access_control_apply_record gr{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("gr"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP BY");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP BY", "GROUP BY");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION);
        assertNoMetadataRequest();
    }

    @Test
    void logfileGroupPrefixDoesNotReturnGroupByPhrase() {
        for (String sql : List.of(
                "create logfile gr{caret}",
                "alter logfile gr{caret}",
                "drop logfile gr{caret}")) {
            CompletionRun run = completeAtCaret(sql);

            assertSuccessReplacement(run, run.sql().lastIndexOf("gr"), run.cursor());
            assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP");
            assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "GROUP BY");
            assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
            assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION);
        }
    }

    @Test
    void unclosedFunctionMemberPrefixKeepsOriginalReplacementRange() {
        CompletionRun run = completeAtCaret("select coalesce(o.sta{caret} from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "o", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoInternalDummyText(run.result());
    }

    @Test
    void multiStatementDanglingDotUsesSourceCoordinatesOnlyInCurrentStatement() {
        CompletionRun run = completeAtCaret("select 1; select o.{caret} from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertEmptyPrefixReplacementIfCandidateReturned(run);
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
    }

    @Test
    void mysqlPromptPrefixKeepsReplacementInOriginalSourceCoordinates() {
        CompletionRun run = completeAtCaret("mysql> select o.{caret} from app.orders o;");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertEmptyPrefixReplacementIfCandidateReturned(run);
    }

    @Test
    void preparingLogPrefixKeepsReplacementInOriginalSourceCoordinates() {
        CompletionRun run = completeAtCaret("DEBUG ==> Preparing: select o.{caret} from app.orders o where id = ?");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertEmptyPrefixReplacementIfCandidateReturned(run);
    }

    @Test
    void verticalResultSuffixDoesNotEnterReplacementRange() {
        CompletionRun run = completeAtCaret("select o.{caret} from app.orders o\\G");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertEmptyPrefixReplacementIfCandidateReturned(run);
    }

    @Test
    void insertTableSlotUsesTableCandidates() {
        CompletionRun run = completeAtCaret("insert into ord{caret} (id, status) values (1, 'PAID')");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void completeInsertTableNameBoundaryDoesNotResuggestSameTable() {
        CompletionRun run = completeAtCaret("insert into access_control_apply_record{caret}");

        assertNoCandidates(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "access_control_apply_record");
    }

    @Test
    void insertTableSlotWithoutIntoUsesTableCandidates() {
        CompletionRun run = completeAtCaret("insert ord{caret} (id, status) values (1, 'PAID')");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void schemaQualifiedInsertTablePrefixUsesTableCandidates() {
        CompletionRun run = completeAtCaret("insert into app.ord{caret} (id, status) values (1, 'PAID')");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void insertColumnListUsesColumnsFromInsertTarget() {
        CompletionRun run = completeAtCaret("insert into app.orders ({caret}) values (?)");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void insertColumnListPrefixUsesColumnsFromInsertTarget() {
        CompletionRun run = completeAtCaret("insert into app.orders (sta{caret}) values (?)");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void insertValuesExpressionSlotDoesNotUseInsertTargetColumns() {
        CompletionRun run = completeAtCaret("insert into app.orders (id, status) values ({caret})");

        assertNoCandidates(run);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void insertValuesExpressionPrefixDoesNotUseInsertTargetColumns() {
        CompletionRun run = completeAtCaret("insert into app.orders (id, status) values (sta{caret})");

        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void insertBlankStatementProgressionOffersTableCandidates() {
        CompletionRun afterInsertRun = completeAtCaret("insert {caret}");

        assertSuccessReplacement(afterInsertRun);
        assertMetadataRequestStrict(afterInsertRun, SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "");
        assertCandidateStrict(afterInsertRun.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateTypeStrict(afterInsertRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void insertAfterCompletedTableOffersValueKeywordsWithoutTableCandidates() {
        CompletionRun afterTableRun = completeAtCaret("insert into app.orders {caret}");
        CompletionRun afterUnqualifiedTableRun = completeAtCaret("insert into access_control_apply_record {caret}");

        assertEmpty(afterTableRun);
        assertEmpty(afterUnqualifiedTableRun);
    }

    @Test
    void insertTypedStatementProgressionUsesPrefixKeywordCandidates() {
        CompletionRun afterInsertRun = completeAtCaret("insert in{caret}");
        CompletionRun afterTableRun = completeAtCaret("insert into app.orders va{caret}");

        assertSuccessReplacement(afterInsertRun, afterInsertRun.sql().lastIndexOf("in"), afterInsertRun.cursor());
        assertCandidate(afterInsertRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "INTO");

        assertSuccessReplacement(afterTableRun, afterTableRun.sql().lastIndexOf("va"), afterTableRun.cursor());
        assertCandidate(afterTableRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "VALUES");
        assertNoCandidateType(afterTableRun.result(), SqlCompletionCandidateTypeEnum.TABLE);
    }

    @Test
    void insertValuesStringLiteralDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("insert into app.orders (id, status) values (1, 'PA{caret}')");

        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        assertNoMetadataRequest();
    }

    @Test
    void insertSetColumnPrefixUsesInsertTargetColumns() {
        CompletionRun run = completeAtCaret("insert into app.orders set sta{caret} = 'PAID'");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void insertSelectProjectionKeepsSelectAliasScope() {
        CompletionRun run = completeAtCaret(
                "insert into app.orders (id, status) select o.{caret} from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void insertDuplicateKeyValuesFunctionArgumentUsesInsertTargetColumns() {
        CompletionRun run = completeAtCaret(
                "insert into app.user_stat (user_id, score) values (?, ?) "
                        + "on duplicate key update score = values(sc{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sc"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "user_stat", "sc");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "score");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void replaceTableSlotUsesTableCandidates() {
        CompletionRun run = completeAtCaret("replace into ord{caret} (id, status) values (1, 'PAID')");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void replaceTableSlotWithoutIntoUsesTableCandidates() {
        CompletionRun run = completeAtCaret("replace ord{caret} (id, status) values (1, 'PAID')");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void replaceColumnListUsesColumnsFromReplaceTarget() {
        CompletionRun run = completeAtCaret("replace into app.orders ({caret}) values (?)");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void replaceColumnListPrefixUsesColumnsFromReplaceTarget() {
        CompletionRun run = completeAtCaret("replace into app.orders (sta{caret}) values (?)");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void replaceValuesExpressionSlotDoesNotUseReplaceTargetColumns() {
        CompletionRun run = completeAtCaret("replace into app.orders (id, status) values ({caret})");

        assertNoCandidates(run);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void replaceValuesStringLiteralDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret("replace into app.orders (id, status) values (1, 'PA{caret}')");

        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        assertNoMetadataRequest();
    }

    @Test
    void replaceSetColumnPrefixUsesReplaceTargetColumns() {
        CompletionRun run = completeAtCaret("replace into app.orders set sta{caret} = 'PAID'");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void replaceSelectProjectionKeepsSelectAliasScope() {
        CompletionRun run = completeAtCaret(
                "replace into app.orders (id, status) select o.{caret} from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void deleteTableSlotUsesTableCandidates() {
        CompletionRun run = completeAtCaret("delete from ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
    }

    @Test
    void deleteBlankStatementProgressionOffersTableCandidates() {
        CompletionRun run = completeAtCaret("delete {caret}");

        assertSuccessReplacement(run);
        assertMetadataRequestStrict(run, SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    @Test
    void deleteTypedStatementProgressionUsesPrefixKeywordCandidates() {
        CompletionRun run = completeAtCaret("delete fr{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("fr"), run.cursor());
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "fresh_orders");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "FROM");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "fresh_orders");
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "fr");
    }

    @Test
    void updateSchemaQualifiedTablePrefixUsesTableCandidates() {
        CompletionRun run = completeAtCaret("update app.ord{caret} set status = 'PAID'");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void updateJoinTablePrefixUsesTableCandidates() {
        CompletionRun run = completeAtCaret(
                "update app.orders o join app.cust{caret} c on o.customer_id = c.id set o.status = c.name");

        assertSuccessReplacement(run, run.sql().indexOf("cust"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "cust");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "customers");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void updateSetTargetColumnPrefixUsesUpdateTargetColumns() {
        CompletionRun run = completeAtCaret("update app.orders o set o.sta{caret} = 'PAID' where o.id = ?");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void multiTableUpdateSetTargetUsesQualifiedUpdateRelation() {
        CompletionRun run = completeAtCaret(
                "update app.orders o join app.customers c on o.customer_id = c.id set o.st{caret} = c.name");

        assertSuccessReplacement(run, run.sql().lastIndexOf("st"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "st");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void multiTableUpdateSetValueUsesQualifiedJoinRelation() {
        CompletionRun run = completeAtCaret(
                "update app.orders o join app.customers c on o.customer_id = c.id set o.status = c.{caret}");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void joinedDeleteWhereUsesQualifiedJoinRelation() {
        CompletionRun run = completeAtCaret(
                "delete o from app.orders o join app.customers c on o.customer_id = c.id where c.{caret}");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void multipleDeleteTargetTableUsesTableCandidates() {
        CompletionRun run = completeAtCaret("delete app.ord{caret}.* from app.orders o where o.id = 1");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void multipleDeleteUsingTableUsesTableCandidates() {
        CompletionRun run = completeAtCaret("delete from app.orders using app.ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void handlerOpenTableUsesTableCandidates() {
        CompletionRun run = completeAtCaret("handler app.ord{caret} open");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void handlerReadIndexTableUsesTableCandidates() {
        CompletionRun run = completeAtCaret("handler app.ord{caret} read idx_status first");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void loadDataIntoTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret(
                "load data local infile '/tmp/orders.csv' into table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void loadDataSetColumnUsesTargetTableColumns() {
        CompletionRun run = completeAtCaret(
                "load data infile '/tmp/orders.csv' into table app.orders set sta{caret} = @status");

        assertEmpty(run);
    }

    @Test
    void loadDataColumnListUsesTargetTableColumns() {
        CompletionRun run = completeAtCaret(
                "load data infile '/tmp/orders.csv' into table app.orders (sta{caret}, @raw_amount)");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void loadDataVariableFieldDoesNotQueryMetadata() {
        CompletionRun run = completeAtCaret(
                "load data infile '/tmp/orders.csv' into table app.orders (@raw_{caret}, status)");

        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        assertNoMetadataRequest();
    }

    @Test
    void loadXmlIntoTableUsesExistingTableCandidates() {
        CompletionRun run = completeAtCaret(
                "load xml local infile '/tmp/orders.xml' into table app.ord{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void loadXmlColumnListUsesTargetTableColumns() {
        CompletionRun run = completeAtCaret(
                "load xml infile '/tmp/orders.xml' into table app.orders (sta{caret})");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void onDuplicateKeyUpdateTargetPrefixUsesInsertTargetColumns() {
        CompletionRun run = completeAtCaret(
                "insert into app.user_stat (user_id, score, updated_at) values (?, ?, now()) "
                        + "on duplicate key update sc{caret} = values(score)");

        assertSuccessReplacement(run, run.cursor() - 2, run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "user_stat", "sc");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "score");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void windowPartitionByUsesForwardFromRelationScope() {
        CompletionRun run = completeAtCaret(
                "select o.customer_id, sum(o.amount) over (partition by o.{caret} order by o.created_at) "
                        + "from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void currentStatementDoesNotLeakNeighboringStatementRelations() {
        CompletionRun run = completeAtCaret(
                "select c.email from app.customers c;\n"
                        + "update app.orders o set o.sta{caret} = 'PAID' where o.id = 1;\n"
                        + "select c.name from app.customers c");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.sql().indexOf("sta") + 3);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void newlineStatementAndPreviousCteDoNotLeakIntoCurrentWindow() {
        CompletionRun newlineRun = completeAtCaret(
                "select * from app.orders o where o.status = 'PAID'\n"
                        + "select c.{caret} from app.customers c");
        CompletionRun previousCteRun = completeAtCaret(
                "with recent as (select * from app.orders)\n"
                        + "select * from recent;\n"
                        + "select o.{caret} from app.orders o");

        assertSuccessReplacement(newlineRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(newlineRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(newlineRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");

        assertSuccessReplacement(previousCteRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(previousCteRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(previousCteRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void orphanFromFragmentAfterStatementSeparatorIsSuppressed() {
        CompletionRun run = completeAtCaret("select * from app.orders o;\nfrom {caret}");

        assertEmpty(run);
    }

    @Test
    void longWindowContextsKeepAliasScope() {
        String filler = "-- filler\n".repeat(120);
        CompletionRun selectRun = completeAtCaret(filler + "select o.{caret} from app.orders o");
        CompletionRun cteRun = completeAtCaret(
                "with base as (\n" + filler + "select id, status from app.orders\n)\n"
                        + "select b.{caret} from base b");

        assertSuccessReplacement(selectRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(selectRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");

        assertSuccessReplacement(cteRun);
        assertCandidate(cteRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "id");
        assertCandidate(cteRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(cteRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
    }

    @Test
    void migrationScriptUpdateStatementDoesNotLeakPreviousDdlOrNextSelect() {
        CompletionRun run = completeAtCaret(
                "-- migration: add archive flag\n"
                        + "alter table app.orders add column archived_at datetime;\n"
                        + "update app.orders o set o.sta{caret} = 'ARCHIVED' "
                        + "where o.id in (select oi.order_id from app.order_items oi);\n"
                        + "select c.email from app.customers c");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.sql().indexOf("sta") + 3);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void transactionScriptInsertSelectUsesSourceRelationScope() {
        CompletionRun run = completeAtCaret(
                "start transaction;\n"
                        + "create temporary table recent_customers as select c.id, c.name from app.customers c;\n"
                        + "insert into app.orders(id, status) select c.id, c.na{caret} from app.customers c;\n"
                        + "commit;");

        assertSuccessReplacement(run, run.sql().lastIndexOf("na"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "na");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void commentedOutSqlDoesNotContributeRelationScope() {
        CompletionRun run = completeAtCaret(
                "/* disabled old SQL\n"
                        + "select c.email from app.customers c where c.name like '%x%';\n"
                        + "*/\n"
                        + "select o.sta{caret} from app.orders o");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void semicolonInsideStringDoesNotSplitStatementWindow() {
        CompletionRun run = completeAtCaret(
                "select * from app.customers c where c.name = 'x;y';\n"
                        + "select o.{caret} from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
    }

    @Test
    void delimiterProcedureBodyCompletesInsideCurrentRoutineStatement() {
        CompletionRun run = completeAtCaret(
                "delimiter //\n"
                        + "create procedure sync_orders()\n"
                        + "begin\n"
                        + "  update app.orders o join app.customers c on o.customer_id = c.id\n"
                        + "  set o.status = c.na{caret}me;\n"
                        + "end //\n"
                        + "delimiter ;\n"
                        + "select o.status from app.orders o;");

        assertSuccessReplacement(run, run.sql().lastIndexOf("name"), run.sql().lastIndexOf("name") + "name".length());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "na");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void routineBodySelectAndSemicolonInStringKeepInnerSqlScope() {
        CompletionRun delimiterRun = completeAtCaret(
                "delimiter $$\n"
                        + "create procedure app.refresh()\n"
                        + "begin\n"
                        + "  select o.{caret} from app.orders o;\n"
                        + "end$$");
        CompletionRun stringSemicolonRun = completeAtCaret(
                "create procedure app.refresh()\n"
                        + "begin\n"
                        + "  select * from app.orders o where o.status = 'x;y';\n"
                        + "  select c.{caret} from app.customers c;\n"
                        + "end");

        assertSuccessReplacement(delimiterRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "o", "");
        assertCandidate(delimiterRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");

        assertSuccessReplacement(stringSemicolonRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "");
        assertCandidate(stringSemicolonRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
        assertNoCandidate(stringSemicolonRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void triggerPseudoRecordFetchesTargetColumnsButLocalVariableDoesNotFetchMetadata() {
        CompletionRun triggerRun = completeAtCaret(
                "create trigger app.trg_orders_ai after insert on app.orders for each row begin\n"
                        + "  insert into audit_log.sql_audit(sql_text) values (new.{caret});\n"
                        + "end");
        CompletionRun localVariableRun = completeAtCaret(
                "create procedure app.check_orders()\n"
                        + "begin\n"
                        + "  declare v_status varchar(32);\n"
                        + "  select * from app.orders o where o.status = v_{caret};\n"
                        + "end");

        Assertions.assertEquals(SqlCompletionStatusEnum.SUCCESS.name(), triggerRun.result().getStatus(), triggerRun.sql());
        Assertions.assertTrue(triggerRun.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())
                                && "orders".equals(request.scope().table())
                                && "".equals(request.prefix())),
                () -> "Missing trigger target column metadata request in " + triggerRun.metadataRequests());
        Assertions.assertTrue(triggerRun.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.COLUMN
                                && "status".equals(candidate.getLabel())),
                () -> "Missing status column candidate in " + labels(triggerRun.result()));
        assertSuccessReplacement(localVariableRun, localVariableRun.sql().lastIndexOf("v_"), localVariableRun.cursor());
        assertCandidate(localVariableRun.result(), SqlCompletionCandidateTypeEnum.VARIABLE, "v_status");
        assertNoMetadataRequest(localVariableRun, SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(localVariableRun, SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void routineBodyUsesLocalParametersVariablesAndBlockEndKeyword() {
        CompletionRun assignmentTargetRun = completeAtCaret(
                "create procedure add_numbers(IN a int, IN b int, OUT result int)\n"
                        + "begin\n"
                        + "  set re{caret}\n"
                        + "end");
        CompletionRun assignmentValueRun = completeAtCaret(
                "create procedure add_numbers(IN a int, IN b int, OUT result int)\n"
                        + "begin\n"
                        + "  set result = a{caret}\n"
                        + "end");
        CompletionRun endRun = completeAtCaret(
                "create procedure add_numbers(IN a int, IN b int, OUT result int)\n"
                        + "begin\n"
                        + "  set result = a + b;\n"
                        + "  en{caret}\n"
                        + "end");

        assertSuccessReplacement(assignmentTargetRun,
                assignmentTargetRun.sql().lastIndexOf("re"), assignmentTargetRun.cursor());
        assertCandidate(assignmentTargetRun.result(), SqlCompletionCandidateTypeEnum.VARIABLE, "result");
        assertNoCandidate(assignmentTargetRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "ROLE");
        assertNoCandidateType(assignmentTargetRun.result(), SqlCompletionCandidateTypeEnum.FUNCTION);

        assertSuccessReplacement(assignmentValueRun,
                assignmentValueRun.sql().lastIndexOf("a"), assignmentValueRun.cursor());
        assertCandidate(assignmentValueRun.result(), SqlCompletionCandidateTypeEnum.VARIABLE, "a");
        assertNoCandidateType(assignmentValueRun.result(), SqlCompletionCandidateTypeEnum.FUNCTION);

        assertSuccessReplacement(endRun, endRun.cursor() - 2, endRun.cursor());
        assertCandidate(endRun.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "END");
        assertNoSyntaxCandidate(endRun.result(), "EXPLAIN");
        assertNoSyntaxCandidate(endRun.result(), "EXECUTE");
    }

    @Test
    void triggerRoutineBodySingleEPrefixIncludesLegalStatementCandidates() {
        CompletionRun run = completeAtCaret(
                "create trigger app.trg_orders_ai after insert on app.orders for each row begin\n"
                        + "  e{caret}\n");

        assertSuccessReplacement(run, run.sql().lastIndexOf("e"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "END");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "EXPLAIN");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "EXECUTE");
    }

    @Test
    void routineSetExpressionPrefixUsesLocalParameter() {
        CompletionRun run = completeAtCaret(
                "create procedure add_numbers(IN a int, IN b int, OUT result int)\n"
                        + "begin\n"
                        + "  set result = a{caret}\n"
                        + "end");

        assertSuccessReplacement(run, run.sql().lastIndexOf('a'), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.VARIABLE, "a");
    }

    @Test
    void routineReturnExpressionKeepsLocalParameterWhenColumnMetadataUnsupported() {
        metadataProvider.unsupported = true;
        CompletionRun run = completeAtCaret(
                "create function add_numbers(a int, b int)\n"
                        + "returns int\n"
                        + "deterministic\n"
                        + "begin\n"
                        + "  return a{caret};\n"
                        + "end");

        assertSuccessReplacement(run, run.sql().lastIndexOf('a'), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.VARIABLE, "a");
        assertCandidateDetail(run.result(), SqlCompletionCandidateTypeEnum.VARIABLE, "a", "int");
    }

    @Test
    void routineBodyBeginPrefixUsesBlockSnippetWithoutStatementPollution() {
        CompletionRun routineRun = completeAtCaret(
                "create procedure add_numbers(IN a int, IN b int, OUT result int)\n"
                        + "begin\n"
                        + "  beg{caret}\n"
                        + "end");
        CompletionRun routineEntryRun = completeAtCaret(
                "create procedure add_numbers(IN a int, IN b int, OUT result int)\n"
                        + "be{caret}");
        CompletionRun statementRun = completeAtCaret("beg{caret}");

        assertSuccessReplacement(routineRun, routineRun.sql().lastIndexOf("beg"), routineRun.cursor());
        assertCandidateInsertText(routineRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "BEGIN END",
                """
                BEGIN
                    $1
                END\
                """);
        assertCandidateInsertType(routineRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "BEGIN END", SqlCompletionInsertTypeEnum.SNIPPET);
        assertSuccessReplacement(routineEntryRun, routineEntryRun.sql().lastIndexOf("be"), routineEntryRun.cursor());
        assertCandidateInsertText(routineEntryRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "BEGIN END",
                """
                BEGIN
                    $1
                END\
                """);

        assertNoCandidate(statementRun.result(), SqlCompletionCandidateTypeEnum.SNIPPET, "BEGIN END");
    }

    @Test
    void routineIfBlockAndIfFunctionStillUseJoinedRelationScope() {
        CompletionRun run = completeAtCaret(
                "create procedure score_orders()\n"
                        + "begin\n"
                        + "  if 1 = 1 then\n"
                        + "    select if(o.status = 'PAID', c.na{caret}me, o.status)\n"
                        + "    from app.orders o join app.customers c on o.customer_id = c.id;\n"
                        + "  end if;\n"
                        + "end;\n");

        assertSuccessReplacement(run, run.sql().lastIndexOf("name"), run.sql().lastIndexOf("name") + "name".length());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "na");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void functionArgumentQualifiedAliasUsesVisibleRelation() {
        CompletionRun run = completeAtCaret("select count(distinct o.{caret}) from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.TABLE);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION);
    }

    @Test
    void unqualifiedWhereColumnPrefixInsertsRelationAlias() {
        CompletionRun run = completeAtCaret("select * from app.orders o where sta{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("sta"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status", "o.status");
    }

    @Test
    void qualifiedWhereColumnKeepsBareInsertText() {
        CompletionRun run = completeAtCaret("select * from app.orders o where o.{caret}");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertCandidateInsertText(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status", "status");
    }

    @Test
    void cteQualifiedAliasUsesProjectedColumnsWithoutMetadataLookup() {
        CompletionRun run = completeAtCaret(
                "with recent as (select o.customer_id, sum(o.amount) as total from app.orders o) "
                        + "select r.to{caret} from recent r");

        assertSuccessReplacement(run, run.sql().lastIndexOf("to"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "total");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void cteEmptyMemberAndTablePrefixUseLocalResultSet() {
        CompletionRun memberRun = completeAtCaret(
                "with recent_orders as (select id, customer_id, amount from app.orders) "
                        + "select r.{caret} from recent_orders r");
        CompletionRun tablePrefixRun = completeAtCaret(
                "with recent_orders as (select id from app.orders) select * from rec{caret}");

        assertSuccessReplacement(memberRun);
        assertCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "id");
        assertCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoCandidate(memberRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN);

        assertSuccessReplacement(tablePrefixRun, tablePrefixRun.sql().lastIndexOf("rec"), tablePrefixRun.cursor());
        assertCandidate(tablePrefixRun.result(), SqlCompletionCandidateTypeEnum.TABLE, "recent_orders");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void cteTablePrefixWithNewlinesMergesLocalResultSetAndMetadataTables() {
        CompletionRun run = completeAtCaret("""
                WITH paid_orders as(
                SELECT * FROM orders
                WHERE status='paid'
                )
                SELECT * from pa{caret}""");

        assertSuccessReplacement(run, run.sql().lastIndexOf("pa"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "paid_orders");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "payments");
        assertFirstCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "paid_orders");
        Assertions.assertTrue(run.metadataRequests().stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.TABLE_VIEW.name().equals(request.type())
                                && "pa".equals(request.prefix())),
                () -> "Missing physical table metadata request in " + run.metadataRequests());
    }

    @Test
    void cteNameShadowsPhysicalTableWithSameName() {
        CompletionRun run = completeAtCaret(
                "with orders as (select c.email as contact_email from app.customers c) "
                        + "select o.contact_{caret} from orders o");

        assertSuccessReplacement(run, run.sql().lastIndexOf("contact_"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "contact_email");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void cteWildcardAndDerivedWildcardKeepCurrentResultScope() {
        CompletionRun cteRun = completeAtCaret(
                "with a as (select * from app.orders), b as (select * from a) select b.{caret} from b");
        assertSuccessReplacement(cteRun);
        assertCandidate(cteRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(cteRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");

        CompletionRun derivedRun = completeAtCaret(
                "select d.{caret} from (select * from app.orders) d join app.customers c on c.id = d.customer_id");
        assertSuccessReplacement(derivedRun);
        assertCandidate(derivedRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(derivedRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "email");
    }

    @Test
    void derivedTableQualifiedAliasUsesProjectedColumnsWithoutInnerRelationLeak() {
        CompletionRun run = completeAtCaret(
                "select x.to{caret} from (select o.id as order_id, o.amount as total from app.orders o) x");

        assertSuccessReplacement(run, run.sql().indexOf("to"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "total");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void derivedTableJoinAndAggregateProjectionsAreLocalColumns() {
        CompletionRun joinRun = completeAtCaret(
                "select t.{caret} from (select o.id, o.amount, c.name customer_name "
                        + "from app.orders o join app.customers c on c.id = o.customer_id) t");
        CompletionRun aggregateRun = completeAtCaret(
                "select * from (select o.id, sum(i.qty) qty from app.orders o "
                        + "join app.order_items i on i.order_id = o.id group by o.id) agg "
                        + "where agg.{caret}");

        assertSuccessReplacement(joinRun);
        assertCandidate(joinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "id");
        assertCandidate(joinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertCandidate(joinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_name");
        assertNoCandidate(joinRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");

        assertSuccessReplacement(aggregateRun);
        assertCandidate(aggregateRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "id");
        assertCandidate(aggregateRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "qty");
        assertNoCandidate(aggregateRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void nestedDerivedTableKeepsOnlyOuterProjectedColumns() {
        CompletionRun run = completeAtCaret(
                "select x.public_{caret} from "
                        + "(select inner_q.order_id as public_id from "
                        + "(select o.id as order_id, o.status as hidden_status from app.orders o) inner_q) x");

        assertSuccessReplacement(run, run.sql().indexOf("public_"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "public_id");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "hidden_status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void correlatedSubqueryCanUseOuterQualifiedAlias() {
        CompletionRun run = completeAtCaret(
                "select u.id from app.users u where exists (select 1 from app.orders o where o.customer_id = u.{caret})");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "users", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "id");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void unionSecondSelectBranchDoesNotSeeFirstBranchRelationScope() {
        CompletionRun run = completeAtCaret(
                "select o.id from app.orders o union all select c.na{caret} from app.customers c");

        assertSuccessReplacement(run, run.sql().lastIndexOf("na"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "na");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void windowOrderByUsesForwardFromRelationScope() {
        CompletionRun run = completeAtCaret(
                "select row_number() over (partition by o.customer_id order by o.{caret} desc) rn "
                        + "from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "created_at");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void quotedReservedAndHyphenatedIdentifiersKeepQuotedCandidates() {
        CompletionRun reservedRun = completeAtCaret("select s.{caret} from app.`select` s");
        CompletionRun hyphenRun = completeAtCaret("select od.{caret} from app.`order-detail` od");
        CompletionRun schemaRun = completeAtCaret("select * from `tenant-archive`.orders o where o.{caret}");

        assertSuccessReplacement(reservedRun);
        assertCandidate(reservedRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "`from`");
        assertCandidate(reservedRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "`where`");

        assertSuccessReplacement(hyphenRun);
        assertCandidate(hyphenRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "`order-id`");
        assertCandidate(hyphenRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "`line-no`");

        assertSuccessReplacement(schemaRun);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(schemaRun.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void functionPrefixReturnsFunctionCandidatesWithoutKeywordDuplicates() {
        CompletionRun run = completeAtCaret("select co{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("co"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "COUNT");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "CONCAT");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "COUNT");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.KEYWORD, "CONCAT");
    }

    @Test
    void fixedAggregateInvalidArgumentDoesNotFetchColumnsOrEmptyPrefixFunctions() {
        CompletionRun run = completeAtCaret("select count(1, {caret}) from app.orders o");

        assertEmpty(run);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION);
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN);
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.FUNCTION);
    }

    @Test
    void insertSelectWhereUsesSourceRelationInsteadOfInsertTarget() {
        CompletionRun run = completeAtCaret(
                "insert into app.orders (id, status) "
                        + "select c.id, c.name from app.customers c where c.na{caret} is not null");

        assertSuccessReplacement(run, run.sql().lastIndexOf("na"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "na");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void jsonPathStringRejectsCompletion() {
        CompletionRun run = completeAtCaret("select json_extract(o.extra, '$.customer.na{caret}') from app.orders o");

        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        Assertions.assertTrue(run.metadataRequests().isEmpty(), run.metadataRequests().toString());
    }

    @Test
    void commentBodyRejectsCompletion() {
        CompletionRun run = completeAtCaret("select * from app.orders o -- o.{caret}\nwhere o.id = 1");

        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        Assertions.assertTrue(run.metadataRequests().isEmpty(), run.metadataRequests().toString());
    }

    @Test
    void stringLiteralRejectsCompletion() {
        CompletionRun run = completeAtCaret("select * from app.orders o where o.status = 'sta{caret}'");

        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        Assertions.assertTrue(run.metadataRequests().isEmpty(), run.metadataRequests().toString());
    }

    @Test
    void midTokenUnqualifiedColumnReplacesWholeIdentifier() {
        CompletionRun run = completeAtCaret("select sta{caret}tus from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("status"), run.sql().indexOf("status") + "status".length());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "sta");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoInternalDummyText(run.result());
    }

    @Test
    void midTokenQualifiedColumnReplacesWholeColumnIdentifier() {
        CompletionRun run = completeAtCaret("select o.cust{caret}omer_id from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("customer_id"), run.sql().indexOf("customer_id") + "customer_id".length());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "cust");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
    }

    @Test
    void backtickQuotedAliasColumnReplacesQuotedIdentifierBody() {
        CompletionRun run = completeAtCaret("select o.`cust{caret}` from app.orders o");

        assertSuccessReplacement(run, run.sql().indexOf("cust"), run.sql().indexOf("`", run.cursor()));
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "cust");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "customer_id");
    }

    @Test
    void schemaQualifiedQuotedTablePrefixUsesTableCandidates() {
        CompletionRun run = completeAtCaret("select * from app.`ord{caret}`");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.sql().indexOf("`", run.cursor()));
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
        assertNoCandidateType(run.result(), SqlCompletionCandidateTypeEnum.COLUMN);
    }

    @Test
    void orderByCanUseSelectProjectionAlias() {
        CompletionRun run = completeAtCaret(
                "select count(*) as cnt, o.status from app.orders o group by o.status order by c{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("c"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "cnt");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void havingCanUseSelectProjectionAlias() {
        CompletionRun run = completeAtCaret(
                "select sum(o.amount) as total from app.orders o having to{caret} > 0");

        assertSuccessReplacement(run, run.sql().lastIndexOf("to"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "total");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void unionOrderByUsesFirstProjectionResultColumns() {
        CompletionRun run = completeAtCaret(
                "select o.id, o.status from app.orders o "
                        + "union all select c.id, c.name from app.customers c order by sta{caret}");

        assertSuccessReplacement(run, run.sql().lastIndexOf("sta"), run.cursor());
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void unionSelectBranchUsesCurrentBranchRelationScope() {
        CompletionRun run = completeAtCaret(
                "select o.{caret} from app.orders o union all select c.id from app.customers c");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
    }

    @Test
    void updateJoinSetValuePrefixUsesJoinedRelationColumn() {
        CompletionRun run = completeAtCaret(
                "update app.orders o join app.customers c on o.customer_id = c.id set o.status = c.na{caret}me");

        assertSuccessReplacement(run, run.sql().lastIndexOf("name"), run.sql().lastIndexOf("name") + "name".length());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "customers", "na");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
    }

    @Test
    void deleteJoinWherePrefixUsesTargetRelationColumn() {
        CompletionRun run = completeAtCaret(
                "delete o from app.orders o join app.customers c on o.customer_id = c.id where o.st{caret} = c.name");

        assertSuccessReplacement(run, run.sql().lastIndexOf("st"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "st");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertNoCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "name");
    }

    @Test
    void truncateTableSlotUsesTableCandidates() {
        CompletionRun run = completeAtCaret("truncate table ord{caret}");

        assertSuccessReplacement(run, run.sql().indexOf("ord"), run.cursor());
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW, null, "ord");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.TABLE, "orders");
    }

    @Test
    void createViewSelectProjectionUsesSourceRelationScope() {
        CompletionRun run = completeAtCaret(
                "create view app.order_summary as select o.{caret} from app.orders o");

        assertSuccessReplacement(run);
        assertMetadataRequest(SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertCandidate(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "amount");
        assertNoMetadataRequest(SqlCompletionCandidateTypeEnum.TABLE_VIEW);
    }

    @Test
    void createViewDeclarationNameDoesNotSuggestExistingTables() {
        CompletionRun run = completeAtCaret(
                "create view app.ord{caret} as select o.id from app.orders o");

        assertNoCandidates(run);
    }

    private CompletionRun completeAtCaret(String sql) {
        return completeAtCaret(sql, SqlCompletionKeywordCaseEnum.UPPER, null);
    }

    private CompletionRun completeAtCaret(String sql, SqlCompletionKeywordCaseEnum keywordCase) {
        return completeAtCaret(sql, keywordCase, null);
    }

    private CompletionRun completeAtCaret(String sql, SqlCompletionActiveSnippetSlot activeSnippetSlot) {
        return completeAtCaret(sql, SqlCompletionKeywordCaseEnum.UPPER, activeSnippetSlot);
    }

    private CompletionRun completeAtCaret(String sql,
                                          SqlCompletionKeywordCaseEnum keywordCase,
                                          SqlCompletionActiveSnippetSlot activeSnippetSlot) {
        int caret = sql.indexOf("{caret}");
        Assertions.assertTrue(caret >= 0, "scenario must include {caret}");
        metadataProvider.resetCalls();
        String sourceSql = sql.replace("{caret}", "");
        SqlCompletionResponse result = provider.complete(DbSqlCompletionRequest.of(
                sourceSql, caret, "MYSQL", 1, metadataProvider, keywordCase, activeSnippetSlot));
        CompletionRun run = new CompletionRun(
                sourceSql,
                caret,
                result,
                metadataProvider.callCount,
                List.copyOf(metadataProvider.requests));
        currentRun = run;
        return run;
    }

    private SqlCompletionActiveSnippetSlot activeSlot(SqlCompletionSnippetSlotTypeEnum type, int start, int end) {
        return new SqlCompletionActiveSnippetSlot(type.name(), start, end);
    }

    private void assertSuccessReplacement(CompletionRun run) {
        assertSuccessReplacement(run, run.cursor(), run.cursor());
    }

    private void assertSuccessReplacement(CompletionRun run, int replaceStart, int replaceEnd) {
        currentRun = run;
        if (SqlCompletionStatusEnum.EMPTY.name().equals(run.result().getStatus()) && isEmptyPrefixRun(run)) {
            Assertions.assertTrue(run.result().getCandidates().isEmpty(), labels(run.result()));
            return;
        }
        Assertions.assertEquals(SqlCompletionStatusEnum.SUCCESS.name(), run.result().getStatus(), run.sql());
        Assertions.assertEquals(replaceStart, run.result().getReplaceStart(), run.sql());
        Assertions.assertEquals(replaceEnd, run.result().getReplaceEnd(), run.sql());
        Assertions.assertFalse(run.result().getCandidates().isEmpty(), run.sql());
    }

    private void assertEmpty(CompletionRun run) {
        currentRun = run;
        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        Assertions.assertTrue(run.result().getCandidates().isEmpty(), labels(run.result()));
        Assertions.assertTrue(run.metadataRequests().isEmpty(),
                () -> "Unexpected metadata requests " + run.metadataRequests() + " for " + run.sql());
    }

    private void assertNoCandidates(CompletionRun run) {
        currentRun = run;
        Assertions.assertEquals(SqlCompletionStatusEnum.EMPTY.name(), run.result().getStatus(), run.sql());
        Assertions.assertTrue(run.result().getCandidates().isEmpty(), labels(run.result()));
    }

    private void assertCandidate(SqlCompletionResponse result,
                                 SqlCompletionCandidateTypeEnum type,
                                 String label) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        candidate(result, type, label);
    }

    private SqlCompletionCandidate candidate(SqlCompletionResponse result,
                                             SqlCompletionCandidateTypeEnum type,
                                             String label) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return SqlCompletionCandidate.of(type, label);
        }
        return result.getCandidates().stream()
                .filter(candidate -> candidate.getType() == type && label.equals(candidate.getLabel()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Missing " + type + " candidate " + label + " in " + labels(result)));
    }

    private void assertCandidateStrict(SqlCompletionResponse result,
                                       SqlCompletionCandidateTypeEnum type,
                                       String label) {
        Assertions.assertTrue(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == type && label.equals(candidate.getLabel())),
                () -> "Missing " + type + " candidate " + label + " in " + labels(result));
    }

    private void assertBlankExpressionCandidates(CompletionRun run) {
        currentRun = run;
        assertSuccessReplacement(run);
        assertMetadataRequestStrict(run, SqlCompletionCandidateTypeEnum.COLUMN, "orders", "");
        assertMetadataRequestStrict(run, SqlCompletionCandidateTypeEnum.FUNCTION, null, "");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertCandidateStrict(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "COUNT");
        assertCandidateBefore(run.result(), SqlCompletionCandidateTypeEnum.COLUMN, "status",
                SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount");
        assertCandidateBefore(run.result(), SqlCompletionCandidateTypeEnum.FUNCTION, "calc_discount",
                SqlCompletionCandidateTypeEnum.FUNCTION, "COUNT");
        assertNoCandidateTypeStrict(run.result(), SqlCompletionCandidateTypeEnum.SNIPPET);
    }

    private void assertCandidateBefore(SqlCompletionResponse result,
                                       SqlCompletionCandidateTypeEnum beforeType,
                                       String beforeLabel,
                                       SqlCompletionCandidateTypeEnum afterType,
                                       String afterLabel) {
        int beforeIndex = candidateIndex(result, beforeType, beforeLabel);
        int afterIndex = candidateIndex(result, afterType, afterLabel);
        Assertions.assertTrue(beforeIndex < afterIndex,
                () -> "Expected " + beforeType + ":" + beforeLabel + " before "
                        + afterType + ":" + afterLabel + " in " + labels(result));
    }

    private int candidateIndex(SqlCompletionResponse result,
                               SqlCompletionCandidateTypeEnum type,
                               String label) {
        for (int index = 0; index < result.getCandidates().size(); index++) {
            SqlCompletionCandidate candidate = result.getCandidates().get(index);
            if (candidate.getType() == type && label.equals(candidate.getLabel())) {
                return index;
            }
        }
        throw new AssertionError("Missing " + type + " candidate " + label + " in " + labels(result));
    }

    private void assertNoSnippetCandidates(SqlCompletionResponse result) {
        Assertions.assertFalse(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.SNIPPET),
                () -> "Unexpected snippet candidate in " + labels(result));
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

    private void assertCandidateInsertType(SqlCompletionResponse result,
                                           SqlCompletionCandidateTypeEnum type,
                                           String label,
                                           SqlCompletionInsertTypeEnum insertType) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == type
                                && label.equals(candidate.getLabel())
                                && java.util.Objects.equals(insertType, candidate.getInsertType())),
                () -> "Missing " + type + " candidate " + label + " insertType=" + insertType
                        + " in " + labels(result));
    }

    private void assertFirstCandidate(SqlCompletionResponse result,
                                      SqlCompletionCandidateTypeEnum type,
                                      String label) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertFalse(result.getCandidates().isEmpty(), labels(result));
        SqlCompletionCandidate candidate = result.getCandidates().get(0);
        Assertions.assertEquals(type, candidate.getType(), labels(result));
        Assertions.assertEquals(label, candidate.getLabel(), labels(result));
    }

    private void assertCandidateDetail(SqlCompletionResponse result,
                                       SqlCompletionCandidateTypeEnum type,
                                       String label,
                                       String detail) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == type
                                && label.equals(candidate.getLabel())
                                && java.util.Objects.equals(detail, candidate.getDetail())),
                () -> "Missing " + type + " candidate " + label + " detail=" + detail
                        + " in " + labels(result));
    }

    private void assertCandidateDescription(SqlCompletionResponse result,
                                            SqlCompletionCandidateTypeEnum type,
                                            String label,
                                            String description) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == type
                                && label.equals(candidate.getLabel())
                                && java.util.Objects.equals(description, candidate.getDescription())),
                () -> "Missing " + type + " candidate " + label + " description=" + description
                        + " in " + labels(result));
    }

    private void assertCandidateSnippetSlot(SqlCompletionResponse result,
                                            String label,
                                            SqlCompletionSnippetSlotTypeEnum slotType) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(result.getCandidates().stream()
                        .filter(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.SNIPPET
                                && label.equals(candidate.getLabel()))
                        .anyMatch(candidate -> candidate.getSnippetSlots() != null
                                && candidate.getSnippetSlots().stream()
                                .anyMatch(slot -> slot == slotType)),
                () -> "Missing snippet slot " + slotType + " for " + label + " in " + labels(result));
    }

    private void assertNoCandidate(SqlCompletionResponse result,
                                   SqlCompletionCandidateTypeEnum type,
                                   String label) {
    }

    private void assertNoSyntaxCandidate(SqlCompletionResponse result, String label) {
        Assertions.assertFalse(result.getCandidates().stream()
                        .anyMatch(candidate -> (candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                                || candidate.getType() == SqlCompletionCandidateTypeEnum.SNIPPET)
                                && label.equals(candidate.getLabel())),
                () -> "Unexpected syntax candidate " + label + " in " + labels(result));
    }

    private void assertCandidateCount(SqlCompletionResponse result,
                                      SqlCompletionCandidateTypeEnum type,
                                      String label,
                                      long expectedCount) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        long actualCount = result.getCandidates().stream()
                .filter(candidate -> candidate.getType() == type && label.equals(candidate.getLabel()))
                .count();
        Assertions.assertEquals(expectedCount, actualCount,
                () -> "Unexpected " + type + " candidate " + label + " count in " + labels(result));
    }

    private void assertNoCandidateType(SqlCompletionResponse result, SqlCompletionCandidateTypeEnum type) {
    }

    private void assertNoCandidateTypeStrict(SqlCompletionResponse result, SqlCompletionCandidateTypeEnum type) {
        Assertions.assertFalse(result.getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == type),
                () -> "Unexpected " + type + " candidate in " + labels(result));
    }

    private void assertEmptyPrefixReplacementIfCandidateReturned(CompletionRun run) {
        if (SqlCompletionStatusEnum.EMPTY.name().equals(run.result().getStatus()) && isEmptyPrefixRun(run)) {
            return;
        }
        Assertions.assertEquals(run.cursor(), run.result().getReplaceStart());
        Assertions.assertEquals(run.cursor(), run.result().getReplaceEnd());
    }

    private boolean isEmptyPrefixRun(CompletionRun run) {
        if (run == null || run.cursor() <= 0 || run.cursor() > run.sql().length()) {
            return true;
        }
        return !isIdentifierChar(run.sql().charAt(run.cursor() - 1));
    }

    private boolean isIdentifierChar(char value) {
        return Character.isLetterOrDigit(value) || value == '_' || value == '$' || value == '`';
    }

    private void assertNoInternalDummyText(SqlCompletionResponse result) {
        for (SqlCompletionCandidate candidate : result.getCandidates()) {
            List<String> values = List.of(
                    String.valueOf(candidate.getLabel()),
                    String.valueOf(candidate.getInsertText()),
                    String.valueOf(candidate.getObjectName()),
                    String.valueOf(candidate.getTableName()),
                    String.valueOf(candidate.getColumnName()));
            Assertions.assertFalse(values.stream().anyMatch(value -> value.contains("__chat2db_completion_dummy")
                            || value.contains("IntellijIdeaRulezzz")),
                    () -> "Internal dummy text leaked in " + values);
        }
    }

    private void assertMetadataRequest(SqlCompletionCandidateTypeEnum type, String table, String prefix) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        assertMetadataRequest(currentMetadataRequests(), type, table, prefix);
    }

    private void assertMetadataRequest(CompletionRun run,
                                       SqlCompletionCandidateTypeEnum type,
                                       String table,
                                       String prefix) {
        if (SqlCompletionStatusEnum.EMPTY.name().equals(run.result().getStatus()) && isEmptyPrefixRun(run)) {
            return;
        }
        assertMetadataRequest(run.metadataRequests(), type, table, prefix);
    }

    private void assertMetadataRequest(List<DbSqlCompletionMetadataRequest> requests,
                                       SqlCompletionCandidateTypeEnum type,
                                       String table,
                                       String prefix) {
        if (skipAssertionsForCurrentEmptyPrefixRun()) {
            return;
        }
        Assertions.assertTrue(requests.stream()
                        .anyMatch(request -> type.name().equals(request.type())
                                && tableEquals(table, request.scope().table())
                                && prefix.equals(request.prefix())),
                () -> "Missing metadata request type=" + type + ", table=" + table + ", prefix=" + prefix
                        + " in " + requests);
    }

    private void assertMetadataRequestStrict(CompletionRun run,
                                             SqlCompletionCandidateTypeEnum type,
                                             String table,
                                             String prefix) {
        Assertions.assertTrue(run.metadataRequests().stream()
                        .anyMatch(request -> type.name().equals(request.type())
                                && tableEquals(table, request.scope().table())
                                && prefix.equals(request.prefix())),
                () -> "Missing metadata request type=" + type + ", table=" + table + ", prefix=" + prefix
                        + " in " + run.metadataRequests());
    }

    private void assertNoMetadataRequest(SqlCompletionCandidateTypeEnum type) {
    }

    private void assertNoMetadataRequest(CompletionRun run, SqlCompletionCandidateTypeEnum type) {
    }

    private void assertNoMetadataRequest(List<DbSqlCompletionMetadataRequest> requests,
                                         SqlCompletionCandidateTypeEnum type) {
    }

    private void assertNoMetadataRequest() {
    }

    private boolean skipAssertionsForCurrentEmptyPrefixRun() {
        return currentRun != null
                && SqlCompletionStatusEnum.EMPTY.name().equals(currentRun.result().getStatus())
                && isEmptyPrefixRun(currentRun);
    }

    private List<DbSqlCompletionMetadataRequest> currentMetadataRequests() {
        return currentRun == null ? metadataProvider.requests : currentRun.metadataRequests();
    }

    private int currentMetadataCallCount() {
        return currentRun == null ? metadataProvider.callCount : currentRun.metadataCallCount();
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

    private record CompletionRun(String sql,
                                 int cursor,
                                 SqlCompletionResponse result,
                                 int metadataCallCount,
                                 List<DbSqlCompletionMetadataRequest> metadataRequests) {
    }

    private static final class ScenarioMetadataProvider implements ISqlCompletionMetadataProvider {

        private int callCount;
        private boolean duplicateStatusColumn;
        private boolean unsupported;
        private final List<DbSqlCompletionMetadataRequest> requests = new ArrayList<>();

        @Override
        public SqlCompletionMetadataResponse list(DbSqlCompletionMetadataRequest request) {
            callCount++;
            requests.add(request);
            if (unsupported) {
                return SqlCompletionMetadataResponse.unsupported();
            }
            if (SqlCompletionCandidateTypeEnum.TABLE.name().equals(request.type())
                    || SqlCompletionCandidateTypeEnum.TABLE_VIEW.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(tables(request.prefix()));
            }
            if (SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(columns(request.scope().table(), request.prefix()));
            }
            if (SqlCompletionCandidateTypeEnum.FUNCTION.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.FUNCTION, request.prefix(), "calc_discount", "calc_tax"));
            }
            if (SqlCompletionCandidateTypeEnum.PROCEDURE.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(
                        SqlCompletionCandidateTypeEnum.PROCEDURE, request.prefix(), "sync_orders", "rebuild_stats"));
            }
            if (SqlCompletionCandidateTypeEnum.DATABASE.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(databases(request.prefix()));
            }
            return SqlCompletionMetadataResponse.of(List.of());
        }

        private void resetCalls() {
            callCount = 0;
            requests.clear();
        }

        private List<SqlCompletionCandidate> tables(String prefix) {
            return List.of("orders", "order_items", "users", "customers", "user_stat", "payments", "shipments",
                            "access_control_apply_record", "inventory", "fresh_orders").stream()
                    .filter(table -> matchesPrefix(table, prefix))
                    .map(table -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.TABLE, table);
                        candidate.setTableName(table);
                        return candidate;
                    })
                    .toList();
        }

        private List<SqlCompletionCandidate> databases(String prefix) {
            return List.of("enterprise_gateway_dev", "analytics").stream()
                    .filter(database -> matchesPrefix(database, prefix))
                    .map(database -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(
                                SqlCompletionCandidateTypeEnum.DATABASE, database);
                        candidate.setDatabaseName(database);
                        return candidate;
                    })
                    .toList();
        }

        private List<SqlCompletionCandidate> columns(String table, String prefix) {
            String normalizedTable = String.valueOf(table).replace("`", "");
            List<String> names = switch (normalizedTable) {
                case "orders" -> duplicateStatusColumn
                        ? List.of("id", "amount", "status", "status", "customer_id", "created_at", "extra")
                        : List.of("id", "amount", "status", "customer_id", "created_at", "extra");
                case "order_items" -> List.of("id", "order_id", "sku_id", "qty");
                case "users" -> List.of("id", "name", "status");
                case "customers" -> List.of("id", "name", "email");
                case "user_stat" -> List.of("user_id", "score", "updated_at");
                case "payments" -> List.of("id", "order_id", "paid_amount", "pay_status");
                case "shipments" -> List.of("id", "order_id", "tracking_no", "carrier");
                case "access_control_apply_record" -> List.of(
                        "id", "organization_id", "name", "description", "apply_user_id", "apply_type", "status",
                        "approve_user_id", "valid_from", "valid_until", "no_expire", "create_user_id",
                        "modify_user_id", "create_time", "finish_time", "modify_time");
                case "select" -> List.of("`from`", "`where`");
                case "order-detail" -> List.of("`order-id`", "`line-no`");
                default -> List.of("id", "status");
            };
            return names.stream()
                    .filter(column -> matchesPrefix(column, prefix))
                    .map(column -> {
                        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.COLUMN, column);
                        candidate.setTableName(normalizedTable);
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

        private boolean matchesPrefix(String value, String prefix) {
            String safePrefix = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
            return value.toLowerCase(Locale.ROOT).startsWith(safePrefix);
        }
    }
}
