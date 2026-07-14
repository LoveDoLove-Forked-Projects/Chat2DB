package ai.chat2db.plugin.mysql.completion.presentation;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionKeywordCaseEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionCandidatePostProcessorTest {

    @Test
    void appliesLowercasePolicyToKeywordsAndSnippetsAfterSorting() {
        SqlCompletionCandidate keyword = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, "SELECT");
        keyword.setSortRank(10);
        SqlCompletionCandidate snippet = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.SNIPPET,
                "select from");
        snippet.setInsertText("SELECT \n\t* \nFROM \n\t$1;\n");
        snippet.setSortRank(1);
        SqlCompletionCandidate column = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.COLUMN,
                "UserName");
        column.setInsertText("UserName");

        List<SqlCompletionCandidate> processed = MysqlSqlCompletionCandidatePostProcessor.process(
                List.of(snippet, column, keyword), SqlCompletionKeywordCaseEnum.LOWER);

        Assertions.assertEquals("UserName", processed.get(0).getLabel());
        Assertions.assertEquals("UserName", processed.get(0).getInsertText());
        Assertions.assertEquals("mysql_00000", processed.get(0).getSortText());
        Assertions.assertEquals("select", processed.get(1).getLabel());
        Assertions.assertEquals("select", processed.get(1).getInsertText());
        Assertions.assertEquals("mysql_00001", processed.get(1).getSortText());
        Assertions.assertEquals("select from", processed.get(2).getLabel());
        Assertions.assertEquals("select \n\t* \nfrom \n\t$1;\n", processed.get(2).getInsertText());
        Assertions.assertEquals("mysql_00002", processed.get(2).getSortText());
    }

    @Test
    void sortsMetadataCandidatesBeforeKeywords() {
        SqlCompletionCandidate keyword = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, "SELECT");
        SqlCompletionCandidate table = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.TABLE,
                "access_control_apply_record");

        List<SqlCompletionCandidate> processed = MysqlSqlCompletionCandidatePostProcessor.process(
                List.of(keyword, table), SqlCompletionKeywordCaseEnum.LOWER);

        Assertions.assertEquals("access_control_apply_record", processed.get(0).getLabel());
        Assertions.assertEquals(SqlCompletionCandidateTypeEnum.TABLE, processed.get(0).getType());
        Assertions.assertEquals("select", processed.get(1).getLabel());
        Assertions.assertEquals(SqlCompletionCandidateTypeEnum.KEYWORD, processed.get(1).getType());
    }

    @Test
    void appliesUppercasePolicyToKeywordsAndSnippetsAfterSorting() {
        SqlCompletionCandidate keyword = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, "select");
        SqlCompletionCandidate snippet = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.SNIPPET,
                "select from");
        snippet.setInsertText("select \n\t* \nfrom \n\t$1;\n");

        List<SqlCompletionCandidate> processed = MysqlSqlCompletionCandidatePostProcessor.process(
                List.of(snippet, keyword), SqlCompletionKeywordCaseEnum.UPPER);

        Assertions.assertEquals("SELECT", processed.get(0).getLabel());
        Assertions.assertEquals("SELECT", processed.get(0).getInsertText());
        Assertions.assertEquals("SELECT FROM", processed.get(1).getLabel());
        Assertions.assertEquals("SELECT \n\t* \nFROM \n\t$1;\n", processed.get(1).getInsertText());
    }

    @Test
    void appliesLowercasePolicyWhenPolicyIsNull() {
        SqlCompletionCandidate keyword = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, "SELECT");
        SqlCompletionCandidate snippet = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.SNIPPET,
                "select from");
        snippet.setInsertText("SELECT \n\t* \nFROM \n\t$1;\n");

        List<SqlCompletionCandidate> processed = MysqlSqlCompletionCandidatePostProcessor.process(List.of(keyword,
                snippet), null);

        Assertions.assertEquals("select", processed.get(0).getLabel());
        Assertions.assertEquals("select from", processed.get(1).getLabel());
        Assertions.assertEquals("select \n\t* \nfrom \n\t$1;\n", processed.get(1).getInsertText());
    }

    @Test
    void keepsHigherPriorityFunctionWhenSyntaxAndCatalogCandidatesOverlap() {
        SqlCompletionCandidate syntaxFunction = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.FUNCTION,
                "MAX");
        syntaxFunction.setInsertText("MAX(${1:})$0");
        syntaxFunction.setObjectName("MAX");
        syntaxFunction.setSortRank(1000);
        SqlCompletionCandidate catalogFunction = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.FUNCTION,
                "MAX");
        catalogFunction.setInsertText("MAX(${1:expr})$0");
        catalogFunction.setObjectName("MAX");
        catalogFunction.setSortRank(700);

        List<SqlCompletionCandidate> processed = MysqlSqlCompletionCandidatePostProcessor.process(
                List.of(syntaxFunction, catalogFunction), SqlCompletionKeywordCaseEnum.UPPER);

        Assertions.assertEquals(1, processed.size());
        Assertions.assertEquals("MAX", processed.get(0).getLabel());
        Assertions.assertEquals("MAX(${1:expr})$0", processed.get(0).getInsertText());
    }
}
