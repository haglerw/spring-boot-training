package com.caehag.autocase.repository;

import com.caehag.autocase.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByCode(String code);
    Role findRoleByRoleId(String roleId);
}
