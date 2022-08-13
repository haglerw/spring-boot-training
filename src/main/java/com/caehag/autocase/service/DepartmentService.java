package com.caehag.autocase.service;

import com.caehag.autocase.domain.Department;
import com.caehag.autocase.exception.domain.department.DepartmentExistException;
import com.caehag.autocase.exception.domain.department.DepartmentNotFoundException;
import com.caehag.autocase.mapstruct.dtos.department.DepartmentDto;

import java.util.Map;

public interface DepartmentService {
    Map<String, Object> getDepartments(int page, int size);

    Department createDepartment(DepartmentDto departmentDto) throws DepartmentExistException, DepartmentNotFoundException;

    Department findDepartmentById(Long id);

    Department findDepartmentByName(String departmentName);

    Department findDepartmentByDepartmentId(String departmentId);

    Department updateDepartment(String departmentId, DepartmentDto departmentDto) throws DepartmentExistException, DepartmentNotFoundException;

    void deleteDepartment(String departmentId) throws DepartmentNotFoundException;
}
