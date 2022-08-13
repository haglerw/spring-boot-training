package com.caehag.autocase.resource;

import com.caehag.autocase.domain.Privilege;
import com.caehag.autocase.domain.Response;
import com.caehag.autocase.exception.domain.ExceptionHandling;
import com.caehag.autocase.exception.domain.privilege.PrivilegeExistException;
import com.caehag.autocase.exception.domain.privilege.PrivilegeNotFoundException;
import com.caehag.autocase.mapstruct.mappers.PrivilegeMapper;
import com.caehag.autocase.service.PrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_NUMBER;
import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_SIZE;
import static com.caehag.autocase.constant.PrivilegeConstant.*;
import static com.caehag.autocase.utility.Utility.returnOkResponse;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = { "/", "/privileges" })
public class PrivilegeResource extends ExceptionHandling {
    private final PrivilegeMapper privilegeMapper;
    private final PrivilegeService privilegeService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('create:privilege')")
    ResponseEntity<Response> createPrivilege(@Valid @RequestBody Privilege privilegeEntity) throws PrivilegeExistException, PrivilegeNotFoundException {
        Privilege privilege = privilegeService.createPrivilege(privilegeEntity);
        return returnOkResponse(OK, PRIVILEGE_CREATED_SUCCESSFULLY, privilegeMapper.toPrivilegeDto(privilege));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('read:privilege')")
    ResponseEntity<Response> getAllPrivileges(@RequestParam Optional<Integer> page,
                                              @RequestParam Optional<Integer> size) {
        Map<String, Object> privileges = privilegeService.getPrivileges(page.orElse(DEFAULT_PAGE_NUMBER), size.orElse(DEFAULT_PAGE_SIZE));
        return returnOkResponse(OK, PRIVILEGES_FETCHED_SUCCESSFULLY, privileges);
    }

    @GetMapping("/find/{privilegeId}")
    @PreAuthorize("hasAnyAuthority('read:privilege')")
    ResponseEntity<Response> getPrivilegeByPrivilegeId(@PathVariable("privilegeId") String privilegeId) throws PrivilegeNotFoundException {
        Privilege privilege = privilegeService.findPrivilegeByPrivilegeId(privilegeId);
        if (privilege == null) throw new PrivilegeNotFoundException(NO_PRIVILEGE_FOUND);
        return returnOkResponse(OK, PRIVILEGE_FETCHED_SUCCESSFULLY, privilegeMapper.toPrivilegeDto(privilege));
    }

    @PutMapping("/update/{privilegeId}")
    @PreAuthorize("hasAnyAuthority('update:privilege')")
    ResponseEntity<Response> updatePrivilege(@PathVariable("privilegeId") String privilegeId, @Valid @RequestBody Privilege newPrivilege) throws PrivilegeNotFoundException, PrivilegeExistException {
        Privilege updatePrivilege = privilegeService.updatePrivilege(privilegeId, newPrivilege);
        return returnOkResponse(OK, PRIVILEGE_UPDATED_SUCCESSFULLY, privilegeMapper.toPrivilegeDto(updatePrivilege));
    }

    @DeleteMapping("/delete/{privilegeId}")
    @PreAuthorize("hasAnyAuthority('delete:privilege')")
    ResponseEntity<Response> deleteGroup(@PathVariable("privilegeId") String privilegeId) throws PrivilegeNotFoundException {
        privilegeService.deletePrivilege(privilegeId);
        return returnOkResponse(OK, PRIVILEGE_DELETED_SUCCESSFULLY, null);
    }
}
