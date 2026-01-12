package ma.whitecare.mvc.ui.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementPanel extends JPanel {
    public UserManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Gestion des Utilisateurs");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Nom", "Email", "Rôle", "Statut" };
        Object[][] data = {
                { "1", "Admin", "admin@whitecare.ma", "ADMIN", "Actif" },
                { "2", "Dr. Ahmed", "ahmed@whitecare.ma", "DENTISTE", "Actif" }
        };
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(new JButton("Ajouter"));
        actions.add(new JButton("Modifier"));
        actions.add(new JButton("Supprimer"));
        actions.add(new JButton("Réinitialiser MDP"));
        actions.add(new JButton("Activer/Désactiver"));
        add(actions, BorderLayout.SOUTH);
    }

    public JTable getUserTable() {
        for (Component c : getComponents()) {
            if (c instanceof JScrollPane) {
                return (JTable) ((JScrollPane) c).getViewport().getView();
            }
        }
        return null;
    }

    public JButton getAddUserButton() {
        return findButton("Ajouter");
    }

    public JButton getEditUserButton() {
        return findButton("Modifier");
    }

    public JButton getDeleteUserButton() {
        return findButton("Supprimer");
    }

    public JButton getResetPasswordButton() {
        return findButton("Réinitialiser MDP");
    }

    public JButton getActivateDeactivateButton() {
        return findButton("Activer/Désactiver");
    }

    private JButton findButton(String text) {
        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                for (Component sub : ((JPanel) c).getComponents()) {
                    if (sub instanceof JButton && ((JButton) sub).getText().equals(text)) {
                        return (JButton) sub;
                    }
                }
            }
        }
        return null;
    }
}
