package com.campus.cycle.events;

import com.campus.cycle.util.AppLogger;

public final class AuditLogListener implements EventListener {
    @Override public void onEvent(AppEvent event) { AppLogger.info(event.type() + " - " + event.detail()); }
}
