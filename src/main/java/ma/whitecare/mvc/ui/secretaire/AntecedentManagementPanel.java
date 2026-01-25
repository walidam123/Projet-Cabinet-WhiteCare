package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AntecedentManagementPanel extends JPanel {
    private JTable antecedentTable;
    private DefaultTableModel tableModel;
    private RoundedButton addBtn, editBtn, deleteBtn, searchBtn;
    private JTextField searchField;

    public AntecedentManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        DesignSystem.stylePanel(this);

        // Header
        JLabel header = new JLabel("Gestion des Antécédents");
        header.setFont(DesignSystem.TITLE);
        header.setForeground(DesignSystem.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        // Center Panel (Search + Table)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        DesignSystem.stylePanel(centerPanel);

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(searchPanel);
        JLabel sLabel = new JLabel("Rechercher (Nom):");
        DesignSystem.styleLabel(sLabel, DesignSystem.BODY);
        searchField = new JTextField(20);
        searchField.setFont(DesignSystem.BODY);
        searchBtn = new RoundedButton("Rechercher");

        searchPanel.add(sLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Libellé", "Catégorie" };
        tableModel = new DefaultTableModel(new Object[][] {}, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        antecedentTable = new JTable(tableModel);
        DesignSystem.styleTable(antecedentTable);

        // Hide ID
        antecedentTable.getColumnModel().getColumn(0).setMinWidth(0);
        antecedentTable.getColumnModel().getColumn(0).setMaxWidth(0);
        antecedentTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        centerPanel.add(new JScrollPane(antecedentTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(actions);
        addBtn = new RoundedButton("Ajouter");
        editBtn = new RoundedButton("Modifier");
        deleteBtn = new RoundedButton("Supprimer");

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        add(actions, BorderLayout.SOUTH);
    }

    public JTable getAntecedentTable() {
        return antecedentTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public RoundedButton getAddBtn() {
        return addBtn;
    }

    public RoundedButton getEditBtn() {
        return editBtn;
    }

    public RoundedButton getDeleteBtn() {
        return deleteBtn;
    }

    public RoundedButton getSearchBtn() {
        return searchBtn;
    }

    public JTextField getSearchField() {
        return searchField;
    }
}
