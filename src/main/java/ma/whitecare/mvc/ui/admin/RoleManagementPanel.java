package ma.whitecare.mvc.ui.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoleManagementPanel extends JPanel {
    public RoleManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Gestion des Rôles");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        String[] columns = { "ID", "Nom du Rôle", "Description" };
        Object[][] data = {
                { "1", "ADMIN", "Accès total au système" },
                { "2", "DENTISTE", "Gestion médicale et patients" },
                { "3", "SECRETAIRE", "Gestion des rendez-vous" }
        };
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(new JButton("Ajouter"));
        actions.add(new JButton("Modifier"));
        actions.add(new JButton("Supprimer"));
        add(actions, BorderLayout.SOUTH);
    }

    public JTable getRoleTable() {
        for (Component c : getComponents()) {
            if (c instanceof JScrollPane) {
                return (JTable) ((JScrollPane) c).getViewport().getView();
            }
        }
        return null;
    }

    public JButton getAddRoleButton() {
        return findButton("Ajouter");
    }

    public JButton getEditRoleButton() {
        return findButton("Modifier");
    }

    public JButton getDeleteRoleButton() {
        return findButton("Supprimer");
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
