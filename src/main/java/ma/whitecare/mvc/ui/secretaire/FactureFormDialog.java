package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.enums.StatutFacture;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Vector;

public class FactureFormDialog extends JDialog {
    private JComboBox<Patient> patientCombo;
    private JTextField totalField;
    private JTextField payeField;
    private JComboBox<StatutFacture> statutCombo;

    private JButton saveBtn;
    private JButton cancelBtn;

    private boolean confirmed = false;
    private Facture facture;

    public FactureFormDialog(Frame parent, Facture facture, List<Patient> patients) {
        super(parent, facture == null ? "Nouvelle Facture" : "Modifier Facture", true);
        this.facture = facture;

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(500, 450);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(mainPanel);

        mainPanel.add(createSectionHeader("Détails de la Facture"));

        patientCombo = new JComboBox<>(new Vector<>(patients));
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    Patient p = (Patient) value;
                    setText(p.getNom() + " " + p.getPrenom());
                }
                return this;
            }
        });
        mainPanel.add(createFieldPanel("Patient *:", patientCombo));

        mainPanel.add(createFieldPanel("Total (MAD) *:", totalField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Montant Payé (MAD):", payeField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Statut:", statutCombo = new JComboBox<>(StatutFacture.values())));

        if (facture != null) {
            // Fix: select patient by ID to ensure it matches the combo box model
            if (facture.getSituationFinanciere() != null
                    && facture.getSituationFinanciere().getDossierMedicale() != null
                    && facture.getSituationFinanciere().getDossierMedicale().getPatient() != null) {
                Long targetId = facture.getSituationFinanciere().getDossierMedicale().getPatient().getId_Patient();
                for (int i = 0; i < patientCombo.getItemCount(); i++) {
                    Patient p = patientCombo.getItemAt(i);
                    if (p.getId_Patient().equals(targetId)) {
                        patientCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            patientCombo.setEnabled(false); // Do not allow changing patient on existing facture
            totalField.setText(String.valueOf(facture.getTotaleFacture()));
            payeField.setText(String.valueOf(facture.getTotalePayé()));
            statutCombo.setSelectedItem(facture.getStatut());
        } else {
            totalField.setText("0.0");
            payeField.setText("0.0");
            statutCombo.setSelectedItem(StatutFacture.IMPAYEE);
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
        add(mainPanel, BorderLayout.CENTER); // Fix: Add the main panel to the dialog
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
        if (patientCombo.getSelectedItem() == null || totalField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir les champs obligatoires.");
            return false;
        }
        try {
            Double.parseDouble(totalField.getText());
            Double.parseDouble(payeField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des montants valides.");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Patient getSelectedPatient() {
        return (Patient) patientCombo.getSelectedItem();
    }

    public Double getTotal() {
        return Double.parseDouble(totalField.getText());
    }

    public Double getPaye() {
        return Double.parseDouble(payeField.getText());
    }

    public StatutFacture getStatut() {
        return (StatutFacture) statutCombo.getSelectedItem();
    }
}
