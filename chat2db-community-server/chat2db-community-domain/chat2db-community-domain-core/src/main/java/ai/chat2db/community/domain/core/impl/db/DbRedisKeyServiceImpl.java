package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.key.KeyCreate;
import ai.chat2db.community.domain.api.model.key.KeyDelete;
import ai.chat2db.community.domain.api.model.key.KeyDetailRequest;
import ai.chat2db.community.domain.api.model.key.KeyEntry;
import ai.chat2db.community.domain.api.model.key.KeyRequest;
import ai.chat2db.community.domain.api.model.key.KeyScanRequest;
import ai.chat2db.community.domain.api.model.key.KeyScanResult;
import ai.chat2db.community.domain.api.model.key.KeyUpdate;
import ai.chat2db.community.domain.api.service.db.IDbRedisKeyService;
import ai.chat2db.spi.IKeyOperations;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;

@Service
public class DbRedisKeyServiceImpl implements IDbRedisKeyService {

    @Override
    public KeyEntry create(KeyCreate param) {
        return keyOperations().create(connection(), param);
    }

    @Override
    public KeyEntry update(KeyUpdate param) {
        return keyOperations().update(connection(), param);
    }

    @Override
    public void delete(KeyDelete param) {
        keyOperations().delete(connection(), param);
    }

    @Override
    public List<KeyEntry> query(KeyRequest param) {
        return keyOperations().query(connection(), param);
    }

    @Override
    public KeyScanResult scan(KeyScanRequest param) {
        return keyOperations().scan(connection(), param);
    }

    @Override
    public KeyEntry keyDetail(KeyDetailRequest param) {
        return keyOperations().keyDetail(connection(), param);
    }

    private IKeyOperations keyOperations() {
        return Chat2DBContext.getDbMetaData().keyOperations();
    }

    private Connection connection() {
        return Chat2DBContext.getConnection();
    }
}
