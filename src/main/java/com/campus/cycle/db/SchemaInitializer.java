package com.campus.cycle.db;

import com.campus.cycle.config.DatabaseManager;
import com.campus.cycle.exception.DataAccessException;
import com.campus.cycle.util.PasswordUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.stream.Collectors;

public final class SchemaInitializer {
    private SchemaInitializer() {}

    public static void initialize() {
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            String sql;
            try (var input = SchemaInitializer.class.getResourceAsStream("/db/schema.sql")) {
                if (input == null) throw new IllegalStateException("schema.sql not found");
                sql = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining("\n"));
            }
            try (var statement = connection.createStatement()) {
                for (String command : sql.split(";")) {
                    if (!command.isBlank()) statement.execute(command);
                }
            }
            seed(connection);
        } catch (Exception e) {
            if (e instanceof DataAccessException dae) throw dae;
            throw new DataAccessException("初始化数据库失败", e);
        }
    }

    private static void seed(Connection c) throws Exception {
        try (var check = c.prepareStatement("SELECT COUNT(*) FROM users"); var rs = check.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (var ps = c.prepareStatement("INSERT INTO users(username,password_hash,display_name,role) VALUES(?,?,?,?)")) {
            ps.setString(1, "admin"); ps.setString(2, PasswordUtil.sha256("admin123"));
            ps.setString(3, "系统管理员"); ps.setString(4, "ADMIN"); ps.executeUpdate();
            ps.setString(1, "demo"); ps.setString(2, PasswordUtil.sha256("123456"));
            ps.setString(3, "演示用户"); ps.setString(4, "STUDENT"); ps.executeUpdate();
        }
        try (var ps = c.prepareStatement("INSERT INTO items(owner_id,name,category,description,price,status,tags) VALUES(2,?,?,?,?,?,?)")) {
            ps.setString(1, "高等数学教材"); ps.setString(2, "教材"); ps.setString(3, "九成新，适合大一课程");
            ps.setDouble(4, 18); ps.setString(5, "AVAILABLE"); ps.setString(6, "教材,低碳"); ps.executeUpdate();
        }
    }
}
