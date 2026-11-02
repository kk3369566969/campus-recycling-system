package com.campus.cycle.service;

import com.campus.cycle.domain.Item;
import com.campus.cycle.events.AppEvent;
import com.campus.cycle.events.EventPublisher;
import com.campus.cycle.exception.ValidationException;
import com.campus.cycle.filter.*;
import com.campus.cycle.repository.DaoFactory;
import com.campus.cycle.repository.ItemRepository;
import java.util.List;

public final class MarketplaceService {
    private final ItemRepository items = DaoFactory.items();
    private final EventPublisher events;
    public MarketplaceService(EventPublisher events) { this.events = events; }

    public List<Item> search(long viewerId, String keyword, String category, double min, double max) {
        if (min < 0 || max < min) throw new ValidationException("价格区间不合法");
        ItemFilter filter = new CompositeFilter(List.of(new KeywordFilter(keyword), new CategoryFilter(category), new PriceRangeFilter(min, max)));
        return items.findAll(viewerId).stream().filter(filter::matches).toList();
    }

    public Item publish(long ownerId, String name, String category, String description, double price, String tags) {
        if (name == null || name.isBlank() || name.length() > 60) throw new ValidationException("物品名称不能为空且不超过 60 字");
        if (category == null || category.isBlank()) throw new ValidationException("请选择物品分类");
        if (description == null || description.isBlank()) throw new ValidationException("请填写物品描述");
        if (price < 0 || price > 1_000_000) throw new ValidationException("价格范围不合法");
        Item item = items.create(ownerId, name.trim(), category.trim(), description.trim(), price, tags == null ? "" : tags.trim());
        events.publish(new AppEvent("ITEM_PUBLISHED", item.name()));
        return item;
    }

    public void toggleFavorite(long userId, long itemId) { items.toggleFavorite(userId, itemId); events.publish(new AppEvent("FAVORITE_CHANGED", String.valueOf(itemId))); }
    public void withdraw(long ownerId, long itemId) { items.updateStatusByOwner(itemId, ownerId, "WITHDRAWN"); events.publish(new AppEvent("ITEM_WITHDRAWN", String.valueOf(itemId))); }
    public void moderateWithdraw(long itemId) { items.updateStatus(itemId, "WITHDRAWN"); events.publish(new AppEvent("ADMIN_WITHDRAW", String.valueOf(itemId))); }
}
