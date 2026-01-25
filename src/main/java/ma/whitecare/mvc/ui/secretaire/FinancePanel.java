package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FinancePanel extends JPanel {
    private JTable factureTable, sfTable;
    private DefaultTableModel factureModel, sfModel;
    private RoundedButton createFactureBtn, editBtn, cancelBtn, viewCaisseBtn, reportBtn;
    private RoundedButton resetSFBtn, searchSFBtn;
    private JTextField searchSFField, factureSearchField, factureDateField;
    private RoundedButton factureSearchBtn;

    public FinancePanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Gestion Financière & Caisse");
        header.setFont(DesignSystem.TITLE);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(DesignSystem.BUTTON_FONT);

        // --- TAB 1: FACTURES ---
        JPanel facturePanel = new JPanel(new BorderLayout(10, 10));
        DesignSystem.stylePanel(facturePanel);

        String[] factCols = { "N° Facture", "Date", "Patient", "Total (MAD)", "Payé", "Reste", "Statut" };
        factureModel = new DefaultTableModel(factCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        factureTable = new JTable(factureModel);
        DesignSystem.styleTable(factureTable);
        // Search Panel for Factures
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(searchPanel);
        searchPanel.add(new JLabel("Date (AAAA-MM-JJ):"));
        factureDateField = new JTextField(10);
        searchPanel.add(factureDateField);
        searchPanel.add(new JLabel("Patient:"));
        factureSearchField = new JTextField(15);
        searchPanel.add(factureSearchField);
        factureSearchBtn = new RoundedButton("Chercher");
        searchPanel.add(factureSearchBtn);

        facturePanel.add(searchPanel, BorderLayout.NORTH);
        facturePanel.add(new JScrollPane(factureTable), BorderLayout.CENTER);

        JPanel factActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        DesignSystem.stylePanel(factActions);
        createFactureBtn = new RoundedButton("Nouvelle Facture");
        editBtn = new RoundedButton("Modifier");
        cancelBtn = new RoundedButton("Annuler");
        reportBtn = new RoundedButton("Imprimer");
        factActions.add(createFactureBtn);
        factActions.add(editBtn);
        factActions.add(cancelBtn);
        factActions.add(reportBtn);
        facturePanel.add(factActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Factures", facturePanel);

        // --- TAB 2: SITUATIONS FINANCIERES ---
        JPanel sfPanel = new JPanel(new BorderLayout(10, 10));
        DesignSystem.stylePanel(sfPanel);

        JPanel sfTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(sfTop);
        sfTop.add(new JLabel("Patient:"));
        searchSFField = new JTextField(20);
        searchSFBtn = new RoundedButton("Chercher");
        sfTop.add(searchSFField);
        sfTop.add(searchSFBtn);
        sfPanel.add(sfTop, BorderLayout.NORTH);

        String[] sfCols = { "ID SF", "Patient", "Totale Actes", "Totale Payé", "Crédit", "Statut" };
        sfModel = new DefaultTableModel(sfCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        sfTable = new JTable(sfModel);
        DesignSystem.styleTable(sfTable);
        sfPanel.add(new JScrollPane(sfTable), BorderLayout.CENTER);

        JPanel sfActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        DesignSystem.stylePanel(sfActions);
        resetSFBtn = new RoundedButton("Réinitialiser Situation");
        sfActions.add(resetSFBtn);
        sfPanel.add(sfActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Situations Financières", sfPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // --- FOOTER: CAISSE ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(footer);
        viewCaisseBtn = new RoundedButton("État de Caisse (Stats)");
        viewCaisseBtn.setBackground(DesignSystem.SECONDARY);
        viewCaisseBtn.setPreferredSize(new Dimension(200, 40));
        footer.add(viewCaisseBtn);
        add(footer, BorderLayout.SOUTH);
    }

    public JTable getFactureTable() {
        return factureTable;
    }

    public DefaultTableModel getTableModel() {
        return factureModel;
    }

    public RoundedButton getCreateFactureBtn() {
        return createFactureBtn;
    }

    public RoundedButton getEditBtn() {
        return editBtn;
    }

    public RoundedButton getCancelBtn() {
        return cancelBtn;
    }

    public RoundedButton getViewCaisseBtn() {
        return viewCaisseBtn;
    }

    public RoundedButton getReportBtn() {
        return reportBtn;
    }

    public JTable getSfTable() {
        return sfTable;
    }

    public DefaultTableModel getSfModel() {
        return sfModel;
    }

    public RoundedButton getResetSFBtn() {
        return resetSFBtn;
    }

    public RoundedButton getSearchSFBtn() {
        return searchSFBtn;
    }

    public JTextField getSearchSFField() {
        return searchSFField;
    }

    public JTextField getFactureSearchField() {
        return factureSearchField;
    }

    public JTextField getFactureDateField() {
        return factureDateField;
    }

    public RoundedButton getFactureSearchBtn() {
        return factureSearchBtn;
    }
}
