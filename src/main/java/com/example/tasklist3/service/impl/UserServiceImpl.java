package com.example.tasklist3.service.impl;

import com.example.tasklist3.domain.exception.ResourceNotFoundException;
import com.example.tasklist3.domain.user.Role;
import com.example.tasklist3.domain.user.User;
import com.example.tasklist3.repository.UserRepository;
import com.example.tasklist3.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "UserService::getById", key = "#id")
  public User getById(Long id) {
    return userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User by id not found."));
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "UserService::getByUsername", key = "#username")
  public User getByUsername(String username) {
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("Username not found."));
  }

  @Override
  @Transactional
  @Caching(put = {
    @CachePut(value = "UserService::getById", key = "#user.id"),
    @CachePut(value = "UserService::getByUsername", key = "#user.username")
  })
  public User update(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
//    userRepository.update(user);
    userRepository.save(user);
    return user;
  }

  @Override
  @Transactional
  @Caching(cacheable = {
    @Cacheable(value = "UserService::getById", key = "#user.id"),
    @Cacheable(value = "UserService::getByUsername", key = "#user.username")
  })
  public User create(User user) {
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
      throw new IllegalStateException("Username already exists.");
    }
    if (!user.getPassword().equals(user.getPasswordConfirmation())) {
      throw new IllegalStateException("Password and confirmation do not match.");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
//    userRepository.create(user);
    Set<Role> roles = Set.of(Role.ROLE_USER);
//    userRepository.insertUserRole(user.getId(), Role.ROLE_USER);
    user.setRoles(roles);
    userRepository.save(user);
    return user;
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "UserService::isTaskOwner", key = "#userId + '.' + #taskId")
  public boolean isTaskOwner(Long userId, Long taskId) {
    return userRepository.isTaskOwner(userId, taskId);
  }

  @Override
  @Transactional
  @Caching(evict = {
    @CacheEvict(value = "UserService::getById", key = "#id"),
//    @CacheEvict(value = "UserService::getByUsername", key = "#username") // убрал т.к здесь нет username
  })
  public void delete(Long id) {
//    userRepository.delete(id);
    userRepository.deleteById(id);
  }

}
