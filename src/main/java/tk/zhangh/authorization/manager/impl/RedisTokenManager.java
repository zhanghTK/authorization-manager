package tk.zhangh.authorization.manager.impl;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 使用Redis存储Token
 * 若用户Token唯一，分别维护AUTHORIZATION_ID和AUTHORIZATION_TOKEN；
 * 否则只维护AUTHORIZATION_TOKEN
 * Created by ZhangHao on 2017/11/20.
 */
public class RedisTokenManager extends AbstractTokenManager {

    /**
     * Id前缀
     */
    private static final String REDIS_ID_PREFIX = "AUTHORIZATION_ID_";

    /**
     * Token前缀
     */
    private static final String REDIS_TOKEN_PREFIX = "AUTHORIZATION_TOKEN_";

    private JedisPool jedisPool;

    public RedisTokenManager(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    protected void deleteSingleRecordById(String id) {
        String token = getToken(id);
        if (token != null) {
            delete(formatId(id), formatToken(token));
        }
    }

    @Override
    public void deleteByToken(String token) {
        if (singleTokenWithUser) {
            String id = getId(token);
            delete(formatToken(id), formatToken(token));
        } else {
            delete(formatToken(token));
        }
    }

    @Override
    protected void createSingleRecord(String id, String token) {
        // 若Id已存在对应Token，先删除
        String oldToken = get(formatId(id));
        if (oldToken != null) {
            delete(formatToken(oldToken));
        }
        set(formatToken(token), id, tokenExpireSeconds);
        set(formatId(id), token, tokenExpireSeconds);
    }

    @Override
    protected void createMultipleRecord(String id, String token) {
        set(formatToken(token), id, tokenExpireSeconds);
    }

    @Override
    protected String getIdByToken(String token) {
        return get(formatToken(token));
    }

    @Override
    protected void updateExpire(String id, String token) {
        if (singleTokenWithUser) {
            expire(formatId(id), tokenExpireSeconds);
        }
        expire(formatToken(token), tokenExpireSeconds);
    }

    private String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    private String set(String key, String value, int expireSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, expireSeconds, value);
        }
    }

    private void expire(String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, seconds);
        }
    }

    private void delete(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(keys);
        }
    }

    private String getToken(String key) {
        return get(formatId(key));
    }

    private String formatId(String key) {
        return REDIS_ID_PREFIX + key;
    }

    private String formatToken(String token) {
        return REDIS_TOKEN_PREFIX + token;
    }
}
