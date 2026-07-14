package ai.chat2db.community.domain.api.enums.completion;

import java.sql.DatabaseMetaData;


public enum SqlCompletionParameterModeTypeEnum {
    UNKNOWN,
    IN,
    OUT,
    INOUT,
    RETURN,
    RESULT;

    public boolean outputArgument() {
        return this == OUT || this == INOUT;
    }

    public static SqlCompletionParameterModeTypeEnum fromProcedureColumnType(Integer columnType) {
        if (columnType == null) {
            return UNKNOWN;
        }
        return switch (columnType) {
            case DatabaseMetaData.procedureColumnIn -> IN;
            case DatabaseMetaData.procedureColumnOut -> OUT;
            case DatabaseMetaData.procedureColumnInOut -> INOUT;
            case DatabaseMetaData.procedureColumnReturn -> RETURN;
            case DatabaseMetaData.procedureColumnResult -> RESULT;
            default -> UNKNOWN;
        };
    }

    public static SqlCompletionParameterModeTypeEnum fromFunctionColumnType(Integer columnType) {
        if (columnType == null) {
            return UNKNOWN;
        }
        return switch (columnType) {
            case DatabaseMetaData.functionColumnIn -> IN;
            case DatabaseMetaData.functionColumnOut -> OUT;
            case DatabaseMetaData.functionColumnInOut -> INOUT;
            case DatabaseMetaData.functionReturn -> RETURN;
            case DatabaseMetaData.functionColumnResult -> RESULT;
            default -> UNKNOWN;
        };
    }
}
