package be.school.portal.auth_service.permission.infrastructure.repositories;

import be.school.portal.auth_service.permission.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {}
