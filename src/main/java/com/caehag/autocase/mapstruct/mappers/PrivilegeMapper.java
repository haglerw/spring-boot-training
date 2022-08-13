package com.caehag.autocase.mapstruct.mappers;

import com.caehag.autocase.domain.Privilege;
import com.caehag.autocase.mapstruct.dtos.privilege.PrivilegeDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PrivilegeMapper {
    Privilege toPrivilege(PrivilegeDto privilegeDto);

    PrivilegeDto toPrivilegeDto(Privilege privilege);

    List<PrivilegeDto> toPrivilegeDtoList(List<Privilege> privileges);
}
