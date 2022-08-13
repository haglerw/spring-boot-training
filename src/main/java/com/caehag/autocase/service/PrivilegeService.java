package com.caehag.autocase.service;

import com.caehag.autocase.domain.Privilege;
import com.caehag.autocase.exception.domain.privilege.PrivilegeExistException;
import com.caehag.autocase.exception.domain.privilege.PrivilegeNotFoundException;

import java.util.Map;

public interface PrivilegeService {
    Privilege createPrivilege(Privilege privilege) throws PrivilegeExistException, PrivilegeNotFoundException;
    Map<String, Object> getPrivileges(int page, int size);
    Privilege findPrivilegeByPrivilegeId(String privilegeId);
    Privilege findPrivilegeByName(String name);
    Privilege findPrivilegeById(Long id);
    Privilege updatePrivilege(String privilegeId, Privilege newPrivilege) throws PrivilegeNotFoundException, PrivilegeExistException;
    void deletePrivilege(String privilegeId) throws PrivilegeNotFoundException;
}
