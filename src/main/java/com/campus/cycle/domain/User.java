package com.campus.cycle.domain;

public record User(long id, String username, String displayName, String role, double creditScore) {
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
