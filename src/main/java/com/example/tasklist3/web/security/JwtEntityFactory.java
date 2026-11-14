package com.example.tasklist3.web.security;

import com.example.tasklist3.domain.user.Role;
import com.example.tasklist3.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Паттерн фабрика

public /*final*/ class JwtEntityFactory {

    public static JwtEntity create(final User user) {
        return new JwtEntity(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getPassword(),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles()))
        );
    }

    // мы можем хранить пользователей в любом виде и здесь маппим их для спринга
    private static List<GrantedAuthority> mapToGrantedAuthorities(
      final List<Role> roles
    ) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
