package com.caehag.autocase.resource;

import com.caehag.autocase.domain.Response;
import com.caehag.autocase.domain.Role;
import com.caehag.autocase.exception.domain.ExceptionHandling;
import com.caehag.autocase.exception.domain.privilege.PrivilegeNotFoundException;
import com.caehag.autocase.exception.domain.role.RoleCodeExistException;
import com.caehag.autocase.exception.domain.role.RoleNotFoundException;
import com.caehag.autocase.mapstruct.dtos.privilege.PrivilegeDto;
import com.caehag.autocase.mapstruct.mappers.RoleMapper;
import com.caehag.autocase.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_NUMBER;
import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_SIZE;
import static com.caehag.autocase.constant.PrivilegeConstant.PRIVILEGE_ADDED_SUCCESSFULLY;
import static com.caehag.autocase.constant.RoleConstant.*;
import static com.caehag.autocase.utility.Utility.returnOkResponse;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/", "/roles"})
@RequiredArgsConstructor
public class RoleResource extends ExceptionHandling {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('create:role')")
    ResponseEntity<Response> createRole(@Valid @RequestBody Role role) throws RoleCodeExistException, RoleNotFoundException {
        Role newRole = roleService.createRole(role);
        return returnOkResponse(OK, ROLE_CREATED_SUCCESSFULLY, roleMapper.toRoleDto(newRole));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('read:role')")
    ResponseEntity<Response> getAllRoles(@RequestParam Optional<Integer> page,
                                         @RequestParam Optional<Integer> size) {
        Map<String, Object> roles = roleService.getRoles(page.orElse(DEFAULT_PAGE_NUMBER), size.orElse(DEFAULT_PAGE_SIZE));
        return returnOkResponse(OK, ROLES_FETCHED_SUCCESSFULLY, roles);
    }

    @GetMapping("/find/{roleId}")
    @PreAuthorize("hasAnyAuthority('read:role')")
    ResponseEntity<Response> getRoleById(@PathVariable("roleId") String roleId) throws RoleNotFoundException {
        Role role = roleService.findRoleByRoleId(roleId);
        if (role == null) throw new RoleNotFoundException(NO_ROLE_FOUND);
        return returnOkResponse(OK, ROLE_FETCHED_SUCCESSFULLY, roleMapper.toRoleDto(role));
    }

    @PutMapping("/update/{roleId}")
    @PreAuthorize("hasAnyAuthority('update:role')")
    ResponseEntity<Response> updateGroup(@PathVariable("roleId") String roleId, @Valid @RequestBody Role newRole) throws RoleCodeExistException, RoleNotFoundException {
        Role updateRole = roleService.updateRole(roleId, newRole);
        return returnOkResponse(OK, ROLE_UPDATED_SUCCESSFULLY, roleMapper.toRoleDto(updateRole));
    }

    @DeleteMapping("/delete/{roleId}")
    @PreAuthorize("hasAnyAuthority('delete:role')")
    ResponseEntity<Response> deleteRole(@PathVariable("roleId") String roleId) throws RoleNotFoundException {
        roleService.deleteRole(roleId);
        return returnOkResponse(OK, ROLE_DELETED_SUCCESSFULLY, null);
    }

    @PostMapping("/add-privilege/{roleId}")
    @PreAuthorize("hasAnyAuthority('update:role', 'update: privilege')")
    ResponseEntity<Response> addPrivilege(@PathVariable("roleId") String roleId, @RequestBody PrivilegeDto privilegeDto) throws PrivilegeNotFoundException, RoleNotFoundException {
        roleService.addPrivilege(roleId, privilegeDto.getPrivilegeId());
        return returnOkResponse(OK, PRIVILEGE_ADDED_SUCCESSFULLY, null);
    }
}
