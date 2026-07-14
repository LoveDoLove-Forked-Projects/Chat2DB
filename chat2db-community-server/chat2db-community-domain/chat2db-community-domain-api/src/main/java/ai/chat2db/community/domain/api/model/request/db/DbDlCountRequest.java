package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class DbDlCountRequest {


    @NotNull
    private String sql;


    @NotNull
    private Long consoleId;


    @NotNull
    private Long dataSourceId;


    @NotNull
    private String databaseName;


    @NotNull
    private String tableName;
}
