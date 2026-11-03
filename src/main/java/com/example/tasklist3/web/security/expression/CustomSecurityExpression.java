package com.example.tasklist3.web.security.expression;

import com.example.tasklist3.domain.user.Role;
import com.example.tasklist3.service.UserService;
import com.example.tasklist3.web.security.JwtEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("customSecurityExpression")
@RequiredArgsConstructor
public class CustomSecurityExpression {

  private final UserService userService;

  // Может ли сейчас авторизированный пользователь обращаться к пользователю с id
  public boolean canAccessUser(Long id) {
    // Аутентификацию сетаем в jwtTokenFilter => doFilter => setAuthentification
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    JwtEntity user = (JwtEntity) authentication.getPrincipal();
    Long userId = user.getId();

    // Можно достать роли, хоть здесь роли не нужны
    return userId.equals(id) || hasAnyRole(authentication, Role.ROLE_ADMIN);
  }

  private boolean hasAnyRole(Authentication authentication, Role... roles) {

    for (Role role : roles) {
      SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
      if (authentication.getAuthorities().contains(authority)) {
        return true;
      }
    }
    return false;
  }

  public boolean canAccessTask(Long taskId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    JwtEntity user = (JwtEntity) authentication.getPrincipal();
    Long userId = user.getId();

    return userService.isTaskOwner(userId, taskId);
  }

}
