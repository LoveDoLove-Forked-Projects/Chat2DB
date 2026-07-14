package ai.chat2db.plugin.redis.type;

import ai.chat2db.plugin.redis.RedisScriptExecutor;
import ai.chat2db.plugin.redis.constant.RedisConstants;
import ai.chat2db.plugin.redis.model.Action;
import ai.chat2db.plugin.redis.model.RedisKey;
import ai.chat2db.plugin.redis.model.ZSetValue;
import ai.chat2db.spi.DefaultSQLExecutor;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ai.chat2db.plugin.redis.util.RedisValueUtils.getRedisValue;

public class ZSetTypeScript extends BaseTypeScript implements ITypeScript {
    @Override
    public String getKey(RedisKey redisKey) {
        return RedisConstants.COMMAND_ZSET_RANGE_PREFIX + getRedisValue(redisKey.getName())
                + RedisConstants.COMMAND_ZSET_RANGE_WITH_SCORES_SUFFIX;
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
            List<ZSetValue> zSetValues = new ArrayList<>();
            while (resultSet.next()) {
                Object value = resultSet.getObject(RedisConstants.FIELD_VALUE);
                Object score = resultSet.getObject(RedisConstants.FIELD_SCORE);
                if (Objects.nonNull(value) && Objects.nonNull(score)) {
                    ZSetValue zSetValue = new ZSetValue();
                    zSetValue.setValue(value.toString());
                    zSetValue.setScore(Double.parseDouble(score.toString()));
                    zSetValues.add(zSetValue);
                }
            }
            rs.setZsValues(zSetValues);
        });
        String ttl = RedisScriptExecutor.getInstance().getTtl(redisKey.getName());
        if (StringUtils.isNotBlank(ttl)) {
            rs.setTtl(Long.parseLong(ttl));
        } else {
            rs.setTtl(-1L);
        }
        rs.setValue(rs.getZsValues().toString());
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
            String del = delete(oldKey.getName());
            return List.of(del);
        } else {
            List<String> script = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(newKey.getZsValues())) {
                for (ZSetValue field : newKey.getZsValues()) {
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.DELETE.equals(field.getAction())) {
                        String s = deleteItem(newKey.getName(), field);
                        script.add(s);
                    }
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.CREATE.equals(field.getAction())) {
                        String s = createItem(newKey.getName(), field);
                        script.add(s);
                    }
                    if(ai.chat2db.plugin.redis.constant.ActionConstants.UPDATE.equals(field.getAction())){
                        String s = deleteItem(newKey.getName(), field);
                        script.add(s);
                        s = createItem(newKey.getName(),field);
                        script.add(s);
                    }
                }
            }
            return script;
        }
    }

    private String createItem(String name, ZSetValue field) {
        return RedisConstants.COMMAND_ZSET_ADD_PREFIX + getRedisValue(name)
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + field.getScore()
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + getRedisValue(field.getValue())
                + RedisConstants.COMMAND_LINE_SEPARATOR;
    }

    private String deleteItem(String keyName, ZSetValue value) {
        return RedisConstants.COMMAND_ZSET_REMOVE_PREFIX + getRedisValue(keyName)
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + getRedisValue(value.getValue())
                + RedisConstants.COMMAND_LINE_SEPARATOR;
    }

    private List<String> addItem(RedisKey newKey) {
        if(newKey == null || CollectionUtils.isEmpty(newKey.getZsValues())) {
            return Lists.newArrayList();
        }
        List<String> scripts = new ArrayList<>();
        StringBuilder script = new StringBuilder();
        script.append(RedisConstants.COMMAND_ZSET_ADD_PREFIX).append(getRedisValue(newKey.getName()))
                .append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR);
        if (CollectionUtils.isNotEmpty(newKey.getZsValues())) {
            for (ZSetValue value : newKey.getZsValues()) {
                script.append(value.getScore()).append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR)
                        .append(getRedisValue(value.getValue())).append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR);
            }
        }
        scripts.add(script.toString());

        script = new StringBuilder();
        if (newKey.getTtl() != null && newKey.getTtl() > 0) {
            script.append(RedisConstants.COMMAND_EXPIRE_KEY_PREFIX).append(getRedisValue(newKey.getName()))
                    .append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR).append(newKey.getTtl())
                    .append(RedisConstants.COMMAND_LINE_SEPARATOR);
            scripts.add(script.toString());
        }
        return scripts;
    }

}
