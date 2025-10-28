package be.school.portal.auth_service.account.infrastructure.repositories;

import be.school.portal.auth_service.account.domain.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {}
