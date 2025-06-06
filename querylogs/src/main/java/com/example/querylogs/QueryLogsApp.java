package com.example.querylogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

@SpringBootApplication
public class QueryLogsApp implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(QueryLogsApp.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(QueryLogsApp.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("\n\nUsing jdbcTemplate ++++++++++");

        var create = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE,
                    age INTEGER
                )
                """;
        jdbcTemplate.execute(create);

        var inserts = """
                INSERT INTO users (name, email, age) VALUES
                ('Alice Smith', 'alice.smith@example.com', 30),
                ('Bob Johnson', 'bob.j@example.com', 24),
                ('Charlie Brown', 'charlie.b@example.com', 35),
                ('Diana Miller', 'diana.m@example.com', 28),
                ('Eve Davis', 'eve.d@example.com', 42);
                """;
        jdbcTemplate.execute(inserts);

        jdbcTemplate.queryForList("select * from users");
        jdbcTemplate.queryForMap("SELECT * FROM users WHERE email='alice.smith@example.com'");
        jdbcTemplate.queryForObject("SELECT email FROM users WHERE email='alice.smith@example.com'", String.class);

        // This query won't be logged.
        // JdbcTemplate has to be Spring injected bean
        log.info("\n\nUsing local instance +++++++++++");
        new JdbcTemplate(dataSource).queryForObject("SELECT email FROM users WHERE name='Charlie Brown'", String.class);

        log.info("\n\nUsing namedParameterJdbcTemplate +++++++++");
        namedParameterJdbcTemplate.queryForList("select * from users", Map.of());
        namedParameterJdbcTemplate.queryForMap("SELECT * FROM users WHERE email='alice.smith@example.com'", Map.of());
        namedParameterJdbcTemplate.queryForObject("SELECT email FROM users WHERE email='alice.smith@example.com'", Map.of(), String.class);

        jdbcTemplate.execute("drop table users");
    }

}
