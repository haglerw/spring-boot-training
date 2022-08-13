package com.caehag.autocase.repository;

import com.caehag.autocase.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findDepartmentByDepartmentName(String departmentName);
    Department findDepartmentByDepartmentId(String departmentId);
}
