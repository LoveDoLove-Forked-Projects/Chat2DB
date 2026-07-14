package ai.chat2db.plugin.kylin;

import ai.chat2db.spi.ISqlSyntaxPlugin;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.plugin.kylin.parser.KylinSqlParser;
import ai.chat2db.spi.ISQLParser;

public class KylinSyntaxPlugin implements ISqlSyntaxPlugin {

    @Override
    public String getDatabaseType() {
        return DatabaseTypeEnum.KYLIN.name();
    }

    @Override
    public ISQLParser getSQLParser() {
        return new KylinSqlParser();
    }
}
