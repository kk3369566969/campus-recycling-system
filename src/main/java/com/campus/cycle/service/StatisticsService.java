package com.campus.cycle.service;

import com.campus.cycle.config.DatabaseManager;
import com.campus.cycle.domain.Statistics;
import com.campus.cycle.exception.DataAccessException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public final class StatisticsService {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public Statistics summary() {
        String sql = "SELECT (SELECT COUNT(*) FROM items),(SELECT COUNT(*) FROM items WHERE status='AVAILABLE'),(SELECT COUNT(*) FROM transactions WHERE status='COMPLETED'),(SELECT COUNT(*) FROM users WHERE active=1)";
        try (var c = db.getConnection(); var ps = c.prepareStatement(sql); var rs = ps.executeQuery()) {
            if (!rs.next()) return new Statistics(0,0,0,0,0);
            long total = rs.getLong(1), available = rs.getLong(2), completed = rs.getLong(3), users = rs.getLong(4);
            return new Statistics(total, available, completed, completed * 1.8, users);
        } catch (SQLException e) { throw new DataAccessException("读取统计失败", e); }
    }

    public void exportCsv(Path path) {
        Statistics s = summary();
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            Files.writeString(path, "指标,数值\n物品总数," + s.totalItems() + "\n可用物品," + s.availableItems() + "\n已完成流转," + s.completedTransactions() + "\n估算节约碳排(kg)," + s.carbonSavedKg() + "\n活跃用户," + s.activeUsers() + "\n", StandardCharsets.UTF_8);
        } catch (IOException e) { throw new DataAccessException("导出 CSV 失败", e); }
    }
}
