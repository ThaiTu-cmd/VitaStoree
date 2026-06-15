package com.doan.VitaStore.security.service;

import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.enums.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails{
    private UserEntity user;

    public UserDetailsImpl(UserEntity user) {this.user=user;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()));
    }

    @Override
    public String getPassword() { return user.getPasswordHash();}

    @Override
    public String getUsername() {return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return user.getStatus()!= Status.BLOCKED;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return user.getStatus() == Status.ACTIVE;}
}
