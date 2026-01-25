package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.dto.financial.CaisseStatsDTO;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import ma.whitecare.service.modules.caisse.api.CaisseService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

public class CaisseStatsDialog extends JDialog {
    private final CaisseService caisseService;
    private final JLabel invoicedVal;
    private final JLabel revenueVal;
    private final JLabel chargesVal;
    private final JLabel balanceVal;

    private JComboBox<String> periodCombo;
    private JTextField dateField;
    private JPanel customDatePanel;

    public CaisseStatsDialog(Frame parent, CaisseService caisseService) {
        super(parent, "État de la Caisse", true);
        this.caisseService = caisseService;

        setSize(550, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        DesignSystem.stylePanel(filterPanel);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        periodCombo = new JComboBox<>(
                new String[] { "Global", "Aujourd'hui", "Cette Semaine", "Ce Mois", "Date précise" });
        periodCombo.setFont(DesignSystem.BODY);
        periodCombo.addActionListener(e -> handlePeriodChange());

        filterPanel.add(new JLabel("Période :"));
        filterPanel.add(periodCombo);

        customDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(customDatePanel);
        customDatePanel.add(new JLabel("Date (AAAA-MM-JJ) :"));
        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().toString());
        customDatePanel.add(dateField);
        customDatePanel.setVisible(false);

        JButton refreshBtn = new RoundedButton("Actualiser");
        refreshBtn.addActionListener(e -> refreshStats());

        filterPanel.add(customDatePanel);
        filterPanel.add(refreshBtn);

        add(filterPanel, BorderLayout.NORTH);

        // Stats Panel
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        DesignSystem.stylePanel(mainPanel);

        mainPanel.add(createStatRow("Total Facturé :", invoicedVal = new JLabel("0.00 DH"), Color.BLACK));
        mainPanel.add(createStatRow("Total Encaissé (Revenus) :", revenueVal = new JLabel("0.00 DH"),
                new Color(40, 167, 69)));
        mainPanel.add(createStatRow("Total Charges :", chargesVal = new JLabel("0.00 DH"), new Color(220, 53, 69)));

        JPanel balanceRow = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(balanceRow);
        JLabel title = new JLabel("Solde Net :");
        title.setFont(DesignSystem.SUBTITLE);
        balanceVal = new JLabel("0.00 DH");
        balanceVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        balanceRow.add(title, BorderLayout.WEST);
        balanceRow.add(balanceVal, BorderLayout.EAST);
        mainPanel.add(balanceRow);

        add(mainPanel, BorderLayout.CENTER);

        // Footer
        RoundedButton closeBtn = new RoundedButton("Fermer");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        DesignSystem.stylePanel(btnPanel);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Initial load
        refreshStats();
    }

    private void handlePeriodChange() {
        String selection = (String) periodCombo.getSelectedItem();
        customDatePanel.setVisible("Date précise".equals(selection));
        pack();
        revalidate();
        repaint();
    }

    private void refreshStats() {
        String selection = (String) periodCombo.getSelectedItem();
        CaisseStatsDTO stats;

        try {
            if ("Aujourd'hui".equals(selection)) {
                LocalDate today = LocalDate.now();
                stats = caisseService.getStatsByPeriod(today.atStartOfDay(), today.atTime(LocalTime.MAX));
            } else if ("Cette Semaine".equals(selection)) {
                LocalDate start = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate end = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                stats = caisseService.getStatsByPeriod(start.atStartOfDay(), end.atTime(LocalTime.MAX));
            } else if ("Ce Mois".equals(selection)) {
                LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
                stats = caisseService.getStatsByPeriod(start.atStartOfDay(), end.atTime(LocalTime.MAX));
            } else if ("Date précise".equals(selection)) {
                LocalDate d = LocalDate.parse(dateField.getText());
                stats = caisseService.getStatsByPeriod(d.atStartOfDay(), d.atTime(LocalTime.MAX));
            } else {
                stats = caisseService.getGlobalStats();
            }

            updateUI(stats);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du calcul des statistiques: " + ex.getMessage());
        }
    }

    private void updateUI(CaisseStatsDTO stats) {
        invoicedVal.setText(String.format("%.2f DH", stats.getTotalInvoiced()));
        revenueVal.setText(String.format("%.2f DH", stats.getTotalRevenue()));
        chargesVal.setText(String.format("%.2f DH", stats.getTotalCharges()));
        balanceVal.setText(String.format("%.2f DH", stats.getBalance()));
        balanceVal.setForeground(stats.getBalance() >= 0 ? new Color(40, 167, 69) : new Color(220, 53, 69));
    }

    private JPanel createStatRow(String label, JLabel valLabel, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(p);
        JLabel lbl = new JLabel(label);
        lbl.setFont(DesignSystem.BODY);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valLabel.setForeground(color);
        p.add(lbl, BorderLayout.WEST);
        p.add(valLabel, BorderLayout.EAST);
        return p;
    }
}
