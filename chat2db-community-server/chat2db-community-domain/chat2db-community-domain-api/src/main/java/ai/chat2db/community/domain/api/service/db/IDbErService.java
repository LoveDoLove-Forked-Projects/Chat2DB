package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.er.ERModel;
import ai.chat2db.community.domain.api.model.er.ERPosition;
import ai.chat2db.community.domain.api.model.request.er.DbErQueryRequest;

/**
 * Builds entity-relationship metadata models for relational database objects.
 */
public interface IDbErService {

    /**
     * Builds an entity-relationship model for the requested datasource scope.
     *
     * @param dbErQueryRequest ER query parameters.
     * @return ER model.
     */
    ERModel getModel(DbErQueryRequest dbErQueryRequest);

    /**
     * Builds an entity-relationship model and attaches persisted layout
     * positions for the requested datasource scope.
     *
     * @param dbErQueryRequest ER query parameters.
     * @return ER model with persisted position.
     */
    ERModel getModelWithPosition(DbErQueryRequest dbErQueryRequest);

    /**
     * Persists ER diagram positions.
     *
     * @param erPosition position payload.
     */
    void savePosition(ERPosition erPosition);

}
