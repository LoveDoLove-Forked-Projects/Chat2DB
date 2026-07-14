package ai.chat2db.plugin.redis.converter;

import ai.chat2db.plugin.redis.model.HashValue;
import ai.chat2db.plugin.redis.model.ListValue;
import ai.chat2db.plugin.redis.model.RedisKey;
import ai.chat2db.plugin.redis.model.SetValue;
import ai.chat2db.plugin.redis.model.StreamValue;
import ai.chat2db.plugin.redis.model.ZSetValue;
import ai.chat2db.community.domain.api.model.datasource.KeyValue;
import ai.chat2db.community.domain.api.model.key.KeyCreate;
import ai.chat2db.community.domain.api.model.key.KeyEntry;
import ai.chat2db.community.domain.api.model.key.KeyValueItem;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RedisKeyConverter {

    public RedisKey keyCreate2redisKey(KeyCreate command) {
        RedisKey redisKey = new RedisKey();
        redisKey.setName(command.getName());
        redisKey.setValue(command.getValue());
        redisKey.setTtl(command.getTtl());
        redisKey.setType(command.getType());
        redisKey.setListValues(keyItem2listValue(command.getListValues()));
        redisKey.setHashValues(keyItem2hashValue(command.getHashValues()));
        redisKey.setZsValues(keyItem2zsetValue(command.getZsValues()));
        redisKey.setStreamValues(keyItem2streamValue(command.getStreamValues()));
        redisKey.setValues(keyItem2setValue(command.getValues()));
        return redisKey;
    }

    public RedisKey keyEntry2redisKey(KeyEntry entry) {
        RedisKey redisKey = new RedisKey();
        redisKey.setName(entry.getName());
        redisKey.setValue(entry.getValue() == null ? null : entry.getValue().toString());
        redisKey.setTtl(entry.getTtl());
        redisKey.setType(entry.getType());
        redisKey.setListValues(keyItem2listValue(entry.getListValues()));
        redisKey.setHashValues(keyItem2hashValue(entry.getHashValues()));
        redisKey.setZsValues(keyItem2zsetValue(entry.getZsValues()));
        redisKey.setStreamValues(keyItem2streamValue(entry.getStreamValues()));
        redisKey.setValues(keyItem2setValue(entry.getValues()));
        return redisKey;
    }

    public KeyEntry redisKey2keyEntry(RedisKey redisKey) {
        KeyEntry entry = new KeyEntry();
        entry.setName(redisKey.getName());
        entry.setValue(redisKey.getValue());
        entry.setTtl(redisKey.getTtl());
        entry.setType(redisKey.getType());
        entry.setListValues(listValue2keyItem(redisKey.getListValues()));
        entry.setHashValues(hashValue2keyItem(redisKey.getHashValues()));
        entry.setZsValues(zsetValue2keyItem(redisKey.getZsValues()));
        entry.setStreamValues(streamValue2keyItem(redisKey.getStreamValues()));
        entry.setValues(setValue2keyItem(redisKey.getValues()));
        return entry;
    }

    private List<ListValue> keyItem2listValue(List<KeyValueItem> values) {
        return values == null ? null : values.stream().map(value -> {
            ListValue listValue = new ListValue();
            listValue.setAction(value.getAction());
            listValue.setIndex(value.getIndex());
            listValue.setValue(value.getValue() == null ? null : value.getValue().toString());
            return listValue;
        }).collect(Collectors.toList());
    }

    private List<HashValue> keyItem2hashValue(List<KeyValueItem> values) {
        return values == null ? null : values.stream().map(value -> {
            HashValue hashValue = new HashValue();
            hashValue.setAction(value.getAction());
            hashValue.setField(value.getField());
            hashValue.setValue(value.getValue() == null ? null : value.getValue().toString());
            return hashValue;
        }).collect(Collectors.toList());
    }

    private List<ZSetValue> keyItem2zsetValue(List<KeyValueItem> values) {
        return values == null ? null : values.stream().map(value -> {
            ZSetValue zSetValue = new ZSetValue();
            zSetValue.setAction(value.getAction());
            zSetValue.setScore(value.getScore() == null ? 0 : value.getScore());
            zSetValue.setValue(value.getValue() == null ? null : value.getValue().toString());
            return zSetValue;
        }).collect(Collectors.toList());
    }

    private List<StreamValue> keyItem2streamValue(List<KeyValueItem> values) {
        return values == null ? null : values.stream().map(value -> {
            StreamValue streamValue = new StreamValue();
            streamValue.setAction(value.getAction());
            streamValue.setId(value.getId());
            streamValue.setValues(keyItem2keyValue(value.getValues()));
            return streamValue;
        }).collect(Collectors.toList());
    }

    private List<SetValue> keyItem2setValue(List<KeyValueItem> values) {
        return values == null ? null : values.stream().map(value -> {
            SetValue setValue = new SetValue();
            setValue.setAction(value.getAction());
            setValue.setValue(value.getValue() == null ? null : value.getValue().toString());
            return setValue;
        }).collect(Collectors.toList());
    }

    private List<KeyValueItem> listValue2keyItem(List<ListValue> values) {
        return redisValue2keyItem(values, value -> {
            KeyValueItem item = new KeyValueItem();
            item.setAction(value.getAction());
            item.setIndex(value.getIndex());
            item.setValue(value.getValue());
            return item;
        });
    }

    private List<KeyValueItem> hashValue2keyItem(List<HashValue> values) {
        return redisValue2keyItem(values, value -> {
            KeyValueItem item = new KeyValueItem();
            item.setAction(value.getAction());
            item.setField(value.getField());
            item.setValue(value.getValue());
            return item;
        });
    }

    private List<KeyValueItem> zsetValue2keyItem(List<ZSetValue> values) {
        return redisValue2keyItem(values, value -> {
            KeyValueItem item = new KeyValueItem();
            item.setAction(value.getAction());
            item.setScore(value.getScore());
            item.setValue(value.getValue());
            return item;
        });
    }

    private List<KeyValueItem> streamValue2keyItem(List<StreamValue> values) {
        return redisValue2keyItem(values, value -> {
            KeyValueItem item = new KeyValueItem();
            item.setAction(value.getAction());
            item.setId(value.getId());
            item.setValues(keyValue2keyItem(value.getValues()));
            return item;
        });
    }

    private List<KeyValueItem> setValue2keyItem(List<SetValue> values) {
        return redisValue2keyItem(values, value -> {
            KeyValueItem item = new KeyValueItem();
            item.setAction(value.getAction());
            item.setValue(value.getValue());
            return item;
        });
    }

    private List<KeyValue> keyItem2keyValue(List<KeyValueItem> values) {
        return values == null ? null : values.stream().map(value -> {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey(value.getKey());
            keyValue.setValue(value.getValue() == null ? null : value.getValue().toString());
            return keyValue;
        }).collect(Collectors.toList());
    }

    private List<KeyValueItem> keyValue2keyItem(List<KeyValue> values) {
        return values == null ? null : values.stream().map(value -> {
            KeyValueItem item = new KeyValueItem();
            item.setKey(value.getKey());
            item.setValue(value.getValue());
            return item;
        }).collect(Collectors.toList());
    }

    private <T> List<KeyValueItem> redisValue2keyItem(List<T> values, Function<T, KeyValueItem> converter) {
        return values == null ? null : values.stream().map(converter).collect(Collectors.toList());
    }
}
