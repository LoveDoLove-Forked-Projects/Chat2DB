package ai.chat2db.plugin.mysql.converter;

import ai.chat2db.plugin.mysql.model.RoutineParameter;
import ai.chat2db.community.domain.api.model.metadata.FunctionParameter;
import ai.chat2db.community.domain.api.model.metadata.ProcedureParameter;
import org.apache.commons.lang3.StringUtils;

import java.sql.DatabaseMetaData;
import java.util.Locale;
import java.util.Objects;

import static ai.chat2db.plugin.mysql.constant.MysqlRoutineConverterConstants.*;
public class MysqlRoutineConverter {




    public RoutineParameter functionParameter2routineParameter(FunctionParameter parameter) {
        if (parameter == null || StringUtils.isBlank(parameter.getColumnName())) {
            return null;
        }
        Integer columnType = parameter.getColumnType();
        if (Objects.equals(columnType, DatabaseMetaData.functionReturn)
                || Objects.equals(columnType, DatabaseMetaData.functionColumnResult)) {
            return null;
        }
        return new RoutineParameter(
                parameter.getColumnName(),
                functionColumnType2mode(columnType),
                StringUtils.trimToEmpty(parameter.getTypeName()).toUpperCase(Locale.ROOT),
                parameter.getOrdinalPosition() == null ? Integer.MAX_VALUE : parameter.getOrdinalPosition());
    }

    public RoutineParameter procedureParameter2routineParameter(ProcedureParameter parameter) {
        if (parameter == null || StringUtils.isBlank(parameter.getColumnName())) {
            return null;
        }
        Integer columnType = parameter.getColumnType();
        if (Objects.equals(columnType, DatabaseMetaData.procedureColumnReturn)
                || Objects.equals(columnType, DatabaseMetaData.procedureColumnResult)) {
            return null;
        }
        return new RoutineParameter(
                parameter.getColumnName(),
                procedureColumnType2mode(columnType),
                StringUtils.trimToEmpty(parameter.getTypeName()).toUpperCase(Locale.ROOT),
                parameter.getOrdinalPosition() == null ? Integer.MAX_VALUE : parameter.getOrdinalPosition());
    }

    private String functionColumnType2mode(Integer columnType) {
        if (Objects.equals(columnType, DatabaseMetaData.functionColumnOut)) {
            return OUT;
        }
        if (Objects.equals(columnType, DatabaseMetaData.functionColumnInOut)) {
            return INOUT;
        }
        return IN;
    }

    private String procedureColumnType2mode(Integer columnType) {
        if (Objects.equals(columnType, DatabaseMetaData.procedureColumnOut)) {
            return OUT;
        }
        if (Objects.equals(columnType, DatabaseMetaData.procedureColumnInOut)) {
            return INOUT;
        }
        return IN;
    }
}
