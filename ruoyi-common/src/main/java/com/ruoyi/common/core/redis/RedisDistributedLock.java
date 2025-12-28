package com.ruoyi.common.core.redis;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * Redis分布式锁工具类
 * 
 * @author ruoyi
 * @date 2024-12-XX
 */
@Component
public class RedisDistributedLock {
    
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_LOCK_TIMEOUT = 30; // 默认锁超时时间（秒）
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";
    
    /**
     * 尝试获取分布式锁
     * 
     * @param key 锁的键
     * @param timeout 锁的超时时间（秒）
     * @return 锁的值（用于释放锁），如果获取失败返回null
     */
    public String tryLock(String key, long timeout) {
        return tryLock(key, timeout, TimeUnit.SECONDS);
    }
    
    /**
     * 尝试获取分布式锁
     * 
     * @param key 锁的键
     * @param timeout 锁的超时时间
     * @param timeUnit 时间单位
     * @return 锁的值（用于释放锁），如果获取失败返回null
     */
    public String tryLock(String key, long timeout, TimeUnit timeUnit) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        
        // 使用SET NX EX命令原子性地设置锁（Redis 2.6.12+）
        @SuppressWarnings("unchecked")
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
            lockKey, 
            lockValue, 
            timeout, 
            timeUnit
        );
        
        if (Boolean.TRUE.equals(success)) {
            return lockValue;
        }
        return null;
    }
    
    /**
     * 尝试获取分布式锁（使用默认超时时间）
     * 
     * @param key 锁的键
     * @return 锁的值（用于释放锁），如果获取失败返回null
     */
    public String tryLock(String key) {
        return tryLock(key, DEFAULT_LOCK_TIMEOUT);
    }
    
    /**
     * 尝试获取分布式锁（带重试机制）
     * 
     * @param key 锁的键
     * @param timeout 锁的超时时间（秒）
     * @param waitTime 等待时间（毫秒）
     * @param retryTimes 重试次数
     * @return 锁的值（用于释放锁），如果获取失败返回null
     */
    public String tryLockWithRetry(String key, long timeout, long waitTime, int retryTimes) {
        String lockValue = null;
        int attempts = 0;
        
        while (attempts < retryTimes && lockValue == null) {
            lockValue = tryLock(key, timeout);
            if (lockValue == null && attempts < retryTimes - 1) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            attempts++;
        }
        
        return lockValue;
    }
    
    /**
     * 释放分布式锁
     * 使用Lua脚本保证释放锁的原子性，防止误删其他线程的锁
     * 
     * @param key 锁的键
     * @param lockValue 锁的值（获取锁时返回的值）
     * @return true=释放成功，false=释放失败（锁已过期或被其他线程持有）
     */
    public boolean releaseLock(String key, String lockValue) {
        if (key == null || lockValue == null) {
            return false;
        }
        
        String lockKey = LOCK_PREFIX + key;
        
        // 使用Lua脚本保证原子性：只有锁的值匹配时才删除
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(UNLOCK_SCRIPT);
        script.setResultType(Long.class);
        
        @SuppressWarnings("unchecked")
        Long result = redisTemplate.execute(script, 
            java.util.Collections.singletonList(lockKey), 
            lockValue
        );
        
        return result != null && result > 0;
    }
    
    /**
     * 检查锁是否存在
     * 
     * @param key 锁的键
     * @return true=锁存在，false=锁不存在
     */
    public boolean isLocked(String key) {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }
    
    /**
     * 延长锁的过期时间（锁续期）
     * 
     * @param key 锁的键
     * @param lockValue 锁的值
     * @param timeout 新的超时时间（秒）
     * @return true=续期成功，false=续期失败（锁已过期或被其他线程持有）
     */
    public boolean renewLock(String key, String lockValue, long timeout) {
        if (key == null || lockValue == null) {
            return false;
        }
        
        String lockKey = LOCK_PREFIX + key;
        
        // 使用Lua脚本保证原子性：只有锁的值匹配时才续期
        String renewScript = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('expire', KEYS[1], ARGV[2]) " +
            "else " +
            "    return 0 " +
            "end";
        
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(renewScript);
        script.setResultType(Long.class);
        
        @SuppressWarnings("unchecked")
        Long result = redisTemplate.execute(script, 
            java.util.Collections.singletonList(lockKey), 
            lockValue, 
            String.valueOf(timeout)
        );
        
        return result != null && result > 0;
    }
}

