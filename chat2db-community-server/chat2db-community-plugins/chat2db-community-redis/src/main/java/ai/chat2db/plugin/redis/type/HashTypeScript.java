package ai.chat2db.plugin.redis.type;

import ai.chat2db.plugin.redis.RedisScriptExecutor;
import ai.chat2db.plugin.redis.constant.RedisConstants;
import ai.chat2db.plugin.redis.model.Action;
import ai.chat2db.plugin.redis.model.HashValue;
import ai.chat2db.plugin.redis.model.RedisKey;
import ai.chat2db.spi.DefaultSQLExecutor;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ai.chat2db.plugin.redis.util.RedisValueUtils.getRedisValue;

public class HashTypeScript extends BaseTypeScript implements ITypeScript {
    @Override
    public String getKey(RedisKey redisKey) {
        StringBuilder script = new StringBuilder();
        if (CollectionUtils.isEmpty(redisKey.getHashValues())) {
            script.append(RedisConstants.COMMAND_HASH_GET_ALL_PREFIX).append(getRedisValue(redisKey.getName()))
                    .append(RedisConstants.COMMAND_LINE_SEPARATOR);
        } else {
            script.append(RedisConstants.COMMAND_HASH_GET_PREFIX).append(getRedisValue(redisKey.getName()))
                    .append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR).append(redisKey.getHashValues().get(0).getField())
                    .append(RedisConstants.COMMAND_LINE_SEPARATOR);
        }
        return script.toString();
    }

    @Override
    public RedisKey getKeyR(Connection connection, RedisKey redisKey) {
        if (!existKey(connection, redisKey.getName())) {
            return null;
        }
        String script = getKey(redisKey);
        RedisKey rs = new RedisKey();
        rs.setName(redisKey.getName());
        rs.setType(redisKey.getType());
        DefaultSQLExecutor.getInstance().execute(connection, script, resultSet -> {
            List<HashValue> hashValues = new ArrayList<>();
            while (resultSet.next()) {
                Object field = resultSet.getObject(RedisConstants.FIELD_FIELD);
                Object value = resultSet.getObject(RedisConstants.FIELD_VALUE);
                HashValue hs = new HashValue();
                if (Objects.nonNull(field)) {
                    hs.setField(field.toString());
                    hs.setValue(value != null ? value.toString() : null);
                }
                hashValues.add(hs);
            }
            rs.setHashValues(hashValues);
        });
        String ttl = RedisScriptExecutor.getInstance().getTtl(redisKey.getName());
        if (StringUtils.isNotBlank(ttl)) {
            rs.setTtl(Long.parseLong(ttl));
        } else {
            rs.setTtl(-1L);
        }
        rs.setValue(rs.getHashValues().toString());
        return rs;
    }

    @Override
    public List<String> createKey(RedisKey redisKey) {
        return addItem(redisKey);
    }

    @Override
    public List<String> updateKey(RedisKey oldKey, RedisKey newKey) {
        if (oldKey == null && newKey == null) {
            return null;
        }
        if (oldKey == null) {
            return createKey(newKey);
        }
        if (newKey == null) {
            String delete = delete(oldKey.getName());
            return Lists.newArrayList(delete);
        } else {
            List<String> script = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(newKey.getHashValues())) {
                for (HashValue field : newKey.getHashValues()) {
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.DELETE.equals(field.getAction())) {
                        String s = deleteItem(newKey.getName(), field.getField());
                        script.add(s);
                    }
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.CREATE.equals(field.getAction())) {
                        String s = createItem(newKey.getName(), field);
                        script.add(s);
                    }
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.UPDATE.equals(field.getAction())) {
                        String s = deleteItem(newKey.getName(), field.getField());
                        script.add(s);
                        s = createItem(newKey.getName(), field);
                        script.add(s);
                    }
                }
            }
            return script;
        }
    }

    private String createItem(String name, HashValue hashValue) {
        return RedisConstants.COMMAND_HASH_SET_PREFIX + getRedisValue(name)
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + getRedisValue(hashValue.getField())
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + getRedisValue(hashValue.getValue())
                + RedisConstants.COMMAND_LINE_SEPARATOR;
    }

    private List<String> addItem(RedisKey redisKey) {
        if(redisKey == null || CollectionUtils.isEmpty(redisKey.getHashValues())) {
            return Lists.newArrayList();
        }
        List<String> scripts = new ArrayList<>();
        StringBuilder script = new StringBuilder();
        script.append(RedisConstants.COMMAND_HASH_SET_PREFIX).append(getRedisValue(redisKey.getName()))
                .append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR);
        if (CollectionUtils.isNotEmpty(redisKey.getHashValues())) {
            for (HashValue field : redisKey.getHashValues()) {
                script.append(getRedisValue(field.getField())).append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR)
                        .append(getRedisValue(field.getValue())).append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR);
            }
        }
        scripts.add(script.toString());
        script = new StringBuilder();
        if (redisKey.getTtl() != null && redisKey.getTtl() > 0) {
            script.append(RedisConstants.COMMAND_EXPIRE_KEY_PREFIX).append(getRedisValue(redisKey.getName()))
                    .append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR).append(redisKey.getTtl())
                    .append(RedisConstants.COMMAND_LINE_SEPARATOR);
            scripts.add(script.toString());
        }
        return scripts;
    }

    private String deleteItem(String keyName, String filedName) {
        return RedisConstants.COMMAND_HASH_DELETE_PREFIX + getRedisValue(keyName)
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + getRedisValue(filedName)
                + RedisConstants.COMMAND_LINE_SEPARATOR;
    }

}
