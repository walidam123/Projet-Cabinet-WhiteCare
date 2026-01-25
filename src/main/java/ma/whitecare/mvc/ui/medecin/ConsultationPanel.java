package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ConsultationPanel extends JPanel {
    private JLabel patientNameLabel, ageLabel, historyLabel;
    private JTextArea observationArea;
    private JTable actsTable;
    private DefaultTableModel actsModel;
    private JTable prescriptionTable;
    private DefaultTableModel prescriptionModel;
    private JButton addActBtn, addDrugBtn, finishBtn, certBtn;

    // Inputs for adding Act
    private JComboBox<String> actsCombo; // Placeholder for actual Act objects
    private JTextField toothField;
    private JTextField priceField;

    public ConsultationPanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));

        // 1. Patient Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        headerPanel.setBackground(DesignSystem.SECONDARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        patientNameLabel = new JLabel("Patient: -");
        patientNameLabel.setFont(DesignSystem.TITLE);
        patientNameLabel.setForeground(DesignSystem.PRIMARY);

        ageLabel = new JLabel("Âge: -");
        ageLabel.setFont(DesignSystem.SUBTITLE);

        historyLabel = new JLabel("Antécédents: -");
        historyLabel.setFont(DesignSystem.BODY);

        headerPanel.add(patientNameLabel);
        headerPanel.add(ageLabel);
        headerPanel.add(historyLabel);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(DesignSystem.SUBTITLE);

        // -- Tab 1: Observation --
        JPanel obsPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(obsPanel);
        obsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        observationArea = new JTextArea();
        observationArea.setFont(DesignSystem.BODY);
        observationArea.setLineWrap(true);
        observationArea.setWrapStyleWord(true);
        obsPanel.add(new JLabel("Observations Cliniques / Motif:"), BorderLayout.NORTH);
        obsPanel.add(new JScrollPane(observationArea), BorderLayout.CENTER);
        tabs.addTab("Observation", obsPanel);

        // -- Tab 2: Actes & Soins --
        JPanel actsPanel = new JPanel(new BorderLayout(5, 5));
        DesignSystem.stylePanel(actsPanel);
        actsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add Act Form (Top)
        JPanel addActPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(addActPanel);
        addActPanel.add(new JLabel("Acte:"));
        actsCombo = new JComboBox<>(); // Fill later
        actsCombo.setPreferredSize(new Dimension(200, 30));
        addActPanel.add(actsCombo);

        addActPanel.add(new JLabel("Dent:"));
        toothField = new JTextField(3);
        addActPanel.add(toothField);

        addActPanel.add(new JLabel("Prix:"));
        priceField = new JTextField(5);
        addActPanel.add(priceField);

        addActBtn = new RoundedButton("Ajouter");
        addActBtn.setBackground(DesignSystem.PRIMARY);
        addActBtn.setForeground(Color.WHITE);
        addActPanel.add(addActBtn);

        actsPanel.add(addActPanel, BorderLayout.NORTH);

        // Center Content (Table + Image Assistant)
        JPanel centerContent = new JPanel(new BorderLayout(10, 0));
        centerContent.setOpaque(false);

        // Acts Table
        String[] actsCols = { "Acte", "Dent", "Prix (DH)", "Remise" };
        actsModel = new DefaultTableModel(actsCols, 0);
        actsTable = new JTable(actsModel);
        actsTable.setRowHeight(25);
        DesignSystem.styleTable(actsTable);
        centerContent.add(new JScrollPane(actsTable), BorderLayout.CENTER);

        // Image Assistant (Right side)
        JPanel assistantPanel = new JPanel(new BorderLayout());
        assistantPanel.setOpaque(false);
        assistantPanel.setBorder(BorderFactory.createTitledBorder("Numérotation des Dents"));
        assistantPanel.setPreferredSize(new Dimension(300, 0));

        try {
            String imgPath = "/static/images/numerotation-des-dents.jpg";
            java.net.URL imgUrl = getClass().getResource(imgPath);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage();
                // Scale to fit width (280)
                int newH = (int) (img.getHeight(null) * (280.0 / img.getWidth(null)));
                Image scaled = img.getScaledInstance(280, newH, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaled));
                JScrollPane imgScroll = new JScrollPane(imgLabel);
                imgScroll.setBorder(null);
                imgScroll.setOpaque(false);
                imgScroll.getViewport().setOpaque(false);
                assistantPanel.add(imgScroll, BorderLayout.CENTER);
            } else {
                assistantPanel.add(new JLabel("Image non trouvée"), BorderLayout.CENTER);
            }
        } catch (Exception e) {
            assistantPanel.add(new JLabel("Erreur image"), BorderLayout.CENTER);
        }
        centerContent.add(assistantPanel, BorderLayout.EAST);

        actsPanel.add(centerContent, BorderLayout.CENTER);
        tabs.addTab("Actes & Soins", actsPanel);

        // -- Tab 3: Ordonnance --
        JPanel drugsPanel = new JPanel(new BorderLayout(5, 5));
        DesignSystem.stylePanel(drugsPanel);
        drugsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] drugCols = { "Médicament", "Posologie", "Durée", "Instruction" };
        prescriptionModel = new DefaultTableModel(drugCols, 0);
        prescriptionTable = new JTable(prescriptionModel);
        prescriptionTable.setRowHeight(25);

        JPanel drugsBtnBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(drugsBtnBar);
        addDrugBtn = new RoundedButton("Ajouter Médicament");
        addDrugBtn.setBackground(DesignSystem.PRIMARY);
        addDrugBtn.setForeground(Color.WHITE);
        drugsBtnBar.add(addDrugBtn);

        drugsPanel.add(drugsBtnBar, BorderLayout.NORTH);
        drugsPanel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);

        tabs.addTab("Ordonnance", drugsPanel);

        add(tabs, BorderLayout.CENTER);

        // 3. Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(footer);
        finishBtn = new RoundedButton("Terminer & Facturer");
        finishBtn.setBackground(new Color(40, 167, 69)); // Success Green
        finishBtn.setForeground(Color.WHITE);

        certBtn = new RoundedButton("Certificat");
        certBtn.setBackground(DesignSystem.ACCENT);
        certBtn.setForeground(Color.WHITE);

        footer.add(certBtn);
        footer.add(finishBtn);

        add(footer, BorderLayout.SOUTH);
    }

    // Getters for Controller
    public void setPatientInfo(String name, String age, String history) {
        patientNameLabel.setText("Patient: " + name);
        ageLabel.setText("Âge: " + age);
        historyLabel.setText("Antécédents: " + history);
    }

    public JTextArea getObservationArea() {
        return observationArea;
    }

    public JTable getActsTable() {
        return actsTable;
    }

    public DefaultTableModel getActsModel() {
        return actsModel;
    }

    public JTable getPrescriptionTable() {
        return prescriptionTable;
    }

    public DefaultTableModel getPrescriptionModel() {
        return prescriptionModel;
    }

    public JButton getAddActBtn() {
        return addActBtn;
    }

    public JButton getAddDrugBtn() {
        return addDrugBtn;
    }

    public JButton getCertBtn() {
        return certBtn;
    }

    public JButton getFinishBtn() {
        return finishBtn;
    }

    public JComboBox<String> getActsCombo() {
        return actsCombo;
    }

    public JTextField getToothField() {
        return toothField;
    }

    public JTextField getPriceField() {
        return priceField;
    }
}
