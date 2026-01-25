package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class CertificatDialog extends JDialog {
    private JTextField dateDebutField, dateFinField, dureeField;
    private JTextArea noteArea;
    private boolean succeeded;

    public CertificatDialog(Frame parent, String patientName) {
        super(parent, "Nouveau Certificat Médical", true);
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        DesignSystem.stylePanel(mainPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel header = new JLabel("Certificat pour : " + patientName);
        header.setFont(DesignSystem.SUBTITLE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(header, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Date Début (AAAA-MM-JJ):"), gbc);
        dateDebutField = new JTextField(java.time.LocalDate.now().toString());
        gbc.gridx = 1;
        mainPanel.add(dateDebutField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Date Fin (AAAA-MM-JJ):"), gbc);
        dateFinField = new JTextField(java.time.LocalDate.now().plusDays(1).toString());
        gbc.gridx = 1;
        mainPanel.add(dateFinField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Durée (jours):"), gbc);
        dureeField = new JTextField("1");
        dureeField.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(dureeField, gbc);

        // Auto-calculate duration
        java.awt.event.FocusAdapter dateListener = new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                calculateDuration();
            }
        };
        dateDebutField.addFocusListener(dateListener);
        dateFinField.addFocusListener(dateListener);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Note Médicale / Observation:"), gbc);
        noteArea = new JTextArea(5, 20);
        noteArea.setFont(DesignSystem.BODY);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        mainPanel.add(new JScrollPane(noteArea), gbc);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(bottomPanel);

        RoundedButton btnSave = new RoundedButton("Générer & Enregistrer");
        btnSave.setBackground(DesignSystem.PRIMARY);
        btnSave.addActionListener(e -> {
            if (validateFields()) {
                succeeded = true;
                dispose();
            }
        });

        RoundedButton btnCancel = new RoundedButton("Annuler");
        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        calculateDuration();
    }

    private void calculateDuration() {
        try {
            java.time.LocalDate d1 = java.time.LocalDate.parse(dateDebutField.getText().trim());
            java.time.LocalDate d2 = java.time.LocalDate.parse(dateFinField.getText().trim());
            if (!d2.isBefore(d1)) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(d1, d2) + 1;
                dureeField.setText(String.valueOf(days));
            } else {
                dureeField.setText("0");
            }
        } catch (Exception ignored) {
        }
    }

    private boolean validateFields() {
        try {
            java.time.LocalDate.parse(dateDebutField.getText().trim());
            java.time.LocalDate.parse(dateFinField.getText().trim());
            if (noteArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La note médicale est obligatoire.");
                return false;
            }
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide (AAAA-MM-JJ).");
            return false;
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public java.time.LocalDate getDateDebut() {
        return java.time.LocalDate.parse(dateDebutField.getText().trim());
    }

    public java.time.LocalDate getDateFin() {
        return java.time.LocalDate.parse(dateFinField.getText().trim());
    }

    public int getDuree() {
        return Integer.parseInt(dureeField.getText());
    }

    public String getNoteMedecin() {
        return noteArea.getText().trim();
    }
}
