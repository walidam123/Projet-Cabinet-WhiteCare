package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RDVManagementPanel extends JPanel {
    private JTable rdvTable;
    private DefaultTableModel tableModel;
    private JTextField dateField;
    private RoundedButton scheduleBtn, editBtn, cancelBtn, waitListBtn, filterBtn;

    public RDVManagementPanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Gestion des Rendez-vous & Planning");
        header.setFont(DesignSystem.TITLE);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(filterPanel);
        JLabel dLabel = new JLabel("Date (AAAA-MM-JJ):");
        DesignSystem.styleLabel(dLabel, DesignSystem.BODY);
        dateField = new JTextField(java.time.LocalDate.now().toString(), 10);
        dateField.setFont(DesignSystem.BODY);
        filterBtn = new RoundedButton("Filtrer par Date");

        filterPanel.add(dLabel);
        filterPanel.add(dateField);
        filterPanel.add(filterBtn);

        JPanel northPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(northPanel);
        northPanel.add(header, BorderLayout.NORTH);
        northPanel.add(filterPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Date", "Heure", "Patient", "Motif", "Statut" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rdvTable = new JTable(tableModel);
        DesignSystem.styleTable(rdvTable);

        add(new JScrollPane(rdvTable), BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        DesignSystem.stylePanel(actionPanel);

        scheduleBtn = new RoundedButton("Nouveau RDV");
        editBtn = new RoundedButton("Modifier");
        cancelBtn = new RoundedButton("Annuler");
        waitListBtn = new RoundedButton("Liste d'attente");

        Dimension btnSize = new Dimension(160, 40);
        scheduleBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        cancelBtn.setPreferredSize(btnSize);
        waitListBtn.setPreferredSize(btnSize);

        actionPanel.add(scheduleBtn);
        actionPanel.add(editBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(waitListBtn);

        add(actionPanel, BorderLayout.SOUTH);
    }

    public JTable getRdvTable() {
        return rdvTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public RoundedButton getScheduleBtn() {
        return scheduleBtn;
    }

    public RoundedButton getEditBtn() {
        return editBtn;
    }

    public RoundedButton getCancelBtn() {
        return cancelBtn;
    }

    public RoundedButton getWaitListBtn() {
        return waitListBtn;
    }

    public RoundedButton getFilterBtn() {
        return filterBtn;
    }

    public JTextField getDateField() {
        return dateField;
    }

    // New button for Medecin (Blocking) - initialized but hidden/shown by
    // Controller context
    private RoundedButton blockBtn;

    public void addBlockButton() {
        if (blockBtn == null) {
            blockBtn = new RoundedButton("Bloquer Créneau");
            blockBtn.setPreferredSize(new Dimension(160, 40));
            // Add to action panel (which is the last component added to this panel, at
            // SOUTH)
            // We need to retrieve it. It's component index 1 (0 is north, 1 is center, 2 is
            // south in BorderLayout add order? No, check constructor)
            // Layout is BorderLayout. North, Center, South added.
            // Component 0: NorthPanel
            // Component 1: ScrollPane
            // Component 2: ActionPanel
            JPanel actionPanel = (JPanel) getComponent(2);
            actionPanel.add(blockBtn);
            actionPanel.revalidate();
            actionPanel.repaint();
        }
    }

    public RoundedButton getBlockBtn() {
        return blockBtn;
    }
}
