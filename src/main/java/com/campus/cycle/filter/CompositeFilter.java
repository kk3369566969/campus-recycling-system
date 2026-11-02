package com.campus.cycle.filter;

import com.campus.cycle.domain.Item;
import java.util.List;

public record CompositeFilter(List<ItemFilter> filters) implements ItemFilter {
    @Override public boolean matches(Item item) { return filters.stream().allMatch(f -> f.matches(item)); }
}
