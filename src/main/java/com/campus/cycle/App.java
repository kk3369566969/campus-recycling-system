package com.campus.cycle;

import com.campus.cycle.db.SchemaInitializer;
import com.campus.cycle.events.EventPublisher;
import com.campus.cycle.events.AuditLogListener;
import com.campus.cycle.ui.LoginFrame;
import com.campus.cycle.ui.Theme;
import javax.swing.*;

public final class App {
    private App() {}
    public static void main(String[] args) {
        SchemaInitializer.initialize(); Theme.install();
        EventPublisher events = new EventPublisher();
        events.subscribe(new AuditLogListener());
        SwingUtilities.invokeLater(() -> new LoginFrame(events).setVisible(true));
    }
}
