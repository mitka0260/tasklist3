package com.example.tasklist3.repository.impl;

import com.example.tasklist3.domain.exception.ResourceMappingException;
import com.example.tasklist3.domain.task.Task;
import com.example.tasklist3.repository.DataSourceConfig;
import com.example.tasklist3.repository.TaskRepository;
import com.example.tasklist3.repository.mappers.TaskRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

//@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

  // NB! везде одна и та же ошибка -- Connection и PreparedStatement не закрываются в случае эксепшена при обращении к БД.

  private final DataSourceConfig dataSourceConfig;

  private final String FIND_BY_ID = """
      SELECT t.id                 as task_id,
             t.title              as task_title,
             t.description        as task_description,
             t.expiration_date    as task_expiration_date,
             t.status             as task_status
      FROM tasklist3.tasks t
      WHERE t.id = ?
      """;

  @Override
  public Optional<Task> findById(Long id) {
    try {
      Connection connection = dataSourceConfig.getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_BY_ID);
      statement.setLong(1, id);

      try (ResultSet resultSet = statement.executeQuery()) { // == SELECT
        return Optional.ofNullable(TaskRowMapper.mapRow(resultSet));
      }

    } catch (SQLException throwable) {
      throw new ResourceMappingException("Error while finding user by id.");
    }
  }

  private final String FIND_ALL_BY_USER_ID = """
      SELECT t.id                 as task_id,
             t.title              as task_title,
             t.description        as task_description,
             t.expiration_date    as task_expiration_date,
             t.status             as task_status
      FROM tasklist3.tasks t
      JOIN tasklist3.users_tasks ut ON t.id = ut.task_id
      WHERE ut.user_id = ?
      """;

  @Override
  public List<Task> findAllByUserId(Long userId) {
    try {
      Connection connection = dataSourceConfig.getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_USER_ID);
      statement.setLong(1, userId);

      try (ResultSet resultSet = statement.executeQuery()) { // == SELECT
        return TaskRowMapper.mapRows(resultSet);
      }
    } catch (SQLException throwable) {
      throw new ResourceMappingException("Error while finding all by user id.");
    }
  }

  private final String ASSIGN = """
      INSERT INTO tasklist3.users_tasks (task_id, user_id)
      VALUES (?, ?)
      """;

  @Override
  public void assignToUserById(Long taskId, Long userId) {
    try {
      Connection connection = dataSourceConfig.getConnection();
      PreparedStatement statement = connection.prepareStatement(ASSIGN);
      statement.setLong(1, taskId);
      statement.setLong(2, userId);
      statement.executeUpdate();
    } catch (SQLException throwable) {
      throw new ResourceMappingException("Error while assigning task to user.");
    }
  }

  private final String UPDATE = """
      UPDATE tasklist3.tasks
      SET title = ?,
          description = ?,
          expiration_date = ?,
          status = ?
      WHERE id = ?
      """;

  @Override
  public void update(Task task) {
    try {
      Connection connection = dataSourceConfig.getConnection();
      PreparedStatement statement = connection.prepareStatement(UPDATE);
      statement.setString(1, task.getTitle());

      if (task.getDescription() == null) {
        statement.setNull(2, Types.VARCHAR); // NULL типа Варчар (пишем null в столбец, у столбца тип VARCHAR)
      } else {
        statement.setString(2, task.getDescription());
      }

      if (task.getExpirationDate() == null) {
        statement.setNull(3, Types.TIMESTAMP);
      } else {
        statement.setTimestamp(3, Timestamp.valueOf(task.getExpirationDate()));
      }

      statement.setString(4, task.getStatus().name());
      statement.setLong(5, task.getId());

      statement.executeUpdate();
    } catch (SQLException throwable) {
      throw new ResourceMappingException("Error while updating task.");
    }
  }

  private final String CREATE = """
      INSERT INTO tasklist3.tasks (title, description, expiration_date, status)
      VALUES (?, ?, ?, ?)
      """;

  @Override
  public void create(Task task) {
    try {
      Connection connection = dataSourceConfig.getConnection();
      PreparedStatement statement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);

      statement.setString(1, task.getTitle());

      if (task.getDescription() == null) {
        statement.setNull(2, Types.VARCHAR);
      } else {
        statement.setString(2, task.getDescription());
      }

      if (task.getExpirationDate() == null) {
        statement.setNull(3, Types.TIMESTAMP);
      } else {
        statement.setTimestamp(3, Timestamp.valueOf(task.getExpirationDate()));
      }

      statement.setString(4, task.getStatus().name());

      statement.executeUpdate();

      try (ResultSet resultSet = statement.getGeneratedKeys()) {
        resultSet.next();
        task.setId(resultSet.getLong(1));
      }
    } catch (SQLException throwable) {
      throw new ResourceMappingException("Error while creating task.");
    }
  }

  private final String DELETE = """
      DELETE FROM tasklist3.tasks
      WHERE id = ?
      """;

  @Override
  public void delete(Long taskId) {
    try {
      Connection connection = dataSourceConfig.getConnection();
      PreparedStatement statement = connection.prepareStatement(DELETE);

      statement.setLong(1, taskId);
      statement.executeUpdate();
    } catch (SQLException throwable) {
      throw new ResourceMappingException("Error while deleting task.");
    }
  }

}
