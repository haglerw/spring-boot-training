package com.caehag.autocase.resource;

import com.caehag.autocase.domain.Response;
import com.caehag.autocase.domain.User;
import com.caehag.autocase.domain.UserPrincipal;
import com.caehag.autocase.exception.domain.ExceptionHandling;
import com.caehag.autocase.exception.domain.user.EmailExistException;
import com.caehag.autocase.exception.domain.user.EmailNotFoundException;
import com.caehag.autocase.exception.domain.user.UserNotFoundException;
import com.caehag.autocase.exception.domain.user.UsernameExistException;
import com.caehag.autocase.mapstruct.dtos.user.UserPostDto;
import com.caehag.autocase.mapstruct.mappers.UserMapper;
import com.caehag.autocase.service.UserService;
import com.caehag.autocase.utility.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_NUMBER;
import static com.caehag.autocase.constant.AppConstant.DEFAULT_PAGE_SIZE;
import static com.caehag.autocase.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.caehag.autocase.constant.UserConstant.*;
import static com.caehag.autocase.utility.Utility.returnOkResponse;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = { "/", "/users" })
public class UserResource extends ExceptionHandling {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider tokenProvider;

    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('create:user')")
    public ResponseEntity<Response> register(@Valid @RequestBody UserPostDto user) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User registeredUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getDepartmentId(), user.getRoleId());
        return returnOkResponse(OK, USER_REGISTERED_SUCCESSFULLY, userMapper.toUserGetDto(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return ResponseEntity.ok()
                .headers(jwtHeader)
                .body(
                        Response.builder()
                                .timestamp(now())
                                .data(userMapper.toUserGetDto(loginUser))
                                .message(USER_LOGGED_IN_SUCCESSFULLY)
                                .status(OK)
                                .statusCode(OK.value())
                                .build()
                );
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('create:user')")
    public ResponseEntity<Response> addNewUser(@RequestParam("firstName") String firstName,
                                                 @RequestParam("lastName") String lastName,
                                                 @RequestParam("username") String username,
                                                 @RequestParam("email") String email,
                                                 @RequestParam("departmentId") @NotNull(message = "Department id is required") Long departmentId,
                                                 @RequestParam("roleId") @NotNull(message = "Role id is required") Long roleId,
                                                 @RequestParam("isActive") String isActive,
                                                 @RequestParam("isNonLocked") String isNonLocked) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User newUser = userService.addNewUser(firstName, lastName, username, email, departmentId, roleId, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked));
        return returnOkResponse(OK, USER_ADDED_SUCCESSFULLY, of(USER, userMapper.toUserGetDto(newUser)));
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('update:user')")
    public ResponseEntity<Response> updateUser(@PathVariable("userId") String userId,
                                                 @RequestParam("firstName") String firstName,
                                                 @RequestParam("lastName") String lastName,
                                                 @RequestParam("username") String username,
                                                 @RequestParam("email") String email,
                                                 @RequestParam("departmentId") Long departmentId,
                                                 @RequestParam("roleId") Long roleId,
                                                 @RequestParam("isActive") String isActive,
                                                 @RequestParam("isNonLocked") String isNonLocked) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User updateUser = userService.updateUser(userId, firstName, lastName, username, email, departmentId, roleId, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked));
        return returnOkResponse(OK, USER_UPDATED_SUCCESSFULLY, userMapper.toUserGetDto(updateUser));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('read:user')")
    public ResponseEntity<Response> getPaginatedUsers(@RequestParam Optional<Integer> page,
                                                      @RequestParam Optional<Integer> size) {
        Map<String, Object> users = userService.getUsers(page.orElse(DEFAULT_PAGE_NUMBER), size.orElse(DEFAULT_PAGE_SIZE));
        return returnOkResponse(OK, USERS_FETCHED_SUCCESSFULLY, users);
    }

    @GetMapping("/find/{userId}")
    @PreAuthorize("hasAnyAuthority('read:user')")
    public ResponseEntity<Response> getUserByUserId(@PathVariable("userId") String userId) throws UserNotFoundException {
        User user = userService.findUserByUserId(userId);
        if (user == null) throw new UserNotFoundException(NO_USER_FOUND);
        return returnOkResponse(OK, USER_FETCHED_SUCCESSFULLY, userMapper.toUserGetDto(user));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAnyAuthority('delete:user')")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        userService.deleteUser(userId);
        return returnOkResponse(OK, USER_DELETED_SUCCESSFULLY, null);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<Response> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return returnOkResponse(OK, PASSWORD_RESET_WAS_SUCCESSFUL, null);
    }

    @PutMapping("/{userId}/active")
    @PreAuthorize("hasAnyAuthority('update:user')")
    public ResponseEntity<Response> changeActiveStatus(@RequestBody User user, @PathVariable("userId") String userId) throws UserNotFoundException {
        userService.changeActiveStatus(userId, user.getIsActive());
        return returnOkResponse(OK, Objects.equals(user.getIsActive(), true) ? ACTIVATED: DEACTIVATED, null);
    }

    @PutMapping("/{userId}/not-locked")
    @PreAuthorize("hasAnyAuthority('update:user')")
    public ResponseEntity<Response> changeLockStatus(@RequestBody User user, @PathVariable("userId") String userId) throws UserNotFoundException {
        userService.lockUnlockUser(userId, user.getIsNotLocked());
        return returnOkResponse(OK, Objects.equals(user.getIsNotLocked(), true) ? UNLOCKED : LOCKED, null);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, tokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
