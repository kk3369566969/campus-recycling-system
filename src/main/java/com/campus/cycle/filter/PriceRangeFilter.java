package com.campus.cycle.filter;

import com.campus.cycle.domain.Item;

public record PriceRangeFilter(double min, double max) implements ItemFilter {
    @Override public boolean matches(Item i) { return i.price() >= min && i.price() <= max; }
}
