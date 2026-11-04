package com.example.tasklist3.web.security.expression;

import com.example.tasklist3.service.UserService;
import com.example.tasklist3.web.security.JwtEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("customMethodSecurityExpressionRoot")
@RequiredArgsConstructor
public class CustomMethodSecurityExpressionRoot {
  private final UserService userService;

  public boolean canAccessTask(Long taskId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    JwtEntity user = (JwtEntity) authentication.getPrincipal();
    Long userId = user.getId();

    return userService.isTaskOwner(userId, taskId);
  }
}

/**
 * Внизу показан 2-ой способ делать CustomSecurityExpression. Но этот способ не работает в Spring Security 7.
 * Оставил 1-ый способ чтобы не тормозить.
 * */

//@Setter
//@Getter
//public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

//  private Object filterObject;
//  private Object returnObject;
//  private Object target;
//  private HttpServletRequest request;
//
//  private UserService userService;
//
//  public CustomMethodSecurityExpressionRoot(Authentication authentication) {
//    super(authentication);
//  }
//
////  @Override
////  public void setFilterObject(Object filterObject) {
////    this.filterObject = filterObject;
////  }
////
////  @Override
////  public @Nullable Object getFilterObject() {
////    return filterObject;
////  }
//
////  @Override
////  public void setReturnObject(@Nullable Object returnObject) {}
////
////  @Override
////  public @Nullable Object getReturnObject() {
////    return null;
////  }
//
//  public boolean canAccessUser(Long id) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    JwtEntity user = (JwtEntity) authentication.getPrincipal();
//    Long userId = user.getId();
//
//    return userId.equals(id) || hasAnyRole(authentication, Role.ROLE_ADMIN);
//  }
//
//  private boolean hasAnyRole(Authentication authentication, Role... roles) {
//
//    for (Role role : roles) {
//      SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
//      if (authentication.getAuthorities().contains(authority)) {
//        return true;
//      }
//    }
//    return false;
//  }
//
//  public boolean canAccessTask(Long taskId) {
//    System.out.println("canAccessTask");
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    JwtEntity user = (JwtEntity) authentication.getPrincipal();
//    Long userId = user.getId();
//
//    return userService.isTaskOwner(userId, taskId);
//  }
//
//  @Override
//  public @Nullable Object getThis() {
//    return target;
//  }
//
//}
