package ai.chat2db.plugin.sqlserver.parser;

import ai.chat2db.spi.parser.dialect.AbstractSQLDialect;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlLexer;

import java.util.Set;

public class SqlServerDialect extends AbstractSQLDialect {

    private static final Set<Integer> SQL_SERVER_COMMENT_TOKENS = Set.of(TSqlLexer.LINE_COMMENT, TSqlLexer.COMMENT);


    @Override
    public Set<Integer> getCommentTokens() {
        return SQL_SERVER_COMMENT_TOKENS;
    }

    @Override
    public boolean isComment(int tokenType) {
        return getCommentTokens().contains(tokenType);
    }

    @Override
    public Set<String> getStatementDelimiters() {
        Set<String> statementDelimiters = super.getStatementDelimiters();
        statementDelimiters.add("go");
        return statementDelimiters;
    }

    @Override
    public Set<String> getBlockHeaderPrefixes() {
        Set<String> blockStartPrefixes = super.getBlockHeaderPrefixes();
        blockStartPrefixes.add("ALTER");
        return blockStartPrefixes;
    }

    @Override
    public Set<String> getKeywords() {
        Set<String> keywords = super.getKeywords();
        keywords.add("MERGE");
        return keywords;
    }
}
