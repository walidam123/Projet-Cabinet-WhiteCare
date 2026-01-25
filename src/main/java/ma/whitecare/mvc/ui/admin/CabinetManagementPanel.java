package ma.whitecare.mvc.ui.admin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CabinetManagementPanel extends JPanel {
    public CabinetManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        DesignSystem.stylePanel(this);

        JLabel header = new JLabel("Gestion des Cabinets");
        header.setFont(DesignSystem.TITLE);
        header.setForeground(DesignSystem.TEXT_PRIMARY);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(searchPanel);
        JLabel searchLabel = new JLabel("Rechercher (Nom) :");
        DesignSystem.styleLabel(searchLabel, DesignSystem.BODY);
        searchPanel.add(searchLabel);

        JTextField searchField = new JTextField(20);
        searchField.setName("cabinetSearchField");
        searchField.setFont(DesignSystem.BODY);
        searchPanel.add(searchField);

        RoundedButton searchBtn = new RoundedButton("Rechercher");
        searchBtn.setName("cabinetSearchBtn");
        searchBtn.setPreferredSize(new Dimension(120, 35));
        searchPanel.add(searchBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(topPanel);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Nom", "Email", "Téléphone", "Adresse" };
        DefaultTableModel model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        DesignSystem.styleTable(table);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(actions);
        actions.add(new RoundedButton("Ajouter"));
        actions.add(new RoundedButton("Modifier"));
        actions.add(new RoundedButton("Détails"));
        actions.add(new RoundedButton("Supprimer"));
        add(actions, BorderLayout.SOUTH);
    }

    public JTable getCabinetTable() {
        for (Component c : getComponents()) {
            if (c instanceof JScrollPane) {
                return (JTable) ((JScrollPane) c).getViewport().getView();
            }
        }
        return null;
    }

    public JButton getAddButton() {
        return findButton("Ajouter");
    }

    public JButton getEditButton() {
        return findButton("Modifier");
    }

    public JButton getDeleteButton() {
        return findButton("Supprimer");
    }

    public JButton getDetailsButton() {
        return findButton("Détails");
    }

    public JTextField getSearchField() {
        return findSearchField("cabinetSearchField");
    }

    public JButton getSearchButton() {
        return findButton("Rechercher");
    }

    private JTextField findSearchField(String name) {
        return (JTextField) findComponentByName(this, name);
    }

    private JButton findButton(String text) {
        return findButtonRecursive(this, text);
    }

    private JButton findButtonRecursive(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton && text.equals(((JButton) c).getText())) {
                return (JButton) c;
            }
            if (c instanceof Container) {
                JButton b = findButtonRecursive((Container) c, text);
                if (b != null)
                    return b;
            }
        }
        return null;
    }

    private Component findComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name.equals(c.getName())) {
                return c;
            }
            if (c instanceof Container) {
                Component found = findComponentByName((Container) c, name);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
}
