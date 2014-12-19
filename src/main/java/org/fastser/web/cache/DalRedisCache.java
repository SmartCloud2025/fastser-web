package org.fastser.web.cache;

import java.util.Collection;

import org.fastser.dal.cache.Cache;
import org.fastser.web.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class DalRedisCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(DalRedisCache.class);

    private ShardedJedisPool jedisPool;


    @Override
    public Object getObject(Object key) {
        byte[] reslut = null;
        byte[] tkey = SerializeUtil.serialize(key.toString());
        ShardedJedis jedis = null;
        try {
            getJedisPool();
            jedis = jedisPool.getResource();
            if (jedis.exists(tkey)) {
                reslut = jedis.get(tkey);
                if (log.isDebugEnabled())
                    log.debug("-->" + key +  " read from dal redis cache success!");
                return SerializeUtil.unserialize(reslut);
            } else {
                if (log.isDebugEnabled())
                    log.debug("-->" + key + " not exists in dal redis cache!");
                return null;
            }
        } catch (Exception e) {
            log.error("mybatis redis cache error", e);
            return null;
        } finally {
            jedisPool.returnResource(jedis);
        }
    }


    @Override
    public int getSize() {
        int count = 0;
        ShardedJedis jedis = null;
        try {
            getJedisPool();
            jedis = jedisPool.getResource();
            Collection<Jedis> jeds = jedis.getAllShards();
            for (Jedis jed : jeds) {
                count += jed.keys("*").size();
            }
        } catch (Exception e) {
            log.error("Dal redis cache get size error", e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return count;
    }

    @Override
    public void putObject(Object key, Object value) {
        byte[] tkey = SerializeUtil.serialize(key.toString());
        ShardedJedis jedis = null;
        try {
            getJedisPool();
            jedis = jedisPool.getResource();
            if (!jedis.exists(tkey)) {
                jedis.set(tkey, SerializeUtil.serialize(value));
            }
            if (log.isDebugEnabled())
                log.debug("-->" + key +  " store in dal redis cache success!");
        } catch (Exception e) {
            log.error("Dal redis cache store error", e);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    @Override
    public Object removeObject(Object key) {
        byte[] tkey = SerializeUtil.serialize(key.toString());
        ShardedJedis jedis = null;
        try {
            getJedisPool();
            jedis = jedisPool.getResource();
            if (jedis.exists(tkey)) {
                jedis.expire(tkey, 0);
            }
            if (log.isDebugEnabled())
                log.debug("-->" + key +  " remove from dal redis cache success!");
        } catch (Exception e) {
            log.error("Dal redis cache remove error", e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return null;
    }

    /**
     * @return the jedisPool
     */
    public ShardedJedisPool getJedisPool() {
        if (jedisPool == null) {
            jedisPool = (ShardedJedisPool) ContextLoader.getCurrentWebApplicationContext().getBean("shardedJedisPool");
        }
        return jedisPool;
    }


	@Override
	public void putObject(Object key, Object value, int seconds) {
		byte[] tkey = SerializeUtil.serialize(key.toString());
        ShardedJedis jedis = null;
        try {
            getJedisPool();
            jedis = jedisPool.getResource();
            if (!jedis.exists(tkey)) {
                jedis.set(tkey, SerializeUtil.serialize(value));
                jedis.expire(tkey, seconds);
            }
            log.debug("-->" + key +  " store in dal redis cache success!");
        } catch (Exception e) {
            log.error("Dal redis cache store error", e);
        } finally {
            jedisPool.returnResource(jedis);
        }
	}

	@Override
	public void clear(String id) {
		ShardedJedis jedis = null;
        try {
            getJedisPool();
            jedis = jedisPool.getResource();
            Collection<Jedis> jeds = jedis.getAllShards();
            String idPattern = "*" + id + "*";
            for (Jedis jed : jeds) {
                for (byte[] key : jed.keys(idPattern.getBytes())) {
                    jedis.expire(key, 0);
                }
            }
        } catch (Exception e) {
            log.error("mybatis redis cache error", e);
        } finally {
            jedisPool.returnResource(jedis);
        }
		
	}


	public void setJedisPool(ShardedJedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	

}
