package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Vector;

public class RDVFormDialog extends JDialog {
    private JComboBox<Patient> patientCombo;
    private JTextField dateField; // AAAA-MM-JJ
    private JTextField heureField; // HH:MM
    private JTextField motifField;
    private JComboBox<StatutRendezVous> statutCombo;

    private JButton saveBtn;
    private JButton cancelBtn;

    private boolean confirmed = false;
    private RDV rdv;

    public RDVFormDialog(Frame parent, RDV rdv, List<Patient> patients) {
        super(parent, rdv == null ? "Nouveau Rendez-vous" : "Modifier Rendez-vous", true);
        this.rdv = rdv;

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(500, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(mainPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(createSectionHeader("Détails du Rendez-vous"));

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

        mainPanel.add(createFieldPanel("Date (AAAA-MM-JJ) *:", dateField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Heure (HH:MM) *:", heureField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Motif:", motifField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Statut:", statutCombo = new JComboBox<>(StatutRendezVous.values())));

        if (rdv != null) {
            if (rdv.getDossierMedicale() != null && rdv.getDossierMedicale().getPatient() != null) {
                patientCombo.setSelectedItem(rdv.getDossierMedicale().getPatient());
            }
            if (rdv.getDate() != null)
                dateField.setText(rdv.getDate().toString());
            if (rdv.getHeure() != null)
                heureField.setText(rdv.getHeure().toString());
            motifField.setText(rdv.getMotif());
            statutCombo.setSelectedItem(rdv.getStatut());
        } else {
            dateField.setText(LocalDate.now().toString());
            heureField.setText("09:00");
            statutCombo.setSelectedItem(StatutRendezVous.PLANIFIE);
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
        if (patientCombo.getSelectedItem() == null || dateField.getText().isEmpty() || heureField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir les champs obligatoires.");
            return false;
        }
        try {
            LocalDate.parse(dateField.getText());
            LocalTime.parse(heureField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format de date (AAAA-MM-JJ) ou d'heure (HH:MM) invalide.");
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

    public LocalDate getDate() {
        return LocalDate.parse(dateField.getText());
    }

    public LocalTime getHeure() {
        return LocalTime.parse(heureField.getText());
    }

    public String getMotif() {
        return motifField.getText();
    }

    public StatutRendezVous getStatut() {
        return (StatutRendezVous) statutCombo.getSelectedItem();
    }
}
