package ai.chat2db.community.domain.core.converter;

import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.runtime.ConnectionProfile;
import ai.chat2db.community.domain.api.model.request.datasource.DbConnectionDiffRequest;
import ai.chat2db.community.domain.api.model.request.runtime.DbConnectionContextRequest;
import ai.chat2db.community.domain.api.model.request.runtime.McpConnectionContextRequest;
import ai.chat2db.community.domain.api.model.storage.WorkspaceDataSource;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ConnectionContextConverter {

    public ConnectionProfile connectInfo2profile(ConnectInfo connectInfo) {
        if (connectInfo == null) {
            return null;
        }
        ConnectionProfile profile = new ConnectionProfile();
        profile.setDataSourceId(connectInfo.getDataSourceId());
        profile.setConsoleId(connectInfo.getConsoleId());
        profile.setDatabaseName(connectInfo.getDatabaseName());
        profile.setSchemaName(connectInfo.getSchemaName());
        profile.setDbType(connectInfo.getDbType());
        profile.setAlias(connectInfo.getAlias());
        profile.setType(connectInfo.getDbType());
        profile.setUrl(connectInfo.getUrl());
        profile.setUser(connectInfo.getUser());
        return profile;
    }

    public ConnectInfo profile2connectInfo(ConnectionProfile profile) {
        if (profile == null) {
            return null;
        }
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(profile.getDataSourceId());
        connectInfo.setConsoleId(profile.getConsoleId());
        connectInfo.setDatabase(profile.getDatabaseName());
        connectInfo.setSchemaName(profile.getSchemaName());
        connectInfo.setDbType(StringUtils.defaultIfBlank(profile.getDbType(), profile.getType()));
        connectInfo.setAlias(profile.getAlias());
        connectInfo.setUrl(profile.getUrl());
        connectInfo.setUser(profile.getUser());
        connectInfo.setConsoleOwn(false);
        return connectInfo;
    }

    public ConnectInfo datasource2connectInfo(DbConnectionContextRequest param, WorkspaceDataSource dataSource, String url) {
        ConnectInfo connectInfo = datasource2connectInfo(param.getDataSourceId(), dataSource, url);
        connectInfo.setConsoleId(param.getConsoleId());
        connectInfo.setDatabase(param.getDatabaseName());
        connectInfo.setSchemaName(param.getSchemaName());
        return connectInfo;
    }

    public ConnectInfo datasource2connectInfo(DbConnectionDiffRequest param, WorkspaceDataSource dataSource, String url) {
        ConnectInfo connectInfo = datasource2connectInfo(param.getDataSourceId(), dataSource, url);
        connectInfo.setDatabase(param.getDatabaseName());
        connectInfo.setSchemaName(param.getSchemaName());
        return connectInfo;
    }

    public ConnectInfo mcpParam2connectInfo(McpConnectionContextRequest param, Long dataSourceId) {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setAlias("mcp:" + StringUtils.defaultIfBlank(param.getDatabaseName(),
                StringUtils.defaultIfBlank(param.getSchemaName(), "connection")));
        connectInfo.setUser(param.getUser());
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(param.getPassword());
        connectInfo.setDbType(StringUtils.trimToNull(param.getDbType()));
        connectInfo.setUrl(StringUtils.trimToNull(param.getJdbcUrl()));
        connectInfo.setDatabase(param.getDatabaseName());
        connectInfo.setSchemaName(param.getSchemaName());
        connectInfo.setConsoleOwn(false);
        connectInfo.setLoginUser("mcp");
        return connectInfo;
    }

    private ConnectInfo datasource2connectInfo(Long dataSourceId, WorkspaceDataSource dataSource, String url) {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setAlias(dataSource.getAlias());
        connectInfo.setUser(dataSource.getUser());
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(dataSource.getPassword());
        connectInfo.setDbType(dataSource.getType());
        connectInfo.setUrl(url);
        connectInfo.setConsoleOwn(false);
        connectInfo.setDriver(dataSource.getDriver());
        connectInfo.setSsh(dataSource.getSsh());
        connectInfo.setSsl(dataSource.getSsl());
        connectInfo.setJdbc(dataSource.getJdbc());
        connectInfo.setExtendInfo(dataSource.getExtendInfo());
        connectInfo.setPort(StringUtils.isNotBlank(dataSource.getPort()) ? Integer.parseInt(dataSource.getPort()) : null);
        connectInfo.setHost(dataSource.getHost());
        connectInfo.setProject(dataSource.getProject());
        connectInfo.setEmail(dataSource.getEmail());
        connectInfo.setKeyfile(dataSource.getKeyfile());
        connectInfo.setServiceName(dataSource.getServiceName());
        DriverConfig driverConfig = dataSource.getDriverConfig();
        if (driverConfig != null && driverConfig.notEmpty()) {
            connectInfo.setDriverConfig(driverConfig);
        }
        connectInfo.setDesensitizes(dataSource.getDesensitizes());
        return connectInfo;
    }

    public Long buildMcpDataSourceId(McpConnectionContextRequest param) {
        long hash = 17L;
        hash = 31 * hash + Objects.hashCode(StringUtils.trimToNull(param.getDbType()));
        hash = 31 * hash + Objects.hashCode(StringUtils.trimToNull(param.getJdbcUrl()));
        hash = 31 * hash + Objects.hashCode(StringUtils.trimToNull(param.getUser()));
        hash = 31 * hash + Objects.hashCode(StringUtils.trimToNull(param.getDatabaseName()));
        hash = 31 * hash + Objects.hashCode(StringUtils.trimToNull(param.getSchemaName()));
        if (hash == Long.MIN_VALUE) {
            hash = Long.MAX_VALUE;
        }
        hash = Math.abs(hash);
        return hash == 0 ? -1L : -hash;
    }
}
