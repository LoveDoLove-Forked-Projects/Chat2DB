package ai.chat2db.community.domain.api.model.key;

import java.util.List;

import lombok.Data;

@Data
public class KeyValueItem {

    private String id;

    private Long index;

    private String key;

    private String field;

    private Object value;

    private Double score;

    private String member;

    private String action;

    private List<KeyValueItem> values;
}
