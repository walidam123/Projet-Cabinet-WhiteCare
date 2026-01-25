package ma.whitecare.mvc.ui.admin;

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
    private JTextField prixField;
    private JTextArea descArea;
    private JCheckBox remboursableCheck;
    private JButton saveBtn;
    private JButton cancelBtn;
    private boolean confirmed = false;
    private Medicament medicament;

    public MedicamentFormDialog(Frame parent, Medicament medicament) {
        super(parent, medicament == null ? "Ajouter Médicament" : "Modifier Médicament", true);
        this.medicament = medicament != null ? medicament : new Medicament();

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

        JPanel formPanel = new JPanel(new GridBagLayout());
        DesignSystem.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addLabelAndField(formPanel, "Nom *:", nomField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Laboratoire:", laboField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Type:", typeField = new JTextField(20), gbc, row++);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel formeLabel = new JLabel("Forme:");
        DesignSystem.styleLabel(formeLabel, DesignSystem.BODY);
        formPanel.add(formeLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formeCombo = new JComboBox<>(FormeMedicament.values());
        formeCombo.setFont(DesignSystem.BODY);
        formPanel.add(formeCombo, gbc);
        row++;

        addLabelAndField(formPanel, "Prix Unitaire:", prixField = new JTextField(20), gbc, row++);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel rembLabel = new JLabel("Remboursable:");
        DesignSystem.styleLabel(rembLabel, DesignSystem.BODY);
        formPanel.add(rembLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        remboursableCheck = new JCheckBox();
        remboursableCheck.setBackground(DesignSystem.BACKGROUND);
        formPanel.add(remboursableCheck, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel descLabel = new JLabel("Description:");
        DesignSystem.styleLabel(descLabel, DesignSystem.BODY);
        formPanel.add(descLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        descArea = new JTextArea(5, 20);
        descArea.setFont(DesignSystem.BODY);
        descArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descArea), gbc);
        row++;

        mainPanel.add(formPanel);

        if (medicament != null && medicament.getIdMct() != null) {
            nomField.setText(medicament.getNom());
            laboField.setText(medicament.getLaboratoire());
            typeField.setText(medicament.getType());
            if (medicament.getForme() != null)
                formeCombo.setSelectedItem(medicament.getForme());
            prixField
                    .setText(medicament.getPrixUnitaire() != null ? String.valueOf(medicament.getPrixUnitaire()) : "0");
            descArea.setText(medicament.getDescription());
            remboursableCheck.setSelected(medicament.getRemboursable() != null && medicament.getRemboursable());
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saveBtn = new RoundedButton("Enregistrer");
        cancelBtn = new RoundedButton("Annuler");
        cancelBtn.setBackground(DesignSystem.TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                this.medicament.setNom(nomField.getText());
                this.medicament.setLaboratoire(laboField.getText());
                this.medicament.setType(typeField.getText());
                this.medicament.setForme((FormeMedicament) formeCombo.getSelectedItem());
                try {
                    this.medicament.setPrixUnitaire(Double.parseDouble(prixField.getText()));
                } catch (Exception ex) {
                    this.medicament.setPrixUnitaire(0.0);
                }
                this.medicament.setDescription(descArea.getText());
                this.medicament.setRemboursable(remboursableCheck.isSelected());
                confirmed = true;
                dispose();
            }
        });
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lbl = new JLabel(labelText);
        DesignSystem.styleLabel(lbl, DesignSystem.BODY);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        field.setFont(DesignSystem.BODY);
        panel.add(field, gbc);
    }

    private boolean validateForm() {
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire.");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setReadOnly(boolean readOnly) {
        nomField.setEditable(!readOnly);
        laboField.setEditable(!readOnly);
        typeField.setEditable(!readOnly);
        formeCombo.setEnabled(!readOnly);
        prixField.setEditable(!readOnly);
        descArea.setEditable(!readOnly);
        remboursableCheck.setEnabled(!readOnly);

        if (saveBtn != null)
            saveBtn.setVisible(!readOnly);
        setTitle(readOnly ? "Détails Médicament"
                : (medicament.getIdMct() == null ? "Ajouter Médicament" : "Modifier Médicament"));
    }
}
