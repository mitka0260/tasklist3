package com.example.tasklist3.repository;

//import com.example.tasklist3.domain.user.Role;
import com.example.tasklist3.domain.user.User;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//@Mapper
public interface UserRepository extends JpaRepository<User, Long> {
//  Optional<User> findById(Long id);

  // проверка времени рег-ции для Spring Security для авторизации пользователя
  Optional<User> findByUsername(String username);

//  Optional<User> findByNameAndRolesContaining(String name, Role role);

  // уже есть save()
//  void update(User user);
//  void create(User user);

//  void insertUserRole(@Param("userId") Long userId, @Param("role") Role role);

  @Query(value = """
    SELECT exists(
                SELECT 1
                FROM users_tasks
                WHERE user_id = :userId
                AND task_id = :taskId
            )
    """, nativeQuery = true)
  boolean isTaskOwner(
    @Param("userId") Long userId,
    @Param("taskId") Long taskId
  );

//  delete => deleteById
//  void delete(Long id);
}
