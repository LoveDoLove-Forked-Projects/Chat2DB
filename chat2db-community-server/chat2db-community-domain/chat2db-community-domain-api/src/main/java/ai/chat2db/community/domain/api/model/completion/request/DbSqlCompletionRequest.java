package ai.chat2db.community.domain.api.model.completion.request;

import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionKeywordCaseEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionActiveSnippetSlot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;


public record DbSqlCompletionRequest(@NotBlank String sql,
                                   @Min(0) int cursor,
                                   String databaseType,
                                   @Min(0) int minPrefixLength,
                                   @NotNull ISqlCompletionMetadataProvider metadataProvider,
                                   String keywordCase,
                                   @Valid SqlCompletionActiveSnippetSlot activeSnippetSlot) {

    public DbSqlCompletionRequest {
        sql = Objects.toString(sql, "");
        cursor = Math.max(0, Math.min(cursor, sql.length()));
        databaseType = databaseType == null ? null : databaseType.trim();
        minPrefixLength = Math.max(0, minPrefixLength);
        metadataProvider = metadataProvider == null ? ISqlCompletionMetadataProvider.unsupported() : metadataProvider;
        keywordCase = keywordCase == null ? SqlCompletionKeywordCaseEnum.UPPER.name()
                : SqlCompletionKeywordCaseEnum.from(keywordCase).name();
    }

    public static DbSqlCompletionRequest of(String sql,
                                          int cursor,
                                          String databaseType,
                                          int minPrefixLength,
                                          ISqlCompletionMetadataProvider metadataProvider) {
        return of(sql, cursor, databaseType, minPrefixLength, metadataProvider, null);
    }

    public static DbSqlCompletionRequest of(String sql,
                                          int cursor,
                                          String databaseType,
                                          int minPrefixLength,
                                          ISqlCompletionMetadataProvider metadataProvider,
                                          SqlCompletionActiveSnippetSlot activeSnippetSlot) {
        return of(sql, cursor, databaseType, minPrefixLength, metadataProvider, SqlCompletionKeywordCaseEnum.UPPER,
                activeSnippetSlot);
    }

    public static DbSqlCompletionRequest of(String sql,
                                          int cursor,
                                          String databaseType,
                                          int minPrefixLength,
                                          ISqlCompletionMetadataProvider metadataProvider,
                                          SqlCompletionKeywordCaseEnum keywordCase,
                                          SqlCompletionActiveSnippetSlot activeSnippetSlot) {
        return of(sql, cursor, databaseType, minPrefixLength, metadataProvider,
                keywordCase == null ? null : keywordCase.name(), activeSnippetSlot);
    }

    public static DbSqlCompletionRequest of(String sql,
                                          int cursor,
                                          String databaseType,
                                          int minPrefixLength,
                                          ISqlCompletionMetadataProvider metadataProvider,
                                          String keywordCase,
                                          SqlCompletionActiveSnippetSlot activeSnippetSlot) {
        return new DbSqlCompletionRequest(sql, cursor, databaseType, minPrefixLength, metadataProvider,
                keywordCase, activeSnippetSlot);
    }

    public DbSqlCompletionRequest withDatabaseType(String resolvedDatabaseType) {
        return new DbSqlCompletionRequest(sql, cursor, resolvedDatabaseType, minPrefixLength, metadataProvider,
                keywordCase, activeSnippetSlot);
    }

    public DbSqlCompletionRequest withActiveSnippetSlot(SqlCompletionActiveSnippetSlot snippetSlot) {
        return new DbSqlCompletionRequest(sql, cursor, databaseType, minPrefixLength, metadataProvider, keywordCase,
                snippetSlot);
    }
}
