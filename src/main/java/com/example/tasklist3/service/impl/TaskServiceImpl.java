package com.example.tasklist3.service.impl;

import com.example.tasklist3.domain.exception.ResourceNotFoundException;
import com.example.tasklist3.domain.task.Status;
import com.example.tasklist3.domain.task.Task;
import com.example.tasklist3.repository.TaskRepository;
import com.example.tasklist3.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "TaskService::getById", key = "#id")
  public Task getById(Long id) {
    return taskRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Task not found."));
  }

  @Override
  @Transactional(readOnly = true)
  // Здесь нет кэширования, т.к. здесь трудно обработать: много когда надо его обновлять
  public List<Task> getAllByUserId(Long id) {
    return taskRepository.findAllByUserId(id);
  }

  @Override
  @Transactional
  @CachePut(value = "TaskService::getById", key = "#task.id")
  public Task update(Task task) {
    if (task.getStatus() == null) {
      task.setStatus(Status.TODO);
    }
    taskRepository.update(task);
    return task;
  }

  @Override
  @Transactional
  @Cacheable(value = "TaskService::getById", key = "#task.id")
  public Task create(Task task, Long userId) {
    task.setStatus(Status.TODO);
    taskRepository.create(task); // здесь присвоится id
    taskRepository.assignToUserById(task.getId(), userId);
    return task; // вернётся с id
  }

  @Override
  @Transactional
  @CacheEvict(value = "TaskService::getById", key = "#id")
  public void delete(Long id) {
    taskRepository.delete(id);
  }

}
