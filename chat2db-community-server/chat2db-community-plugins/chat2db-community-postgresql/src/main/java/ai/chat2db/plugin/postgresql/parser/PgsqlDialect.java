package ai.chat2db.plugin.postgresql.parser;

import ai.chat2db.spi.parser.dialect.AbstractSQLDialect;
import ai.chat2db.spi.IRuleManager;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLLexer;
import ai.chat2db.plugin.postgresql.parser.rule.manager.PgsqlRuleManager;

import java.util.Set;

public class PgsqlDialect extends AbstractSQLDialect {
    private static final Set<String> SQL_KEY_WORDS = Set.of(
            ";", "SELECT", "INSERT", "UPDATE", "DELETE", "ALTER",
            "ANALYZE", "ANALYSE", "CALL", "CHECKPOINT", "CLOSE", "CLUSTER",
            "COMMENT", "COPY", "CREATE", "DEALLOCATE", "DECLARE",
            "WITH", "DISCARD", "DO", "DROP", "EXPLAIN", "FETCH", "EXECUTE",
            "MOVE", "GRANT", "IMPORT", "MERGE", "LISTEN", "REFRESH",
            "LOAD", "LOCK", "NOTIFY", "REINDEX", "PREPARE", "REASSIGN",
            "REVOKE", "SECURITY", "ABORT", "BEGIN", "START", "COMMIT",
            "END", "ROLLBACK", "SAVEPOINT", "RELEASE", "TRUNCATE",
            "UNLISTEN", "VACUUM", "RESET", "MetaCommand"
    );
    private static final Set<Integer> PGSQL_COMMENT_TOKENS = Set.of(PostgreSQLLexer.BlockComment,PostgreSQLLexer.LineComment);

    private static final IRuleManager PG_SQL_RULE_MANAGER = new PgsqlRuleManager();

    @Override
    public Set<Integer> getCommentTokens() {
        return PGSQL_COMMENT_TOKENS;
    }

    @Override
    public boolean isComment(int tokenType) {
        return getCommentTokens().contains(tokenType);
    }

    @Override
    public Set<String> getSqlStartKeywords() {
        return SQL_KEY_WORDS;
    }

    @Override
    public Set<String> getBlockToggleSymbols() {
        Set<String> blockToggleSymbols = super.getBlockToggleSymbols();
        blockToggleSymbols.add("$$");
        return blockToggleSymbols;
    }

    @Override
    public IRuleManager getRuleManager() {
        return PG_SQL_RULE_MANAGER;
    }
}
