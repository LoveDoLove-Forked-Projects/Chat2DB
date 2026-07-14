package ai.chat2db.plugin.redis.model;

import ai.chat2db.community.domain.api.model.datasource.KeyValue;
import lombok.Data;

import java.util.List;

@Data
public class StreamValue extends Action{

    private String id;

    private List<KeyValue> values;

    @Override
    public String toString() {
        return id;
    }
}
