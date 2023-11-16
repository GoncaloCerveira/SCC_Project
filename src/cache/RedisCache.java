package cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import utils.AzureProperties;


public class RedisCache {
    private static JedisPool instance;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static boolean USE_CACHE = true;

    private synchronized static JedisPool getCachePool() {
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
        instance = new JedisPool(poolConfig, AzureProperties.CACHE_HOSTNAME, 6380, 1000, AzureProperties.CACHE_KEY, true);
        return instance;

    }

    protected static void writeToCache(String operation, String id, Object value) {
        if(USE_CACHE) {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                String jsonValue = mapper.writeValueAsString(value);
                String key = operation + ":" + id;
                jedis.set(key, jsonValue);
                jedis.expire(id, 60);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected static <T> T readFromCache(String operation, String id, TypeReference<T> valueType) {
        if(USE_CACHE) {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                String key = operation + ":" + id;
                String jsonValue = jedis.get(key);
                if (jsonValue != null) {
                    return mapper.readValue(jsonValue, valueType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected static <T> T readFromCache(String operation, String id, Class<T> valueType) {
        if(USE_CACHE) {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                String key = operation + ":" + id;
                String jsonValue = jedis.get(key);
                if (jsonValue != null) {
                    return mapper.readValue(jsonValue, valueType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
