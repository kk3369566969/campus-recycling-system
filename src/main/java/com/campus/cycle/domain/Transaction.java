package com.campus.cycle.domain;

public record Transaction(long id, long itemId, String itemName, long buyerId, long sellerId,
                          String status, String message) {}
