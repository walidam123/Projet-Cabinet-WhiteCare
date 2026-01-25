package ma.whitecare.mvc.ui.auth;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private RoundedButton loginButton;

    public LoginView() {
        setTitle("WhiteCare - Connexion");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DesignSystem.BACKGROUND);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo display
        JLabel logoLabel = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource("/static/images/logo1.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                logoLabel.setText("🦷");
                logoLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 80));
                logoLabel.setForeground(DesignSystem.PRIMARY);
            }
        } catch (Exception e) {
            logoLabel.setText("🦷");
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(logoLabel, gbc);

        // Username
        gbc.gridwidth = 2;
        gbc.gridy = 3;
        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setFont(DesignSystem.SUBTITLE);
        userLabel.setForeground(DesignSystem.TEXT_PRIMARY);
        add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(300, 35));
        usernameField.setFont(DesignSystem.BODY);
        gbc.gridy = 4;
        add(usernameField, gbc);

        // Password
        gbc.gridy = 5;
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(DesignSystem.SUBTITLE);
        passLabel.setForeground(DesignSystem.TEXT_PRIMARY);
        add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 35));
        passwordField.setFont(DesignSystem.BODY);
        gbc.gridy = 6;
        add(passwordField, gbc);

        // Connexion Button
        loginButton = new RoundedButton("Connexion");
        loginButton.setBackground(DesignSystem.BTN_LOGIN);
        loginButton.setPreferredSize(new Dimension(180, 45));
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public RoundedButton getLoginButton() {
        return loginButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
