package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedecinHomePanel extends JPanel {
    private JTable queueTable;
    private DefaultTableModel queueModel;
    private JList<String> alertsList;
    private DefaultListModel<String> alertsModel;
    private RoundedButton startConsultationBtn;

    public MedecinHomePanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel welcome = new JLabel("Espace Médecin - Tableau de Bord", JLabel.LEFT);
        welcome.setFont(DesignSystem.TITLE);
        welcome.setForeground(DesignSystem.TEXT_PRIMARY);
        add(welcome, BorderLayout.NORTH);

        // Center: Queue and Alerts
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        DesignSystem.stylePanel(centerPanel);

        // -- Queue Panel --
        JPanel queuePanel = createSectionPanel("Mes Rendez-vous du Jour");
        String[] qCols = { "ID", "Heure", "Patient", "Motif", "Statut" };
        queueModel = new DefaultTableModel(qCols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        queueTable = new JTable(queueModel);
        DesignSystem.styleTable(queueTable);
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queuePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

        // Action Button for Queue
        startConsultationBtn = new RoundedButton("Démarrer Consultation");
        startConsultationBtn.setBackground(DesignSystem.PRIMARY);
        startConsultationBtn.setPreferredSize(new Dimension(200, 40));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(btnPanel);
        btnPanel.add(startConsultationBtn);
        queuePanel.add(btnPanel, BorderLayout.SOUTH);

        centerPanel.add(queuePanel);

        // -- Alerts Panel --
        JPanel alertsPanel = createSectionPanel("Alertes Médicales");
        alertsModel = new DefaultListModel<>();
        alertsList = new JList<>(alertsModel);
        alertsList.setFont(DesignSystem.BODY);
        alertsList.setForeground(new Color(220, 53, 69));
        alertsPanel.add(new JScrollPane(alertsList), BorderLayout.CENTER);
        centerPanel.add(alertsPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        DesignSystem.stylePanel(p);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(DesignSystem.PRIMARY), title));
        return p;
    }

    public DefaultTableModel getQueueModel() {
        return queueModel;
    }

    public JTable getQueueTable() {
        return queueTable;
    }

    public DefaultListModel<String> getAlertsModel() {
        return alertsModel;
    }

    public RoundedButton getStartConsultationBtn() {
        return startConsultationBtn;
    }
}
