package com.sugarcanelabour.config;


import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;


@Configuration
@EnableCaching
public class RedisConfig {


	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		// Create the default cache configuration
		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(new GenericJackson2JsonRedisSerializer()))
				.entryTtl(Duration.ofDays(30)); // Set TTL of 5 minutes for cache entries

		return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfig) // Apply cache configuration
				.build();
	}

=======
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password:}") // Default to empty string if not set
    private String redisPassword;

    /**
     * Production Redis Configuration
     */
    @Bean
    @Profile("prod")
    public RedisConnectionFactory redisConnectionFactoryProd() {
        return createLettuceConnectionFactory("prod");
    }

    /**
     * Development Redis Configuration
     */
    @Bean
    @Profile("dev")
    public RedisConnectionFactory redisConnectionFactoryDev() {
        return createLettuceConnectionFactory("dev");
    }

    /**
     * Local Redis Configuration
     */
    @Bean
    @Profile("local")
    public RedisConnectionFactory redisConnectionFactoryLocal() {
        return createLettuceConnectionFactory("local");
    }

    /**
     * Creates a Redis connection factory with environment-specific settings.
     */
    private RedisConnectionFactory createLettuceConnectionFactory(String env) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        if (!redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }

        // Create Lettuce connection factory with appropriate pool configuration
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig);
        lettuceConnectionFactory.setShareNativeConnection(false);
        lettuceConnectionFactory.setClientResources(createLettuceClientResources(createLettucePoolConfig(env)));

        return lettuceConnectionFactory;
    }

    /**
     * Creates a GenericObjectPoolConfig based on the environment.
     */
    private GenericObjectPoolConfig<?> createLettucePoolConfig(String env) {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        if ("prod".equals(env)) {
            poolConfig.setMaxTotal(200);
            poolConfig.setMaxIdle(100);
            poolConfig.setMinIdle(50);
        } else {
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(10);
            poolConfig.setMinIdle(5);
        }
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        return poolConfig;
    }

    /**
     * Creates ClientResources for Lettuce with proper resource management.
     */
    private ClientResources createLettuceClientResources(GenericObjectPoolConfig<?> poolConfig) {
        return DefaultClientResources.create();
    }

    /**
     * Configures RedisTemplate with JSON serialization.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);

        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }
>>>>>>> userA
}
