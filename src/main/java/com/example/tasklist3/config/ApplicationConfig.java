package com.example.tasklist3.config;

import com.example.tasklist3.web.security.JwtTokenFilter;
import com.example.tasklist3.web.security.JwtTokenProvider;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // deprecated
@RequiredArgsConstructor(onConstructor_ = {@Lazy}) // (onConstructor = @__(@Lazy))
public class ApplicationConfig {

  //  private final ApplicationContext applicationContext;
  private final JwtTokenProvider tokenProvider;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) /* throws Exception*/ {
    return configuration.getAuthenticationManager();
  }

//  @Bean
//  public MethodSecurityExpressionHandler expressionHandler(ApplicationContext applicationContext) {
//    DefaultMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExceptionHandler();
//    expressionHandler.setApplicationContext(applicationContext);
//    return expressionHandler;
//  }

//  @Bean
//  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//    return RedisCacheManager.create(connectionFactory);
//  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
      .components(
        new Components()
          .addSecuritySchemes("bearerAuth",
            new SecurityScheme()
              .type(SecurityScheme.Type.HTTP)
              .scheme("bearer")
              .bearerFormat("JWT")
          )
      )
      .info(new Info()
        .title("Task list API")
        .description("Demo Spring Boot application")
        .version("1.0")
      );
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) /*throws Exception*/ {
    httpSecurity
      // .csrf().disable()
      // .cors()
      // .and()
      // .httpBasic().disable()  /* убрали автоматическую генерацию пароля (показывает в терминале) */
      // .sessionManagement()
      // .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      // .and()
      // .exceptionHandling()
      // .authenticationEntryPoint(((request, response, authException) -> {
      //     response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401
      //     response.getWriter().write("Unauthorized");
      // }))
      // .accessDeniedHandler(((request, response, accessDeniedException) -> {
      //     response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
      //     response.getWriter().write("Unauthorized"); // недостаточно прав
      // }))
      // .and()
      // .authorizeHttpRequests()  // здесь пути, для которых надо проверять авторизацию (токен + права)
      // .requestMatchers("/api/v1/auth/**").permitAll()
      // .requestMatchers("/swagger-ui/**").permitAll()
      // .requestMatchers("/v3/api-docs/**").permitAll()
      // .anyRequest().authenticated()
      // .and()
      // .anonymous().disable()  // для security expressions

      .csrf(AbstractHttpConfigurer::disable)  // .csrf(csrfCustomizer -> csrfCustomizer.disable())
      .cors(AbstractHttpConfigurer::disable)  // .cors(corsCustomizer -> corsCustomizer.disable())
      // убрали автоматическую генерацию пароля (показывает пароль в терминале, новый пароль при каждом запуске)
      .httpBasic(AbstractHttpConfigurer::disable) // .httpBasic(httpBasicCustomizer -> httpBasicCustomizer.disable())
      // выключает создание сессий, каждый запрос новый, стандарт для RestApi, надо для JWT-авторизации
      .sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exceptionHandlingConfigurer ->
        exceptionHandlingConfigurer
          .authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized 401");   // 401
          })
          .accessDeniedHandler(((request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Unauthorized 403"); // недостаточно прав 403
          }))
      )
      .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
        .requestMatchers("/api/v1/auth/**").permitAll() // чтобы смог залогиниться и зарегистрироваться
        .requestMatchers("/swagger-ui/**").permitAll()
        .requestMatchers("/v3/api-docs/**").permitAll()
        .anyRequest().authenticated()
      )
      // отключено анонимное посещение, надо для Custom Security Expressions
      .anonymous(AbstractHttpConfigurer::disable) // .anonymous(anonymousConfigurer -> anonymousConfigurer.disable())
      // кастомный фильтр для проверки JWT-токенов прямо перед стандартной проверкой по логину и паролю
      // сначала проверит по токену. Если токен в порядке, то пропустит проверку по логину и паролю
      .addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }

}
