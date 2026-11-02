package com.example.tasklist3.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

  private final DataSource dataSource;

//  @Bean
//  @ConfigurationProperties(prefix = "spring.datasource")
  public Connection getConnection() {
    return DataSourceUtils.getConnection(dataSource);
  }

}
