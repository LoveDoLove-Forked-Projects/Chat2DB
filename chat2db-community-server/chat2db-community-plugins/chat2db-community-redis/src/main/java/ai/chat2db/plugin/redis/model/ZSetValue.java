package ai.chat2db.plugin.redis.model;

import lombok.Data;

@Data
public class ZSetValue extends Action{
    private double score;

    private String value;

    @Override
    public String toString() {
        return value + ":" + score;
    }
}
