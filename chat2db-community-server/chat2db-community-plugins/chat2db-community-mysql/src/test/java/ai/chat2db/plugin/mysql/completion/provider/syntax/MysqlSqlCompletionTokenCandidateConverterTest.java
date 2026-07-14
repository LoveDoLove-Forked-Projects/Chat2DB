package ai.chat2db.plugin.mysql.completion.provider.syntax;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionInputCleanResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionInsertTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatementWindowTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionDummySql;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionTokenCandidateConverterTest {

    @Test
    void c3FunctionTokenConvertsToFunctionCandidate() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select cou", "cou");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.COUNT, List.of(MySqlParser.RULE_aggregateWindowedFunction)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION
                        && "COUNT".equals(candidate.getLabel())
                        && "COUNT(${1:})$0".equals(candidate.getInsertText())
                        && SqlCompletionInsertTypeEnum.SNIPPET.equals(candidate.getInsertType())
                        && "([{DISTINCT | ALL}] expr:any*)".equals(candidate.getDetail())
                        && "int".equals(candidate.getDescription())
                        && "int".equals(candidate.getDataType())));
        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "COUNT".equals(candidate.getLabel())));
    }

    @Test
    void c3FunctionTokenWithNewMetadataConvertsToFunctionCandidate() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select bit_c", "bit_c");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.BIT_COUNT, List.of(MySqlParser.RULE_functionNameBase)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION
                        && "BIT_COUNT".equals(candidate.getLabel())
                        && "BIT_COUNT(${1:})$0".equals(candidate.getInsertText())
                        && SqlCompletionInsertTypeEnum.SNIPPET.equals(candidate.getInsertType())
                        && "(n:bigint)".equals(candidate.getDetail())
                        && "int".equals(candidate.getDescription())
                        && "int".equals(candidate.getDataType())));
        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "BIT_COUNT".equals(candidate.getLabel())));
    }

    @Test
    void nonCandidateGrammarFunctionTokenIsDropped() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select sta", "sta");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.STATEMENT, List.of(MySqlParser.RULE_functionNameBase)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertTrue(candidates.isEmpty());
    }

    @Test
    void functionLikeTokenWithoutFunctionRuleContextDoesNotConvertToFunctionCandidate() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select cou", "cou");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.COUNT, List.of(MySqlParser.RULE_keywordsCanBeId)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION
                        && "COUNT".equals(candidate.getLabel())));
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "COUNT".equals(candidate.getLabel())));
    }

    @Test
    void structureTokensDoNotConvertToCandidates() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select ", "");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(
                        MySqlLexer.DOT, List.of(),
                        MySqlLexer.COMMA, List.of(),
                        MySqlLexer.SEMI, List.of(),
                        MySqlLexer.STAR, List.of(),
                        MySqlLexer.LR_BRACKET, List.of(),
                        MySqlLexer.RR_BRACKET, List.of(),
                        MySqlLexer.PLUS, List.of(),
                        MySqlLexer.EQUAL_SYMBOL, List.of(),
                        MySqlLexer.BIT_AND_OP, List.of()), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertTrue(candidates.isEmpty());
    }

    @Test
    void dataTypeTokenOutsideDataTypeRuleConvertsAsKeyword() {
        MysqlSqlCompletionCandidateContext context = candidateContext("se", "se");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.SET, List.of(MySqlParser.RULE_root)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "SET".equals(candidate.getLabel())));
    }

    @Test
    void dataTypeTokenInsideDataTypeRuleDoesNotConvertAsKeyword() {
        MysqlSqlCompletionCandidateContext context = candidateContext("create table t (c se", "se");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.SET, List.of(MySqlParser.RULE_dataType)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "SET".equals(candidate.getLabel())));
    }

    @Test
    void selectClauseOrderAndGroupTokensPromoteToKeywordPhrases() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select * from app.orders ", "");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(
                        MySqlLexer.ORDER, List.of(MySqlParser.RULE_orderByClause),
                        MySqlLexer.GROUP, List.of(MySqlParser.RULE_groupByClause)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "ORDER".equals(candidate.getLabel())));
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "ORDER BY".equals(candidate.getLabel())
                        && "ORDER BY".equals(candidate.getInsertText())
                        && candidate.getInsertType() == SqlCompletionInsertTypeEnum.PLAIN_TEXT));
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "GROUP BY".equals(candidate.getLabel())
                        && "GROUP BY".equals(candidate.getInsertText())
                        && candidate.getInsertType() == SqlCompletionInsertTypeEnum.PLAIN_TEXT));
        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "GROUP".equals(candidate.getLabel())));
    }

    @Test
    void ddlClauseTokensPromoteToKeywordPhrases() {
        MysqlSqlCompletionCandidateContext context = candidateContext("create table app.orders (id int ", "");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(
                        MySqlLexer.PRIMARY, List.of(MySqlParser.RULE_columnConstraint),
                        MySqlLexer.UNIQUE, List.of(MySqlParser.RULE_columnConstraint),
                        MySqlLexer.NOT, List.of(MySqlParser.RULE_nullNotnull),
                        MySqlLexer.CHARACTER, List.of(MySqlParser.RULE_charSet)),
                Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        assertKeywordPhrase(candidates, "PRIMARY KEY");
        assertKeywordPhrase(candidates, "UNIQUE KEY");
        assertKeywordPhrase(candidates, "NOT NULL");
        assertKeywordPhrase(candidates, "CHARACTER SET");
        assertNoKeyword(candidates, "PRIMARY");
        assertNoKeyword(candidates, "UNIQUE");
        assertNoKeyword(candidates, "NOT");
        assertNoKeyword(candidates, "CHARACTER");
    }

    @Test
    void predicateIsTokenPromotesToNullCheckPhrases() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select * from app.orders where deleted_at is",
                "is");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.IS, List.of(MySqlParser.RULE_predicate)),
                Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        assertKeywordPhrase(candidates, "IS NULL");
        assertKeywordPhrase(candidates, "IS NOT NULL");
        assertNoKeyword(candidates, "IS");
    }

    @Test
    void ddlIfTokenPromotesToExistsPhrasesBeforeFunctionCandidate() {
        MysqlSqlCompletionCandidateContext context = candidateContext("drop table if", "if");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.IF, List.of(MySqlParser.RULE_ifExists)),
                Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        assertKeywordPhrase(candidates, "IF EXISTS");
        assertNoKeyword(candidates, "IF");
        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION
                        && "IF".equals(candidate.getLabel())),
                () -> "Unexpected IF function in " + labels(candidates));
    }

    @Test
    void ddlIfTokenPromotesToIfNotExistsPhrase() {
        MysqlSqlCompletionCandidateContext context = candidateContext("create table if", "if");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.IF, List.of(MySqlParser.RULE_ifNotExists)),
                Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        assertKeywordPhrase(candidates, "IF NOT EXISTS");
        assertNoKeyword(candidates, "IF");
    }

    @Test
    void joinClauseTokensPromoteToKeywordPhrases() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select * from app.orders o ", "");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(
                        MySqlLexer.LEFT, List.of(MySqlParser.RULE_joinPart),
                        MySqlLexer.RIGHT, List.of(MySqlParser.RULE_joinPart),
                        MySqlLexer.INNER, List.of(MySqlParser.RULE_joinPart),
                        MySqlLexer.CROSS, List.of(MySqlParser.RULE_joinPart),
                        MySqlLexer.NATURAL, List.of(MySqlParser.RULE_joinPart)),
                Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        assertKeywordPhrase(candidates, "LEFT JOIN");
        assertKeywordPhrase(candidates, "RIGHT JOIN");
        assertKeywordPhrase(candidates, "INNER JOIN");
        assertKeywordPhrase(candidates, "CROSS JOIN");
        assertKeywordPhrase(candidates, "NATURAL JOIN");
        assertNoKeyword(candidates, "LEFT");
        assertNoKeyword(candidates, "RIGHT");
        assertNoKeyword(candidates, "INNER");
        assertNoKeyword(candidates, "CROSS");
        assertNoKeyword(candidates, "NATURAL");
    }

    @Test
    void onTokenCanReturnMultipleContextualKeywordPhrases() {
        MysqlSqlCompletionCandidateContext context = candidateContext(
                "create table app.orders (customer_id bigint references app.customers(id) ", "");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.ON, List.of(MySqlParser.RULE_referenceAction)),
                Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        assertKeywordPhrase(candidates, "ON DELETE");
        assertKeywordPhrase(candidates, "ON UPDATE");
        assertNoKeyword(candidates, "ON DUPLICATE KEY UPDATE");
        assertNoKeyword(candidates, "ON");
    }

    @Test
    void insertOnTokenCanPromoteToDuplicateKeyUpdatePhrase() {
        MysqlSqlCompletionCandidateContext context = candidateContext(
                "insert into app.orders (id, status) values (1, 'PAID') ", "");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.ON, List.of(MySqlParser.RULE_insertStatement)),
                Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        assertKeywordPhrase(candidates, "ON DUPLICATE KEY UPDATE");
        assertNoKeyword(candidates, "ON");
    }

    @Test
    void logfileGroupTokenDoesNotPromoteToGroupByPhrase() {
        MysqlSqlCompletionCandidateContext context = candidateContext("create logfile gr", "gr");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.GROUP, List.of(MySqlParser.RULE_createLogfileGroup)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "GROUP".equals(candidate.getLabel())));
        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "GROUP BY".equals(candidate.getLabel())));
    }

    @Test
    void orderTokenCanPromoteFromCurrentC3RulePath() {
        MysqlSqlCompletionCandidateContext context = candidateContext("select * from app.orders or", "or");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 8,
                Map.of(MySqlLexer.ORDER, List.of(MySqlParser.RULE_root)),
                Map.of(MySqlParser.RULE_orderByClause,
                        new SqlCompletionCandidates.RuleCandidate(8, 8,
                                List.of(MySqlParser.RULE_selectStatement, MySqlParser.RULE_orderByClause))));

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "ORDER".equals(candidate.getLabel())));
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "ORDER BY".equals(candidate.getLabel())
                        && "ORDER BY".equals(candidate.getInsertText())));
    }

    @Test
    void orderTokenCanPromoteFromEarlierRuleCandidatePathInCurrentC3Result() {
        MysqlSqlCompletionCandidateContext context = candidateContext(
                "select * from app.access_control_apply_record a where id=1 and a.no_expire=1 or", "or");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 24,
                Map.of(MySqlLexer.ORDER, List.of(MySqlParser.RULE_createServer)),
                Map.of(MySqlParser.RULE_columnReferenceName,
                        new SqlCompletionCandidates.RuleCandidate(22, 22,
                                List.of(MySqlParser.RULE_root,
                                        MySqlParser.RULE_sqlStatements,
                                        MySqlParser.RULE_sqlStatement,
                                        MySqlParser.RULE_dmlStatement,
                                        MySqlParser.RULE_selectStatement,
                                        MySqlParser.RULE_querySpecificationNointo,
                                        MySqlParser.RULE_fromClause,
                                        MySqlParser.RULE_expression,
                                        MySqlParser.RULE_predicate,
                                        MySqlParser.RULE_expressionAtom))));

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "ORDER".equals(candidate.getLabel())));
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "ORDER BY".equals(candidate.getLabel())
                        && "ORDER BY".equals(candidate.getInsertText())));
    }

    @Test
    void clausePhraseCanPromoteFromCurrentC3RulePath() {
        MysqlSqlCompletionCandidateContext context = candidateContext("""
                CREATE TABLE order_item (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    order_id BIGINT,
                    product_id BIGINT,
                    quantity INT,
                    unit_price DECIMAL(12,2),
                    amount DECIMAL(12,2),
                    CONSTRAINT f""", "f");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 56,
                Map.of(MySqlLexer.FOREIGN, List.of(MySqlParser.RULE_truncateTable)),
                Map.of(MySqlParser.RULE_constraintDeclarationName,
                        new SqlCompletionCandidates.RuleCandidate(56, 56,
                                List.of(MySqlParser.RULE_root,
                                        MySqlParser.RULE_sqlStatements,
                                        MySqlParser.RULE_sqlStatement,
                                        MySqlParser.RULE_ddlStatement,
                                        MySqlParser.RULE_createTable,
                                        MySqlParser.RULE_createDefinitions,
                                        MySqlParser.RULE_createDefinition,
                                        MySqlParser.RULE_tableConstraint,
                                        MySqlParser.RULE_constraintDeclarationName))));

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "FOREIGN".equals(candidate.getLabel())));
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "FOREIGN KEY".equals(candidate.getLabel())
                        && "FOREIGN KEY".equals(candidate.getInsertText())
                        && candidate.getInsertType() == SqlCompletionInsertTypeEnum.PLAIN_TEXT));
    }

    @Test
    void clausePhraseCanPromoteFromBoundaryC3RulePath() {
        MysqlSqlCompletionCandidateContext context = candidateContext("""
                CREATE TABLE order_item (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    order_id BIGINT,
                    product_id BIGINT,
                    quantity INT,
                    unit_price DECIMAL(12,2),
                    amount DECIMAL(12,2),
                    CONSTRAINT fk_item_order
                    f""", "f");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 58,
                Map.of(MySqlLexer.FOREIGN, List.of(MySqlParser.RULE_truncateTable)), Map.of());

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "FOREIGN".equals(candidate.getLabel())));
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "FOREIGN KEY".equals(candidate.getLabel())
                        && "FOREIGN KEY".equals(candidate.getInsertText())
                        && candidate.getInsertType() == SqlCompletionInsertTypeEnum.PLAIN_TEXT));
    }

    @Test
    void foreignTokenDoesNotPromoteOutsideConstraintContext() {
        MysqlSqlCompletionCandidateContext context = candidateContext("foreign", "f");
        SqlCompletionCandidates c3Result = new SqlCompletionCandidates(true, 0,
                Map.of(MySqlLexer.FOREIGN, List.of(MySqlParser.RULE_root)),
                Map.of(MySqlParser.RULE_root,
                        new SqlCompletionCandidates.RuleCandidate(0, 0, List.of(MySqlParser.RULE_root))));

        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result);

        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "FOREIGN".equals(candidate.getLabel())));
        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && "FOREIGN KEY".equals(candidate.getLabel())));
    }

    private static void assertKeywordPhrase(List<SqlCompletionCandidate> candidates, String label) {
        Assertions.assertTrue(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && label.equals(candidate.getLabel())
                        && label.equals(candidate.getInsertText())
                        && candidate.getInsertType() == SqlCompletionInsertTypeEnum.PLAIN_TEXT),
                () -> "Missing keyword phrase " + label + " in " + labels(candidates));
    }

    private static void assertNoKeyword(List<SqlCompletionCandidate> candidates, String label) {
        Assertions.assertFalse(candidates.stream()
                .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                        && label.equals(candidate.getLabel())),
                () -> "Unexpected keyword " + label + " in " + labels(candidates));
    }

    private static String labels(List<SqlCompletionCandidate> candidates) {
        return candidates.stream()
                .map(candidate -> candidate.getType() + ":" + candidate.getLabel())
                .toList()
                .toString();
    }

    private MysqlSqlCompletionCandidateContext candidateContext(String sql, String prefix) {
        return new MysqlSqlCompletionCandidateContext(
                DbSqlCompletionRequest.of(sql, sql.length(), "MYSQL", 1, ISqlCompletionMetadataProvider.unsupported()),
                new SqlCompletionInputCleanResponse(sql, sql, sql.length()),
                new SqlCompletionStatementWindow(sql, sql, 0, sql.length(), sql.length(),
                        SqlCompletionStatementWindowTypeEnum.CURRENT_STATEMENT.name()),
                SqlCompletionDummySql.unchanged(sql, sql.length()),
                SqlCompletionCursorContext.admitted(SqlCompletionMetadataScope.empty(), prefix,
                        Math.max(0, sql.length() - prefix.length()), sql.length(), false));
    }
}
