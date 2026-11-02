package com.campus.cycle.domain;

public record Item(long id, long ownerId, String ownerName, String name, String category,
                   String description, double price, String status, String tags, boolean favorite) {
    public boolean isAvailable() { return "AVAILABLE".equals(status); }
}
