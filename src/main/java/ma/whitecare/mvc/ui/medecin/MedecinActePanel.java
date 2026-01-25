package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.entities.medical.Acte;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedecinActePanel extends JPanel {
    private JTable acteTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addBtn, editBtn, deleteBtn, searchBtn;

    public MedecinActePanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));

        // 1. Top Bar
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Catalogue des Actes Médicaux");
        titleLabel.setFont(DesignSystem.TITLE);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(20);
        searchBtn = new RoundedButton("Rechercher");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        topBar.add(searchPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // 2. Table
        String[] columns = { "ID", "Libellé", "Catégorie", "Prix de Base (DH)" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        acteTable = new JTable(tableModel);
        DesignSystem.styleTable(acteTable);
        JScrollPane scrollPane = new JScrollPane(acteTable);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Right Action Bar
        JPanel actionBar = new JPanel();
        actionBar.setPreferredSize(new Dimension(150, 0));
        DesignSystem.stylePanel(actionBar);
        actionBar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));

        addBtn = new RoundedButton("Nouvel Acte");
        addBtn.setBackground(DesignSystem.PRIMARY);
        addBtn.setForeground(Color.WHITE);
        addBtn.setPreferredSize(new Dimension(130, 35));

        editBtn = new RoundedButton("Modifier");
        editBtn.setBackground(DesignSystem.SECONDARY);
        editBtn.setPreferredSize(new Dimension(130, 35));

        deleteBtn = new RoundedButton("Supprimer");
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setPreferredSize(new Dimension(130, 35));

        actionBar.add(addBtn);
        actionBar.add(editBtn);
        actionBar.add(deleteBtn);

        add(actionBar, BorderLayout.EAST);
    }

    public JTable getActeTable() {
        return acteTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getAddBtn() {
        return addBtn;
    }

    public JButton getEditBtn() {
        return editBtn;
    }

    public JButton getDeleteBtn() {
        return deleteBtn;
    }

    public JButton getSearchBtn() {
        return searchBtn;
    }
}
