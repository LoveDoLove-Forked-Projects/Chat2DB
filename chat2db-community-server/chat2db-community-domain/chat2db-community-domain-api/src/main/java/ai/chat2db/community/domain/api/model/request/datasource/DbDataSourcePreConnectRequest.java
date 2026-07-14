package ai.chat2db.community.domain.api.model.request.datasource;

import java.util.List;

import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.datasource.KeyValue;
import ai.chat2db.community.domain.api.model.datasource.SSHInfo;
import ai.chat2db.community.domain.api.model.datasource.SSLInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DbDataSourcePreConnectRequest {


    private Long id;


    private String alias;


    @NotNull
    private String url;


    private String user;


    @NotNull
    private String password;

    private String authenticationType;


    @NotNull
    private String type;


    private String serviceType;


    private String host;


    private String port;


    private SSHInfo ssh;


    private SSLInfo ssl;


    private String sid;


    private String driver;


    private String jdbc;


    private List<KeyValue> extendInfo;


    private DriverConfig driverConfig;


    private String email;

    private String keyfile;

    private String project;
}
