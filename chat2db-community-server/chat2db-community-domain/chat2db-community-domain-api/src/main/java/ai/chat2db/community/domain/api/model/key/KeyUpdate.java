package ai.chat2db.community.domain.api.model.key;

import lombok.Data;

@Data
public class KeyUpdate {

    private KeyEntry oldKey;

    private KeyEntry newKey;
}
