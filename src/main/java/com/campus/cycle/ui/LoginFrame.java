package com.campus.cycle.ui;

import com.campus.cycle.domain.User;
import com.campus.cycle.exception.AuthenticationException;
import com.campus.cycle.events.EventPublisher;
import com.campus.cycle.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final AuthService auth;
    private JTextField username; private JPasswordField password;
    public LoginFrame(EventPublisher events) {
        auth = new AuthService(events); setTitle("循环校园 · 账号登录"); setSize(860, 560); setMinimumSize(new Dimension(760, 500)); setLocationRelativeTo(null); setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(Theme.MIST);
        root.add(welcomePanel(), BorderLayout.WEST); root.add(loginPanel(), BorderLayout.CENTER); add(root);
        getRootPane().setDefaultButton(loginButton);
    }
    private final JButton loginButton = Theme.button("登录");
    private JPanel welcomePanel() { JPanel panel = new JPanel(); panel.setPreferredSize(new Dimension(330, 0)); panel.setBackground(Theme.FOREST); panel.setBorder(new EmptyBorder(42, 38, 42, 32)); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); JLabel mark = new JLabel("◒  循环校园"); mark.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 22)); mark.setForeground(Color.WHITE); JLabel eyebrow = Theme.eyebrow("CAMPUS CYCLE"); eyebrow.setForeground(new Color(164, 224, 188)); JLabel headline = new JLabel("<html>让闲置物品<br>继续流动</html>"); headline.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 31)); headline.setForeground(Color.WHITE); headline.setAlignmentX(LEFT_ALIGNMENT); JLabel copy = new JLabel("<html>校园里的教材、配件和生活物资，<br>在这里找到下一位使用者。</html>"); copy.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14)); copy.setForeground(new Color(205, 228, 216)); copy.setAlignmentX(LEFT_ALIGNMENT); panel.add(mark); panel.add(Box.createVerticalStrut(54)); panel.add(eyebrow); panel.add(Box.createVerticalStrut(12)); panel.add(headline); panel.add(Box.createVerticalStrut(18)); panel.add(copy); panel.add(Box.createVerticalGlue()); JLabel footer = new JLabel("每一次交换，都是一次资源节约"); footer.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12)); footer.setForeground(new Color(164, 198, 181)); panel.add(footer); return panel; }
    private JPanel loginPanel() { JPanel outer = new JPanel(new GridBagLayout()); outer.setBackground(Theme.MIST); JPanel panel = new JPanel(); panel.setOpaque(false); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); JLabel eyebrow = Theme.eyebrow("WELCOME BACK"); JLabel title = Theme.title("欢迎回来"); JLabel desc = Theme.subtitle("登录后继续管理你的校园闲置物品"); eyebrow.setAlignmentX(LEFT_ALIGNMENT); title.setAlignmentX(LEFT_ALIGNMENT); desc.setAlignmentX(LEFT_ALIGNMENT); panel.add(eyebrow); panel.add(Box.createVerticalStrut(8)); panel.add(title); panel.add(Box.createVerticalStrut(7)); panel.add(desc); panel.add(Box.createVerticalStrut(28)); JPanel form = Theme.card(); form.setLayout(new GridBagLayout()); GridBagConstraints g = new GridBagConstraints(); g.gridx=0; g.weightx=1; g.fill=GridBagConstraints.HORIZONTAL; g.anchor=GridBagConstraints.WEST; g.insets=new Insets(0,0,6,0); username = new JTextField(); password = new JPasswordField(); addField(form,g,0,"账号",username); addField(form,g,1,"密码",password); JButton register = Theme.ghostButton("注册新账号"); JPanel actions = new JPanel(new GridLayout(1,2,10,0)); actions.setOpaque(false); actions.add(loginButton); actions.add(register); g.gridy=4; g.insets=new Insets(15,0,8,0); form.add(actions,g); JLabel hint = Theme.subtitle("演示账号：admin / admin123    用户：demo / 123456"); g.gridy=5; g.insets=new Insets(4,0,0,0); form.add(hint,g); loginButton.addActionListener(e -> doLogin()); register.addActionListener(e -> doRegister()); panel.add(form); GridBagConstraints outerG = new GridBagConstraints(); outerG.weightx=1; outerG.weighty=1; outerG.fill=GridBagConstraints.HORIZONTAL; outerG.insets=new Insets(42,50,42,50); outer.add(panel,outerG); return outer; }
    private void addField(JPanel p, GridBagConstraints g, int row, String label, JComponent field) { int labelRow=row*2; g.gridy=labelRow; JLabel caption=new JLabel(label); caption.setFont(new Font("Microsoft YaHei UI",Font.BOLD,13)); p.add(caption,g); g.gridy=labelRow+1; Theme.styleInput(field); p.add(field,g); }
    private void doLogin() { try { User user = auth.login(username.getText(), new String(password.getPassword())); dispose(); new MainFrame(user).setVisible(true); } catch (RuntimeException ex) { showError(ex); } }
    private void doRegister() { JTextField display = new JTextField(); JPanel panel = new JPanel(new GridLayout(0,1,4,4)); panel.add(new JLabel("昵称")); panel.add(display); panel.add(new JLabel("注册会使用当前账号和密码")); if (JOptionPane.showConfirmDialog(this,panel,"注册账号",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) { try { auth.register(username.getText(),new String(password.getPassword()),display.getText()); JOptionPane.showMessageDialog(this,"注册成功，请登录"); } catch (RuntimeException ex) { showError(ex); } } }
    private void showError(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), ex instanceof AuthenticationException ? "登录失败" : "输入有误", JOptionPane.WARNING_MESSAGE); }
}
