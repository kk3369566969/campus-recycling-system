package com.campus.cycle;

import com.campus.cycle.db.SchemaInitializer;
import com.campus.cycle.domain.Item;
import com.campus.cycle.domain.Statistics;
import com.campus.cycle.domain.Transaction;
import com.campus.cycle.domain.User;
import com.campus.cycle.events.EventPublisher;
import com.campus.cycle.service.AuthService;
import com.campus.cycle.service.MarketplaceService;
import com.campus.cycle.service.StatisticsService;
import com.campus.cycle.service.TransactionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Exercises the real SQLite-backed business flow used by the Swing UI. */
class CoreFlowIntegrationTest {
    @Test
    void userPublishesSearchesAndCompletesAFlow() {
        SchemaInitializer.initialize();
        EventPublisher events = new EventPublisher();
        AuthService auth = new AuthService(events);
        String suffix = String.valueOf(System.nanoTime());
        User seller = auth.register("seller" + suffix.substring(suffix.length() - 8), "123456", "测试发布者");
        User buyer = auth.login("demo", "123456");
        MarketplaceService marketplace = new MarketplaceService(events);
        Item published = marketplace.publish(seller.id(), "测试教材", "教材", "集成测试物品", 12, "测试,教材");
        List<Item> found = marketplace.search(buyer.id(), "测试教材", "教材", 0, 20);
        assertTrue(found.stream().anyMatch(item -> item.id() == published.id()));

        TransactionService flow = new TransactionService(events);
        Transaction transaction = flow.initiate(found.stream().filter(item -> item.id() == published.id()).findFirst().orElseThrow(), buyer.id(), "请保留，谢谢");
        flow.complete(transaction.id(), buyer.id());
        assertEquals("COMPLETED", flow.findByUser(buyer.id()).stream().filter(t -> t.id() == transaction.id()).findFirst().orElseThrow().status());

        Statistics stats = new StatisticsService().summary();
        assertTrue(stats.totalItems() >= 1);
        assertTrue(stats.completedTransactions() >= 1);
    }
}
