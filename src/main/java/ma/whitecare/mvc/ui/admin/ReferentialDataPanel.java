package ma.whitecare.mvc.ui.admin;

import javax.swing.*;
import java.awt.*;

public class ReferentialDataPanel extends JPanel {
    public ReferentialDataPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Gestion des Données Référentielles");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Assurances", createGenericTablePanel("Liste des Assurances"));
        tabs.addTab("Médicaments", createGenericTablePanel("Catalogue de Médicaments"));
        tabs.addTab("Antécédents", createGenericTablePanel("Gestion des Antécédents"));
        tabs.addTab("Actes", createGenericTablePanel("Gestion des Actes"));

        add(tabs, BorderLayout.CENTER);
    }

    public JTabbedPane getTabs() {
        return (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
    }

    public JTable getTableAt(int index) {
        JPanel panel = (JPanel) getTabs().getComponentAt(index);
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

    private JButton findButtonInTab(int index, String text) {
        JPanel panel = (JPanel) getTabs().getComponentAt(index);
        JPanel actions = (JPanel) panel.getComponent(2);
        for (Component c : actions.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals(text)) {
                return (JButton) c;
            }
        }
        return null;
    }

    private JPanel createGenericTablePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title), BorderLayout.NORTH);

        // Simplified generic table for referential data
        String[] columns = { "ID", "Libellé", "Description" };
        DefaultTableModel model = new DefaultTableModel(new Object[][] {}, columns);
        panel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(new JButton("Ajouter"));
        actions.add(new JButton("Modifier"));
        actions.add(new JButton("Supprimer"));
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    // Static inner class for table model since we use it twice now
    private static class DefaultTableModel extends javax.swing.table.DefaultTableModel {
        public DefaultTableModel(Object[][] data, String[] columns) {
            super(data, columns);
        }
    }
}
