package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.db.CellValueChunk;
import ai.chat2db.community.domain.api.model.db.CellValueDownload;
import ai.chat2db.community.domain.api.model.db.LargeValueReference;
import ai.chat2db.community.domain.api.model.request.db.DbCellValueChunkReadRequest;

/**
 * Reads and prepares large relational cell values.
 */
public interface IDbCellValueService {

    /**
     * Reads a chunk of a large cell value.
     *
     * @param dbCellValueChunkReadRequest cell value chunk read parameters.
     * @return cell value chunk.
     */
    CellValueChunk readChunk(DbCellValueChunkReadRequest dbCellValueChunkReadRequest);

    /**
     * Prepares a large cell value for download.
     *
     * @param reference large value reference.
     * @param format download format.
     * @return prepared download descriptor.
     */
    CellValueDownload prepareDownload(LargeValueReference reference, String format);
}
