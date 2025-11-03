package be.school.portal.auth_service.common.config;

import be.school.portal.auth_service.account.domain.projections.UserProjection;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Redis caching configuration for the application.
 *
 * <p>This configuration class enables Spring's caching abstraction and integrates Redis as the
 * cache provider. It customizes cache behavior, including serialization and TTL (time-to-live)
 * settings, for specific cache names. The configuration is extensible to support multiple cache
 * regions with distinct TTLs and serialization strategies.
 *
 * <p>Currently, this configuration sets up the {@code "users"} cache using a Jackson-based JSON
 * serializer for {@link be.school.portal.auth_service.account.domain.projections.UserProjection}.
 */
@Configuration
@EnableCaching
public class CachingConfig {

  /** Cache name used for caching user projection data. */
  public static final String USER_CACHE_NAME = "users";

  /**
   * TTL (time-to-live) for entries in the {@link #USER_CACHE_NAME} cache, in minutes.
   *
   * <p>The value is injected from the {@code application.properties} or {@code application.yml}
   * file using the property key {@code cache.ttl.users.minutes}. Defaults to 30 minutes if not
   * explicitly configured.
   */
  @Value("${cache.ttl.users.minutes:30}")
  private Integer usersCacheTtl;

  /**
   * Customizes the Redis cache manager builder to define cache-specific configurations.
   *
   * <p>This method registers a configuration for the {@code "users"} cache that uses a {@link
   * Jackson2JsonRedisSerializer} to serialize and deserialize {@link
   * be.school.portal.auth_service.account.domain.projections.UserProjection} objects. It can be
   * extended to support additional caches with different TTLs or serialization strategies.
   *
   * @return a {@link RedisCacheManagerBuilderCustomizer} that customizes cache configurations
   *     before the {@link org.springframework.data.redis.cache.RedisCacheManager} is built
   */
  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    return (builder) ->
        builder.withCacheConfiguration(
            USER_CACHE_NAME,
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(usersCacheTtl))
                .disableCachingNullValues()
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(UserProjection.class))));
  }
}
