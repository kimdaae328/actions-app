package com.example.app.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {
//    Redis 캐시 설정
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // 캐시 유지 시간
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(configuration)
                .build();
    }

//    Redis 캐시 직접 조회
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
//        키는 문자열 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

//        값은 JSON 직렬화
        template.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));

        template.afterPropertiesSet();
        return template;
    }

//    Redis 캐시 유지 시간
    public SessionRepositoryCustomizer<RedisIndexedSessionRepository> customizeSessionTimeout() {
        return (repository) -> repository.setDefaultMaxInactiveInterval(Duration.ofMinutes(10));
    }
}
