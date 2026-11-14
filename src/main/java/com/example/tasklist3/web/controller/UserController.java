package com.example.tasklist3.web.controller;

import com.example.tasklist3.domain.task.Task;
import com.example.tasklist3.domain.user.User;
import com.example.tasklist3.service.TaskService;
import com.example.tasklist3.service.UserService;
import com.example.tasklist3.web.dto.task.TaskDto;
import com.example.tasklist3.web.dto.user.UserDto;
import com.example.tasklist3.web.dto.validation.OnCreate;
import com.example.tasklist3.web.dto.validation.OnUpdate;
import com.example.tasklist3.web.mappers.TaskMapper;
import com.example.tasklist3.web.mappers.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User controller", description = "User API")
public class UserController {

  private final UserService userService;
  private final TaskService taskService;

  private final UserMapper userMapper;
  private final TaskMapper taskMapper;

  @PutMapping
  @Operation(summary = "Update user")
  @PreAuthorize("@customSecurityExpression.canAccessUser(#dto.id)")
  public UserDto update(
    @Validated(OnUpdate.class) @RequestBody final UserDto dto
  ) {
    User user = userMapper.toEntity(dto);
    User updatedUser = userService.update(user);
    return userMapper.toDto(updatedUser);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user")
  @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
  public UserDto getById(@PathVariable final Long id) {
    User user = userService.getById(id);
    return userMapper.toDto(user);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete user by id")
  @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
  public void deleteById(@PathVariable final Long id) {
    userService.delete(id);
  }

  @GetMapping("/{id}/tasks")
  @Operation(summary = "Get all User tasks")
  @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
  public List<TaskDto> getTasksByUserId(@PathVariable final Long id) {
    List<Task> tasks = taskService.getAllByUserId(id);
    return taskMapper.toDto(tasks);
  }

  @PostMapping("/{id}/tasks")
  @Operation(summary = "Add task to User")
  @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
  public TaskDto createTask(
    final @PathVariable Long id,
    final @Validated(OnCreate.class) @RequestBody TaskDto dto
  ) {
    Task task = taskMapper.toEntity(dto);
    Task createdTask = taskService.create(task, id);
    return taskMapper.toDto(createdTask);
  }

}
