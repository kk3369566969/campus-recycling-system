package com.campus.cycle.events;

@FunctionalInterface
public interface EventListener { void onEvent(AppEvent event); }
