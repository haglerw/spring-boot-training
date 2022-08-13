package com.caehag.autocase.service;

import com.caehag.autocase.domain.Role;
import com.caehag.autocase.exception.domain.privilege.PrivilegeNotFoundException;
import com.caehag.autocase.exception.domain.role.RoleCodeExistException;
import com.caehag.autocase.exception.domain.role.RoleNotFoundException;

import java.util.Map;

public interface RoleService {

    // Save operation
    Role createRole(Role role) throws RoleCodeExistException, RoleNotFoundException;

    // Read operations
    Map<String, Object> getRoles(int page, int size);

    Role findRoleByCode(String code) throws RoleNotFoundException;

    Role findRoleByRoleId(String roleId) throws RoleNotFoundException;

    Role findRoleById(Long id);

    // Update operation
    Role updateRole(String roleId, Role newRole) throws RoleCodeExistException, RoleNotFoundException;

    // Delete operation
    void deleteRole(String roleId) throws RoleNotFoundException;

    void addPrivilege(String roleId, String privilegeId) throws RoleNotFoundException, PrivilegeNotFoundException;
}
