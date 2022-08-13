package com.caehag.autocase.service.impl;

import com.caehag.autocase.domain.Privilege;
import com.caehag.autocase.domain.Role;
import com.caehag.autocase.exception.domain.privilege.PrivilegeNotFoundException;
import com.caehag.autocase.exception.domain.role.RoleCodeExistException;
import com.caehag.autocase.exception.domain.role.RoleNotFoundException;
import com.caehag.autocase.mapstruct.mappers.RoleMapper;
import com.caehag.autocase.repository.RoleRepository;
import com.caehag.autocase.service.PrivilegeService;
import com.caehag.autocase.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.caehag.autocase.constant.AppConstant.*;
import static com.caehag.autocase.constant.PrivilegeConstant.NO_PRIVILEGE_FOUND;
import static com.caehag.autocase.constant.RoleConstant.*;
import static com.caehag.autocase.utility.Utility.generateRandomId;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PrivilegeService privilegeService;

    @Override
    public Role createRole(Role role) throws RoleCodeExistException, RoleNotFoundException {
        log.info("Validating new role: {}", role.getCode());
        validateNewCode(EMPTY, role.getCode());
        role.setRoleId(generateRandomId());
        log.info("Saving new role: {}", role);
        return roleRepository.save(role);
    }

    @Override
    public Map<String, Object> getRoles(int page, int size) {
        List<Role> roles;
        Pageable pageable = PageRequest.of(page, size);

        log.info("Fetching roles for page {} of size {}", page, size);
        Page<Role> pageRoles = roleRepository.findAll(pageable);
        roles = pageRoles.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put(ROLE, roleMapper.toRoleDtoList(roles));
        response.put(PAGE_NUMBER, pageRoles.getNumber());
        response.put(TOTAL_ELEMENTS, pageRoles.getTotalElements());
        response.put(TOTAL_PAGES, pageRoles.getTotalPages());
        return response;
    }

    @Override
    public Role findRoleByCode(String code) {
        log.info("Fetching role by code name: {}", code);
        return roleRepository.findRoleByCode(code);
    }

    @Override
    public Role findRoleByRoleId(String roleId) {
        log.info("Fetching role by roleId: {}", roleId);
        return roleRepository.findRoleByRoleId(roleId);
    }

    @Override
    public Role findRoleById(Long id) {
        log.info("Fetching role by id: {}", id);
        Optional<Role> optionalRole = roleRepository.findById(id);
        Role role = null;

        if (optionalRole.isPresent()) {
            role = optionalRole.get();
        }

        return role;
    }

    @Override
    public Role updateRole(String roleId, Role newRole) throws RoleCodeExistException, RoleNotFoundException {
        Role oldRole = findRoleByRoleId(roleId);
        if (oldRole == null) throw new RoleNotFoundException(NO_ROLE_FOUND);
        log.info("Validating new role");
        Role currentRole = validateNewCode(oldRole.getCode(), newRole.getCode());
        assert currentRole != null;
        currentRole.setCode(newRole.getCode());
        currentRole.setName(newRole.getName());
        roleRepository.save(currentRole);
        return currentRole;
    }

    @Override
    public void deleteRole(String roleId) throws RoleNotFoundException {
        Role role = findRoleByRoleId(roleId);
        if (role == null) throw new RoleNotFoundException(NO_ROLE_FOUND);
        log.info("Deleting role by roleId");
        roleRepository.deleteById(role.getId());
    }

    @Override
    public void addPrivilege(String roleId, String privilegeId) throws RoleNotFoundException, PrivilegeNotFoundException {
        Role role = findRoleByRoleId(roleId);
        if (role == null) throw new RoleNotFoundException(NO_ROLE_FOUND);
        Privilege privilege = privilegeService.findPrivilegeByPrivilegeId(privilegeId);
        if (privilege == null) throw new PrivilegeNotFoundException(NO_PRIVILEGE_FOUND);
        log.info("Adding privilege {} to role {}", privilege.getName(), role.getName());
        role.addPrivilege(privilege);
        roleRepository.save(role);
    }

    private Role validateNewCode(String currentCode, String newCode) throws RoleNotFoundException, RoleCodeExistException {
        Role newRole = findRoleByCode(newCode);

        if (StringUtils.isNotBlank(currentCode)) {
            Role currentRole = findRoleByCode(currentCode);
            if (currentRole == null) {
                throw new RoleNotFoundException(NO_ROLE_FOUND_BY_CODE + currentCode);
            }
            if (newRole != null && !currentRole.getId().equals(newRole.getId())) {
                throw new RoleCodeExistException(ROLE_ALREADY_EXISTS);
            }
            return currentRole;
        } else {
            if (newRole != null) {
                throw new RoleCodeExistException(ROLE_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
