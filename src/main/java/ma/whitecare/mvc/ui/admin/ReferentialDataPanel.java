package ma.whitecare.mvc.ui.admin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReferentialDataPanel extends JPanel {
    private JTabbedPane tabs;

    public ReferentialDataPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        DesignSystem.stylePanel(this);

        JLabel header = new JLabel("Gestion des Données Référentielles");
        header.setFont(DesignSystem.TITLE);
        header.setForeground(DesignSystem.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(DesignSystem.BUTTON_FONT);
        tabs.setBackground(DesignSystem.SECONDARY);
        tabs.setForeground(DesignSystem.TEXT_PRIMARY);

        tabs.addTab("Médicaments", createGenericTablePanel("Catalogue de Médicaments", true, "Rechercher (Nom) :"));
        tabs.addTab("Antécédents", createGenericTablePanel("Gestion des Antécédents", true, "Rechercher (Nom) :"));
        tabs.addTab("Actes", createGenericTablePanel("Gestion des Actes", true, "Rechercher (Libellé) :"));

        add(tabs, BorderLayout.CENTER);
    }

    public JTabbedPane getTabs() {
        return (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
    }

    public JTable getTableAt(int index) {
        JPanel panel = (JPanel) getTabs().getComponentAt(index);
        // North(panel[0]), JScrollPane(panel[1]), South(panel[2])
        return (JTable) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
    }

    public JButton getAddButtonAt(int index) {
        return findButtonInTab(index, "Ajouter");
    }

    public JButton getEditButtonAt(int index) {
        return findButtonInTab(index, "Modifier");
    }

    public JButton getDeleteButtonAt(int index) {
        return findButtonInTab(index, "Supprimer");
    }

    public JButton getDetailsButtonAt(int index) {
        return findButtonInTab(index, "Détails");
    }

    public JTextField getSearchFieldAt(int index) {
        JPanel panel = (JPanel) getTabs().getComponentAt(index);
        JPanel top = (JPanel) panel.getComponent(0);
        for (Component c : top.getComponents()) {
            if (c instanceof JPanel) { // The searchPanel
                for (Component sub : ((JPanel) c).getComponents()) {
                    if (sub instanceof JTextField)
                        return (JTextField) sub;
                }
            }
        }
        return null;
    }

    public JButton getSearchButtonAt(int index) {
        JPanel panel = (JPanel) getTabs().getComponentAt(index);
        JPanel top = (JPanel) panel.getComponent(0);
        for (Component c : top.getComponents()) {
            if (c instanceof JPanel) { // The searchPanel
                for (Component sub : ((JPanel) c).getComponents()) {
                    if (sub instanceof JButton && ((JButton) sub).getText().equals("Rechercher"))
                        return (JButton) sub;
                }
            }
        }
        return null;
    }

    private JButton findButtonInTab(int index, String text) {
        JPanel panel = (JPanel) getTabs().getComponentAt(index);
        // panel.getComponent(2) is the actions panel at SOUTH
        JPanel actions = (JPanel) panel.getComponent(2);
        for (Component c : actions.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals(text)) {
                return (JButton) c;
            }
        }
        return null;
    }

    private JPanel createGenericTablePanel(String title, boolean hasSearch, String searchLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(panel);

        JPanel topPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(topPanel);
        JLabel panelHeader = new JLabel(title);
        panelHeader.setFont(DesignSystem.SUBTITLE);
        panelHeader.setForeground(DesignSystem.TEXT_PRIMARY);
        topPanel.add(panelHeader, BorderLayout.NORTH);

        if (hasSearch) {
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            DesignSystem.stylePanel(searchPanel);
            JLabel sLabel = new JLabel(searchLabel);
            DesignSystem.styleLabel(sLabel, DesignSystem.BODY);
            searchPanel.add(sLabel);

            JTextField searchField = new JTextField(20);
            searchField.setFont(DesignSystem.BODY);
            searchPanel.add(searchField);

            RoundedButton searchBtn = new RoundedButton("Rechercher");
            searchBtn.setPreferredSize(new Dimension(120, 35));
            searchPanel.add(searchBtn);

            topPanel.add(searchPanel, BorderLayout.SOUTH);
        }

        panel.add(topPanel, BorderLayout.NORTH);

        // Simplified generic table for referential data
        String[] columns = { "ID", "Libellé", "Description" };
        DefaultTableModel model = new DefaultTableModel(new Object[][] {}, columns);
        JTable table = new JTable(model);
        DesignSystem.styleTable(table);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(actions);
        actions.add(new RoundedButton("Ajouter"));
        actions.add(new RoundedButton("Modifier"));
        actions.add(new RoundedButton("Détails"));
        actions.add(new RoundedButton("Supprimer"));
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private static class DefaultTableModel extends javax.swing.table.DefaultTableModel {
        public DefaultTableModel(Object[][] data, String[] columns) {
            super(data, columns);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
