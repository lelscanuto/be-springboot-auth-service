package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.domain.entities.UserAccount;

public interface UserLockUseCase {
  UserAccount lockUser(String username);
}
