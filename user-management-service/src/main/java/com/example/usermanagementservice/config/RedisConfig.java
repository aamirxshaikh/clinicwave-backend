package com.example.usermanagementservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Bean
  public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
    redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }
}
