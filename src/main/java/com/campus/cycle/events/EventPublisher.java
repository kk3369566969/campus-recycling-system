package com.campus.cycle.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventPublisher {
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
    public void subscribe(EventListener listener) { if (listener != null) listeners.add(listener); }
    public void publish(AppEvent event) { listeners.forEach(listener -> listener.onEvent(event)); }
}
