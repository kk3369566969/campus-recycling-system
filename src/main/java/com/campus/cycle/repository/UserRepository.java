package com.campus.cycle.repository;

import com.campus.cycle.config.DatabaseManager;
import com.campus.cycle.domain.User;
import com.campus.cycle.exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class UserRepository {
    private final DatabaseManager db;
    public UserRepository(DatabaseManager db) { this.db = db; }

    public User findByCredentials(String username, String hash) {
        String sql = "SELECT id,username,display_name,role,credit_score FROM users WHERE username=? AND password_hash=? AND active=1";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) { throw new DataAccessException("查询用户失败", e); }
    }

    public User create(String username, String hash, String displayName) {
        String sql = "INSERT INTO users(username,password_hash,display_name) VALUES(?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username); ps.setString(2, hash); ps.setString(3, displayName); ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { return new User(keys.next() ? keys.getLong(1) : 0, username, displayName, "STUDENT", 5.0); }
        } catch (SQLException e) { throw new DataAccessException("创建用户失败，用户名可能已存在", e); }
    }

    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id,username,display_name,role,credit_score FROM users ORDER BY id"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs));
            return result;
        } catch (SQLException e) { throw new DataAccessException("读取用户失败", e); }
    }

    public void adjustCredit(long userId, double delta) {
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE users SET credit_score=MIN(5.0,MAX(0.0,credit_score+?)) WHERE id=?")) {
            ps.setDouble(1, delta); ps.setLong(2, userId); ps.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException("更新信用分失败", e); }
    }

    private User map(ResultSet rs) throws SQLException { return new User(rs.getLong("id"), rs.getString("username"), rs.getString("display_name"), rs.getString("role"), rs.getDouble("credit_score")); }
}
