package com.caehag.autocase.resource;

import com.caehag.autocase.domain.Department;
import com.caehag.autocase.domain.Response;
import com.caehag.autocase.exception.domain.ExceptionHandling;
import com.caehag.autocase.exception.domain.department.DepartmentExistException;
import com.caehag.autocase.exception.domain.department.DepartmentNotFoundException;
import com.caehag.autocase.mapstruct.dtos.department.DepartmentDto;
import com.caehag.autocase.mapstruct.mappers.DepartmentMapper;
import com.caehag.autocase.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_NUMBER;
import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_SIZE;
import static com.caehag.autocase.constant.DepartmentConstant.*;
import static com.caehag.autocase.utility.Utility.returnOkResponse;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = { "/", "/departments" })
public class DepartmentResource extends ExceptionHandling {
    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('create:department')")
    ResponseEntity<Response> createDepartment(@RequestBody DepartmentDto department) throws DepartmentExistException, DepartmentNotFoundException {
        Department savedDepartment = departmentService.createDepartment(department);
        return returnOkResponse(OK, DEPARTMENT_CREATED_SUCCESSFULLY, departmentMapper.toDepartmentDto(savedDepartment));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('read:department')")
    ResponseEntity<Response> getAllDepartments(@RequestParam Optional<Integer> page,
                                               @RequestParam Optional<Integer> size) {
        Map<String, Object> departments = departmentService.getDepartments(page.orElse(DEFAULT_PAGE_NUMBER), size.orElse(DEFAULT_PAGE_SIZE));
        return returnOkResponse(OK, DEPARTMENTS_FETCHED_SUCCESSFULLY, departments);
    }

    @GetMapping("/find/{departmentId}")
    ResponseEntity<Response> getDepartmentById(@PathVariable("departmentId") String departmentId) throws DepartmentNotFoundException {
        Department department = departmentService.findDepartmentByDepartmentId(departmentId);
        if (department == null) throw new DepartmentNotFoundException(NO_DEPARTMENT_FOUND);
        return returnOkResponse(OK, DEPARTMENT_FETCHED_SUCCESSFULLY, departmentMapper.toDepartmentDto(department));
    }

    @PutMapping("/update/{departmentId}")
    ResponseEntity<Response> updateDepartment(@RequestBody DepartmentDto departmentDto, @PathVariable("departmentId") String departmentId) throws DepartmentExistException, DepartmentNotFoundException {
        Department updatedDepartment = departmentService.updateDepartment(departmentId, departmentDto);
        return returnOkResponse(OK, DEPARTMENT_UPDATED_SUCCESSFULLY, departmentMapper.toDepartmentDto(updatedDepartment));
    }

    @DeleteMapping("/delete/{departmentName}")
    ResponseEntity<Response> deleteDepartment(@PathVariable("departmentName") String departmentName) throws DepartmentNotFoundException {
        departmentService.deleteDepartment(departmentName);
        return returnOkResponse(OK, DEPARTMENT_DELETED_SUCCESSFULLY, null);
    }
}
