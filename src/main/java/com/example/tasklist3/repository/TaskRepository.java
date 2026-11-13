package com.example.tasklist3.repository;

import com.example.tasklist3.domain.task.Task;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
//import java.util.Optional;

//@Mapper
public interface TaskRepository extends JpaRepository<Task, Long> {
//  Optional<Task> findById(Long id);

  @Query(value = """
            SELECT * FROM tasks t
            JOIN users_tasks ut ON ut.task_id = t.id
            WHERE ut.user_id = :userId
            """, nativeQuery = true)
  List<Task> findAllByUserId(@Param("userId") Long userId);

  // При создании таски, эта таска ничья. Этот метод связывает таску с пользователем
//  void assignToUserById(@Param("taskId") Long taskId, @Param("userId") Long userId);

// Эти два метода заменяются на save()
//  void update(Task task);
//  void create(Task task);

    // delete => deleteById
//  void delete(Long id);
}
