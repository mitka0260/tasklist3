package com.example.tasklist3.web.security.expression;

//import com.example.tasklist3.service.UserService;
//import org.aopalliance.intercept.MethodInvocation;
//import org.apache.el.lang.EvaluationContext;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.expression.MethodBasedEvaluationContext;
//import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
//import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
//import org.springframework.security.authentication.AuthenticationTrustResolver;
//import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
//import org.springframework.security.core.Authentication;
//
//import java.util.function.Supplier;
//
//public class CustomMethodSecurityExceptionHandler extends DefaultMethodSecurityExpressionHandler {
//
//  private ApplicationContext applicationContext;
//  private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
//
//  @Override
//  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
//    Authentication authentication,
//    MethodInvocation invocation
//  ) {
//    CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
//
//    root.setTrustResolver(trustResolver);
//    root.setPermissionEvaluator(getPermissionEvaluator());
//    root.setRoleHierarchy(getRoleHierarchy());
//    root.setUserService(this.applicationContext.getBean(UserService.class));
//
//    return root;
//  }
//
//  @Override
//  public void setApplicationContext(ApplicationContext applicationContext) {
//    super.setApplicationContext(applicationContext);
//    this.applicationContext = applicationContext;
//  }

//  @Override
//  public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation methodInvocation) {
//    MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
//      createSecurityExpressionRoot(authentication.get(), methodInvocation),
//      methodInvocation.getMethod(),
//      methodInvocation.getArguments(),
//      getParameterNameDiscoverer()
//    );
//    context.setBeanResolver(getBeanResolver());
//
//    return context;
//  }

//  @Override
//  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
//    Authentication authentication, MethodInvocation invocation) {
//
//    CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
//    root.setUserService(userService);
//    root.setTrustResolver(getTrustResolver());
//    root.setRoleHierarchy(getRoleHierarchy());
//    return root;
//  }
//}
