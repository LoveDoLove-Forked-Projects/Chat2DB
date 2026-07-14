package ai.chat2db.plugin.redis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisKey{

    private String name;


    private String value;


    private Long ttl;


    private String type;


    private List<ListValue> listValues;


    private List<HashValue> hashValues;


    private List<ZSetValue> zsValues;


    private List<StreamValue> streamValues;


    private List<SetValue> values;


}
