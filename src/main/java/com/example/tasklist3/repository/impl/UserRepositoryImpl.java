//package com.example.tasklist3.repository.impl;
//
//import com.example.tasklist3.domain.exception.ResourceMappingException;
//import com.example.tasklist3.domain.user.Role;
//import com.example.tasklist3.domain.user.User;
//import com.example.tasklist3.repository.DataSourceConfig;
//import com.example.tasklist3.repository.UserRepository;
//import com.example.tasklist3.repository.mappers.UserRowMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Optional;
//
////@Repository
//@RequiredArgsConstructor
//public class UserRepositoryImpl implements UserRepository {
//
//  // NB! Везде одна и та же ошибка:
//  //   Connection и PreparedStatement не закрываются в случае
//  //   эксепшена при обращении к БД.
//  // Надо try-with-resources:
//  //  try (
//  //    Connection connection = dataSourceConfig.getConnection();
//  //    PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)
//  //  ) {
//  // или везде добавить:
////  finally {
////    // 1. Сначала закрываем ResultSet
////    if (rs != null) {
////      try {
////        rs.close();
////      } catch (SQLException e) {
////        e.printStackTrace(); // Логируем, но не прерываем выполнение finally
////      }
////    }
////    // 2. Затем закрываем PreparedStatement
////    if (pstmt != null) {
////      try {
////        pstmt.close();
////      } catch (SQLException e) {
////        e.printStackTrace();
////      }
////    }
////    // 3. В самом конце закрываем Connection
////    if (conn != null) {
////      try {
////        conn.close();
////      } catch (SQLException e) {
////        e.printStackTrace();
////      }
////    }
//
//  private final DataSourceConfig dataSourceConfig;
//
//  private final String FIND_BY_ID = """
//        SELECT  u.id as user_id,
//                u.name as user_name,
//                u.username as user_username,
//                u.password as user_password,
//                ur.role as user_role_role,
//                t.id as task_id,
//                t.title as task_title,
//                t.description as task_description,
//                t.expiration_date as task_expiration_date,
//                t.status as task_status
//        FROM users u
//            LEFT JOIN users_roles ur ON u.id = ur.user_id
//            LEFT JOIN users_tasks ut ON u.id = ut.user_id
//            LEFT JOIN tasks t ON ut.task_id = t.id
//        WHERE u.id = ?
//        """;
//
//  @Override
//  public Optional<User> findById(Long id) {
//    try {
//      Connection connection = dataSourceConfig.getConnection();
//      PreparedStatement statement = connection.prepareStatement(
//        FIND_BY_ID,
//        // Настройки поведения результирующей таблицы (ResultSet).
//        // Scroll — позволяет перемещаться по полученным строкам
//        // вперед next(), назад previous(),
//        // а также переходить к конкретной строке (absolute(), relative()).
//        // Insensitive — результат нечувствителен к изменениям в базе данных,
//        // которые происходят после выполнения запроса
//        // Будем ходить по ResultSet вперёд и назад, и в начало
//        // NB! Из-за JOIN'ов будем проходить таблицу несколько раз
//        // Нужно, чтобы прочитать весь список
//        // Потом список ролей
//        // Потом достать юзера
//        ResultSet.TYPE_SCROLL_INSENSITIVE,
//        // CONCUR_READ_ONLY — режим параллельного доступа:
//        // Результирующий ResultSet доступен только для чтения
//        ResultSet.CONCUR_READ_ONLY
//      );
//
//      statement.setLong(1, id);
//
//      try (ResultSet resultSet = statement.executeQuery()) {
//        return Optional.ofNullable(UserRowMapper.mapRow(resultSet));
//      }
//
//    } catch (SQLException throwable) {
//      throw new ResourceMappingException(
//        "Exception while finding user by id."
//      );
//    }
//  }
//
//  private final String FIND_BY_USERNAME = """
//        SELECT  u.id as user_id,
//                u.name as user_name,
//                u.username as user_username,
//                u.password as user_password,
//                ur.role as user_role_role,
//                t.id as task_id,
//                t.title as task_title,
//                t.description as task_description,
//                t.expiration_date as task_expiration_date,
//                t.status as task_status
//        FROM users u
//            LEFT JOIN users_roles ur ON u.id = ur.user_id
//            LEFT JOIN users_tasks ut ON u.id = ut.user_id
//            LEFT JOIN tasks t ON ut.task_id = t.id
//        WHERE u.username = ?
//        """;
//
//  @Override
//  public Optional<User> findByUsername(String username) {
//    try {
//      Connection connection = dataSourceConfig.getConnection();
//      PreparedStatement statement = connection.prepareStatement(
//        FIND_BY_USERNAME,
//        ResultSet.TYPE_SCROLL_INSENSITIVE,
//        ResultSet.CONCUR_READ_ONLY
//      );
//
//      statement.setString(1, username);
//
//      try (ResultSet rs = statement.executeQuery()) {
//        return Optional.ofNullable(UserRowMapper.mapRow(rs));
//      }
//
//    } catch (SQLException throwable) {
//      throw new ResourceMappingException(
//        "Exception while finding user by username."
//      );
//    }
//  }
//
//  private final String UPDATE = """
//        UPDATE users
//        SET name = ?,
//            username = ?,
//            password = ?
//        WHERE id = ?
//        """;
//
//  @Override
//  public void update(User user) {
//    try {
//      Connection connection = dataSourceConfig.getConnection();
//      PreparedStatement statement = connection.prepareStatement(UPDATE);
//
//      statement.setString(1, user.getName());
//      statement.setString(2, user.getUsername());
//      statement.setString(3, user.getPassword());
//      statement.setLong(4, user.getId());
//      statement.executeUpdate();  // TODO NB!!! этой строки не было в видео
//
//    } catch (SQLException throwable) {
//      throw new ResourceMappingException("Exception while updating user.");
//    }
//  }
//
//  private final String CREATE = """
//        INSERT INTO users (name, username, password)
//        VALUES (?, ?, ?)
//        """;
//
//  @Override
//  public void create(User user) {
//    try {
//      Connection connection = dataSourceConfig.getConnection();
//      PreparedStatement statement = connection.prepareStatement(
//        CREATE,
//        PreparedStatement.RETURN_GENERATED_KEYS
//      );
//
//      statement.setString(1, user.getName());
//      statement.setString(2, user.getUsername());
//      statement.setString(3, user.getPassword());
//      statement.executeUpdate();
//
//      try (ResultSet rs = statement.getGeneratedKeys()) {
//        rs.next();
//        user.setId(rs.getLong(1));
//      }
//
//
//    } catch (SQLException e) {
//      throw new ResourceMappingException("Exception while creating user.");
//    }
//  }
//
//  private final String INSERT_USER_ROLE = """
//        INSERT INTO users_roles (user_id, role)
//        VALUES (?, ?)
//        """;
//
//  @Override
//  public void insertUserRole(Long userId, Role role) {
//    try {
//      Connection connection = dataSourceConfig.getConnection();
//      PreparedStatement statement = connection.prepareStatement(
//        INSERT_USER_ROLE
//      );
//
//      statement.setLong(1, userId);
//      statement.setString(2, role.name());
//      statement.executeUpdate();
//
//    } catch (SQLException throwable) {
//      throw new ResourceMappingException(
//        "Execution while inserting user role."
//      );
//    }
//  }
//
//  private final String IS_TASK_OWNER = """
//        SELECT EXISTS(
//          SELECT 1
//             FROM users_tasks
//             WHERE user_id = ?
//             AND task_id = ?
//         )
//        """;
//
//  @Override
//  public boolean isTaskOwner(Long userId, Long taskId) {
//    try {
//      Connection connection = dataSourceConfig.getConnection();
//      PreparedStatement statement = connection.prepareStatement(
//        IS_TASK_OWNER
//      );
//
//      statement.setLong(1, userId);
//      statement.setLong(2, taskId);
//
//      try (ResultSet resultSet = statement.executeQuery()) {
//        resultSet.next();
//
//        return resultSet.getBoolean(1);
//      }
//
//    } catch (SQLException e) {
//      throw new ResourceMappingException(
//        "Exception while checking if user is task owner."
//      );
//    }
//  }
//
//  private final String DELETE = """
//        DELETE FROM users
//        WHERE id = ?
//        """;
//
//  @Override
//  public void delete(Long id) {
//    try {
//      Connection connection = dataSourceConfig.getConnection();
//      PreparedStatement statement = connection.prepareStatement(DELETE);
//
//      statement.setLong(1, id);
//      statement.executeUpdate();
//
//    } catch (SQLException e) {
//      throw new ResourceMappingException("Exception while deleting user.");
//    }
//  }
//}
