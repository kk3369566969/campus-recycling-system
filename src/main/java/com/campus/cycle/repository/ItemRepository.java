package com.campus.cycle.repository;

import com.campus.cycle.config.DatabaseManager;
import com.campus.cycle.domain.Item;
import com.campus.cycle.exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class ItemRepository {
    private final DatabaseManager db;
    public ItemRepository(DatabaseManager db) { this.db = db; }

    public List<Item> findAll(long viewerId) {
        String sql = "SELECT i.id,i.owner_id,u.display_name,i.name,i.category,i.description,i.price,i.status,i.tags,CASE WHEN f.user_id IS NULL THEN 0 ELSE 1 END favorite " +
                "FROM items i JOIN users u ON u.id=i.owner_id LEFT JOIN favorites f ON f.item_id=i.id AND f.user_id=? ORDER BY i.id DESC";
        List<Item> result = new ArrayList<>();
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, viewerId); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) result.add(map(rs)); }
            return result;
        } catch (SQLException e) { throw new DataAccessException("读取物品失败", e); }
    }

    public Item create(long ownerId, String name, String category, String description, double price, String tags) {
        String sql = "INSERT INTO items(owner_id,name,category,description,price,tags) VALUES(?,?,?,?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ownerId); ps.setString(2, name); ps.setString(3, category); ps.setString(4, description); ps.setDouble(5, price); ps.setString(6, tags); ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return new Item(rs.next() ? rs.getLong(1) : 0, ownerId, "", name, category, description, price, "AVAILABLE", tags, false); }
        } catch (SQLException e) { throw new DataAccessException("发布物品失败", e); }
    }

    public void updateStatus(long itemId, String status) { update("UPDATE items SET status=? WHERE id=?", status, itemId, null); }
    public void updateStatusByOwner(long itemId, long ownerId, String status) { update("UPDATE items SET status=? WHERE id=? AND owner_id=?", status, itemId, ownerId); }

    public void toggleFavorite(long userId, long itemId) {
        try (Connection c = db.getConnection(); PreparedStatement check = c.prepareStatement("SELECT 1 FROM favorites WHERE user_id=? AND item_id=?")) {
            check.setLong(1, userId); check.setLong(2, itemId);
            boolean exists;
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                } else {
                    exists = false;
                }
            }
            {
                String sql = exists ? "DELETE FROM favorites WHERE user_id=? AND item_id=?" : "INSERT INTO favorites(user_id,item_id) VALUES(?,?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) { ps.setLong(1, userId); ps.setLong(2, itemId); ps.executeUpdate(); }
            }
        } catch (SQLException e) { throw new DataAccessException("更新收藏失败", e); }
    }

    private void update(String sql, String status, long id, Long ownerId) {
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setString(1, status); ps.setLong(2, id); if (ownerId != null) ps.setLong(3, ownerId); ps.executeUpdate(); }
        catch (SQLException e) { throw new DataAccessException("更新物品状态失败", e); }
    }
    private Item map(ResultSet rs) throws SQLException { return new Item(rs.getLong("id"), rs.getLong("owner_id"), rs.getString("display_name"), rs.getString("name"), rs.getString("category"), rs.getString("description"), rs.getDouble("price"), rs.getString("status"), rs.getString("tags"), rs.getInt("favorite") == 1); }
}
