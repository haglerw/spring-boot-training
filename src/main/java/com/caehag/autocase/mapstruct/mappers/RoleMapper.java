package com.caehag.autocase.mapstruct.mappers;

import com.caehag.autocase.domain.Role;
import com.caehag.autocase.mapstruct.dtos.role.RoleDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleDto roleDto);

    RoleDto toRoleDto(Role role);

    List<RoleDto> toRoleDtoList(List<Role> roles);
}
