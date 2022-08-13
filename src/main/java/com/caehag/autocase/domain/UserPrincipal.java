package com.caehag.autocase.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    private final User user;

    // https://www.baeldung.com/role-and-privilege-for-spring-security-registration
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role userRole = this.user.getRole();
        return getAuthorities(getPrivileges(userRole));
    }

    private List<String> getPrivileges(Role role) {
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();

        collection.addAll(role.getPrivileges());

        for (Privilege item: collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege: privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.getIsNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getIsActive();
    }
}
