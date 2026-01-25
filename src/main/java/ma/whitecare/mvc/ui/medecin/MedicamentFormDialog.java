package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.entities.enums.FormeMedicament;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class MedicamentFormDialog extends JDialog {
    private JTextField nomField;
    private JTextField laboField;
    private JTextField typeField;
    private JComboBox<FormeMedicament> formeCombo;
    private JSpinner prixSpinner;
    private JCheckBox remboursableCheck;
    private JTextArea descArea;
    private JButton saveBtn, cancelBtn;
    private boolean confirmed = false;

    public MedicamentFormDialog(Frame owner, String title, Medicament medicament) {
        super(owner, title, true);
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        DesignSystem.stylePanel(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Fields
        nomField = new JTextField(20);
        laboField = new JTextField(20);
        typeField = new JTextField(20);
        formeCombo = new JComboBox<>(FormeMedicament.values());
        prixSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 1.0));
        remboursableCheck = new JCheckBox("Remboursable");
        remboursableCheck.setOpaque(false);
        descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        // Add to panel
        int row = 0;
        addLabelAndField(mainPanel, "Nom :", nomField, gbc, row++);
        addLabelAndField(mainPanel, "Laboratoire :", laboField, gbc, row++);
        addLabelAndField(mainPanel, "Type :", typeField, gbc, row++);
        addLabelAndField(mainPanel, "Forme :", formeCombo, gbc, row++);
        addLabelAndField(mainPanel, "Prix (DH) :", prixSpinner, gbc, row++);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(remboursableCheck, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Description :"), gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(new JScrollPane(descArea), gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(btnPanel);
        saveBtn = new RoundedButton("Enregistrer");
        saveBtn.setBackground(DesignSystem.PRIMARY);
        cancelBtn = new RoundedButton("Annuler");

        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                confirmed = true;
                setVisible(false);
            }
        });
        cancelBtn.addActionListener(e -> setVisible(false));

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Load data if editing
        if (medicament != null) {
            nomField.setText(medicament.getNom());
            laboField.setText(medicament.getLaboratoire());
            typeField.setText(medicament.getType());
            formeCombo.setSelectedItem(medicament.getForme());
            prixSpinner.setValue(medicament.getPrixUnitaire() != null ? medicament.getPrixUnitaire() : 0.0);
            remboursableCheck.setSelected(medicament.getRemboursable() != null ? medicament.getRemboursable() : false);
            descArea.setText(medicament.getDescription());
        }
    }

    private void addLabelAndField(JPanel panel, String label, Component field, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private boolean validateForm() {
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Medicament getMedicament(Medicament existing) {
        Medicament m = existing != null ? existing : new Medicament();
        m.setNom(nomField.getText().trim());
        m.setLaboratoire(laboField.getText().trim());
        m.setType(typeField.getText().trim());
        m.setForme((FormeMedicament) formeCombo.getSelectedItem());
        m.setPrixUnitaire((Double) prixSpinner.getValue());
        m.setRemboursable(remboursableCheck.isSelected());
        m.setDescription(descArea.getText().trim());
        return m;
    }
}
