package com.campus.cycle.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Shared visual language for the desktop client. */
public final class Theme {
    public static final Color FOREST = new Color(18, 60, 53);
    public static final Color FOREST_2 = new Color(28, 91, 72);
    public static final Color LEAF = new Color(42, 145, 103);
    public static final Color MINT = new Color(231, 245, 236);
    public static final Color MIST = new Color(245, 248, 246);
    public static final Color INK = new Color(28, 43, 38);
    public static final Color MUTED = new Color(102, 119, 111);
    public static final Color LINE = new Color(218, 229, 222);
    public static final Color AMBER = new Color(224, 143, 65);
    public static final Color WHITE = Color.WHITE;
    /** Compatibility aliases used by the first UI iteration. */
    public static final Color GREEN = LEAF;
    public static final Color PALE = MINT;
    public static final Color CREAM = MIST;
    private static final Font UI = new Font("Microsoft YaHei UI", Font.PLAIN, 13);
    private static final Font UI_BOLD = new Font("Microsoft YaHei UI", Font.BOLD, 13);
    private Theme() {}

    public static void install() {
        UIManager.put("Panel.background", MIST); UIManager.put("OptionPane.background", WHITE);
        UIManager.put("OptionPane.messageForeground", INK); UIManager.put("Label.foreground", INK);
        UIManager.put("Label.font", UI); UIManager.put("TextField.font", UI); UIManager.put("PasswordField.font", UI);
        UIManager.put("TextArea.font", UI); UIManager.put("ComboBox.font", UI); UIManager.put("Table.font", UI);
        UIManager.put("Table.background", WHITE); UIManager.put("Table.foreground", INK); UIManager.put("Table.gridColor", LINE);
        UIManager.put("Table.selectionBackground", MINT); UIManager.put("Table.selectionForeground", INK);
        UIManager.put("TableHeader.font", UI_BOLD); UIManager.put("TableHeader.background", FOREST); UIManager.put("TableHeader.foreground", WHITE);
        UIManager.put("TabbedPane.font", UI_BOLD); UIManager.put("TabbedPane.selected", WHITE);
        UIManager.put("Button.font", UI_BOLD); UIManager.put("Button.background", LEAF); UIManager.put("Button.foreground", WHITE);
        UIManager.put("Button.select", FOREST_2); UIManager.put("ScrollPane.border", new LineBorder(LINE, 1));
    }

    public static JLabel title(String text) { return label(text, 28, INK, true); }
    public static JLabel pageTitle(String text) { return label(text, 22, INK, true); }
    public static JLabel subtitle(String text) { return label(text, 13, MUTED, false); }
    public static JLabel eyebrow(String text) { return label(text, 11, LEAF, true); }
    private static JLabel label(String text, int size, Color color, boolean bold) { JLabel l = new JLabel(text); l.setFont(new Font("Microsoft YaHei UI", bold ? Font.BOLD : Font.PLAIN, size)); l.setForeground(color); return l; }

    public static JPanel card() { return card(WHITE); }
    public static JPanel card(Color color) { JPanel p = new RoundedPanel(color, 18); p.setBorder(new EmptyBorder(18, 20, 18, 20)); return p; }
    public static JButton button(String text) { return button(text, LEAF); }
    public static JButton button(String text, Color background) { JButton b = new JButton(text); b.setFont(UI_BOLD); b.setForeground(WHITE); b.setBackground(background); b.setFocusPainted(false); b.setBorder(new EmptyBorder(10, 18, 10, 18)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b; }
    public static JButton ghostButton(String text) { JButton b = button(text, WHITE); b.setForeground(FOREST); b.setBorder(BorderFactory.createCompoundBorder(new LineBorder(LINE, 1), new EmptyBorder(9, 16, 9, 16))); return b; }
    public static void styleInput(JComponent c) { c.setFont(UI); c.setForeground(INK); c.setBackground(WHITE); c.setBorder(BorderFactory.createCompoundBorder(new LineBorder(LINE, 1), new EmptyBorder(8, 10, 8, 10))); c.setPreferredSize(new Dimension(220, 38)); }
    public static void styleTable(JTable table) { table.setFont(UI); table.setRowHeight(38); table.setShowVerticalLines(false); table.setShowHorizontalLines(true); table.setGridColor(LINE); table.setIntercellSpacing(new Dimension(0, 1)); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); table.setFillsViewportHeight(true); table.getTableHeader().setFont(UI_BOLD); table.getTableHeader().setPreferredSize(new Dimension(0, 38)); table.getTableHeader().setOpaque(true); table.getTableHeader().setBackground(FOREST); table.getTableHeader().setForeground(WHITE); DefaultTableCellRenderer r = new DefaultTableCellRenderer(); r.setBorder(new EmptyBorder(0, 10, 0, 10)); table.setDefaultRenderer(Object.class, r); }

    private static final class RoundedPanel extends JPanel {
        private final Color color; private final int radius;
        private RoundedPanel(Color color, int radius) { this.color = color; this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) { Graphics2D g = (Graphics2D) graphics.create(); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g.setColor(color); g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius); g.dispose(); super.paintComponent(graphics); }
    }
}
