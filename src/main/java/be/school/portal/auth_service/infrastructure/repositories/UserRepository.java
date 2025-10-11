package be.school.portal.auth_service.infrastructure.repositories;

import be.school.portal.auth_service.domain.entities.UserAccount;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByUsername(@Nonnull String username);
}
