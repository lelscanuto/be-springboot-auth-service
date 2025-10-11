package be.school.portal.auth_service.infrastructure.repositories;

import be.school.portal.auth_service.domain.entities.UserLoginAudit;
import be.school.portal.auth_service.domain.enums.LoginAction;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginAuditRepository extends JpaRepository<UserLoginAudit, Long> {
  

  List<UserLoginAudit> findByUsernameAndActionInAndAttemptedAtBetweenOrderByAttemptedAtAsc(
      String username,
      List<LoginAction> relevantActions,
      ZonedDateTime from,
      ZonedDateTime to,
      PageRequest of);
}
