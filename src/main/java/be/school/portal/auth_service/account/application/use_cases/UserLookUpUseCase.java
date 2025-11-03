package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.domain.entities.UserAccount;

public interface UserLookUpUseCase {

  boolean existsByRole(String name);

  UserAccount findByUsername(String username);
}
