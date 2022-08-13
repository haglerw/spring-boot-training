package com.caehag.autocase.mapstruct.dtos.user;

import com.caehag.autocase.mapstruct.dtos.department.DepartmentDto;
import com.caehag.autocase.mapstruct.dtos.role.RoleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserGetDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private DepartmentDto departmentDto;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private RoleDto roleDto;
    private Boolean isActive;
    private Boolean isNotLocked;
}
