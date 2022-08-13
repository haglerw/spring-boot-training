package com.caehag.autocase.mapstruct.mappers;

import com.caehag.autocase.domain.User;
import com.caehag.autocase.mapstruct.dtos.user.UserGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "departmentDto", target = "department")
    @Mapping(source = "roleDto", target = "role")
    User toUser(UserGetDto userDto);

    @Mapping(source = "department", target = "departmentDto")
    @Mapping(source = "role", target = "roleDto")
    UserGetDto toUserGetDto(User user);

    List<UserGetDto> toUserGetDtoList(List<User> users);
}
