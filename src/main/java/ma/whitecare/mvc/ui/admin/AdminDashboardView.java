package ma.whitecare.mvc.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardView extends JFrame {
    private JPanel sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public AdminDashboardView() {
        setTitle("WhiteCare - Administration");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(25, 25, 112)); // Midnight Blue
        sidebar.setPreferredSize(new Dimension(250, 800));

        JLabel logoLabel = new JLabel("WHITE CARE");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(logoLabel);

        addSidebarButton("Utilisateurs", "USER_MGMT");
        addSidebarButton("Rôles", "ROLE_MGMT");
        addSidebarButton("Données Référentielles", "REF_DATA");

        // Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new UserManagementPanel(), "USER_MGMT");
        contentPanel.add(new RoleManagementPanel(), "ROLE_MGMT");
        contentPanel.add(new ReferentialDataPanel(), "REF_DATA");

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setBackground(new Color(25, 25, 112));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        sidebar.add(btn);
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
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboardView().setVisible(true);
        });
    }
}
