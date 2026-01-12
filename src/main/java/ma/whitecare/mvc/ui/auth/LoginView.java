package ma.whitecare.mvc.ui.auth;

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
        getContentPane().setBackground(Color.WHITE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo placeholder (simulating the tooth logo in the mockup)
        JLabel logoLabel = new JLabel("🦷", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 80));
        logoLabel.setForeground(new Color(194, 153, 103)); // Brownish color from mockup
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(logoLabel, gbc);

        JLabel titleLabel = new JLabel("WHITE CARE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112)); // Midnight Blue
        gbc.gridy = 1;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Cabinet dentaire", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 2;
        add(subtitleLabel, gbc);

        // Username
        gbc.gridwidth = 2;
        gbc.gridy = 3;
        JLabel userLabel = new JLabel("Nom d'utilisateur");
        add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(300, 35));
        gbc.gridy = 4;
        add(usernameField, gbc);

        // Password
        gbc.gridy = 5;
        JLabel passLabel = new JLabel("Mot de passe");
        add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 35));
        gbc.gridy = 6;
        add(passwordField, gbc);

        // Connexion Button
        loginButton = new RoundedButton("Connexion");
        loginButton.setBackground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(150, 40));
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
