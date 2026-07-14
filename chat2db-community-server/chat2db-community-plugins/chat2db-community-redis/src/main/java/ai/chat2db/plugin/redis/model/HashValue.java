package ai.chat2db.plugin.redis.model;

import lombok.Data;

@Data
public class HashValue extends Action {

    private String field;

    private String value;

    @Override
    public String toString() {
        return field + ":" + value;
    }
}
