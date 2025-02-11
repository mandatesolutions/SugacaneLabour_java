package com.sugarcanelabour.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

@Configuration
@EnableCaching
public class RedisConfig {
	@Value("${spring.redis.host}")
	String redisHost;
	@Value("${spring.redis.port}")
	int redisPort;
	@Value("${spring.redis.password}")
	String redisPassword;

	@Bean
	@Profile("prod")
	public RedisConnectionFactory redisConnectionFactoryProd() {
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName(redisHost);
		redisConfig.setPort(redisPort);
		redisConfig.setPassword(redisPassword);

		// Create the pooling configuration for Lettuce
		GenericObjectPoolConfig<?> poolConfig = createLettucePoolConfig("prod");

		// Create a Lettuce connection factory with the pooling configuration
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig);

		// Enable non-blocking connections
		lettuceConnectionFactory.setShareNativeConnection(false);

		// Configure connection pooling
		lettuceConnectionFactory.setClientResources(createLettuceClientResources(poolConfig));

		return lettuceConnectionFactory;
	}

	private GenericObjectPoolConfig<?> createLettucePoolConfig(String env) {
		if (env.equals("prod")) {
			GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
			poolConfig.setMaxTotal(200); // Increase the connection limit based on load testing
			poolConfig.setMaxIdle(100); // Max number of idle connections
			poolConfig.setMinIdle(50); // Min number of idle connections
			poolConfig.setTestOnBorrow(true); // Enable connection validation
			poolConfig.setTestOnReturn(true);
			poolConfig.setTestWhileIdle(true);
			poolConfig.setTimeBetweenEvictionRunsMillis(30000); // Connection eviction
			return poolConfig;
		} else {
			GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
			poolConfig.setMaxTotal(10); // Increase the connection limit based on load testing
			poolConfig.setMaxIdle(10); // Max number of idle connections
			poolConfig.setMinIdle(10); // Min number of idle connections
			poolConfig.setTestOnBorrow(true); // Enable connection validation
			poolConfig.setTestOnReturn(true);
			poolConfig.setTestWhileIdle(true);
			poolConfig.setTimeBetweenEvictionRunsMillis(30000); // Connection eviction
			return poolConfig;
		}
	}

	private ClientResources createLettuceClientResources(GenericObjectPoolConfig<?> poolConfig) {
		// Create a DefaultClientResources object with the pool configuration
		return DefaultClientResources.create();
	}

	@Bean
	@Profile("dev")
	public RedisConnectionFactory redisConnectionFactoryDev() {
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName(redisHost);
		redisConfig.setPort(redisPort);
		redisConfig.setPassword(redisPassword);

		// Create the pooling configuration for Lettuce
		GenericObjectPoolConfig<?> poolConfig = createLettucePoolConfig("dev");

		// Create a Lettuce connection factory with the pooling configuration
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig);

		// Enable non-blocking connections
		lettuceConnectionFactory.setShareNativeConnection(false);

		// Configure connection pooling
		lettuceConnectionFactory.setClientResources(createLettuceClientResources(poolConfig));

		return lettuceConnectionFactory;
	}

	@Bean
	@Profile("local")
	public RedisConnectionFactory redisConnectionFactoryLocal() {
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName(redisHost);
		redisConfig.setPort(redisPort);

		// Create the pooling configuration for Lettuce
		GenericObjectPoolConfig<?> poolConfig = createLettucePoolConfig("local");

		// Create a Lettuce connection factory with the pooling configuration
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig);

		// Enable non-blocking connections
		lettuceConnectionFactory.setShareNativeConnection(false);

		// Configure connection pooling
		lettuceConnectionFactory.setClientResources(createLettuceClientResources(poolConfig));

		return lettuceConnectionFactory;
	}

	@Bean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// Configure the Jackson JSON Redis serializer
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

		// Create ObjectMapper and register JavaTimeModule for LocalDateTime handling
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// Use this ObjectMapper for serialization and deserialization
		GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		// Set the serializer to RedisTemplate
		template.setDefaultSerializer(jsonRedisSerializer);
		return template;
	}
}
