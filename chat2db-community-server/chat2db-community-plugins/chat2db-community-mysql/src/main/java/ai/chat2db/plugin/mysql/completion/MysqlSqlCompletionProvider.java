package ai.chat2db.plugin.mysql.completion;

import ai.chat2db.spi.ISqlCompletionProvider;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.spi.parser.completion.SqlCompletionPipeline;


public class MysqlSqlCompletionProvider implements ISqlCompletionProvider {

    private final SqlCompletionPipeline pipeline = new SqlCompletionPipeline(new MysqlSqlCompletionDialect());

    @Override
    public SqlCompletionResponse complete(DbSqlCompletionRequest request) {
        return pipeline.complete(request);
    }
}
