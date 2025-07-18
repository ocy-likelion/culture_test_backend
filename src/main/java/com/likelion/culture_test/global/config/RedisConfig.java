package com.likelion.culture_test.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

//  @Bean
//  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//        .entryTtl(Duration.ofHours(1))
//        .serializeKeysWith(RedisSerializationContext.SerializationPair
//            .fromSerializer(new StringRedisSerializer()))
//        .serializeValuesWith(RedisSerializationContext.SerializationPair
//            .fromSerializer(new GenericJackson2JsonRedisSerializer()));
//
//    return RedisCacheManager.builder(connectionFactory)
//        .cacheDefaults(config)
//        .build();
//  }
}
