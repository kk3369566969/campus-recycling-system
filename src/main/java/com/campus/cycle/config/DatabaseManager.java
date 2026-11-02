package com.campus.cycle.config;

import com.campus.cycle.exception.DataAccessException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Singleton connection provider for the local SQLite database. */
public final class DatabaseManager {
    private static final DatabaseManager INSTANCE = new DatabaseManager();
    private final String url;

    private DatabaseManager() {
        try { Files.createDirectories(Path.of("data")); } catch (Exception e) {
            throw new DataAccessException("无法创建数据目录", e);
        }
        this.url = "jdbc:sqlite:data/campus-cycle.db";
    }

    public static DatabaseManager getInstance() { return INSTANCE; }

    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(url);
            try (var statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
                statement.execute("PRAGMA busy_timeout = 5000");
            }
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException("无法连接 SQLite 数据库", e);
        }
    }
}
