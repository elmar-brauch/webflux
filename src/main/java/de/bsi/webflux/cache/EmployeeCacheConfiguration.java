package de.bsi.webflux.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
// TODO Activate Cache for application by annotation 
public class EmployeeCacheConfiguration {
	
	// TODO Create RedisCacheConfiguration bean
	// 		with default config, TTL and Jackson serialization
	
	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(1))
				.serializeValuesWith(SerializationPair.fromSerializer(
						new GenericJackson2JsonRedisSerializer()));
	}
}
