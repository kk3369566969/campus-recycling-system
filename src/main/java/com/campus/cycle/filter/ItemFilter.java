package com.campus.cycle.filter;

import com.campus.cycle.domain.Item;

@FunctionalInterface
public interface ItemFilter { boolean matches(Item item); }
