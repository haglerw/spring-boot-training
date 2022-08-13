package com.caehag.autocase.service;

import com.caehag.autocase.domain.User;
import com.caehag.autocase.exception.domain.user.EmailExistException;
import com.caehag.autocase.exception.domain.user.EmailNotFoundException;
import com.caehag.autocase.exception.domain.user.UserNotFoundException;
import com.caehag.autocase.exception.domain.user.UsernameExistException;

import java.util.Map;

public interface UserService {
    User register(String firstName, String lastName, String username, String email, Long departmentId, Long roleId) throws UserNotFoundException, EmailExistException, UsernameExistException;

    Map<String, Object> getUsers(int page, int size);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserByUserId(String userId) throws UserNotFoundException;

    User addNewUser(String firstName, String lastName, String username, String email, Long departmentId, Long roleId, boolean isNonLocked, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException;

    User updateUser(String currentUserId, String newFirstName, String newLastName, String newUsername, String newEmail, Long newDepartmentId, Long newRoleId, boolean isNonLocked, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException;

    void deleteUser(String userId) throws UserNotFoundException;

    void resetPassword(String email) throws EmailNotFoundException;

    void changeActiveStatus(String userId, boolean isActive) throws UserNotFoundException;

    void lockUnlockUser(String userId, boolean isNotLocked) throws UserNotFoundException;
}
