package be.school.portal.auth_service.account.infrastructure.caching;

import be.school.portal.auth_service.account.application.mappers.UserProjectionMapper;
import be.school.portal.auth_service.account.application.port.UserCachingPort;
import be.school.portal.auth_service.account.application.port.UserRepositoryPort;
import be.school.portal.auth_service.account.domain.entities.UserAccount;
import be.school.portal.auth_service.account.domain.projections.UserProjection;
import be.school.portal.auth_service.common.config.CachingConfig;
import jakarta.annotation.Nonnull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Infrastructure-layer implementation of {@link UserCachingPort} providing caching support for
 * {@link UserAccount} lookups.
 *
 * <p>This adapter leverages Spring Cache annotations to manage cache entries in Redis. It ensures
 * high-performance retrieval of user projections while delegating cache misses to the underlying
 * {@link UserRepositoryPort}.
 *
 * <p><strong>Responsibilities:</strong>
 *
 * <ul>
 *   <li>Retrieve user projections from cache or database on cache miss.
 *   <li>Evict stale or invalid cache entries after user updates or logout events.
 *   <li>Update or insert user entries in the cache after changes to ensure consistency.
 * </ul>
 *
 * <p>All caching keys are based on the username, and the cache name is defined in {@link
 * CachingConfig#USER_CACHE_NAME}.
 *
 * @see UserCachingPort
 * @see UserRepositoryPort
 * @see UserProjectionMapper
 * @author Francis Jorell J. Canuto
 */
@Service
public class UserCachingRedisAdapter implements UserCachingPort {

  private final UserRepositoryPort userRepositoryPort;
  private final UserProjectionMapper userProjectionMapper;

  /**
   * Constructs a new {@link UserCachingRedisAdapter}.
   *
   * @param userRepositoryPort the repository port for fetching user entities from the database
   * @param userProjectionMapper the mapper converting {@link UserAccount} entities to {@link
   *     UserProjection} DTOs
   */
  public UserCachingRedisAdapter(
      UserRepositoryPort userRepositoryPort, UserProjectionMapper userProjectionMapper) {
    this.userRepositoryPort = userRepositoryPort;
    this.userProjectionMapper = userProjectionMapper;
  }

  /**
   * Retrieves a {@link UserProjection} for the given username.
   *
   * <p>If the user exists in the cache, the cached value is returned. Otherwise, the repository is
   * queried and the result is stored in the cache. Returns {@code null} if no user is found;
   * missing user handling should occur at the application layer.
   *
   * @param username the username of the user to retrieve
   * @return the {@link UserProjection} associated with the username, or {@code null} if not found
   */
  @Override
  @Cacheable(value = CachingConfig.USER_CACHE_NAME, key = "#username")
  public UserProjection findByUsername(@Nonnull String username) {
    final var existingUser = userRepositoryPort.findByUsername(username).orElse(null);
    return userProjectionMapper.toUserLiteDTO(existingUser);
  }

  /**
   * Evicts the cached {@link UserProjection} entry for a specific username.
   *
   * <p>Subsequent requests for this user will trigger a database fetch and cache population.
   *
   * @param username the username whose cache entry should be evicted
   */
  @Override
  @CacheEvict(value = CachingConfig.USER_CACHE_NAME, key = "#username")
  public void evictUserCache(@Nonnull String username) {
    // Eviction handled by Spring Cache
  }

  /**
   * Updates or inserts the {@link UserProjection} cache entry for the given user.
   *
   * <p>After database changes to a user, this method ensures that the cached representation is
   * consistent with the persisted state.
   *
   * @param user the {@link UserAccount} entity whose projection will be cached
   * @return the cached {@link UserProjection} reflecting the current user state
   */
  @Override
  @CachePut(value = CachingConfig.USER_CACHE_NAME, key = "#user.username")
  public UserProjection updateCache(@Nonnull UserAccount user) {
    return userProjectionMapper.toUserLiteDTO(user);
  }
}
