package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.pin.PinTable;
import ai.chat2db.community.domain.api.model.request.pin.DbTablePinRequest;

import java.util.List;

/**
 * Manages pinned tables in the active workspace.
 */
public interface IDbTablePinService {

    List<String> queryPinTables(DbTablePinRequest request);

    void pinTable(PinTable request);

    void deletePinTable(PinTable request);
}
