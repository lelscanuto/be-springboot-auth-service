package be.school.portal.auth_service.account.application.use_cases;

public interface UserLookUpUseCase {

  boolean existsByRole(String name);
}
