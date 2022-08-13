package com.caehag.autocase.repository;

import com.caehag.autocase.domain.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findPrivilegeByPrivilegeId(String privilegeId);
    Privilege findPrivilegeByName(String privilegeName);
}
