package ai.chat2db.plugin.mysql.completion.provider.metadata;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionDatabaseQualifierCandidateDecoratorTest {

    @Test
    void databaseQualifierDecorationDoesNotMutateMetadataCandidate() {
        SqlCompletionCandidate original = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.DATABASE,
                "enterprise_gateway_dev");
        original.setDatabaseName("enterprise_gateway_dev");

        List<SqlCompletionCandidate> decorated =
                MysqlSqlCompletionDatabaseQualifierCandidateDecorator.decorate(List.of(original));

        Assertions.assertEquals("enterprise_gateway_dev.", decorated.get(0).getInsertText());
        Assertions.assertEquals("enterprise_gateway_dev", original.getInsertText());
        Assertions.assertNotSame(original, decorated.get(0));
    }
}
