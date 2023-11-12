package cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.fasterxml.jackson.core.type.TypeReference;


public class RedisCache {
    private static final String RedisHostname = "scc2324cache4204.redis.cache.windows.net";
    private static final String RedisKey = "HcR09frYgXC3zVhZUll7F9CdVEiRmwVqlAzCaKC6ujM=";

    private static JedisPool instance;
    private static final ObjectMapper mapper = new ObjectMapper();

    public synchronized static JedisPool getCachePool() {
        if( instance != null)
            return instance;
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        instance = new JedisPool(poolConfig, RedisHostname, 6380, 1000, RedisKey, true);
        return instance;

    }

    public static void writeToCache(String operation, String id, Object value) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String jsonValue = mapper.writeValueAsString(value);
            String key = operation + ":" + id;
            jedis.set(key, jsonValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T readFromCache(String operation, String id, TypeReference<T> valueType) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String key = operation + ":" + id;
            String jsonValue = jedis.get(key);
            if (jsonValue != null) {
                return mapper.readValue(jsonValue, valueType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
