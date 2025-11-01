package be.school.portal.auth_service.role.infrastructure.specifications;

import be.school.portal.auth_service.role.domain.entities.Role;
import jakarta.annotation.Nullable;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecificationFactory {

  private static final String ROLE_NAME_FIELD = "name";
  private static final String IS_DELETED_FIELD = "isDeleted";

  private RoleSpecificationFactory() {
    // Prevent instantiation
  }

  public static Specification<Role> byName(@Nullable String name) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(name)) {
        return cb.conjunction();
      }

      return cb.equal(root.get(ROLE_NAME_FIELD), name);
    };
  }

  public static Specification<Role> byStatus(@Nullable Boolean isDeleted) {
    return (root, query, cb) -> {
      if (Objects.isNull(isDeleted)) {
        return cb.conjunction();
      }

      return cb.equal(root.get(IS_DELETED_FIELD), isDeleted);
    };
  }
}
