package com.songpo.searched.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 刘松坡
 */
@Component
public class SmsPasswordCache extends BaseCache<String> {

    public SmsPasswordCache(RedisTemplate<String, String> redisTemplate) {
        super("com.songpo.seached:sms-pwd:", redisTemplate);
    }
}
