package ai.chat2db.plugin.redis.type;

import ai.chat2db.plugin.redis.RedisScriptExecutor;
import ai.chat2db.plugin.redis.constant.RedisConstants;
import ai.chat2db.plugin.redis.model.*;
import ai.chat2db.spi.DefaultSQLExecutor;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ai.chat2db.plugin.redis.util.RedisValueUtils.getRedisValue;

public class SetTypeScript extends BaseTypeScript implements ITypeScript {
    @Override
    public String getKey(RedisKey redisKey) {
        return RedisConstants.COMMAND_SET_MEMBERS_PREFIX + getRedisValue(redisKey.getName());
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
            List<SetValue> zSetValues = new ArrayList<>();
            while (resultSet.next()) {
                Object value = resultSet.getObject(RedisConstants.FIELD_VALUE);
                SetValue setValue = new SetValue();
                if (Objects.nonNull(value)) {
                    setValue.setValue(value.toString());
                }
                zSetValues.add(setValue);
            }
            rs.setValues(zSetValues);
        });
        String ttl = RedisScriptExecutor.getInstance().getTtl(redisKey.getName());
        if (StringUtils.isNotBlank(ttl)) {
            rs.setTtl(Long.parseLong(ttl));
        } else {
            rs.setTtl(-1L);
        }
        rs.setValue(rs.getValues().toString());
        return rs;
    }

    @Override
    public List<String> createKey(RedisKey redisKey) {
        if(redisKey == null || CollectionUtils.isEmpty(redisKey.getValues())) {
            return Lists.newArrayList();
        }
        List<String> scripts = new ArrayList<>();
        StringBuilder script = new StringBuilder();
        script.append(RedisConstants.COMMAND_SET_ADD_PREFIX).append(getRedisValue(redisKey.getName()))
                .append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR);
        List<SetValue> valueList = redisKey.getValues();
        if(CollectionUtils.isNotEmpty(valueList)) {
            for (SetValue value : valueList) {
                script.append(getRedisValue(value.getValue())).append(RedisConstants.COMMAND_ARGUMENT_SEPARATOR);
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
            return List.of(delete);
        } else {
            List<String> script = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(newKey.getValues())) {
                for (SetValue field : newKey.getValues()) {
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.DELETE.equals(field.getAction())) {
                        String s = deleteItem(newKey.getName(), field.getValue());
                        script.add(s);
                    }
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.CREATE.equals(field.getAction())) {
                        String s = createItem(newKey.getName(), field.getValue());
                        script.add(s);
                    }
                    if (ai.chat2db.plugin.redis.constant.ActionConstants.UPDATE.equals(field.getAction())) {
                        String s = deleteItem(newKey.getName(), field.getValue());
                        script.add(s);
                        s = createItem(newKey.getName(), field.getValue());
                        script.add(s);
                    }
                }
            }
            return script;
        }
    }

    private String createItem(String name, String value) {
        return RedisConstants.COMMAND_SET_ADD_PREFIX + getRedisValue(name)
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + getRedisValue(value)
                + RedisConstants.COMMAND_LINE_SEPARATOR;
    }

    private String deleteItem(String name, String value) {
        return RedisConstants.COMMAND_SET_REMOVE_PREFIX + getRedisValue(name)
                + RedisConstants.COMMAND_ARGUMENT_SEPARATOR + getRedisValue(value)
                + RedisConstants.COMMAND_LINE_SEPARATOR;
    }

}
