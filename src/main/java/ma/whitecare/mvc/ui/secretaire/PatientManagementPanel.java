package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientManagementPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton addBtn, editBtn, deleteBtn, viewRecordBtn, assignAntBtn;

    public PatientManagementPanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel header = new JLabel("Gestion des Patients");
        header.setFont(DesignSystem.TITLE);
        add(header, BorderLayout.NORTH);

        // Center: Search + Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        DesignSystem.stylePanel(centerPanel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(searchPanel);
        JLabel sLabel = new JLabel("Rechercher:");
        DesignSystem.styleLabel(sLabel, DesignSystem.BODY);
        searchField = new JTextField(20);
        searchField.setFont(DesignSystem.BODY);

        RoundedButton searchBtn = new RoundedButton("Rechercher");
        searchBtn.setPreferredSize(new Dimension(120, 35));

        searchPanel.add(sLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Nom", "Prénom", "Téléphone", "Email", "Sexe", "Assurance" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        DesignSystem.styleTable(patientTable);

        // Hide ID Column (Index 0)
        patientTable.getColumnModel().getColumn(0).setMinWidth(0);
        patientTable.getColumnModel().getColumn(0).setMaxWidth(0);
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // South: Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        DesignSystem.stylePanel(actionPanel);

        addBtn = new RoundedButton("Ajouter");
        editBtn = new RoundedButton("Modifier");
        deleteBtn = new RoundedButton("Supprimer");
        viewRecordBtn = new RoundedButton("Dossier Médical");
        assignAntBtn = new RoundedButton("Antécédents");

        Dimension btnSize = new Dimension(160, 40);
        addBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);
        viewRecordBtn.setPreferredSize(btnSize);
        assignAntBtn.setPreferredSize(btnSize);

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(viewRecordBtn);
        actionPanel.add(assignAntBtn);
        actionPanel.add(deleteBtn);

        add(actionPanel, BorderLayout.SOUTH);
    }

    // Getters for controller access
    public JTable getPatientTable() {
        return patientTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getSearchField() {
        return searchField;
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

    public RoundedButton getViewRecordBtn() {
        return viewRecordBtn;
    }

    public RoundedButton getAssignAntBtn() {
        return assignAntBtn;
    }
}
