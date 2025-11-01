package be.school.portal.auth_service.permission.infrastructure.repositories;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository
    extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
  boolean existsByName(String permissionName);

  Page<Permission> findAllByIdIn(Set<Long> ids, Pageable pageable);
}
