package ai.chat2db.plugin.mysql.model;

import java.util.Objects;

import static ai.chat2db.plugin.mysql.constant.RoutineParameterConstants.*;
public record RoutineParameter(String name, String mode, String dataType, int ordinalPosition) {




    public boolean isInput() {
        return Objects.equals(mode, IN) || Objects.equals(mode, INOUT);
    }

    public boolean isOutput() {
        return Objects.equals(mode, OUT) || Objects.equals(mode, INOUT);
    }
}
