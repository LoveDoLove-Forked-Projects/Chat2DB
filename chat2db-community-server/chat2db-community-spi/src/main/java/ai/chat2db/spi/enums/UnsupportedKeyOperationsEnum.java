package ai.chat2db.spi.enums;

import ai.chat2db.community.domain.api.model.key.KeyCreate;
import ai.chat2db.community.domain.api.model.key.KeyDelete;
import ai.chat2db.community.domain.api.model.key.KeyDetailRequest;
import ai.chat2db.community.domain.api.model.key.KeyEntry;
import ai.chat2db.community.domain.api.model.key.KeyScanRequest;
import ai.chat2db.community.domain.api.model.key.KeyScanResult;
import ai.chat2db.spi.IKeyOperations;
import ai.chat2db.community.domain.api.model.key.KeyRequest;
import ai.chat2db.community.domain.api.model.key.KeyUpdate;

import java.sql.Connection;
import java.util.List;

public enum UnsupportedKeyOperationsEnum implements IKeyOperations {

    INSTANCE;

    @Override
    public List<KeyEntry> query(Connection connection, KeyRequest query) {
        throw unsupported();
    }

    @Override
    public KeyScanResult scan(Connection connection, KeyScanRequest query) {
        throw unsupported();
    }

    @Override
    public KeyEntry keyDetail(Connection connection, KeyDetailRequest query) {
        throw unsupported();
    }

    @Override
    public KeyEntry create(Connection connection, KeyCreate command) {
        throw unsupported();
    }

    @Override
    public KeyEntry update(Connection connection, KeyUpdate command) {
        throw unsupported();
    }

    @Override
    public void delete(Connection connection, KeyDelete command) {
        throw unsupported();
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("current datasource does not support key operations");
    }
}
