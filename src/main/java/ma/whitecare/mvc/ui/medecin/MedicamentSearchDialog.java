package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MedicamentSearchDialog extends JDialog {
    private JTextField searchField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private List<Medicament> allMedicaments;
    private Medicament selectedMedicament;
    private boolean succeeded;

    public MedicamentSearchDialog(Frame parent, List<Medicament> initialData) {
        super(parent, "Rechercher un Médicament", true);
        this.allMedicaments = initialData;

        setLayout(new BorderLayout(10, 10));
        setSize(500, 400);
        setLocationRelativeTo(parent);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        DesignSystem.stylePanel(searchPanel);

        searchField = new JTextField();
        searchField.addActionListener(e -> filterResults());

        RoundedButton btnSearch = new RoundedButton("Chercher");
        btnSearch.addActionListener(e -> filterResults());

        searchPanel.add(new JLabel("Nom du médicament:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        // Table
        String[] cols = { "ID", "Nom", "Laboratoire", "Prix" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        DesignSystem.styleTable(resultTable);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(bottomPanel);

        RoundedButton btnSelect = new RoundedButton("Sélectionner");
        btnSelect.setBackground(DesignSystem.PRIMARY);
        btnSelect.addActionListener(e -> {
            int row = resultTable.getSelectedRow();
            if (row != -1) {
                Long id = (Long) tableModel.getValueAt(row, 0);
                selectedMedicament = allMedicaments.stream()
                        .filter(m -> m.getIdMct().equals(id))
                        .findFirst()
                        .orElse(null);
                succeeded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médicament.");
            }
        });

        RoundedButton btnCancel = new RoundedButton("Annuler");
        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSelect);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable(allMedicaments);
    }

    private void filterResults() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshTable(allMedicaments);
            return;
        }

        List<Medicament> filtered = allMedicaments.stream()
                .filter(m -> m.getNom().toLowerCase().contains(query) ||
                        (m.getLaboratoire() != null && m.getLaboratoire().toLowerCase().contains(query)))
                .collect(Collectors.toList());
        refreshTable(filtered);
    }

    private void refreshTable(List<Medicament> list) {
        tableModel.setRowCount(0);
        for (Medicament m : list) {
            tableModel.addRow(new Object[] {
                    m.getIdMct(),
                    m.getNom(),
                    m.getLaboratoire(),
                    m.getPrixUnitaire() != null ? m.getPrixUnitaire() + " Dhs" : "N/A"
            });
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public Medicament getSelectedMedicament() {
        return selectedMedicament;
    }
}
