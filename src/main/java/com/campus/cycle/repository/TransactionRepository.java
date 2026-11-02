package com.campus.cycle.repository;

import com.campus.cycle.config.DatabaseManager;
import com.campus.cycle.domain.Transaction;
import com.campus.cycle.exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class TransactionRepository {
    private final DatabaseManager db;
    public TransactionRepository(DatabaseManager db) { this.db = db; }

    public Transaction create(long itemId, long buyerId, long sellerId, String message) {
        String sql = "INSERT INTO transactions(item_id,buyer_id,seller_id,message) VALUES(?,?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, itemId); ps.setLong(2, buyerId); ps.setLong(3, sellerId); ps.setString(4, message); ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { return new Transaction(keys.next() ? keys.getLong(1) : 0, itemId, "", buyerId, sellerId, "PENDING", message); }
        } catch (SQLException e) { throw new DataAccessException("创建流转记录失败", e); }
    }

    public List<Transaction> findByUser(long userId) {
        String sql = "SELECT t.id,t.item_id,i.name,t.buyer_id,t.seller_id,t.status,t.message FROM transactions t JOIN items i ON i.id=t.item_id WHERE t.buyer_id=? OR t.seller_id=? ORDER BY t.id DESC";
        List<Transaction> result = new ArrayList<>();
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId); ps.setLong(2, userId); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) result.add(map(rs)); }
            return result;
        } catch (SQLException e) { throw new DataAccessException("读取流转记录失败", e); }
    }

    public Transaction find(long id) {
        String sql = "SELECT t.id,t.item_id,i.name,t.buyer_id,t.seller_id,t.status,t.message FROM transactions t JOIN items i ON i.id=t.item_id WHERE t.id=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) { throw new DataAccessException("读取流转记录失败", e); }
    }

    public void complete(long id, long actorId) {
        String sql = "UPDATE transactions SET status='COMPLETED',completed_at=CURRENT_TIMESTAMP WHERE id=? AND (buyer_id=? OR seller_id=?) AND status='PENDING'";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setLong(1, id); ps.setLong(2, actorId); ps.setLong(3, actorId); ps.executeUpdate(); }
        catch (SQLException e) { throw new DataAccessException("完成流转失败", e); }
    }

    public long completedCount() {
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM transactions WHERE status='COMPLETED'"); ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getLong(1) : 0; }
        catch (SQLException e) { throw new DataAccessException("统计流转失败", e); }
    }

    public void addRating(long transactionId, long from, long to, int score, String comment) {
        String sql = "INSERT INTO ratings(transaction_id,from_user_id,to_user_id,score,comment) VALUES(?,?,?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setLong(1, transactionId); ps.setLong(2, from); ps.setLong(3, to); ps.setInt(4, score); ps.setString(5, comment); ps.executeUpdate(); }
        catch (SQLException e) { throw new DataAccessException("保存评价失败，可能已评价", e); }
    }

    private Transaction map(ResultSet rs) throws SQLException { return new Transaction(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getLong(4), rs.getLong(5), rs.getString(6), rs.getString(7)); }
}
