package be.school.portal.auth_service.role.infrastructure.persistence.repositories;

import be.school.portal.auth_service.role.domain.entities.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

  boolean existsByName(String name);

  List<Role> findAllByIsDeletedFalseAndPermissions_Id(Long permissionId);
}
