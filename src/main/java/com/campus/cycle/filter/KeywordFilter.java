package com.campus.cycle.filter;

import com.campus.cycle.domain.Item;

public record KeywordFilter(String keyword) implements ItemFilter {
    @Override public boolean matches(Item i) {
        if (keyword == null || keyword.isBlank()) return true;
        String q = keyword.toLowerCase();
        return (i.name() + " " + i.description() + " " + i.tags()).toLowerCase().contains(q);
    }
}
