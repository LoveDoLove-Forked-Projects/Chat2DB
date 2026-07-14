package ai.chat2db.plugin.mysql.parser;

import ai.chat2db.spi.parser.dialect.AbstractSQLDialect;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.plugin.mysql.parser.rule.manager.MysqlRuleManager;
import ai.chat2db.spi.IRuleManager;

import java.util.Set;

public class MysqlDialect extends AbstractSQLDialect {

    private static final Set<Integer> MYSQL_COMMENT_TOKENS = Set.of(MySqlLexer.SPEC_MYSQL_COMMENT, MySqlLexer.COMMENT_INPUT, MySqlLexer.LINE_COMMENT);

    private static final IRuleManager MYSQL_RULE_MANAGER = new MysqlRuleManager();

    @Override
    public Set<Integer> getCommentTokens() {
        return MYSQL_COMMENT_TOKENS;
    }

    @Override
    public boolean isComment(int tokenType) {
        return getCommentTokens().contains(tokenType);
    }

    @Override
    public Set<String> getSetDelimiters() {
        Set<String> setDelimiters = super.getSetDelimiters();
        setDelimiters.add("DELIMITER");
        return setDelimiters;
    }

    @Override
    public Set<String> getFunctionNames() {
        Set<String> functionNames = super.getFunctionNames();
        functionNames.add("IF");
        return functionNames;
    }

    @Override
    public IRuleManager getRuleManager() {
        return MYSQL_RULE_MANAGER;
    }
}
