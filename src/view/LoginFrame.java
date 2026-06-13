package view;

import controller.LoginController;
import java.awt.*;
import javax.swing.*;
import util.RoundedButtonUI;
import util.RoundedPanel;
import util.UITheme;
import view.common.RoundedPasswordField;
import view.common.RoundedTextField;

public class LoginFrame extends JFrame {
    private final RoundedTextField txtUsername = new RoundedTextField("Tên đăng nhập");
    private final RoundedPasswordField txtPassword = new RoundedPasswordField("Mật khẩu");
    private final LoginController controller = new LoginController();

    public LoginFrame() {
        setTitle("Đăng nhập - Quản lý nhà hàng");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 520));
        setLocationRelativeTo(null);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        getContentPane().setBackground(UITheme.BEIGE);
        setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(18);
        card.setBackground(UITheme.SAND);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(440, 520));

        JLabel logo = new JLabel();
        logo.setPreferredSize(new Dimension(360, 160));
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setVerticalAlignment(SwingConstants.CENTER);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setOpaque(false);

        logo.setIcon(resizeIcon("/logo.png", 140, 140));
        card.add(logo);
        card.add(Box.createVerticalStrut(14));

        JLabel title = new JLabel("NHÀ HÀNG UMAMI BAM", SwingConstants.CENTER);
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

        JButton btnExit = new JButton("Thoát");

        JButton btnLogin = new JButton("Đăng nhập");

        //styleSecondary(btnExit);
        btnExit.setUI(new RoundedButtonUI());
        btnExit.setBackground(UITheme.LATTE);
        btnExit.setForeground(Color.WHITE);

        //styleSecondary(btnLogin);
        btnLogin.setUI(new RoundedButtonUI());
        btnLogin.setBackground(UITheme.CARAMEL);
        btnLogin.setForeground(Color.WHITE);

        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));

        txtPassword.addActionListener(e -> doLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        actions.add(btnExit);
        actions.add(btnLogin);
        card.add(actions);

        add(card, new GridBagConstraints());
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));

        Image img = icon.getImage().getScaledInstance(
                width,
                height,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(img);
    }

    private void doLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        controller.login(username, password, this);
    }
}
