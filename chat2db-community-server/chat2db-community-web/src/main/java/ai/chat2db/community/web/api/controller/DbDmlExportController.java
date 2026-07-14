package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.model.db.DbDmlExportPlan;
import ai.chat2db.community.domain.api.service.db.IDbDmlExportDeliveryService;
import ai.chat2db.community.domain.api.service.db.IDbDmlExportService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.db.DbWebConverter;
import ai.chat2db.community.web.api.model.request.db.DataExportRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles relational DML export previews and export task creation.
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/dml")
@RestController
public class DbDmlExportController {

    private final IDbDmlExportService dbDmlExportService;
    private final IDbDmlExportDeliveryService dmlExportDeliveryService;
    private final DbWebConverter dbWebConverter;

    public DbDmlExportController(IDbDmlExportService dbDmlExportService,
            IDbDmlExportDeliveryService dmlExportDeliveryService,
            DbWebConverter dbWebConverter) {
        this.dbDmlExportService = dbDmlExportService;
        this.dmlExportDeliveryService = dmlExportDeliveryService;
        this.dbWebConverter = dbWebConverter;
    }

    /**
     * Endpoint: {@code POST /api/rdb/dml/export}.
     */
    @PostMapping("/export")
    public DataResult<String> export(@Valid @RequestBody DataExportRequest request) throws IOException {
        DbDmlExportPlan exportPlan = dbDmlExportService.prepareExport(dbWebConverter.exportRequest2param(request));
        DataResult<String> result = new DataResult<>();

        try (OutputStream outputStream = dmlExportDeliveryService.openOutputStream(exportPlan.getFileName(),
                exportPlan.getExportType(),
                result::setData)) {
            dbDmlExportService.export(exportPlan.getExportRequest(), outputStream);
        }
        return result;
    }
}
