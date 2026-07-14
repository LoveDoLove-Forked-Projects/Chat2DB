package ai.chat2db.community.domain.api.enums.completion;


public enum SqlCompletionCandidateTypeEnum {
    CATALOG,
    DATABASE,
    SCHEMA,
    KEYWORD,
    TABLE,
    VIEW,
    TABLE_VIEW,
    COLUMN,
    ALL_COLUMN,
    JOIN_CLAUSE,
    INDEX,
    PROCEDURE,
    FUNCTION,
    EVENT,
    PARAMETER,
    TYPE,
    USER,
    ROLE,
    TABLESPACE,
    TRIGGER,
    SEQUENCE,
    MATERIALIZED_VIEW,
    PACKAGE,
    CONSTRAINT,
    SYNONYM,
    ALIAS,
    VARIABLE,
    DBLINK,
    ROUTINE,
    SNIPPET,
    TEMP_TABLE,
    OTHER;

    public static SqlCompletionCandidateTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        try {
            return valueOf(value.trim());
        } catch (IllegalArgumentException ignored) {
            return OTHER;
        }
    }
}
