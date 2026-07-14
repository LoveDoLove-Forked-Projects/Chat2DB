package ai.chat2db.community.web.api.converter.cli;

import ai.chat2db.community.domain.api.model.cli.CliDataSource;
import ai.chat2db.community.domain.api.model.cli.CliPage;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceCreateRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceListRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceUpdateRequest;
import ai.chat2db.community.web.api.model.request.cli.CliConnectionTestRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceCreateRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceListRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceUpdateRequest;
import ai.chat2db.community.web.api.model.response.cli.CliConnectionTestResponse;
import ai.chat2db.community.web.api.model.response.cli.CliDatasourceResponse;
import ai.chat2db.community.web.api.model.response.cli.CliPageResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class CliDatasourceConverter {

    public CliDataSourceListRequest request2param(CliDatasourceListRequest request) {
        CliDataSourceListRequest param = new CliDataSourceListRequest();
        param.setPageNo(request.getPageNo());
        param.setPageSize(request.getPageSize());
        param.setRefresh(request.getRefresh());
        param.setSearchKey(request.getSearchKey());
        return param;
    }

    public ai.chat2db.community.domain.api.model.request.cli.CliConnectionTestRequest request2param(CliConnectionTestRequest request) {
        ai.chat2db.community.domain.api.model.request.cli.CliConnectionTestRequest param =
                new ai.chat2db.community.domain.api.model.request.cli.CliConnectionTestRequest();
        param.setDataSourceId(request.getDataSourceId());
        param.setDbType(request.getDbType());
        param.setUrl(request.getUrl());
        param.setHost(request.getHost());
        param.setPort(request.getPort());
        param.setDatabase(request.getDatabase());
        param.setUser(request.getUser());
        param.setPassword(request.getPassword());
        param.setAuthenticationType(request.getAuthenticationType());
        param.setSsh(request.getSsh());
        param.setSsl(request.getSsl());
        param.setSid(request.getSid());
        param.setDriver(request.getDriver());
        param.setJdbc(request.getJdbc());
        param.setExtendInfo(request.getExtendInfo());
        param.setDriverConfig(request.getDriverConfig());
        param.setServiceName(request.getServiceName());
        param.setServiceType(request.getServiceType());
        param.setEmail(request.getEmail());
        param.setKeyfile(request.getKeyfile());
        param.setProject(request.getProject());
        return param;
    }

    public CliDataSourceCreateRequest request2param(CliDatasourceCreateRequest request) {
        CliDataSourceCreateRequest param = new CliDataSourceCreateRequest();
        param.setAlias(request.getAlias());
        param.setDbType(request.getDbType());
        param.setUrl(request.getUrl());
        param.setUser(request.getUser());
        param.setPassword(request.getPassword());
        param.setAuthenticationType(request.getAuthenticationType());
        param.setHost(request.getHost());
        param.setPort(request.getPort());
        param.setSsh(request.getSsh());
        param.setSsl(request.getSsl());
        param.setSid(request.getSid());
        param.setDriver(request.getDriver());
        param.setJdbc(request.getJdbc());
        param.setExtendInfo(request.getExtendInfo());
        param.setDriverConfig(request.getDriverConfig());
        param.setEnvironmentId(request.getEnvironmentId());
        param.setDatabase(request.getDatabase());
        param.setServiceName(request.getServiceName());
        param.setServiceType(request.getServiceType());
        param.setEmail(request.getEmail());
        param.setKeyfile(request.getKeyfile());
        param.setProject(request.getProject());
        param.setOrganizationId(request.getOrganizationId());
        param.setToken(request.getToken());
        param.setStorageType(request.getStorageType());
        param.setSpaceId(request.getSpaceId());
        return param;
    }

    public CliDataSourceUpdateRequest request2param(CliDatasourceUpdateRequest request) {
        CliDataSourceUpdateRequest param = new CliDataSourceUpdateRequest();
        param.setDataSourceId(request.getDataSourceId());
        param.setAlias(request.getAlias());
        param.setDbType(request.getDbType());
        param.setUrl(request.getUrl());
        param.setUser(request.getUser());
        param.setPassword(request.getPassword());
        param.setAuthenticationType(request.getAuthenticationType());
        param.setEnvironment(request.getEnvironment());
        param.setEnvironmentId(request.getEnvironmentId());
        param.setHost(request.getHost());
        param.setPort(request.getPort());
        param.setSsh(request.getSsh());
        param.setSsl(request.getSsl());
        param.setSid(request.getSid());
        param.setDriver(request.getDriver());
        param.setJdbc(request.getJdbc());
        param.setExtendInfo(request.getExtendInfo());
        param.setDriverConfig(request.getDriverConfig());
        param.setDatabase(request.getDatabase());
        param.setServiceName(request.getServiceName());
        param.setServiceType(request.getServiceType());
        param.setOrganizationId(request.getOrganizationId());
        param.setToken(request.getToken());
        param.setStorageType(request.getStorageType());
        param.setSpaceId(request.getSpaceId());
        return param;
    }

    public CliPageResponse<CliDatasourceResponse> page2response(CliPage<CliDataSource> page) {
        if (page == null) {
            return CliPageResponse.of(Collections.emptyList(), null, null, 0L);
        }
        return CliPageResponse.of(datasource2response(page.getItems()), page.getPageNo(), page.getPageSize(),
                page.getTotal());
    }

    public CliConnectionTestResponse result2response(
            ai.chat2db.community.domain.api.model.cli.CliConnectionTestResponse result) {
        if (result == null) {
            return null;
        }
        CliConnectionTestResponse response = new CliConnectionTestResponse();
        response.setMode(result.getMode());
        response.setDataSourceId(result.getDataSourceId());
        response.setDbType(result.getDbType());
        response.setCanConnect(result.getCanConnect());
        response.setDurationMs(result.getDurationMs());
        response.setErrorCode(result.getErrorCode());
        response.setErrorMessage(result.getErrorMessage());
        response.setErrorDetail(result.getErrorDetail());
        return response;
    }

    public List<CliDatasourceResponse> datasource2response(List<CliDataSource> dataSources) {
        if (dataSources == null) {
            return Collections.emptyList();
        }
        return dataSources.stream()
                .filter(Objects::nonNull)
                .map(this::datasource2response)
                .toList();
    }

    public CliDatasourceResponse datasource2response(CliDataSource dataSource) {
        if (dataSource == null) {
            return null;
        }
        CliDatasourceResponse response = new CliDatasourceResponse();
        response.setId(dataSource.getId());
        response.setAlias(dataSource.getAlias());
        response.setDbType(dataSource.getDbType());
        response.setHost(dataSource.getHost());
        response.setPort(dataSource.getPort());
        response.setUser(dataSource.getUser());
        response.setUrl(dataSource.getUrl());
        response.setDatabase(dataSource.getDatabase());
        response.setEnvironment(dataSource.getEnvironment());
        response.setSupportDatabase(dataSource.getSupportDatabase());
        response.setSupportSchema(dataSource.getSupportSchema());
        return response;
    }

}
