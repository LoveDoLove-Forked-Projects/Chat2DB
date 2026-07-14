package ai.chat2db.plugin.redis.type;

import ai.chat2db.plugin.redis.model.RedisKey;

import java.sql.Connection;
import java.util.List;

public interface ITypeScript {


    String getKey(RedisKey redisKey);


    RedisKey getKeyR(Connection connection, RedisKey redisKey);


    List<String> createKey(RedisKey redisKey);


    List<String> updateKey(RedisKey oldKey, RedisKey newKey);


}
