package com.example.tasklist3.web.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
public class JwtEntity implements UserDetails {

    private Long id;  // user_id
    private final String username;
    private final String name;
    private final String password;
    // коллекция ролей и полномочий
    private final Collection<? extends GrantedAuthority> authorities;

    // 3 метода внизу методы создала аннотация @Data (создала геттеры)
    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     return authorities;   // null
    // }
    //
    // @Override
    // public String getPassword() {
    // return password;  // null
    // }
    //
    // @Override
    // public String getUsername() {
    // return username;  // null
    // }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
