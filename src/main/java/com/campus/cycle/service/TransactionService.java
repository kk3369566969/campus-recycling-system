package com.campus.cycle.service;

import com.campus.cycle.domain.Item;
import com.campus.cycle.domain.Transaction;
import com.campus.cycle.events.AppEvent;
import com.campus.cycle.events.EventPublisher;
import com.campus.cycle.exception.ValidationException;
import com.campus.cycle.repository.DaoFactory;
import com.campus.cycle.repository.ItemRepository;
import com.campus.cycle.repository.TransactionRepository;
import java.util.List;

public final class TransactionService {
    private final TransactionRepository transactions = DaoFactory.transactions();
    private final ItemRepository items = DaoFactory.items();
    private final EventPublisher events;
    public TransactionService(EventPublisher events) { this.events = events; }

    public Transaction initiate(Item item, long buyerId, String message) {
        if (item == null || !item.isAvailable()) throw new ValidationException("该物品当前不可发起流转");
        if (item.ownerId() == buyerId) throw new ValidationException("不能对自己发布的物品发起流转");
        Transaction result = transactions.create(item.id(), buyerId, item.ownerId(), message == null ? "" : message.trim());
        events.publish(new AppEvent("TRANSACTION_CREATED", String.valueOf(result.id())));
        return result;
    }
    public List<Transaction> findByUser(long userId) { return transactions.findByUser(userId); }
    public void complete(long transactionId, long actorId) {
        Transaction transaction = transactions.find(transactionId);
        if (transaction == null || !(transaction.buyerId() == actorId || transaction.sellerId() == actorId)) throw new ValidationException("无权操作该流转记录");
        transactions.complete(transactionId, actorId); items.updateStatus(transaction.itemId(), "SOLD"); events.publish(new AppEvent("TRANSACTION_COMPLETED", String.valueOf(transactionId)));
    }
    public void rate(long transactionId, long from, int score, String comment) {
        Transaction t = transactions.find(transactionId);
        if (t == null || !"COMPLETED".equals(t.status())) throw new ValidationException("只有已完成流转才能评价");
        if (from != t.buyerId() && from != t.sellerId()) throw new ValidationException("无权评价该流转");
        if (score < 1 || score > 5) throw new ValidationException("评分需为 1-5 分");
        long to = from == t.buyerId() ? t.sellerId() : t.buyerId();
        transactions.addRating(transactionId, from, to, score, comment == null ? "" : comment.trim());
        events.publish(new AppEvent("RATING_CREATED", String.valueOf(transactionId)));
    }
}
