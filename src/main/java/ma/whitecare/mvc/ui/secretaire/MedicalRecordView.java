package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MedicalRecordView extends JDialog {
    private RoundedButton manageAntecedentsBtn;

    public MedicalRecordView(Frame parent, Patient patient, List<RDV> rdvList, List<Facture> factureList,
            List<ma.whitecare.entities.medical.Consultation> consultations,
            List<Ordonnance> ordonnanceList, List<Certificat> certificatList) {
        super(parent, "Dossier Médical - " + patient.getNom() + " " + patient.getPrenom(), true);

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(800, 700);
        setLocationRelativeTo(parent);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(DesignSystem.BODY);

        // General Info
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 20, 10));
        DesignSystem.stylePanel(infoPanel);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        addInfo(infoPanel, "Nom:", patient.getNom());
        addInfo(infoPanel, "Prénom:", patient.getPrenom());
        addInfo(infoPanel, "Date Naissance:",
                patient.getDateNaissance() != null ? patient.getDateNaissance().toString() : "-");
        addInfo(infoPanel, "Sexe:", patient.getSexe().toString());
        addInfo(infoPanel, "Téléphone:", patient.getTelephone());
        addInfo(infoPanel, "Email:", patient.getEmail());
        addInfo(infoPanel, "Adresse:", patient.getAdresse());
        addInfo(infoPanel, "Assurance:", patient.getAssurance().toString());
        tabs.addTab("Informations Générales", new JScrollPane(infoPanel));

        // Antecedents
        JPanel antPanel = new JPanel();
        antPanel.setLayout(new BoxLayout(antPanel, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(antPanel);
        antPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        if (patient.getAntecedents() != null) {
            for (Antecedents a : patient.getAntecedents()) {
                JLabel lbl = new JLabel(
                        "• " + a.getNom() + " (" + a.getCategorie() + ") - Risque: " + a.getNiveauDeRisque());
                lbl.setFont(DesignSystem.BODY);
                antPanel.add(lbl);
            }
        } else {
            antPanel.add(new JLabel("Aucun antécédent renseigné."));
        }

        JPanel antContainer = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(antContainer);
        antContainer.add(new JScrollPane(antPanel), BorderLayout.CENTER);

        manageAntecedentsBtn = new RoundedButton("Gérer les Antécédents");
        manageAntecedentsBtn.setBackground(DesignSystem.PRIMARY);
        manageAntecedentsBtn.setForeground(Color.WHITE);
        JPanel antActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        DesignSystem.stylePanel(antActions);
        antActions.add(manageAntecedentsBtn);
        antContainer.add(antActions, BorderLayout.SOUTH);

        tabs.addTab("Antécédents", antContainer);

        // Rendez-vous
        JPanel rdvPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(rdvPanel);
        String[] rdvCols = { "Date", "Heure", "Motif", "Statut" };
        Object[][] rdvData = new Object[rdvList.size()][4];
        for (int i = 0; i < rdvList.size(); i++) {
            RDV r = rdvList.get(i);
            rdvData[i] = new Object[] { r.getDate(), r.getHeure(), r.getMotif(), r.getStatut() };
        }
        JTable rdvTable = new JTable(rdvData, rdvCols);
        rdvTable.setFont(DesignSystem.BODY);
        rdvPanel.add(new JScrollPane(rdvTable), BorderLayout.CENTER);
        tabs.addTab("Rendez-vous", rdvPanel);

        // Facturation
        JPanel finPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(finPanel);
        String[] finCols = { "Date", "Total", "Payé", "Reste", "Statut" };
        Object[][] finData = new Object[factureList.size()][5];
        for (int i = 0; i < factureList.size(); i++) {
            Facture f = factureList.get(i);
            finData[i] = new Object[] { f.getDateFacture(), f.getTotaleFacture(), f.getTotalePayé(), f.getReste(),
                    f.getStatut() };
        }
        JTable finTable = new JTable(finData, finCols);
        finTable.setFont(DesignSystem.BODY);
        finPanel.add(new JScrollPane(finTable), BorderLayout.CENTER);
        tabs.addTab("Facturation", finPanel);

        // Consultations
        JPanel consPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(consPanel);
        String[] consCols = { "Date", "Type", "Actes", "Statut" };
        Object[][] consData = new Object[consultations.size()][4];
        for (int i = 0; i < consultations.size(); i++) {
            ma.whitecare.entities.medical.Consultation c = consultations.get(i);
            // Format Acts with Details (Name, Tooth, Price)
            String actsStr = "-";
            if (c.getInterventionMedecinList() != null && !c.getInterventionMedecinList().isEmpty()) {
                actsStr = c.getInterventionMedecinList().stream()
                        .map(im -> {
                            String name = (im.getActe() != null && im.getActe().getLibelle() != null)
                                    ? im.getActe().getLibelle()
                                    : "Acte Inconnu";
                            String tooth = (im.getNumDent() != null && im.getNumDent() != 0)
                                    ? " (Dent: " + im.getNumDent() + ")"
                                    : " (Global)";
                            String price = (im.getPrixDePatient() != null)
                                    ? " [" + im.getPrixDePatient() + " MAD]"
                                    : "";
                            return name + tooth + price;
                        })
                        .collect(java.util.stream.Collectors.joining(", "));
            }

            consData[i] = new Object[] {
                    c.getDate(),
                    "Consultation",
                    actsStr,
                    c.getStatut()
            };
        }
        JTable consTable = new JTable(consData, consCols);
        consTable.setFont(DesignSystem.BODY);
        consPanel.add(new JScrollPane(consTable), BorderLayout.CENTER);

        // Details Button for Consultations
        JPanel consActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        DesignSystem.stylePanel(consActions);
        RoundedButton viewDetailsBtn = new RoundedButton("Afficher Détails");
        viewDetailsBtn.setBackground(DesignSystem.PRIMARY);
        viewDetailsBtn.setForeground(Color.WHITE);
        viewDetailsBtn.addActionListener(e -> {
            int row = consTable.getSelectedRow();
            if (row != -1) {
                showConsultationDetails(consultations.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une consultation pour voir les détails.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        consActions.add(viewDetailsBtn);
        consPanel.add(consActions, BorderLayout.SOUTH);

        tabs.addTab("Consultations", consPanel);

        // Ordonnances
        JPanel ordPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(ordPanel);
        String[] ordCols = { "Date", "Médicaments", "ID" };
        Object[][] ordData = new Object[ordonnanceList.size()][3];
        for (int i = 0; i < ordonnanceList.size(); i++) {
            Ordonnance o = ordonnanceList.get(i);
            String medsSummary = (o.getPrescriptionList() != null)
                    ? o.getPrescriptionList().size() + " médicament(s)"
                    : "0 médicament";
            ordData[i] = new Object[] { o.getDate(), medsSummary, o.getIdOrd() };
        }
        JTable ordTable = new JTable(ordData, ordCols);
        ordTable.setFont(DesignSystem.BODY);
        ordPanel.add(new JScrollPane(ordTable), BorderLayout.CENTER);

        JPanel ordActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        DesignSystem.stylePanel(ordActions);
        RoundedButton viewOrdDetailsBtn = new RoundedButton("Afficher Détails Ordonnance");
        viewOrdDetailsBtn.setBackground(DesignSystem.PRIMARY);
        viewOrdDetailsBtn.setForeground(Color.WHITE);
        viewOrdDetailsBtn.addActionListener(e -> {
            int row = ordTable.getSelectedRow();
            if (row != -1) {
                showOrdonnanceDetails(ordonnanceList.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ordonnance.");
            }
        });
        ordActions.add(viewOrdDetailsBtn);
        ordPanel.add(ordActions, BorderLayout.SOUTH);
        tabs.addTab("Ordonnances", ordPanel);

        // Certificats
        JPanel certPanel = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(certPanel);
        String[] certCols = { "Date Début", "Date Fin", "Durée", "ID" };
        Object[][] certData = new Object[certificatList.size()][4];
        for (int i = 0; i < certificatList.size(); i++) {
            Certificat c = certificatList.get(i);
            certData[i] = new Object[] { c.getDateDebut(), c.getDateFin(), c.getDuree() + " jrs", c.getIdCertif() };
        }
        JTable certTable = new JTable(certData, certCols);
        certTable.setFont(DesignSystem.BODY);
        certPanel.add(new JScrollPane(certTable), BorderLayout.CENTER);

        JPanel certActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        DesignSystem.stylePanel(certActions);
        RoundedButton viewCertDetailsBtn = new RoundedButton("Afficher Détails Certificat");
        viewCertDetailsBtn.setBackground(DesignSystem.PRIMARY);
        viewCertDetailsBtn.setForeground(Color.WHITE);
        viewCertDetailsBtn.addActionListener(e -> {
            int row = certTable.getSelectedRow();
            if (row != -1) {
                showCertificatDetails(certificatList.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un certificat.");
            }
        });
        certActions.add(viewCertDetailsBtn);
        certPanel.add(certActions, BorderLayout.SOUTH);
        tabs.addTab("Certificats", certPanel);

        add(tabs, BorderLayout.CENTER);

        // Close button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(btnPanel);
        RoundedButton closeBtn = new RoundedButton("Fermer");
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addInfo(JPanel p, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(DesignSystem.SUBTITLE);
        lbl.setForeground(DesignSystem.PRIMARY);
        p.add(lbl);

        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(DesignSystem.BODY);
        p.add(val);
    }

    private void showConsultationDetails(ma.whitecare.entities.medical.Consultation consultation) {
        JDialog detailDialog = new JDialog(this, "Détails de la Consultation - " + consultation.getDate(), true);
        detailDialog.setSize(500, 400);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        DesignSystem.stylePanel(mainPanel);

        JLabel title = new JLabel("Interventions effectuées :");
        title.setFont(DesignSystem.SUBTITLE);
        mainPanel.add(title, BorderLayout.NORTH);

        String[] cols = { "Acte", "Dent", "Prix" };
        List<ma.whitecare.entities.medical.InterventionMedecin> interventions = consultation
                .getInterventionMedecinList();
        Object[][] data = new Object[interventions != null ? interventions.size() : 0][3];

        if (interventions != null) {
            for (int i = 0; i < interventions.size(); i++) {
                ma.whitecare.entities.medical.InterventionMedecin im = interventions.get(i);
                data[i] = new Object[] {
                        (im.getActe() != null && im.getActe().getLibelle() != null) ? im.getActe().getLibelle()
                                : "Acte Inconnu",
                        (im.getNumDent() != null && im.getNumDent() != 0) ? im.getNumDent() : "Global",
                        (im.getPrixDePatient() != null ? im.getPrixDePatient() : 0.0) + " MAD"
                };
            }
        }

        JTable table = new JTable(data, cols);
        table.setFont(DesignSystem.BODY);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        RoundedButton closeBtn = new RoundedButton("Fermer");
        closeBtn.addActionListener(e -> detailDialog.dispose());
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(pnl);
        pnl.add(closeBtn);

        detailDialog.add(mainPanel, BorderLayout.CENTER);
        detailDialog.add(pnl, BorderLayout.SOUTH);
        detailDialog.setVisible(true);
    }

    private void showOrdonnanceDetails(Ordonnance ordonnance) {
        JDialog detailDialog = new JDialog(this, "Détails Ordonnance - " + ordonnance.getDate(), true);
        detailDialog.setSize(600, 400);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        DesignSystem.stylePanel(mainPanel);

        JLabel title = new JLabel("Médicaments prescrits :");
        title.setFont(DesignSystem.SUBTITLE);
        mainPanel.add(title, BorderLayout.NORTH);

        String[] cols = { "Médicament", "Quantité", "Fréquence", "Durée" };
        List<Prescription> prescriptions = ordonnance.getPrescriptionList();
        Object[][] data = new Object[prescriptions != null ? prescriptions.size() : 0][4];

        if (prescriptions != null) {
            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription p = prescriptions.get(i);
                data[i] = new Object[] {
                        (p.getMedicament() != null) ? p.getMedicament().getNom() : "Inconnu",
                        p.getQuantite(),
                        p.getFrequence(),
                        p.getDureeEnJours() + " jrs"
                };
            }
        }

        JTable table = new JTable(data, cols);
        table.setFont(DesignSystem.BODY);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        RoundedButton closeBtn = new RoundedButton("Fermer");
        closeBtn.addActionListener(e -> detailDialog.dispose());
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(pnl);
        pnl.add(closeBtn);

        detailDialog.add(mainPanel, BorderLayout.CENTER);
        detailDialog.add(pnl, BorderLayout.SOUTH);
        detailDialog.setVisible(true);
    }

    private void showCertificatDetails(Certificat certificat) {
        JDialog detailDialog = new JDialog(this, "Détails Certificat", true);
        detailDialog.setSize(500, 450);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        DesignSystem.stylePanel(mainPanel);

        addDetailRow(mainPanel, "Date Début:", certificat.getDateDebut().toString());
        addDetailRow(mainPanel, "Date Fin:", certificat.getDateFin().toString());
        addDetailRow(mainPanel, "Durée:", certificat.getDuree() + " jours");

        mainPanel.add(Box.createVerticalStrut(20));
        JLabel noteLbl = new JLabel("Note Médicale:");
        noteLbl.setFont(DesignSystem.SUBTITLE);
        noteLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(noteLbl);

        JTextArea noteArea = new JTextArea(certificat.getNoteMedecin());
        noteArea.setFont(DesignSystem.BODY);
        noteArea.setEditable(false);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setBackground(DesignSystem.BACKGROUND_ALT);
        JScrollPane scroll = new JScrollPane(noteArea);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scroll);

        RoundedButton closeBtn = new RoundedButton("Fermer");
        closeBtn.addActionListener(e -> detailDialog.dispose());
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(pnl);
        pnl.add(closeBtn);

        detailDialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        detailDialog.add(pnl, BorderLayout.SOUTH);
        detailDialog.setVisible(true);
    }

    private void addDetailRow(JPanel p, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(row);
        JLabel lbl = new JLabel(label);
        lbl.setFont(DesignSystem.SUBTITLE);
        lbl.setPreferredSize(new Dimension(120, 25));
        row.add(lbl);
        JLabel val = new JLabel(value);
        val.setFont(DesignSystem.BODY);
        row.add(val);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(row);
    }

    public RoundedButton getManageAntecedentsBtn() {
        return manageAntecedentsBtn;
    }
}
