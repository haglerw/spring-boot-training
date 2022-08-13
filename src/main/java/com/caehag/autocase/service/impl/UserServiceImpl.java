package com.caehag.autocase.service.impl;

import com.caehag.autocase.domain.User;
import com.caehag.autocase.domain.UserPrincipal;
import com.caehag.autocase.exception.domain.user.EmailExistException;
import com.caehag.autocase.exception.domain.user.EmailNotFoundException;
import com.caehag.autocase.exception.domain.user.UserNotFoundException;
import com.caehag.autocase.exception.domain.user.UsernameExistException;
import com.caehag.autocase.mapstruct.mappers.UserMapper;
import com.caehag.autocase.repository.UserRepository;
import com.caehag.autocase.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caehag.autocase.constant.AppConstant.*;
import static com.caehag.autocase.constant.EmailConstant.*;
import static com.caehag.autocase.constant.UserConstant.*;
import static com.caehag.autocase.utility.Utility.generatePassword;
import static com.caehag.autocase.utility.Utility.generateRandomId;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.domain.PageRequest.of;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final RoleService roleService;
    private final DepartmentService departmentService;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email, Long departmentId, Long roleId) throws UserNotFoundException, EmailExistException, UsernameExistException {
        log.info("Validating new username: {}", username);
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        VelocityContext velocityContext = new VelocityContext();
        String password = generatePassword();
        user.setUserId(generateRandomId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(roleService.findRoleById(roleId));
        user.setDepartment(departmentService.findDepartmentById(departmentId));
        log.info("Saving new user: {}", userMapper.toUserGetDto(user));
        userRepo.save(user);
        velocityContext.put(USER, user);
        velocityContext.put(PASSWORD_KEY_NAME, password);
        velocityContext.put(APPLICATION_KEY_NAME, APPLICATION_NAME);
        emailService.sendEmail(WELCOME_SUBJECT, email, velocityContext, WELCOME_TEMPLATE);
        return user;
    }

    @Override
    public Map<String, Object> getUsers(int page, int size) {
        List<User> users;
        Map<String, Object> response = new HashMap<>();
        Pageable paging = of(page, size);

        log.info("Fetching users for page {} of size {}", page, size);
        Page<User> pageUsers = userRepo.findAll(paging);
        users = pageUsers.getContent();
        response.put(USERS, userMapper.toUserGetDtoList(users));
        response.put(PAGE_NUMBER, pageUsers.getNumber());
        response.put(TOTAL_ELEMENTS, pageUsers.getTotalElements());
        response.put(TOTAL_PAGES, pageUsers.getTotalPages());
        return response;
    }

    @Override
    public User findUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepo.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepo.findUserByEmail(email);
    }

    @Override
    public User findUserByUserId(String userId) {
        log.info("Fetching user by userId: {}", userId);
        return userRepo.findUserByUserId(userId);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, Long departmentId, Long roleId, boolean isNonLocked, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException {
        log.info("Validating new username: {}", username);
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        VelocityContext velocityContext = new VelocityContext();
        String password = generatePassword();
        user.setUserId(generateRandomId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setIsActive(isActive);
        user.setIsNotLocked(isNonLocked);
        user.setRole(roleService.findRoleById(roleId));
        user.setDepartment(departmentService.findDepartmentById(departmentId));
        log.info("Saving new user: {}", userMapper.toUserGetDto(user));
        velocityContext.put(USER, user);
        velocityContext.put(PASSWORD_KEY_NAME, password);
        velocityContext.put(APPLICATION_KEY_NAME, APPLICATION_NAME);
        emailService.sendEmail(WELCOME_SUBJECT, email, velocityContext, WELCOME_TEMPLATE);
        return user;
    }

    @Override
    public User updateUser(String currentUserId, String newFirstName, String newLastName, String newUsername, String newEmail, Long newDepartmentId, Long newRoleId, boolean isNonLocked, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User oldUser = findUserByUserId(currentUserId);
        if (oldUser == null) throw new UserNotFoundException(NO_USER_FOUND);
        log.info("Validating user to be updated: {}", currentUserId);
        User user = validateNewUsernameAndEmail(oldUser.getUsername(), newUsername, newEmail);
        assert user != null;
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setDepartment(departmentService.findDepartmentById(newDepartmentId));
        user.setRole(roleService.findRoleById(newRoleId));
        user.setIsActive(isActive);
        user.setIsNotLocked(isNonLocked);
        log.info("Saving updated user: {}", user);
        userRepo.save(user);
        return user;
    }

    @Override
    public void deleteUser(String userId) throws UserNotFoundException {
        User user = findUserByUserId(userId);
        if (user == null) throw new UserNotFoundException(NO_USER_FOUND);
        log.info("Deleting user by userId: {}", userId);
        userRepo.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = findUserByEmail(email);
        VelocityContext velocityContext = new VelocityContext();
        if (user == null) throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        String password = generatePassword();
        log.info("Resetting user password");
        user.setPassword(encodePassword(password));
        userRepo.save(user);
        velocityContext.put(USER, user);
        velocityContext.put(PASSWORD_KEY_NAME, password);
        velocityContext.put(APPLICATION_KEY_NAME, APPLICATION_NAME);
        emailService.sendEmail(PASSWORD_RESET_SUBJECT, email, velocityContext, PASSWORD_RESET_TEMPLATE);
    }

    @Override
    public void changeActiveStatus(String userId, boolean isActive) throws UserNotFoundException {
        User user = findUserByUserId(userId);
        if (user == null) throw new UserNotFoundException(NO_USER_FOUND);
        user.setIsActive(isActive);
        userRepo.save(user);
    }

    @Override
    public void lockUnlockUser(String userId, boolean isNotLocked) throws UserNotFoundException {
        User user = findUserByUserId(userId);
        if (user == null) throw new UserNotFoundException(NO_USER_FOUND);
        user.setIsNotLocked(isNotLocked);
        userRepo.save(user);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void validateLoginAttempt(User user) {
        if (user.getIsNotLocked()) {
            user.setIsNotLocked(!loginAttemptService.hasExceededMaxLoginAttempt(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        if (isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getUserId().equals(userByNewUsername.getUserId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getUserId().equals(userByNewEmail.getUserId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
