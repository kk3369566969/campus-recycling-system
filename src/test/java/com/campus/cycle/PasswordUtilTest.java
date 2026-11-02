package com.campus.cycle;

import com.campus.cycle.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test void sha256IsDeterministicAndNotPlainText() {
        assertEquals("42c97db3df4b1373d9484116af081d539c72309ff2adf6f20cd55c7951a355d3", PasswordUtil.sha256("campus"));
        assertNotEquals("campus", PasswordUtil.sha256("campus"));
    }
}
