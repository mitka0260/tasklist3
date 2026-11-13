package com.example.tasklist3.web.controller;

import com.example.tasklist3.domain.task.Task;
import com.example.tasklist3.domain.task.TaskImage;
import com.example.tasklist3.service.TaskService;
import com.example.tasklist3.web.dto.task.TaskDto;
import com.example.tasklist3.web.dto.task.TaskImageDto;
import com.example.tasklist3.web.dto.validation.OnUpdate;
import com.example.tasklist3.web.mappers.TaskImageMapper;
import com.example.tasklist3.web.mappers.TaskMapper;
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
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Task controller", description = "Task API")
public class TaskController {

  private final TaskService taskService;

  private final TaskMapper taskMapper;
  private final TaskImageMapper taskImageMapper;

  @PutMapping
  @Operation(summary = "Update task")
  @PreAuthorize("@customMethodSecurityExpressionRoot.canAccessTask(#dto.id)")
  public TaskDto update(
    @Validated(OnUpdate.class) @RequestBody final TaskDto dto
  ) {
    Task task = taskMapper.toEntity(dto);
    Task updatedTask = taskService.update(task);
    return taskMapper.toDto(updatedTask);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get TaskDto by id")
  @PreAuthorize("@customMethodSecurityExpressionRoot.canAccessTask(#id)")
  public TaskDto getById(@PathVariable final Long id) {
    Task task = taskService.getById(id);
    return taskMapper.toDto(task);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete task by id")
  @PreAuthorize("@customMethodSecurityExpressionRoot.canAccessTask(#id)")
  public void deleteById(@PathVariable final Long id) {
    taskService.delete(id);
  }

  @PostMapping("/{id}/image")
  @Operation(summary = "Upload image to task")
  @PreAuthorize("@customMethodSecurityExpressionRoot.canAccessTask(#id)")
  public void uploadImage(
    @PathVariable final Long id,
    @Validated @ModelAttribute final TaskImageDto imageDto
  ) {
    TaskImage image = taskImageMapper.toEntity(imageDto);
    taskService.uploadImage(id, image);
  }

}
