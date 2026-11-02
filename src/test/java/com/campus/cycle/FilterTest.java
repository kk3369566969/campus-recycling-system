package com.campus.cycle;

import com.campus.cycle.domain.Item;
import com.campus.cycle.filter.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FilterTest {
    private final Item item = new Item(1,2,"小林","高等数学教材","教材","九成新教材",18,"AVAILABLE","教材,低碳",false);
    @Test void compositeFilterMatchesBusinessQuery() {
        assertTrue(new CompositeFilter(List.of(new KeywordFilter("数学"), new CategoryFilter("教材"), new PriceRangeFilter(10,20))).matches(item));
        assertFalse(new CompositeFilter(List.of(new KeywordFilter("电脑"), new CategoryFilter("教材"))).matches(item));
    }
}
