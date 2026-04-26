package com.karan.ecommerce_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        // NOTE: Old ObjectMapper and Jackson2JsonRedisSerializer is deprecated!
        RedisSerializer<Object> serializer = RedisSerializer.json();
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig().serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)).entryTtl(Duration.ofMinutes(10));

        // whatever namespace has more possibility of changing in the DB must be given less TTL for consistency
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("userById", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigs.put("userByEmail", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigs.put("userList", defaultConfig.entryTtl(Duration.ofMinutes(2)));
        cacheConfigs.put("userImages", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        // ttl config for the product related namespaces
        cacheConfigs.put("productById", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put("productSearch", defaultConfig.entryTtl(Duration.ofMinutes(2)));
        cacheConfigs.put("productImageById", defaultConfig.entryTtl(Duration.ofDays(1)));
        //NOTE: since freshness is protected by Eviction, we can go for longer TTL even when user changes the cart frequently!l
        cacheConfigs.put("cartByUserId", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        return RedisCacheManager.builder(factory).cacheDefaults(defaultConfig).withInitialCacheConfigurations(cacheConfigs).build();


    }
}
