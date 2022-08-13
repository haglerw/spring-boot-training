package com.caehag.autocase.mapstruct.mappers;

import com.caehag.autocase.domain.Department;
import com.caehag.autocase.mapstruct.dtos.department.DepartmentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toDepartment(DepartmentDto departmentDto);

    DepartmentDto toDepartmentDto(Department department);

    List<Department> toDepartmentList(List<DepartmentDto> departmentDtoList);

    List<DepartmentDto> toDepartmentDtoList(List<Department> departments);
}
