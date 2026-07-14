
package ai.chat2db.community.domain.api.model.datasource;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;


@Data
public class KeyValue {



    private String key;




    private String value;




    private boolean required;




    private List<String> choices;

    public static Map<String, Object> toMap(List<KeyValue> keyValues) {
        if (CollectionUtils.isEmpty(keyValues)) {
            return Maps.newHashMap();
        } else {
            Map<String, Object> map = Maps.newHashMap();
            keyValues.forEach(keyValue -> map.put(keyValue.getKey(), String.valueOf(keyValue.getValue())));
            return map;
        }
    }
}
