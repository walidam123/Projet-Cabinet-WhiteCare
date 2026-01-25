package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedecinPatientPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchBtn;
    private RoundedButton addBtn, editBtn, deleteBtn, viewRecordBtn;

    public MedecinPatientPanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel header = new JLabel("Gestion des Dossiers Médicaux");
        header.setFont(DesignSystem.TITLE);
        add(header, BorderLayout.NORTH);

        // Center: Search + Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        DesignSystem.stylePanel(centerPanel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(searchPanel);
        JLabel sLabel = new JLabel("Rechercher Prénom/Nom:");
        DesignSystem.styleLabel(sLabel, DesignSystem.BODY);
        searchField = new JTextField(20);
        searchField.setFont(DesignSystem.BODY);

        searchBtn = new RoundedButton("Rechercher");
        searchBtn.setPreferredSize(new Dimension(120, 35));

        searchPanel.add(sLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Nom", "Prénom", "Téléphone", "Email", "Sexe", "Date Naissance" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        DesignSystem.styleTable(patientTable);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // South: Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        DesignSystem.stylePanel(actionPanel);

        addBtn = new RoundedButton("Nouveau Dossier");
        editBtn = new RoundedButton("Modifier Infos");
        deleteBtn = new RoundedButton("Supprimer");
        viewRecordBtn = new RoundedButton("Consulter Historique");

        // Styling buttons (Success for Add, Primary for others, Danger for Delete)
        addBtn.setBackground(new Color(40, 167, 69));
        deleteBtn.setBackground(new Color(220, 53, 69));

        Dimension btnSize = new Dimension(180, 40);
        addBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);
        viewRecordBtn.setPreferredSize(btnSize);

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(viewRecordBtn);
        actionPanel.add(deleteBtn);

        add(actionPanel, BorderLayout.SOUTH);
    }

    // Getters
    public JTable getPatientTable() {
        return patientTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public RoundedButton getSearchBtn() {
        return searchBtn;
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
}
