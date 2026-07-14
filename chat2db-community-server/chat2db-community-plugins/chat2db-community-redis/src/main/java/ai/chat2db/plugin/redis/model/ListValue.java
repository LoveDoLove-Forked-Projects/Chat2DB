package ai.chat2db.plugin.redis.model;

import lombok.Data;

@Data
public class ListValue extends Action{

    private String value;

    private Long index;


    @Override
    public String toString() {
        return value;
    }
}
