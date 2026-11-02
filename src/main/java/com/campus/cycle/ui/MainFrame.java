package com.campus.cycle.ui;

import com.campus.cycle.domain.*;
import com.campus.cycle.events.*;
import com.campus.cycle.repository.DaoFactory;
import com.campus.cycle.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

public final class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final EventPublisher events = new EventPublisher();
    private final MarketplaceService marketplace;
    private final TransactionService transactions;
    private final StatisticsService statistics = new StatisticsService();
    private JTable itemTable;
    private DefaultTableModel itemModel;
    private List<Item> currentItems = List.of();
    private JLabel status;

    public MainFrame(User user) {
        this.user = user;
        events.subscribe(new AuditLogListener());
        marketplace = new MarketplaceService(events);
        transactions = new TransactionService(events);
        setTitle("循环校园 · 资源循环流转");
        setSize(1180, 790);
        setMinimumSize(new Dimension(980, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        build();
        refreshItems();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.MIST);
        root.add(header(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        tabs.setBackground(Theme.MIST);
        tabs.setForeground(Theme.FOREST);
        tabs.addTab(" 物品广场 ", marketplacePanel());
        tabs.addTab(" 我的流转 ", transactionPanel());
        tabs.addTab(" 数据看板 ", dashboardPanel());
        if (user.isAdmin()) {
            tabs.addTab(" 管理后台 ", adminPanel());
        }
        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(Theme.FOREST);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 30, 20, 30));

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("◒  循环校园");
        logo.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 25));
        logo.setForeground(Color.WHITE);
        JLabel tagline = new JLabel("让每一件物品的生命周期更长一点");
        tagline.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(191, 222, 207));
        identity.add(logo);
        identity.add(Box.createVerticalStrut(5));
        identity.add(tagline);

        JPanel account = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        account.setOpaque(false);
        JLabel profile = new JLabel(user.displayName() + "  ·  " + (user.isAdmin() ? "管理员" : "学生") + "  ·  信用 " + String.format("%.1f", user.creditScore()));
        profile.setForeground(new Color(221, 239, 227));
        profile.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        JButton logout = Theme.button("退出登录", new Color(177, 76, 64));
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame(events).setVisible(true);
        });
        account.add(profile);
        account.add(logout);
        panel.add(identity, BorderLayout.WEST);
        panel.add(account, BorderLayout.EAST);
        return panel;
    }

    private JPanel marketplacePanel() {
        JPanel root = pageRoot();
        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        JLabel eyebrow = Theme.eyebrow("MARKETPLACE / 校园闲置");
        JLabel title = Theme.pageTitle("找到下一件值得继续使用的物品");
        JLabel desc = Theme.subtitle("按关键词、分类和价格筛选，或发布一件你暂时用不到的好物。");
        intro.add(eyebrow);
        intro.add(Box.createVerticalStrut(6));
        intro.add(title);
        intro.add(Box.createVerticalStrut(4));
        intro.add(desc);

        JPanel filters = Theme.card();
        filters.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
        JTextField key = new JTextField(14);
        JComboBox<String> category = new JComboBox<>(new String[]{"全部", "教材", "数码", "文体", "生活", "其他"});
        JTextField min = new JTextField("0", 5);
        JTextField max = new JTextField("100000", 6);
        Theme.styleInput(key);
        Theme.styleInput(category);
        Theme.styleInput(min);
        Theme.styleInput(max);
        JButton search = Theme.ghostButton("筛选");
        JButton publish = Theme.button("发布闲置");
        filters.add(fieldLabel("关键词"));
        filters.add(key);
        filters.add(fieldLabel("分类"));
        filters.add(category);
        filters.add(fieldLabel("价格"));
        filters.add(min);
        filters.add(new JLabel("至"));
        filters.add(max);
        filters.add(search);
        filters.add(publish);

        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(intro, BorderLayout.NORTH);
        top.add(filters, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        itemModel = new DefaultTableModel(new Object[]{"物品", "分类", "描述", "价格", "发布人", "状态", "收藏"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        itemTable = new JTable(itemModel);
        Theme.styleTable(itemTable);
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(320);
        itemTable.getColumnModel().getColumn(6).setPreferredWidth(55);
        root.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(12, 0));
        actions.setOpaque(false);
        status = Theme.subtitle("准备就绪");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton favorite = Theme.ghostButton("收藏 / 取消");
        JButton contact = Theme.button("发起流转");
        JButton withdraw = Theme.button(user.isAdmin() ? "管理员下架" : "下架我的物品", new Color(177, 76, 64));
        buttons.add(favorite);
        buttons.add(contact);
        buttons.add(withdraw);
        actions.add(status, BorderLayout.WEST);
        actions.add(buttons, BorderLayout.EAST);
        root.add(actions, BorderLayout.SOUTH);

        search.addActionListener(e -> {
            try {
                currentItems = marketplace.search(user.id(), key.getText(), (String) category.getSelectedItem(),
                        Double.parseDouble(min.getText()), Double.parseDouble(max.getText()));
                fillItems();
            } catch (Exception ex) {
                error(ex);
            }
        });
        publish.addActionListener(e -> publishDialog());
        favorite.addActionListener(e -> withSelected(item -> marketplace.toggleFavorite(user.id(), item.id())));
        contact.addActionListener(e -> contactSelected());
        withdraw.addActionListener(e -> withSelected(item -> {
            if (user.isAdmin()) {
                marketplace.moderateWithdraw(item.id());
            } else {
                marketplace.withdraw(user.id(), item.id());
            }
            refreshItems();
        }));
        return root;
    }

    private JPanel transactionPanel() {
        JPanel root = pageRoot();
        JPanel intro = Theme.card(Theme.MINT);
        intro.setLayout(new BorderLayout(12, 0));
        JLabel title = Theme.pageTitle("我的流转");
        JLabel copy = Theme.subtitle("记录沟通、完成状态和互相评价，让每次交换都有迹可循。");
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(copy);
        intro.add(text, BorderLayout.WEST);
        root.add(intro, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"编号", "物品", "角色", "状态", "留言"}, 0);
        JTable table = new JTable(model);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(320);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton refresh = Theme.ghostButton("刷新");
        JButton complete = Theme.button("标记已完成");
        JButton rate = Theme.button("评价");
        actions.add(refresh);
        actions.add(complete);
        actions.add(rate);
        root.add(actions, BorderLayout.SOUTH);

        Runnable load = () -> {
            model.setRowCount(0);
            for (Transaction transaction : transactions.findByUser(user.id())) {
                model.addRow(new Object[]{transaction.id(), transaction.itemName(),
                        transaction.buyerId() == user.id() ? "发起方" : "发布方",
                        transaction.status(), transaction.message()});
            }
        };
        load.run();
        refresh.addActionListener(e -> load.run());
        complete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    transactions.complete((long) model.getValueAt(row, 0), user.id());
                    load.run();
                } catch (Exception ex) {
                    error(ex);
                }
            }
        });
        rate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            String score = JOptionPane.showInputDialog(this, "请输入 1-5 分", "5");
            if (score != null) {
                try {
                    transactions.rate((long) model.getValueAt(row, 0), user.id(), Integer.parseInt(score), "来自校园用户的评价");
                    JOptionPane.showMessageDialog(this, "评价已保存");
                } catch (Exception ex) {
                    error(ex);
                }
            }
        });
        return root;
    }

    private JPanel dashboardPanel() {
        JPanel root = pageRoot();
        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.add(Theme.eyebrow("IMPACT / 低碳数据"));
        intro.add(Box.createVerticalStrut(6));
        intro.add(Theme.pageTitle("每一次流转，都在减少一次浪费"));
        intro.add(Box.createVerticalStrut(4));
        intro.add(Theme.subtitle("用可读的数据记录校园资源循环的真实进展。"));
        root.add(intro, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(0, 0, 14, 14);

        JLabel carbonValue = new JLabel("—");
        carbonValue.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 42));
        carbonValue.setForeground(Color.WHITE);
        JPanel hero = Theme.card(Theme.FOREST);
        hero.setLayout(new BorderLayout(14, 8));
        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        JLabel heroEyebrow = new JLabel("CIRCULAR IMPACT");
        heroEyebrow.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 11));
        heroEyebrow.setForeground(new Color(164, 224, 188));
        JLabel heroTitle = new JLabel("校园资源已经少浪费了一点");
        heroTitle.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 21));
        heroTitle.setForeground(Color.WHITE);
        JLabel heroNote = new JLabel("按完成流转估算的节约碳排");
        heroNote.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        heroNote.setForeground(new Color(205, 228, 216));
        heroText.add(heroEyebrow);
        heroText.add(Box.createVerticalStrut(9));
        heroText.add(heroTitle);
        heroText.add(Box.createVerticalStrut(5));
        heroText.add(heroNote);
        hero.add(heroText, BorderLayout.NORTH);
        JPanel carbon = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        carbon.setOpaque(false);
        carbon.add(carbonValue);
        JLabel kg = new JLabel(" kg CO₂e");
        kg.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        kg.setForeground(new Color(164, 224, 188));
        carbon.add(kg);
        hero.add(carbon, BorderLayout.CENTER);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; gc.gridheight = 2; gc.insets = new Insets(0, 0, 14, 14);
        content.add(hero, gc);

        JLabel totalValue = metricValue();
        JLabel availableValue = metricValue();
        JLabel completedValue = metricValue();
        content.add(metricCard("物品总数", totalValue, "已进入校园循环的物品", Theme.MINT), metricConstraint(2, 0));
        content.add(metricCard("当前可用", availableValue, "仍在等待下一位使用者", new Color(239, 246, 238)), metricConstraint(3, 0));
        content.add(metricCard("完成流转", completedValue, "已经完成交接的记录", new Color(255, 245, 231)), metricConstraint(2, 1));

        JPanel insight = Theme.card(Color.WHITE);
        insight.setLayout(new BoxLayout(insight, BoxLayout.Y_AXIS));
        insight.add(Theme.eyebrow("RESOURCE LOOP"));
        insight.add(Box.createVerticalStrut(6));
        insight.add(Theme.pageTitle("资源复用率"));
        insight.add(Box.createVerticalStrut(3));
        JLabel reuseLabel = Theme.subtitle("完成流转 ÷ 物品总数");
        insight.add(reuseLabel);
        insight.add(Box.createVerticalStrut(13));
        JProgressBar reuseBar = progressBar();
        insight.add(reuseBar);
        JLabel reuseValue = Theme.subtitle("—");
        reuseValue.setAlignmentX(LEFT_ALIGNMENT);
        insight.add(Box.createVerticalStrut(6));
        insight.add(reuseValue);
        insight.add(Box.createVerticalGlue());
        content.add(insight, metricConstraint(3, 1));

        root.add(content, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);
        bottom.add(Theme.subtitle("每完成一次流转，按 1.8kg 估算节约碳排。"), BorderLayout.WEST);
        JButton export = Theme.button("导出 CSV 报表");
        bottom.add(export, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        Runnable load = () -> {
            Statistics s = statistics.summary();
            carbonValue.setText(String.format("%.1f", s.carbonSavedKg()));
            totalValue.setText(String.valueOf(s.totalItems()));
            availableValue.setText(String.valueOf(s.availableItems()));
            completedValue.setText(String.valueOf(s.completedTransactions()));
            int reuse = s.totalItems() == 0 ? 0 : (int) Math.min(100, Math.round(s.completedTransactions() * 100.0 / s.totalItems()));
            reuseBar.setValue(reuse);
            reuseValue.setText(reuse + "% 的物品完成了流转");
        };
        load.run();
        export.addActionListener(e -> {
            try {
                statistics.exportCsv(Path.of("data", "statistics.csv"));
                JOptionPane.showMessageDialog(this, "已导出到 data/statistics.csv");
            } catch (Exception ex) {
                error(ex);
            }
        });
        return root;
    }

    private JLabel metricValue() {
        JLabel label = new JLabel("—");
        label.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 30));
        label.setForeground(Theme.FOREST);
        return label;
    }

    private JPanel metricCard(String name, JLabel value, String note, Color tint) {
        JPanel card = Theme.card(tint);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel title = Theme.subtitle(name);
        title.setForeground(Theme.FOREST_2);
        JLabel detail = Theme.subtitle(note);
        card.add(title);
        card.add(Box.createVerticalStrut(11));
        card.add(value);
        card.add(Box.createVerticalGlue());
        card.add(detail);
        return card;
    }

    private GridBagConstraints metricConstraint(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y; c.gridwidth = 1; c.gridheight = 1;
        c.weightx = 1; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 14, 14);
        return c;
    }

    private JProgressBar progressBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(0);
        bar.setStringPainted(false);
        bar.setBorderPainted(false);
        bar.setForeground(Theme.LEAF);
        bar.setBackground(Theme.MINT);
        bar.setPreferredSize(new Dimension(100, 12));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        return bar;
    }

    private JPanel adminPanel() {
        JPanel root = pageRoot();
        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.add(Theme.eyebrow("ADMIN / 平台管理"));
        intro.add(Box.createVerticalStrut(6));
        intro.add(Theme.pageTitle("管理后台"));
        intro.add(Box.createVerticalStrut(4));
        intro.add(Theme.subtitle("查看用户信用与平台流转情况，违规物品可在物品广场下架。"));
        root.add(intro, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"用户 ID", "账号", "昵称", "角色", "信用"}, 0);
        JTable table = new JTable(model);
        Theme.styleTable(table);
        for (User u : DaoFactory.users().findAll()) {
            model.addRow(new Object[]{u.id(), u.username(), u.displayName(), u.role(), u.creditScore()});
        }
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(Theme.subtitle("当前总流转：" + statistics.summary().completedTransactions()), BorderLayout.SOUTH);
        return root;
    }

    private JPanel pageRoot() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 12, 4, 12));
        panel.setBackground(Theme.MIST);
        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        label.setForeground(Theme.MUTED);
        return label;
    }

    private void refreshItems() {
        currentItems = marketplace.search(user.id(), "", "全部", 0, 1000000);
        fillItems();
    }

    private void fillItems() {
        itemModel.setRowCount(0);
        for (Item item : currentItems) {
            itemModel.addRow(new Object[]{item.name(), item.category(), item.description(),
                    String.format("¥ %.2f", item.price()), item.ownerName(), item.status(), item.favorite() ? "★" : ""});
        }
        status.setText("共 " + currentItems.size() + " 件物品");
    }

    private Item selected() {
        int row = itemTable.getSelectedRow();
        return row >= 0 && row < currentItems.size() ? currentItems.get(row) : null;
    }

    private void withSelected(java.util.function.Consumer<Item> action) {
        Item item = selected();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "请先选择一件物品");
            return;
        }
        try {
            action.accept(item);
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void contactSelected() {
        Item item = selected();
        if (item == null) return;
        String message = JOptionPane.showInputDialog(this, "给发布者留言", "我对这件物品感兴趣");
        if (message != null) {
            try {
                transactions.initiate(item, user.id(), message);
                JOptionPane.showMessageDialog(this, "已发起流转意向");
            } catch (Exception ex) {
                error(ex);
            }
        }
    }

    private void publishDialog() {
        JTextField name = new JTextField();
        JTextField description = new JTextField();
        JTextField price = new JTextField("0");
        JTextField tags = new JTextField();
        JComboBox<String> category = new JComboBox<>(new String[]{"教材", "数码", "文体", "生活", "其他"});
        Theme.styleInput(name); Theme.styleInput(description); Theme.styleInput(price); Theme.styleInput(tags); Theme.styleInput(category);
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(fieldLabel("名称")); panel.add(name);
        panel.add(fieldLabel("分类")); panel.add(category);
        panel.add(fieldLabel("描述")); panel.add(description);
        panel.add(fieldLabel("价格")); panel.add(price);
        panel.add(fieldLabel("标签")); panel.add(tags);
        if (JOptionPane.showConfirmDialog(this, panel, "发布闲置物品", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                marketplace.publish(user.id(), name.getText(), (String) category.getSelectedItem(),
                        description.getText(), Double.parseDouble(price.getText()), tags.getText());
                refreshItems();
            } catch (Exception ex) {
                error(ex);
            }
        }
    }

    private void error(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "操作未完成", JOptionPane.WARNING_MESSAGE);
    }
}

