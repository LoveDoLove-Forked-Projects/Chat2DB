package ai.chat2db.community.web.api.model.request.cli;

import java.util.List;

import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.datasource.KeyValue;
import ai.chat2db.community.domain.api.model.datasource.SSHInfo;
import ai.chat2db.community.domain.api.model.datasource.SSLInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CliDatasourceUpdateRequest {

    @NotNull
    private Long dataSourceId;

    private String alias;

    private String dbType;

    private String url;

    private String user;

    private String password;

    private String authenticationType;

    private String environment;

    private Integer environmentId;

    private String host;

    private String port;

    private SSHInfo ssh;

    private SSLInfo ssl;

    private String sid;

    private String driver;

    private String jdbc;

    private List<KeyValue> extendInfo;

    private DriverConfig driverConfig;

    private String database;

    private String serviceName;

    private String serviceType;

    private Long organizationId;

    private String token;

    private String storageType;

    private Long spaceId;
}
