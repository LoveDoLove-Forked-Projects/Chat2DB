package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.metadata.FunctionParameter;
import ai.chat2db.community.domain.api.model.metadata.ProcedureParameter;
import lombok.Data;


@Data
public class SimpleParameter {

    private String parameterName;
    private String parameterType;


    public SimpleParameter(FunctionParameter functionParameter) {
        this.parameterName = functionParameter.getColumnName();
        this.parameterType = functionParameter.getTypeName();

    }

    public SimpleParameter(ProcedureParameter procedureParameter) {
        this.parameterName = procedureParameter.getColumnName();
        this.parameterType = procedureParameter.getTypeName();
    }
}
