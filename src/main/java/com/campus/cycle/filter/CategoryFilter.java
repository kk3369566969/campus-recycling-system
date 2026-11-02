package com.campus.cycle.filter;

import com.campus.cycle.domain.Item;

public record CategoryFilter(String category) implements ItemFilter {
    @Override public boolean matches(Item i) { return category == null || category.isBlank() || "全部".equals(category) || category.equals(i.category()); }
}
