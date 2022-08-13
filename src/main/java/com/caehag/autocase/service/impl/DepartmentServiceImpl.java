package com.caehag.autocase.service.impl;

import com.caehag.autocase.domain.Department;
import com.caehag.autocase.exception.domain.department.DepartmentExistException;
import com.caehag.autocase.exception.domain.department.DepartmentNotFoundException;
import com.caehag.autocase.mapstruct.dtos.department.DepartmentDto;
import com.caehag.autocase.mapstruct.mappers.DepartmentMapper;
import com.caehag.autocase.repository.DepartmentRepository;
import com.caehag.autocase.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.caehag.autocase.constant.AppConstant.*;
import static com.caehag.autocase.constant.DepartmentConstant.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.data.domain.PageRequest.of;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepo;
    private final DepartmentMapper departmentMapper;

    @Override
    public Map<String, Object> getDepartments(int page, int size) {
        List<Department> departments;
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = of(page, size);

        log.info("Fetching departments for page {} of size {}", page, size);
        Page<Department> pageDepartments = departmentRepo.findAll(pageable);
        departments = pageDepartments.getContent();
        response.put(DEPARTMENTS, departmentMapper.toDepartmentDtoList(departments));
        response.put(PAGE_NUMBER, pageDepartments.getNumber());
        response.put(TOTAL_ELEMENTS, pageDepartments.getTotalElements());
        response.put(TOTAL_PAGES, pageDepartments.getTotalPages());
        return response;
    }

    @Override
    public Department createDepartment(DepartmentDto departmentDto) throws DepartmentExistException, DepartmentNotFoundException {
        log.info("Validating new department: {}", departmentDto);
        validateDepartmentName(EMPTY, departmentDto.getDepartmentName());
        departmentDto.setDepartmentId(generateDepartmentId());
        log.info("Saving new department: {}", departmentDto);
        return departmentRepo.save(departmentMapper.toDepartment(departmentDto));
    }

    @Override
    public Department findDepartmentById(Long id) {
        log.info("Fetching department by id: {}", id);

        Optional<Department> optionalDepartment = departmentRepo.findById(id);
        Department departmentEntity = null;

        if (optionalDepartment.isPresent()) {
            departmentEntity = optionalDepartment.get();
        }

        return departmentEntity;
    }

    @Override
    public Department findDepartmentByName(String departmentName) {
        log.info("Fetching department by department name: {}", departmentName);
        return departmentRepo.findDepartmentByDepartmentName(departmentName);
    }

    @Override
    public Department findDepartmentByDepartmentId(String departmentId) {
        log.info("Fetching department by department id: {}", departmentId);
        return departmentRepo.findDepartmentByDepartmentId(departmentId);
    }

    @Override
    public Department updateDepartment(String departmentId, DepartmentDto departmentDto) throws DepartmentExistException, DepartmentNotFoundException {
        Department oldDepartment = findDepartmentByDepartmentId(departmentId);
        if (oldDepartment == null) throw new DepartmentNotFoundException(NO_DEPARTMENT_FOUND);
        log.info("Validating department to be updated: {}", departmentId);
        Department currentDepartment = validateDepartmentName(oldDepartment.getDepartmentName(), departmentDto.getDepartmentName());
        assert currentDepartment != null;
        currentDepartment.setDepartmentName(departmentDto.getDepartmentName());
        log.info("Saving updated department: {}", currentDepartment);
        departmentRepo.save(currentDepartment);
        return currentDepartment;
    }

    @Override
    public void deleteDepartment(String departmentId) throws DepartmentNotFoundException {
        Department department = findDepartmentByDepartmentId(departmentId);
        if (department == null) throw new DepartmentNotFoundException(NO_DEPARTMENT_FOUND);
        log.info("Deleting department by department id: {}", departmentId);
        departmentRepo.deleteById(department.getId());
    }

    private String generateDepartmentId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Department validateDepartmentName(String currentName, String newName) throws DepartmentNotFoundException, DepartmentExistException {
        Department newDepartment = findDepartmentByName(newName);

        if (StringUtils.isNotBlank(currentName)) {
            Department currentDepartment = findDepartmentByName(currentName);
            if (currentDepartment == null) {
                throw new DepartmentNotFoundException(NO_DEPARTMENT_FOUND_BY_NAME + currentName);
            }
            if (newDepartment != null && !currentDepartment.getId().equals(newDepartment.getId())) {
                throw new DepartmentExistException(DEPARTMENT_ALREADY_EXISTS);
            }
            return currentDepartment;
        } else {
            if (newDepartment != null) {
                throw new DepartmentExistException(DEPARTMENT_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
