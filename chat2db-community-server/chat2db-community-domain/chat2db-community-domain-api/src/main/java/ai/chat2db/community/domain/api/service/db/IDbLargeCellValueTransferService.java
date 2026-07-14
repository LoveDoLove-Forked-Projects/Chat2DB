package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.db.DbCellValueTokenReadRequest;

/**
 * Reads and transfers large relational cell values through a transport-specific response type.
 *
 */
public interface IDbLargeCellValueTransferService<R, C> {

    /**
     * Reads a large cell value chunk by token.
     *
     * @param dbCellValueTokenReadRequest large cell value token read request.
     * @return cell value chunk response.
     */
    C readByToken(DbCellValueTokenReadRequest dbCellValueTokenReadRequest);

    /**
     * Writes a large cell value download to the supplied transport response.
     *
     * @param largeValueId large cell value token.
     * @param format output format.
     * @param response transport response.
     */
    void download(String largeValueId, String format, R response);

    /**
     * Writes a large cell value download to a local file.
     *
     * @param largeValueId large cell value token.
     * @param format output format.
     * @return local file path.
     */
    String downloadToLocalFile(String largeValueId, String format);
}
