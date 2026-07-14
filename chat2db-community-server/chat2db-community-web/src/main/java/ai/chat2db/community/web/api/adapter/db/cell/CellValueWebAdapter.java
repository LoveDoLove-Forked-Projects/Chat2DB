package ai.chat2db.community.web.api.adapter.db.cell;

import ai.chat2db.community.domain.api.model.db.CellValueDownload;
import ai.chat2db.community.domain.api.model.db.LargeValueToken;
import ai.chat2db.community.domain.api.model.request.db.DbCellValueChunkReadRequest;
import ai.chat2db.community.domain.api.model.request.db.DbCellValueTokenReadRequest;
import ai.chat2db.community.domain.api.model.request.runtime.DbConnectionContextRequest;
import ai.chat2db.community.domain.api.service.db.IDbConnectionContextService;
import ai.chat2db.community.domain.api.service.db.IDbLargeCellValueTransferService;
import ai.chat2db.community.domain.api.service.db.IDbLargeValueTokenService;
import ai.chat2db.community.web.api.aspect.connection.ICustomConnection;
import ai.chat2db.community.web.api.converter.db.CellValueConverter;
import ai.chat2db.community.web.api.model.response.db.cell.CellValueChunkResponse;
import ai.chat2db.community.web.api.util.ApplicationContextUtil;
import ai.chat2db.community.web.api.util.DownloadUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class CellValueWebAdapter implements IDbLargeCellValueTransferService<HttpServletResponse, CellValueChunkResponse> {

    private static final int STREAM_BUFFER_SIZE = 8192;

    private final IDbLargeValueTokenService largeValueTokenService;
    private final IDbConnectionContextService connectionContextService;
    private final ai.chat2db.community.domain.api.service.db.IDbCellValueService domainCellValueService;
    private final CellValueConverter cellValueConverter;

    public CellValueWebAdapter(IDbLargeValueTokenService largeValueTokenService,
                            IDbConnectionContextService connectionContextService,
                            ai.chat2db.community.domain.api.service.db.IDbCellValueService domainCellValueService,
                            CellValueConverter cellValueConverter) {
        this.largeValueTokenService = largeValueTokenService;
        this.connectionContextService = connectionContextService;
        this.domainCellValueService = domainCellValueService;
        this.cellValueConverter = cellValueConverter;
    }

    @Override
    public CellValueChunkResponse readByToken(DbCellValueTokenReadRequest dbCellValueTokenReadRequest) {
        LargeValueToken token = largeValueTokenService.requireValid(dbCellValueTokenReadRequest.getLargeValueId());
        return withContext(token, () -> {
            DbCellValueChunkReadRequest readCellValueChunkRequest = new DbCellValueChunkReadRequest();
            readCellValueChunkRequest.setReference(cellValueConverter.token2reference(token));
            readCellValueChunkRequest.setOffset(dbCellValueTokenReadRequest.getOffset());
            readCellValueChunkRequest.setLimit(dbCellValueTokenReadRequest.getLimit());
            readCellValueChunkRequest.setFormat(dbCellValueTokenReadRequest.getFormat());
            return cellValueConverter.chunk2response(domainCellValueService.readChunk(readCellValueChunkRequest));
        });
    }

    public void download(String largeValueId, String format, HttpServletResponse response) {
        LargeValueToken token = largeValueTokenService.requireValid(largeValueId);
        withContext(token, () -> {
            CellValueDownload payload = domainCellValueService.prepareDownload(cellValueConverter.token2reference(token),
                    format);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + UriUtils.encode(payload.getFileName(), StandardCharsets.UTF_8));
            response.setContentType(payload.getContentType());
            stream(payload.getInputStream(), response.getOutputStream());
            return null;
        });
    }

    public String downloadToLocalFile(String largeValueId, String format) {
        LargeValueToken token = largeValueTokenService.requireValid(largeValueId);
        return withContext(token, () -> {
            CellValueDownload payload = domainCellValueService.prepareDownload(cellValueConverter.token2reference(token),
                    format);
            File file = DownloadUtil.createDownloadFile(fileNamePrefix(payload.getFileName()),
                    fileNameSuffix(payload.getFileName()), true);
            try (InputStream inputStream = payload.getInputStream();
                 OutputStream outputStream = java.nio.file.Files.newOutputStream(file.toPath())) {
                stream(inputStream, outputStream);
            }
            return file.getAbsolutePath();
        });
    }

    private <T> T withContext(LargeValueToken token, ICellValueCallable<T> callable) {
        try {
            DbConnectionContextRequest contextParam = connectionContext(token);
            if (contextParam != null) {
                connectionContextService.bind(contextParam);
            }
            return callable.call();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connectionContextService.clear();
        }
    }

    private DbConnectionContextRequest connectionContext(LargeValueToken token) {
        Long dataSourceId = token.getDataSourceId();
        if (dataSourceId == null) {
            return null;
        }
        if (dataSourceId > 1L) {
            DbConnectionContextRequest param = new DbConnectionContextRequest();
            param.setDataSourceId(dataSourceId);
            param.setDatabaseName(token.getDatabaseName());
            param.setSchemaName(token.getSchemaName());
            return param;
        }
        ICustomConnection customConnection = ApplicationContextUtil.getBean(ICustomConnection.class);
        return customConnection == null ? null : customConnection.getConnectionInfo(dataSourceId,
                token.getDatabaseName(), token.getSchemaName(), null);
    }

    private void stream(InputStream inputStream, OutputStream outputStream) throws IOException {
        try (inputStream) {
            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }
    }

    private String fileNamePrefix(String fileName) {
        int suffixStart = fileName.lastIndexOf('.');
        String prefix = suffixStart > 0 ? fileName.substring(0, suffixStart) : fileName;
        return org.apache.commons.lang3.StringUtils.defaultIfBlank(prefix, "cell-value");
    }

    private String fileNameSuffix(String fileName) {
        int suffixStart = fileName.lastIndexOf('.');
        return suffixStart >= 0 ? fileName.substring(suffixStart) : ".bin";
    }

    @FunctionalInterface
    private interface ICellValueCallable<T> {
        T call() throws IOException;
    }
}
