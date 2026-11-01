package com.example.tasklist3.repository;

import com.example.tasklist3.domain.user.Role;
import com.example.tasklist3.domain.user.User;

import java.util.Optional;

public interface UserRepository {
  Optional<User> findById(Long id);

  // проверка времени регистрации и для Spring Security для авторизации пользователя
  Optional<User> findByUsername(String username);

  void update(User user);

  void create(User user);

  void insertUserRole(Long userId, Role role);

  boolean isTaskOwner(Long userId, Long taskId);

  void delete(Long id);
}
