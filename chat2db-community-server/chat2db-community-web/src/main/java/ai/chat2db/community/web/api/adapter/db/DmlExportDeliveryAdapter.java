package ai.chat2db.community.web.api.adapter.db;

import ai.chat2db.community.domain.api.enums.ExportTypeEnum;
import ai.chat2db.community.domain.api.service.db.IDbDmlExportDeliveryService;
import ai.chat2db.community.tools.exception.ParamBusinessException;
import ai.chat2db.community.tools.util.ConfigUtils;
import ai.chat2db.community.web.api.util.DownloadUtil;
import cn.hutool.core.io.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class DmlExportDeliveryAdapter implements IDbDmlExportDeliveryService {

    @Override
    public OutputStream openOutputStream(String fileName, ExportTypeEnum exportType, ExportResultHolder resultHolder)
            throws IOException {
        String suffix = resolveSuffix(exportType);
        if (!ConfigUtils.isDesktop()) {
            HttpServletResponse response = currentResponse();
            response.setCharacterEncoding("utf-8");
            response.setContentType(resolveContentType(exportType));
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + suffix);
            return response.getOutputStream();
        }

        File file = DownloadUtil.createDownloadFile("chat2db", suffix, true);
        resultHolder.setData(file.getAbsolutePath());
        return FileUtil.getOutputStream(file);
    }

    private HttpServletResponse currentResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || attributes.getResponse() == null) {
            throw new ParamBusinessException("response");
        }
        return attributes.getResponse();
    }

    private String resolveSuffix(ExportTypeEnum exportType) {
        if (exportType == ExportTypeEnum.CSV) {
            return ".csv";
        }
        if (exportType == ExportTypeEnum.EXCEL) {
            return ".xlsx";
        }
        return ".sql";
    }

    private String resolveContentType(ExportTypeEnum exportType) {
        if (exportType == ExportTypeEnum.CSV) {
            return "text/csv";
        }
        if (exportType == ExportTypeEnum.EXCEL) {
            return "text/excel";
        }
        return "text/sql";
    }
}
