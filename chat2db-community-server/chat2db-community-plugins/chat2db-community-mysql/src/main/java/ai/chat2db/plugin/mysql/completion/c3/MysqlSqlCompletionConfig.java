package ai.chat2db.plugin.mysql.completion.c3;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import java.util.Set;


public final class MysqlSqlCompletionConfig {

    private MysqlSqlCompletionConfig() {
    }

    public static Set<Integer> preferredRules() {
        return Set.of(
                MySqlParser.RULE_databaseReferenceName,
                MySqlParser.RULE_databaseDeclarationName,
                MySqlParser.RULE_eventReferenceName,
                MySqlParser.RULE_eventDeclarationName,
                MySqlParser.RULE_functionReferenceName,
                MySqlParser.RULE_functionDeclarationName,
                MySqlParser.RULE_procedureReferenceName,
                MySqlParser.RULE_procedureDeclarationName,
                MySqlParser.RULE_roleReferenceName,
                MySqlParser.RULE_roleDeclarationName,
                MySqlParser.RULE_tablespaceReferenceName,
                MySqlParser.RULE_tablespaceDeclarationName,
                MySqlParser.RULE_triggerReferenceName,
                MySqlParser.RULE_triggerDeclarationName,
                MySqlParser.RULE_userReferenceName,
                MySqlParser.RULE_userDeclarationName,
                MySqlParser.RULE_tableReferenceName,
                MySqlParser.RULE_tableDeclarationName,
                MySqlParser.RULE_viewReferenceName,
                MySqlParser.RULE_viewDeclarationName,
                MySqlParser.RULE_columnReferenceName,
                MySqlParser.RULE_charsetName,
                MySqlParser.RULE_collationName,
                MySqlParser.RULE_dataType,
                MySqlParser.RULE_dataTypeBase,
                MySqlParser.RULE_insertValueExpressions,
                MySqlParser.RULE_insertValueExpression,
                MySqlParser.RULE_columnDeclarationName,
                MySqlParser.RULE_uid,
                MySqlParser.RULE_simpleUserName);
    }

    public static Set<Integer> ignoredTokens() {
        return Set.of(
                MySqlLexer.SPACE,
                MySqlLexer.SPEC_MYSQL_COMMENT,
                MySqlLexer.COMMENT_INPUT,
                MySqlLexer.LINE_COMMENT,
                MySqlLexer.DOT,
                MySqlLexer.COMMA,
                MySqlLexer.SEMI,
                MySqlLexer.LR_BRACKET,
                MySqlLexer.RR_BRACKET,
                MySqlLexer.STAR,
                MySqlLexer.DIVIDE,
                MySqlLexer.MODULE,
                MySqlLexer.PLUS,
                MySqlLexer.MINUS,
                MySqlLexer.EQUAL_SYMBOL,
                MySqlLexer.GREATER_SYMBOL,
                MySqlLexer.LESS_SYMBOL,
                MySqlLexer.EXCLAMATION_SYMBOL,
                MySqlLexer.BIT_NOT_OP,
                MySqlLexer.BIT_OR_OP,
                MySqlLexer.BIT_AND_OP,
                MySqlLexer.BIT_XOR_OP,
                MySqlLexer.EOF);
    }

}
