package com.example.tasklist3.domain.user;

import com.example.tasklist3.domain.user.Role;
import com.example.tasklist3.domain.task.Task;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

//  @Column(name = "name", unique = true, length = 1024)
  private String name;
  private String username;
  private String password;

  @Transient // не хранится в БД
  private String passwordConfirmation;

  @Column(name = "role")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "users_roles")
  @Enumerated(value = EnumType.STRING)
  private Set<Role> roles;

  @CollectionTable(name = "users_tasks")
  @OneToMany
  @JoinColumn(name = "task_id")
  private List<Task> tasks;

}
