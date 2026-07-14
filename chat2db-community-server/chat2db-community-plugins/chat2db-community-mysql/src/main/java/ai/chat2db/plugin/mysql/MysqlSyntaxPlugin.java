package ai.chat2db.plugin.mysql;

import ai.chat2db.spi.ISqlSyntaxPlugin;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.plugin.mysql.completion.MysqlSqlCompletionProvider;
import ai.chat2db.spi.ISQLParser;
import ai.chat2db.plugin.mysql.parser.MysqlSqlParser;
import ai.chat2db.spi.ISqlCompletionProvider;

public class MysqlSyntaxPlugin implements ISqlSyntaxPlugin {

    @Override
    public String getDatabaseType() {
        return DatabaseTypeEnum.MYSQL.name();
    }

    @Override
    public ISQLParser getSQLParser() {
        return new MysqlSqlParser();
    }

    @Override
    public ISqlCompletionProvider getSqlCompletionProvider() {
        return new MysqlSqlCompletionProvider();
    }
}
