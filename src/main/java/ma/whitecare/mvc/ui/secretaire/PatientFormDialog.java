package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class PatientFormDialog extends JDialog {
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField telField;
    private JTextField emailField;
    private JTextField adresseField;
    private JTextField dateNaissanceField; // Simple text for now, format: YYYY-MM-DD
    private JComboBox<Sexe> sexeCombo;
    private JComboBox<Assurance> assuranceCombo;

    private JButton saveBtn;
    private JButton cancelBtn;

    private boolean confirmed = false;
    private Patient patient;

    public PatientFormDialog(Frame parent, Patient patient) {
        super(parent, patient == null ? "Ajouter Patient" : "Modifier Patient", true);
        this.patient = patient;

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(500, 600);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(mainPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(createSectionHeader("Informations Personnelles"));
        mainPanel.add(createFieldPanel("Nom *:", nomField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Prénom *:", prenomField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Date Naissance (AAAA-MM-JJ):", dateNaissanceField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Sexe:", sexeCombo = new JComboBox<>(Sexe.values())));

        mainPanel.add(createSectionHeader("Coordonnées"));
        mainPanel.add(createFieldPanel("Téléphone *:", telField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Email:", emailField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Adresse:", adresseField = new JTextField(20)));

        mainPanel.add(createSectionHeader("Assurance"));
        mainPanel.add(createFieldPanel("Type d'assurance:", assuranceCombo = new JComboBox<>(Assurance.values())));

        if (patient != null) {
            nomField.setText(patient.getNom());
            prenomField.setText(patient.getPrenom());
            telField.setText(patient.getTelephone());
            emailField.setText(patient.getEmail());
            adresseField.setText(patient.getAdresse());
            if (patient.getDateNaissance() != null) {
                dateNaissanceField.setText(patient.getDateNaissance().toString());
            }
            sexeCombo.setSelectedItem(patient.getSexe());
            assuranceCombo.setSelectedItem(patient.getAssurance());
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saveBtn = new RoundedButton("Enregistrer");
        cancelBtn = new RoundedButton("Annuler");
        cancelBtn.setBackground(DesignSystem.TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                confirmed = true;
                dispose();
            }
        });
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFieldPanel(String label, Component field) {
        JPanel p = new JPanel(new BorderLayout());
        DesignSystem.stylePanel(p);
        p.setMaximumSize(new Dimension(500, 45));
        p.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel lbl = new JLabel(label);
        DesignSystem.styleLabel(lbl, DesignSystem.BODY);
        lbl.setPreferredSize(new Dimension(160, 25));

        p.add(lbl, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);

        if (field instanceof JComponent) {
            ((JComponent) field).setFont(DesignSystem.BODY);
        }

        return p;
    }

    private JPanel createSectionHeader(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        DesignSystem.stylePanel(p);
        p.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        JLabel lbl = new JLabel(title);
        lbl.setFont(DesignSystem.SUBTITLE);
        lbl.setForeground(DesignSystem.PRIMARY);
        p.add(lbl);
        return p;
    }

    private boolean validateForm() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || telField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Les champs Nom, Prénom et Téléphone sont obligatoires.");
            return false;
        }
        try {
            if (!dateNaissanceField.getText().isEmpty()) {
                java.time.LocalDate.parse(dateNaissanceField.getText());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide (AAAA-MM-JJ).");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getNom() {
        return nomField.getText();
    }

    public String getPrenom() {
        return prenomField.getText();
    }

    public String getTel() {
        return telField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getAdresse() {
        return adresseField.getText();
    }

    public java.time.LocalDate getDateNaissance() {
        return dateNaissanceField.getText().isEmpty() ? null : java.time.LocalDate.parse(dateNaissanceField.getText());
    }

    public Sexe getSexe() {
        return (Sexe) sexeCombo.getSelectedItem();
    }

    public Assurance getAssurance() {
        return (Assurance) assuranceCombo.getSelectedItem();
    }
}
