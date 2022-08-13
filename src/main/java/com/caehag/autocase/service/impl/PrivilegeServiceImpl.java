package com.caehag.autocase.service.impl;

import com.caehag.autocase.domain.Privilege;
import com.caehag.autocase.exception.domain.privilege.PrivilegeExistException;
import com.caehag.autocase.exception.domain.privilege.PrivilegeNotFoundException;
import com.caehag.autocase.mapstruct.mappers.PrivilegeMapper;
import com.caehag.autocase.repository.PrivilegeRepository;
import com.caehag.autocase.service.PrivilegeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.caehag.autocase.constant.AppConstant.*;
import static com.caehag.autocase.constant.PrivilegeConstant.*;
import static com.caehag.autocase.utility.Utility.generateRandomId;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PrivilegeServiceImpl implements PrivilegeService {
    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeMapper privilegeMapper;

    @Override
    public Privilege createPrivilege(Privilege privilege) throws PrivilegeExistException, PrivilegeNotFoundException {
        log.info("Validating new privilege: {}", privilege.getName());
        validateNewPrivilegeName(StringUtils.EMPTY, privilege.getName());
        privilege.setPrivilegeId(generateRandomId());
        log.info("Saving new privilege: {}", privilege);
        return privilegeRepository.save(privilege);
    }

    @Override
    public Map<String, Object> getPrivileges(int page, int size) {
        List<Privilege> privileges;
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size);

        log.info("Fetching privileges for page {} of size {}", page, size);
        Page<Privilege> pagePrivileges = privilegeRepository.findAll(pageable);
        privileges = pagePrivileges.getContent();
        response.put(PRIVILEGES, privilegeMapper.toPrivilegeDtoList(privileges));
        response.put(PAGE_NUMBER, pagePrivileges.getNumber());
        response.put(TOTAL_ELEMENTS, pagePrivileges.getTotalElements());
        response.put(TOTAL_PAGES, pagePrivileges.getTotalPages());
        return response;
    }

    @Override
    public Privilege findPrivilegeByPrivilegeId(String privilegeId) {
        log.info("Fetching privilege by privilege id");
        return privilegeRepository.findPrivilegeByPrivilegeId(privilegeId);
    }

    @Override
    public Privilege findPrivilegeByName(String name) {
        log.info("Fetching privilege by name");
        return privilegeRepository.findPrivilegeByName(name);
    }

    @Override
    public Privilege findPrivilegeById(Long id) {
        log.info("Fetching privilege by id");
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(id);
        Privilege privilege = null;

        if (optionalPrivilege.isPresent()) {
            privilege = optionalPrivilege.get();
        }

        return privilege;
    }

    @Override
    public Privilege updatePrivilege(String privilegeId, Privilege newPrivilege) throws PrivilegeNotFoundException, PrivilegeExistException {
        Privilege oldPrivilege = findPrivilegeByPrivilegeId(privilegeId);
        log.info("Validating new privilege");
        Privilege updatePrivilege = validateNewPrivilegeName(oldPrivilege.getName(), newPrivilege.getName());
        assert updatePrivilege != null;
        updatePrivilege.setName(newPrivilege.getName());
        privilegeRepository.save(updatePrivilege);
        return updatePrivilege;
    }

    @Override
    public void deletePrivilege(String privilegeId) throws PrivilegeNotFoundException {
        Privilege privilege = findPrivilegeByPrivilegeId(privilegeId);
        if (privilege == null) throw new PrivilegeNotFoundException(NO_PRIVILEGE_FOUND);
        log.info("Deleting privilege");
        privilegeRepository.deleteById(privilege.getId());
    }

    private Privilege validateNewPrivilegeName(String currentPrivilegeName, String newPrivilegeName) throws PrivilegeNotFoundException, PrivilegeExistException {
        Privilege newPrivilege = findPrivilegeByName(newPrivilegeName);

        if (StringUtils.isNotBlank(currentPrivilegeName)) {
            Privilege currentPrivilege = findPrivilegeByName(currentPrivilegeName);
            if (currentPrivilege == null) {
                throw new PrivilegeNotFoundException(NO_PRIVILEGE_FOUND_BY_NAME + currentPrivilegeName);
            }
            if (newPrivilege != null && !currentPrivilege.getId().equals(newPrivilege.getId())) {
                throw new PrivilegeExistException(PRIVILEGE_ALREADY_EXISTS);
            }
            return currentPrivilege;
        } else {
            if (newPrivilege != null) {
                throw new PrivilegeExistException(PRIVILEGE_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
