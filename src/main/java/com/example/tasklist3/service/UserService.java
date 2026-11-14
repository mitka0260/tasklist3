package com.example.tasklist3.service;

import com.example.tasklist3.domain.user.User;

public interface UserService {
  // Здесь нет Optional, т.к. возвращает либо объект, либо исключение
  User getById(Long id);

  User getByUsername(String username);

  User update(User user);

  User create(User user);

  boolean isTaskOwner(Long userId, Long taskId);

  void delete(Long id);
}
