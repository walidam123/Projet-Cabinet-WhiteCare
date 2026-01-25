package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardHomePanel extends JPanel {
    private JTable queueTable;
    private DefaultTableModel queueModel;
    private JList<String> alertsList;
    private DefaultListModel<String> alertsModel;
    private JLabel totalPatientsLabel, revenueLabel, rdvTodayLabel, completedConsultationsLabel;

    public DashboardHomePanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel welcome = new JLabel("Tableau de Bord - Aujourd'hui", JLabel.LEFT);
        welcome.setFont(DesignSystem.TITLE);
        welcome.setForeground(DesignSystem.TEXT_PRIMARY);
        add(welcome, BorderLayout.NORTH);

        // Center: Split Pane for Queue (Left) and Alerts (Right)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        DesignSystem.stylePanel(centerPanel);

        // -- Queue Panel --
        JPanel queuePanel = createSectionPanel("File d'attente (RDV du jour)");
        String[] qCols = { "Heure", "Patient", "Motif", "Statut" };
        queueModel = new DefaultTableModel(qCols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        queueTable = new JTable(queueModel);
        queueTable.setFont(DesignSystem.BODY);
        queueTable.setRowHeight(25);
        queuePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);
        centerPanel.add(queuePanel);

        // -- Alerts Panel --
        JPanel alertsPanel = createSectionPanel("Alertes & Notifications");
        alertsModel = new DefaultListModel<>();
        alertsList = new JList<>(alertsModel);
        alertsList.setFont(DesignSystem.BODY);
        alertsList.setForeground(new Color(220, 53, 69)); // Red
                                                          // for
                                                          // alerts
        alertsPanel.add(new JScrollPane(alertsList), BorderLayout.CENTER);
        centerPanel.add(alertsPanel);

        add(centerPanel, BorderLayout.CENTER);

        // South: KPIs
        JPanel kpiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        DesignSystem.stylePanel(kpiPanel);

        totalPatientsLabel = createKpiLabel("Total Patients: 0");
        revenueLabel = createKpiLabel("Recette du jour: 0.00 DH");

        rdvTodayLabel = createKpiLabel("RDV du jour: 0");
        completedConsultationsLabel = createKpiLabel("Consultations: 0");

        kpiPanel.add(totalPatientsLabel);
        kpiPanel.add(rdvTodayLabel);
        kpiPanel.add(completedConsultationsLabel);
        kpiPanel.add(revenueLabel);

        add(kpiPanel, BorderLayout.SOUTH);
    }

    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        DesignSystem.stylePanel(p);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(DesignSystem.PRIMARY), title));
        return p;
    }

    private JLabel createKpiLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(DesignSystem.SUBTITLE);
        l.setForeground(DesignSystem.PRIMARY);
        return l;
    }

    public DefaultTableModel getQueueModel() {
        return queueModel;
    }

    public DefaultListModel<String> getAlertsModel() {
        return alertsModel;
    }

    public void setTotalPatients(long count) {
        totalPatientsLabel.setText("Total Patients: " + count);
    }

    public void setDailyRevenue(double amount) {
        revenueLabel.setText(String.format("Recette du jour: %.2f DH", amount));
    }

    public void setRdvTodayCount(long count) {
        rdvTodayLabel.setText("RDV du jour: " + count);
    }

    public void setCompletedConsultationsCount(long count) {
        completedConsultationsLabel.setText("Consultations: " + count);
    }
}
