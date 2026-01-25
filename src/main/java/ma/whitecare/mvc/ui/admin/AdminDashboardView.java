package ma.whitecare.mvc.ui.admin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import java.awt.*;
import java.awt.Component;

public class AdminDashboardView extends JFrame {
    private JPanel sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public AdminDashboardView(String userName) {
        setTitle("WhiteCare - Administration");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(DesignSystem.PRIMARY); // Taupe doux
        sidebar.setPreferredSize(new Dimension(280, 800));

        JLabel logoLabel = new JLabel("WHITE CARE");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(DesignSystem.TITLE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        sidebar.add(logoLabel);

        // User name label
        JLabel userLabel = new JLabel(userName != null ? userName : "Administrateur");
        userLabel.setForeground(new Color(220, 220, 220));
        userLabel.setFont(DesignSystem.BODY);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        sidebar.add(userLabel);

        addSidebarButton("Utilisateurs", "USER_MGMT");
        addSidebarButton("Rôles", "ROLE_MGMT");
        addSidebarButton("Données Référentielles", "REF_DATA");
        addSidebarButton("Gestion Cabinet", "CABINET_MGMT");

        sidebar.add(Box.createVerticalStrut(10));
        RoundedButton profileBtn = new RoundedButton("Mon Profil");
        profileBtn.setName("profileBtn");
        profileBtn.setMaximumSize(new Dimension(240, 50));
        profileBtn.setBackground(DesignSystem.PRIMARY.darker());
        profileBtn.setForeground(Color.WHITE);
        profileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(profileBtn);

        sidebar.add(Box.createVerticalGlue());
        RoundedButton logoutBtn = new RoundedButton("Déconnexion");
        logoutBtn.setName("logoutBtn");
        logoutBtn.setMaximumSize(new Dimension(240, 50));
        logoutBtn.setBackground(DesignSystem.BTN_LOGIN);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        // Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new UserManagementPanel(), "USER_MGMT");
        contentPanel.add(new RoleManagementPanel(), "ROLE_MGMT");
        contentPanel.add(new ReferentialDataPanel(), "REF_DATA");
        contentPanel.add(new CabinetManagementPanel(), "CABINET_MGMT");

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addSidebarButton(String text, String cardName) {
        RoundedButton btn = new RoundedButton(text);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setBackground(DesignSystem.PRIMARY.darker());
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(10));
    }

    public UserManagementPanel getUserPanel() {
        return (UserManagementPanel) findPanel("USER_MGMT");
    }

    public RoleManagementPanel getRolePanel() {
        return (RoleManagementPanel) findPanel("ROLE_MGMT");
    }

    public ReferentialDataPanel getReferentialPanel() {
        return (ReferentialDataPanel) findPanel("REF_DATA");
    }

    public CabinetManagementPanel getCabinetPanel() {
        return (CabinetManagementPanel) findPanel("CABINET_MGMT");
    }

    public JButton getLogoutButton() {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton && "logoutBtn".equals(c.getName())) {
                return (JButton) c;
            }
        }
        return null;
    }

    public JButton getProfileButton() {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton && "profileBtn".equals(c.getName())) {
                return (JButton) c;
            }
        }
        return null;
    }

    private JPanel findPanel(String name) {
        for (Component c : contentPanel.getComponents()) {
            if (contentPanel.getLayout() instanceof CardLayout) {
                // CardLayout doesn't easily expose the current card by name,
                // but since we added them, we can find them if they match the type or we store
                // them.
                // For simplicity, let's just use the classes since each is unique in this view.
                if (name.equals("USER_MGMT") && c instanceof UserManagementPanel)
                    return (JPanel) c;
                if (name.equals("ROLE_MGMT") && c instanceof RoleManagementPanel)
                    return (JPanel) c;
                if (name.equals("REF_DATA") && c instanceof ReferentialDataPanel)
                    return (JPanel) c;
                if (name.equals("CABINET_MGMT") && c instanceof CabinetManagementPanel)
                    return (JPanel) c;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboardView("Administrateur").setVisible(true);
        });
    }
}
