package com.caehag.autocase.mapstruct.dtos.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class UserPostDto {
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    @NotNull(message = "Username is required")
    private String username;
    @Email
    @NotNull(message = "Email is required")
    private String email;
    private String password;
    private String phone;
    @NotNull(message = "Department id is required")
    private Long departmentId;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    @NotNull(message = "Role id is required")
    private Long roleId;
    private Boolean isActive;
    private Boolean isNotLocked;
}
