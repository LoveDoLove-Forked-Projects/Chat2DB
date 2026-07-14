package ai.chat2db.community.domain.core.converter;

import java.util.Collections;
import java.util.List;

import ai.chat2db.community.domain.api.enums.plugin.AuthenticationTypeEnum;
import ai.chat2db.community.domain.api.model.cli.CliDataSource;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.datasource.SSHInfo;
import ai.chat2db.community.domain.api.model.storage.WorkspaceDataSource;
import ai.chat2db.community.domain.api.model.request.cli.CliConnectionTestRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceCreateRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceUpdateRequest;
import ai.chat2db.community.domain.api.model.request.datasource.DbDataSourcePreConnectRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CliDataSourceConverter {

    private static final String REDACTED = "<redacted>";

    public List<CliDataSource> datasource2response(List<WorkspaceDataSource> dataSources) {
        if (dataSources == null) {
            return Collections.emptyList();
        }
        return dataSources.stream().map(this::datasource2response).toList();
    }

    public CliDataSource datasource2response(WorkspaceDataSource dataSource) {
        return datasource2response(dataSource, false);
    }

    public CliDataSource datasource2response(WorkspaceDataSource dataSource, boolean detailResponse) {
        if (dataSource == null) {
            return null;
        }
        CliDataSource response = new CliDataSource();
        response.setId(dataSource.getId());
        response.setAlias(dataSource.getAlias());
        response.setDbType(dataSource.getType());
        response.setHost(detailResponse ? maskEncryptedHost(dataSource.getHost()) : dataSource.getHost());
        response.setPort(dataSource.getPort());
        response.setUser(maskSensitive(dataSource.getUser()));
        response.setUrl(maskSensitive(dataSource.getUrl()));
        response.setDatabase(dataSource.getServiceName());
        response.setEnvironment(dataSource.getEnvType());
        response.setSupportDatabase(dataSource.isSupportDatabase());
        response.setSupportSchema(dataSource.isSupportSchema());
        return response;
    }

    public WorkspaceDataSource create2storage(CliDataSourceCreateRequest source) {
        WorkspaceDataSource target = new WorkspaceDataSource();
        target.setAlias(source.getAlias());
        target.setUrl(source.getUrl());
        target.setUser(source.getUser());
        target.setPassword(source.getPassword());
        target.setAuthenticationType(source.getAuthenticationType());
        target.setType(source.getDbType());
        target.setHost(source.getHost());
        target.setPort(source.getPort());
        target.setSsh(source.getSsh());
        target.setSsl(source.getSsl());
        target.setSid(source.getSid());
        target.setDriver(source.getDriver());
        target.setJdbc(source.getJdbc());
        target.setExtendInfo(source.getExtendInfo());
        target.setDriverConfig(source.getDriverConfig());
        target.setEnvironmentId(source.getEnvironmentId());
        target.setServiceName(firstNonBlank(source.getServiceName(), source.getDatabase()));
        target.setServiceType(source.getServiceType());
        target.setEmail(source.getEmail());
        target.setKeyfile(source.getKeyfile());
        target.setProject(source.getProject());
        target.setOrganizationId(source.getOrganizationId());
        target.setStorageType(source.getStorageType());
        target.setSpaceId(source.getSpaceId());
        return target;
    }

    public WorkspaceDataSource update2storage(CliDataSourceUpdateRequest source) {
        WorkspaceDataSource target = new WorkspaceDataSource();
        target.setId(source.getDataSourceId());
        target.setAlias(source.getAlias());
        target.setUrl(source.getUrl());
        target.setUser(source.getUser());
        target.setPassword(source.getPassword());
        target.setType(source.getDbType());
        target.setEnvType(source.getEnvironment());
        target.setEnvironmentId(source.getEnvironmentId() == null ? null : source.getEnvironmentId().longValue());
        target.setHost(source.getHost());
        target.setPort(source.getPort());
        target.setSsh(source.getSsh());
        target.setSsl(source.getSsl());
        target.setSid(source.getSid());
        target.setDriver(source.getDriver());
        target.setJdbc(source.getJdbc());
        target.setExtendInfo(source.getExtendInfo());
        target.setDriverConfig(source.getDriverConfig());
        target.setServiceName(firstNonBlank(source.getServiceName(), source.getDatabase()));
        target.setServiceType(source.getServiceType());
        target.setOrganizationId(source.getOrganizationId());
        target.setStorageType(source.getStorageType());
        target.setSpaceId(source.getSpaceId());
        return target;
    }

    public CliConnectionTestRequest create2connectionTest(CliDataSourceCreateRequest source) {
        CliConnectionTestRequest request = new CliConnectionTestRequest();
        request.setDbType(source.getDbType());
        request.setUrl(source.getUrl());
        request.setHost(source.getHost());
        request.setPort(source.getPort());
        request.setDatabase(source.getDatabase());
        request.setUser(source.getUser());
        request.setPassword(source.getPassword());
        request.setAuthenticationType(source.getAuthenticationType());
        request.setSsh(source.getSsh());
        request.setSsl(source.getSsl());
        request.setSid(source.getSid());
        request.setDriver(source.getDriver());
        request.setJdbc(source.getJdbc());
        request.setExtendInfo(source.getExtendInfo());
        request.setDriverConfig(source.getDriverConfig());
        request.setServiceName(source.getServiceName());
        request.setServiceType(source.getServiceType());
        request.setEmail(source.getEmail());
        request.setKeyfile(source.getKeyfile());
        request.setProject(source.getProject());
        return request;
    }

    public DbDataSourcePreConnectRequest connectionTest2param(CliConnectionTestRequest source, String url, SSHInfo ssh,
            DriverConfig driverConfig) {
        DbDataSourcePreConnectRequest target = new DbDataSourcePreConnectRequest();
        target.setType(source.getDbType());
        target.setUrl(url);
        target.setUser(source.getUser());
        target.setPassword(source.getPassword());
        target.setHost(source.getHost());
        target.setPort(source.getPort());
        target.setServiceType(source.getServiceType());
        target.setSsh(ssh);
        target.setSsl(source.getSsl());
        target.setSid(source.getSid());
        target.setDriver(source.getDriver());
        target.setJdbc(source.getJdbc());
        target.setExtendInfo(source.getExtendInfo());
        target.setDriverConfig(driverConfig);
        target.setEmail(source.getEmail());
        target.setKeyfile(source.getKeyfile());
        target.setProject(source.getProject());
        return target;
    }

    public DbDataSourcePreConnectRequest datasource2preConnect(Long dataSourceId, WorkspaceDataSource dataSource, String url,
            SSHInfo ssh, DriverConfig driverConfig) {
        DbDataSourcePreConnectRequest target = new DbDataSourcePreConnectRequest();
        target.setId(dataSourceId);
        target.setAlias(dataSource.getAlias());
        target.setType(dataSource.getType());
        target.setUrl(url);
        target.setUser(dataSource.getUser());
        if (!AuthenticationTypeEnum.NONE.getCode().equals(dataSource.getAuthenticationType())) {
            target.setPassword(dataSource.getPassword());
        }
        target.setHost(dataSource.getHost());
        target.setPort(dataSource.getPort());
        target.setServiceType(dataSource.getServiceType());
        target.setSsh(ssh);
        target.setSsl(dataSource.getSsl());
        target.setSid(dataSource.getSid());
        target.setDriver(dataSource.getDriver());
        target.setJdbc(dataSource.getJdbc());
        target.setExtendInfo(dataSource.getExtendInfo());
        target.setDriverConfig(driverConfig);
        target.setEmail(dataSource.getEmail());
        target.setKeyfile(dataSource.getKeyfile());
        target.setProject(dataSource.getProject());
        return target;
    }

    private String firstNonBlank(String primary, String fallback) {
        return StringUtils.isNotBlank(primary) ? primary : fallback;
    }

    private String maskSensitive(String value) {
        return StringUtils.isBlank(value) ? value : REDACTED;
    }

    private String maskEncryptedHost(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        return looksEncrypted(value) ? REDACTED : value;
    }

    private boolean looksEncrypted(String value) {
        return value.length() > 64;
    }
}
