package ai.chat2db.plugin.elasticsearch;

import ai.chat2db.spi.ISqlSyntaxPlugin;
import ai.chat2db.plugin.elasticsearch.parser.ElasticSearchParser;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.spi.ISQLParser;

public class ElasticSearchSyntaxPlugin implements ISqlSyntaxPlugin {
    @Override
    public String getDatabaseType() {
        return DatabaseTypeEnum.ELASTICSEARCH.name();
    }

    @Override
    public ISQLParser getSQLParser() {
        return new ElasticSearchParser();
    }
}
