package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.enums.ExportTypeEnum;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Opens output streams for DML export delivery.
 */
public interface IDbDmlExportDeliveryService {

    OutputStream openOutputStream(String fileName, ExportTypeEnum exportType, ExportResultHolder resultHolder)
            throws IOException;

    interface ExportResultHolder {

        void setData(String data);
    }
}
