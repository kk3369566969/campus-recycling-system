package com.campus.cycle.repository;

import com.campus.cycle.config.DatabaseManager;

/** Factory for data-access components. */
public final class DaoFactory {
    private DaoFactory() {}
    public static UserRepository users() { return new UserRepository(DatabaseManager.getInstance()); }
    public static ItemRepository items() { return new ItemRepository(DatabaseManager.getInstance()); }
    public static TransactionRepository transactions() { return new TransactionRepository(DatabaseManager.getInstance()); }
}
