package be.school.portal.auth_service.account.application.use_cases;

import be.school.portal.auth_service.account.domain.projections.UserProjection;

public interface UserLockUseCase {
  UserProjection lockUser(String username);
}
