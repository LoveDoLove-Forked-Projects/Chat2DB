package ai.chat2db.community.domain.api.model.key;

import java.util.List;

import lombok.Data;

@Data
public class KeyCreate {

    private String name;

    private String value;

    private Long ttl;

    private String type;

    private List<KeyValueItem> listValues;

    private List<KeyValueItem> hashValues;

    private List<KeyValueItem> zsValues;

    private List<KeyValueItem> streamValues;

    private List<KeyValueItem> values;
}
