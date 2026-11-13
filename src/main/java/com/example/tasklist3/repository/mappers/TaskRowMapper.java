//package com.example.tasklist3.repository.mappers;
//
//import com.example.tasklist3.domain.task.Status;
//import com.example.tasklist3.domain.task.Task;
//import lombok.SneakyThrows;
//
//import java.sql.ResultSet;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//
//public class TaskRowMapper {
//
//  // мэппим строку из resultSet к объекту
//  @SneakyThrows // внизу есть шанс на ошибку и эта аннотация будет выбрасывать эти ошибки выше
//  public static Task mapRow(ResultSet resultSet) {
//    if (resultSet.next()) {
//      Task task = new Task();
//      task.setId(resultSet.getLong("task_id"));
//      task.setTitle(resultSet.getString("task_title"));
//      task.setDescription(resultSet.getString("task_description"));
//      task.setStatus(Status.valueOf(resultSet.getString("task_status"))); // valueOf т.к статус - это enum
//
//      Timestamp timestamp = resultSet.getTimestamp("task_expiration_date");
//      if (timestamp != null) {
//        task.setExpirationDate(timestamp.toLocalDateTime());
//      }
//
//      return task;
//    }
//
//    return null;
//  }
//
//  @SneakyThrows // внизу есть шанс на ошибку и эта аннотация будет выбрасывать эти ошибки выше
//  public static List<Task> mapRows(ResultSet resultSet) {
//    List<Task> tasks = new ArrayList<>(/* сюда можно задать кол-строк и оптимизируем ~resultSet.getCount() */); // https://www.baeldung.com/java-resultset-number-of-rows
//
//    while (resultSet.next()) {  // next() переносит на 1 указатель task_id
//      Task task = new Task();
//      task.setId(resultSet.getLong("task_id"));
//
//      if (!resultSet.wasNull()) { // у нового пользователя нет тасок
//        task.setTitle(resultSet.getString("task_title"));
//        task.setDescription(resultSet.getString("task_description"));
//        task.setStatus(Status.valueOf(resultSet.getString("task_status"))); // valueOf т.к статус - это enum
//
//        Timestamp timestamp = resultSet.getTimestamp("task_expiration_date");
//        if (timestamp != null) {
//          task.setExpirationDate(timestamp.toLocalDateTime());
//        }
//        tasks.add(task);
//      }
//    }
//
//    return tasks;
//  }
//
//}
