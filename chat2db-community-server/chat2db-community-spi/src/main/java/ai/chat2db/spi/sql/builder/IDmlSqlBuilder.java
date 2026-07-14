package ai.chat2db.spi.sql.builder;

import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.result.QueryResponse;
import ai.chat2db.spi.model.request.DeleteSqlRequest;
import ai.chat2db.spi.model.request.MultiInsertSqlRequest;
import ai.chat2db.spi.model.request.SingleInsertSqlRequest;
import ai.chat2db.spi.model.request.UpdateSqlRequest;

public interface IDmlSqlBuilder {

    String buildInsert(SingleInsertSqlRequest singleInsertSqlRequest);

    String buildBatchInsert(MultiInsertSqlRequest multiInsertSqlRequest);

    String buildUpdate(UpdateSqlRequest updateSqlRequest);

    String buildDelete(DeleteSqlRequest deleteSqlRequest);

    String buildByQueryResult(QueryResponse queryResult);

    String buildTemplate(Table table, String dmlType);

    String buildCopyByQueryResult(QueryResponse queryResult);
}
