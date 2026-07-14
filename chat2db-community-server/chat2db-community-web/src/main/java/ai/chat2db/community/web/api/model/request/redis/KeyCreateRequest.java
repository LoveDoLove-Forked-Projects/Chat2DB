package ai.chat2db.community.web.api.model.request.redis;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import ai.chat2db.community.domain.api.model.key.KeyValueItem;
import java.util.List;
import lombok.Data;


@Data
public class KeyCreateRequest extends DataSourceBaseRequest {

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
