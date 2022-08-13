package com.caehag.autocase.mapstruct.dtos.role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class RoleDto {
    private String roleId;
    @NotNull
    private String code;
    @NotNull
    private String name;
}
