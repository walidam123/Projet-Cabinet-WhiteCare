package ma.whitecare.mvc.ui.admin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoleManagementPanel extends JPanel {
    public RoleManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        DesignSystem.stylePanel(this);

        JLabel header = new JLabel("Gestion des Rôles");
        header.setFont(DesignSystem.TITLE);
        header.setForeground(DesignSystem.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        String[] columns = { "ID", "Nom du Rôle" };
        Object[][] data = {
                { "1", "ADMIN" },
                { "2", "DENTISTE" },
                { "3", "SECRETAIRE" }
        };
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        DesignSystem.styleTable(table);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(actions);
        actions.add(new RoundedButton("Ajouter"));
        actions.add(new RoundedButton("Modifier"));
        actions.add(new RoundedButton("Supprimer"));
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
        // Look in the actions panel (SOUTH)
        if (getLayout() instanceof BorderLayout) {
            Component south = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.SOUTH);
            if (south instanceof JPanel) {
                for (Component c : ((JPanel) south).getComponents()) {
                    if (c instanceof JButton && text.equals(((JButton) c).getText())) {
                        return (JButton) c;
                    }
                }
            }
        }
        return null; // Fallback
    }
}
