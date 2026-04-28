package view;

import controller.LoginController;
import util.UITheme;
import view.common.RoundedPasswordField;
import view.common.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    private final RoundedTextField txtUsername = new RoundedTextField("Username");
    private final RoundedPasswordField txtPassword = new RoundedPasswordField("Password");
    private final LoginController controller = new LoginController();

    public LoginFrame() {
        setTitle("Dang nhap - Quan ly nha hang");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 520));
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        getContentPane().setBackground(UITheme.BEIGE);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(UITheme.BEIGE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(440, 520));

        JPanel logo = new JPanel();
        logo.setPreferredSize(new Dimension(360, 160));
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        logo.setBackground(Color.WHITE);
        logo.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        // Placeholder: user will add logo later
        card.add(logo);

        card.add(Box.createVerticalStrut(14));

        JLabel title = new JLabel("NHA HANG UMAMI BAM", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        card.add(Box.createVerticalStrut(18));

        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        card.add(txtUsername);
        card.add(Box.createVerticalStrut(10));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(16));

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton btnExit = new JButton("Thoat");
        JButton btnLogin = new JButton("Dang nhap");

        styleSecondary(btnExit);
        stylePrimary(btnLogin);

        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));

        txtPassword.addActionListener(e -> doLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        actions.add(btnExit);
        actions.add(btnLogin);
        card.add(actions);

        add(card, new GridBagConstraints());
    }

    private void stylePrimary(JButton b) {
        Color base = new Color(0xC7A77A);
        Color hover = new Color(0xB89260);
        Color press = new Color(0xA97D48);
        b.setFocusPainted(false);
        b.setBackground(base);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(base.darker(), 1, true));
        b.setOpaque(true);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(base);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(press);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(b.getBounds().contains(e.getPoint()) ? hover : base);
            }
        });
    }

    private void styleSecondary(JButton b) {
        Color base = Color.WHITE;
        Color hover = new Color(0xF2E9DA);
        Color press = new Color(0xE2D6C0);
        Color border = new Color(0xC7A77A);
        b.setFocusPainted(false);
        b.setBackground(base);
        b.setForeground(border.darker());
        b.setBorder(BorderFactory.createLineBorder(border, 1, true));
        b.setOpaque(true);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(base);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(press);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(b.getBounds().contains(e.getPoint()) ? hover : base);
            }
        });
    }

    private void doLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        controller.login(username, password, this);
    }
}

