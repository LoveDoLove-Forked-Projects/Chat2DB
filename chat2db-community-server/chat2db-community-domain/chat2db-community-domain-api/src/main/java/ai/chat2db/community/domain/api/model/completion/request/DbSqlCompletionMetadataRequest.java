package ai.chat2db.community.domain.api.model.completion.request;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;


public record DbSqlCompletionMetadataRequest(@NotBlank String type,
                                           @Valid SqlCompletionMetadataScope scope,
                                           String prefix,
                                           String objectType) {

    public DbSqlCompletionMetadataRequest {
        type = type == null ? SqlCompletionCandidateTypeEnum.OTHER.name() : type;
        scope = scope == null ? SqlCompletionMetadataScope.empty() : scope;
        prefix = prefix == null ? "" : prefix;
    }

    public DbSqlCompletionMetadataRequest(String type,
                                        SqlCompletionMetadataScope scope,
                                        String prefix) {
        this(type, scope, prefix, null);
    }

    public DbSqlCompletionMetadataRequest(SqlCompletionCandidateTypeEnum type,
                                        SqlCompletionMetadataScope scope,
                                        String prefix) {
        this(type == null ? null : type.name(), scope, prefix, null);
    }

    public static DbSqlCompletionMetadataRequest of(String type,
                                                  SqlCompletionMetadataScope scope,
                                                  String prefix) {
        return new DbSqlCompletionMetadataRequest(type, scope, prefix);
    }

    public static DbSqlCompletionMetadataRequest of(SqlCompletionCandidateTypeEnum type,
                                                  SqlCompletionMetadataScope scope,
                                                  String prefix) {
        return of(type == null ? null : type.name(), scope, prefix);
    }

    public static DbSqlCompletionMetadataRequest of(String type,
                                                  SqlCompletionMetadataScope scope,
                                                  String prefix,
                                                  String objectType) {
        return new DbSqlCompletionMetadataRequest(type, scope, prefix, objectType);
    }

    public static DbSqlCompletionMetadataRequest of(SqlCompletionCandidateTypeEnum type,
                                                  SqlCompletionMetadataScope scope,
                                                  String prefix,
                                                  SqlCompletionCandidateTypeEnum objectType) {
        return of(type == null ? null : type.name(), scope, prefix, objectType == null ? null : objectType.name());
    }
}
