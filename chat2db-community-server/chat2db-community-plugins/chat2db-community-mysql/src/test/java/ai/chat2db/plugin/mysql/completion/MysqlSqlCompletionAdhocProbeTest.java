package ai.chat2db.plugin.mysql.completion;

import ai.chat2db.plugin.mysql.completion.analysis.MysqlSqlCompletionCursorAnalyzer;
import ai.chat2db.plugin.mysql.completion.c3.MysqlSqlCompletionEngine;
import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidatePlanExecutor;
import ai.chat2db.plugin.mysql.completion.dummy.MysqlSqlCompletionDummyBuilder;
import ai.chat2db.plugin.mysql.completion.locate.MysqlSqlCompletionStatementLocator;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.slot.MysqlSqlCompletionRuleSlot;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionInsertStatementContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionInputCleaner;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalContext;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionInputCleanResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionDummyTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionDummySql;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionAdhocProbeTest {

    @Test
    void probeAlterTableDrop() {
        String sql = "alter table app.orders drop";
        probe(sql);
    }

    @Test
    void probeAlterTableDropAfterSpace() {
        String sql = "alter table app.orders drop ";
        probe(sql);
    }

    @Test
    void probeCreateTableTypeInPrefix() {
        String sql = "create table test (id in";
        probe(sql);
    }

    @Test
    void probeCreateTableColumnConstraintInPrefix() {
        String sql = "create table test (id int in";
        probe(sql);
    }

    @Test
    void probeCreateTableUniqueColumnPrefix() {
        probeMarked("""
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(100) NOT NULL,
                    email VARCHAR(200),
                    mobile VARCHAR(50),
                    age INT,
                    dept_id BIGINT,
                    status VARCHAR(20) DEFAULT 'NORMAL',
                     create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,
                        CONSTRAINT uk_user_email
                        UNIQUE(e|)
                );
                """);
    }

    @Test
    void probeCreateViewJoinOnAliasPrefix() {
        probeMarked("""
                CREATE VIEW testas AS
                SELECT * FROM user_document u join obj_namespace o ON u|
                """);
    }

    @Test
    void probeCreateViewJoinOnAliasPrefixWithMetadata() {
        probeMarked("""
                CREATE VIEW testas AS
                SELECT * FROM user_document u join obj_namespace o ON u|
                """, new ProbeMetadataProvider());
    }

    @Test
    void probeAlterTableDropWithoutCompletedKeywordDummyBranch() {
        String sql = "alter table app.orders drop";
        int cursor = sql.length();
        SqlCompletionInputCleanResponse input = MysqlSqlCompletionInputCleaner.clean(sql, cursor);
        SqlCompletionStatementWindow window = new MysqlSqlCompletionStatementLocator().locate(input);
        SqlCompletionCursorContext cursorContext = new MysqlSqlCompletionCursorAnalyzer().analyze(window);
        DbSqlCompletionRequest request = DbSqlCompletionRequest.of(sql, cursor, "MYSQL", 1, null);

        SqlCompletionDummySql identifierDummy = legacyReplace(window.parseSql(), cursorContext.replaceStart(),
                cursorContext.replaceEnd(), ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionTokenUtilConstants.COMPLETION_DUMMY_IDENTIFIER,
                SqlCompletionDummyTypeEnum.IDENTIFIER);
        SqlCompletionDummySql dataTypeDummy = legacyReplace(window.parseSql(), cursorContext.replaceStart(),
                cursorContext.replaceEnd(), "INT", SqlCompletionDummyTypeEnum.DATA_TYPE);

        StringBuilder builder = new StringBuilder();
        builder.append("\n@@ALTER_DROP_LEGACY_DUMMY_PROBE_BEGIN@@\n");
        builder.append("sql=").append(sql).append('\n');
        builder.append("cursor.prefix=").append(cursorContext.prefix()).append('\n');
        builder.append("cursor.replaceStart=").append(cursorContext.replaceStart()).append('\n');
        builder.append("cursor.replaceEnd=").append(cursorContext.replaceEnd()).append('\n');
        appendDummyC3(builder, "identifierDummy", request, input, window, cursorContext, identifierDummy);
        appendDummyC3(builder, "dataTypeDummy", request, input, window, cursorContext, dataTypeDummy);
        builder.append("@@ALTER_DROP_LEGACY_DUMMY_PROBE_END@@\n");
        System.out.println(builder);
    }

    private static void probe(String sql) {
        int cursor = sql.length();
        probe(sql, cursor);
    }

    private static void probeMarked(String markedSql) {
        int cursor = markedSql.indexOf('|');
        if (cursor < 0) {
            throw new IllegalArgumentException("Missing cursor marker");
        }
        probe(markedSql.substring(0, cursor) + markedSql.substring(cursor + 1), cursor);
    }

    private static void probeMarked(String markedSql, ISqlCompletionMetadataProvider metadataProvider) {
        int cursor = markedSql.indexOf('|');
        if (cursor < 0) {
            throw new IllegalArgumentException("Missing cursor marker");
        }
        probe(markedSql.substring(0, cursor) + markedSql.substring(cursor + 1), cursor, metadataProvider);
    }

    private static void probe(String sql, int cursor) {
        probe(sql, cursor, null);
    }

    private static void probe(String sql, int cursor, ISqlCompletionMetadataProvider metadataProvider) {
        SqlCompletionInputCleanResponse input = MysqlSqlCompletionInputCleaner.clean(sql, cursor);
        SqlCompletionStatementWindow window = new MysqlSqlCompletionStatementLocator().locate(input);
        SqlCompletionCursorContext cursorContext = new MysqlSqlCompletionCursorAnalyzer().analyze(window);
        SqlCompletionDummySql dummySql = new MysqlSqlCompletionDummyBuilder().build(window, cursorContext);
        DbSqlCompletionRequest request = DbSqlCompletionRequest.of(sql, cursor, "MYSQL", 1, metadataProvider);
        MysqlSqlCompletionCandidateContext baseContext = new MysqlSqlCompletionCandidateContext(
                request,
                input,
                window,
                dummySql,
                cursorContext,
                MysqlSqlCompletionInsertStatementContext.inactive());
        SqlCompletionCandidates c3Result = MysqlSqlCompletionEngine.collect(baseContext);
        MysqlSqlCompletionRuleSlot ruleSlot = MysqlSqlCompletionRuleSlot.classify(baseContext,
                c3Result);
        MysqlSqlCompletionCandidateContext finalContext = new MysqlSqlCompletionCandidateContext(
                request,
                input,
                window,
                dummySql,
                cursorContext,
                SqlCompletionLocalContext.empty(),
                MysqlSqlCompletionInsertStatementContext.inactive(),
                c3Result,
                ruleSlot);
        SqlCompletionResponse result = MysqlSqlCompletionCandidatePlanExecutor.execute(finalContext);

        StringBuilder builder = new StringBuilder();
        builder.append("\n@@ALTER_DROP_PROBE_BEGIN@@\n");
        builder.append("sql=").append(sql).append('\n');
        builder.append("cursor=").append(cursor).append('\n');
        builder.append("input.sourceSql=").append(visible(input.sourceSql())).append('\n');
        builder.append("input.parseSql=").append(visible(input.parseSql())).append('\n');
        builder.append("input.cursor=").append(input.cursor()).append('\n');
        builder.append("window.sourceSql=").append(visible(window.sourceSql())).append('\n');
        builder.append("window.parseSql=").append(visible(window.parseSql())).append('\n');
        builder.append("window.sourceStartOffset=").append(window.sourceStartOffset()).append('\n');
        builder.append("window.sourceEndOffset=").append(window.sourceEndOffset()).append('\n');
        builder.append("window.cursor=").append(window.cursor()).append('\n');
        builder.append("window.type=").append(window.type()).append('\n');
        builder.append("cursor.admitted=").append(cursorContext.admitted()).append('\n');
        builder.append("cursor.rejectReason=").append(cursorContext.rejectReason()).append('\n');
        builder.append("cursor.scope=").append(cursorContext.scope()).append('\n');
        builder.append("cursor.prefix=").append(cursorContext.prefix()).append('\n');
        builder.append("cursor.replaceStart=").append(cursorContext.replaceStart()).append('\n');
        builder.append("cursor.replaceEnd=").append(cursorContext.replaceEnd()).append('\n');
        builder.append("cursor.dotScoped=").append(cursorContext.dotScoped()).append('\n');
        builder.append("dummy.sql=").append(visible(dummySql.sql())).append('\n');
        builder.append("dummy.cursor=").append(dummySql.cursor()).append('\n');
        builder.append("dummy.type=").append(dummySql.type()).append('\n');
        builder.append("dummy.insertedOffset=").append(dummySql.insertedOffset()).append('\n');
        builder.append("dummy.insertedLength=").append(dummySql.insertedLength()).append('\n');
        builder.append("rawTokens=").append(tokens(input.parseSql())).append('\n');
        builder.append("dummyTokens=").append(tokens(dummySql.sql())).append('\n');
        builder.append("c3.available=").append(c3Result.available()).append('\n');
        builder.append("c3.empty=").append(c3Result.empty()).append('\n');
        builder.append("c3.tokenIndex=").append(c3Result.tokenIndex()).append('\n');
        builder.append("c3.tokens=").append(tokenCandidates(c3Result.tokens())).append('\n');
        builder.append("c3.rules=").append(ruleCandidates(c3Result.rules())).append('\n');
        builder.append("ruleSlot.type=").append(ruleSlot.type()).append('\n');
        builder.append("result.status=").append(result.getStatus()).append('\n');
        builder.append("result.replaceStart=").append(result.getReplaceStart()).append('\n');
        builder.append("result.replaceEnd=").append(result.getReplaceEnd()).append('\n');
        builder.append("result.candidateCount=").append(result.getCandidates().size()).append('\n');
        builder.append("result.containsEmail=").append(containsCandidate(result, "email")).append('\n');
        builder.append("result.candidates=").append(candidateSummary(result)).append('\n');
        builder.append("@@ALTER_DROP_PROBE_END@@\n");
        System.out.println(builder);
    }

    private static SqlCompletionDummySql legacyReplace(String sql,
                                                       int start,
                                                       int end,
                                                       String dummy,
                                                       SqlCompletionDummyTypeEnum type) {
        String patchedSql = sql.substring(0, start) + dummy + sql.substring(end);
        return new SqlCompletionDummySql(patchedSql, start, type.name(), start, dummy.length());
    }

    private static void appendDummyC3(StringBuilder builder,
                                      String label,
                                      DbSqlCompletionRequest request,
                                      SqlCompletionInputCleanResponse input,
                                      SqlCompletionStatementWindow window,
                                      SqlCompletionCursorContext cursorContext,
                                      SqlCompletionDummySql dummySql) {
        MysqlSqlCompletionCandidateContext baseContext = new MysqlSqlCompletionCandidateContext(
                request,
                input,
                window,
                dummySql,
                cursorContext,
                MysqlSqlCompletionInsertStatementContext.inactive());
        SqlCompletionCandidates c3Result = MysqlSqlCompletionEngine.collect(baseContext);
        MysqlSqlCompletionRuleSlot ruleSlot = MysqlSqlCompletionRuleSlot.classify(baseContext,
                c3Result);
        builder.append(label).append(".sql=").append(visible(dummySql.sql())).append('\n');
        builder.append(label).append(".type=").append(dummySql.type()).append('\n');
        builder.append(label).append(".cursor=").append(dummySql.cursor()).append('\n');
        builder.append(label).append(".insertedOffset=").append(dummySql.insertedOffset()).append('\n');
        builder.append(label).append(".insertedLength=").append(dummySql.insertedLength()).append('\n');
        builder.append(label).append(".tokens=").append(tokens(dummySql.sql())).append('\n');
        builder.append(label).append(".c3.available=").append(c3Result.available()).append('\n');
        builder.append(label).append(".c3.empty=").append(c3Result.empty()).append('\n');
        builder.append(label).append(".c3.tokenIndex=").append(c3Result.tokenIndex()).append('\n');
        builder.append(label).append(".c3.tokens=").append(tokenCandidates(c3Result.tokens())).append('\n');
        builder.append(label).append(".c3.rules=").append(ruleCandidates(c3Result.rules())).append('\n');
        builder.append(label).append(".ruleSlot.type=").append(ruleSlot.type()).append('\n');
    }

    private static String visible(String value) {
        return value == null ? "null" : value.replace("\n", "\\n");
    }

    private static boolean containsCandidate(SqlCompletionResponse result, String label) {
        return result != null && result.getCandidates().stream()
                .anyMatch(candidate -> label.equalsIgnoreCase(candidate.getLabel()));
    }

    private static String candidateSummary(SqlCompletionResponse result) {
        if (result == null || result.getCandidates() == null) {
            return "[]";
        }
        return result.getCandidates().stream()
                .limit(40)
                .map(MysqlSqlCompletionAdhocProbeTest::candidateSummary)
                .toList()
                .toString();
    }

    private static String candidateSummary(SqlCompletionCandidate candidate) {
        if (candidate == null) {
            return "null";
        }
        return candidate.getLabel()
                + ":" + candidate.getType()
                + ":rank=" + candidate.getSortRank()
                + ":table=" + candidate.getTableName()
                + ":column=" + candidate.getColumnName();
    }

    private static String tokens(String sql) {
        List<Token> tokens = MysqlSqlCompletionTokenUtil.tokens(sql);
        return tokens.stream()
                .map(token -> token.getTokenIndex()
                        + ":" + tokenName(token.getType())
                        + "(" + visible(token.getText()) + ")"
                        + "@" + token.getStartIndex() + ".." + token.getStopIndex()
                        + "/ch" + token.getChannel())
                .toList()
                .toString();
    }

    private static String tokenCandidates(Map<Integer, List<Integer>> tokens) {
        return tokens.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> tokenName(entry.getKey()) + "<-" + rulePath(entry.getValue()))
                .toList()
                .toString();
    }

    private static String ruleCandidates(Map<Integer, SqlCompletionCandidates.RuleCandidate> rules) {
        return rules.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<Integer, SqlCompletionCandidates.RuleCandidate> entry) ->
                        entry.getValue().startTokenIndex()).thenComparingInt(Map.Entry::getKey))
                .map(entry -> ruleName(entry.getKey())
                        + "{start=" + entry.getValue().startTokenIndex()
                        + ", path=" + rulePath(entry.getValue().ruleList()) + "}")
                .toList()
                .toString();
    }

    private static String rulePath(List<Integer> rules) {
        return rules.stream().map(MysqlSqlCompletionAdhocProbeTest::ruleName).toList().toString();
    }

    private static String tokenName(int tokenType) {
        if (tokenType == Token.EOF) {
            return "EOF";
        }
        String symbolic = MySqlLexer.VOCABULARY.getSymbolicName(tokenType);
        String literal = MySqlLexer.VOCABULARY.getLiteralName(tokenType);
        String display = symbolic != null ? symbolic : literal;
        return tokenType + ":" + display;
    }

    private static String ruleName(int rule) {
        if (rule < 0 || rule >= MySqlParser.ruleNames.length) {
            return rule + ":?";
        }
        return rule + ":" + MySqlParser.ruleNames[rule];
    }

    private static final class ProbeMetadataProvider implements ISqlCompletionMetadataProvider {

        @Override
        public SqlCompletionMetadataResponse list(DbSqlCompletionMetadataRequest request) {
            if (SqlCompletionCandidateTypeEnum.COLUMN.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(columns(request.scope().table(), request.prefix()));
            }
            if (SqlCompletionCandidateTypeEnum.FUNCTION.name().equals(request.type())) {
                return SqlCompletionMetadataResponse.of(objects(request.prefix(), "USER", "UUID", "UPPER"));
            }
            return SqlCompletionMetadataResponse.of(List.of());
        }

        private List<SqlCompletionCandidate> columns(String table, String prefix) {
            List<String> names = switch (String.valueOf(table)) {
                case "user_document" -> List.of("id", "user_id", "document_id", "title");
                case "obj_namespace" -> List.of("id", "namespace_id", "owner_id");
                default -> List.of();
            };
            return names.stream()
                    .filter(name -> matchesPrefix(name, prefix))
                    .map(name -> {
                        SqlCompletionCandidate candidate =
                                SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.COLUMN, name);
                        candidate.setTableName(table);
                        candidate.setColumnName(name);
                        return candidate;
                    })
                    .toList();
        }

        private List<SqlCompletionCandidate> objects(String prefix, String... names) {
            return List.of(names).stream()
                    .filter(name -> matchesPrefix(name, prefix))
                    .map(name -> {
                        SqlCompletionCandidate candidate =
                                SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.FUNCTION, name);
                        candidate.setObjectName(name);
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
