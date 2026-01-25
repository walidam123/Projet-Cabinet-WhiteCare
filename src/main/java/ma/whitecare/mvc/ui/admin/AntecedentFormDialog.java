package ma.whitecare.mvc.ui.admin;

import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class AntecedentFormDialog extends JDialog {
    private JTextField nomField;
    private JTextField categorieField;
    private JComboBox<NiveauDeRisque> risqueCombo;
    private JButton saveBtn;
    private JButton cancelBtn;
    private boolean confirmed = false;
    private Antecedents antecedent;

    public AntecedentFormDialog(Frame parent, Antecedents antecedent) {
        super(parent, antecedent == null ? "Ajouter Antécédent" : "Modifier Antécédent", true);
        this.antecedent = antecedent != null ? antecedent : new Antecedents();

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(450, 350);
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridBagLayout());
        DesignSystem.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addLabelAndField(formPanel, "Nom *:", nomField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Catégorie:", categorieField = new JTextField(20), gbc, row++);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel risqueLabel = new JLabel("Niveau de Risque:");
        DesignSystem.styleLabel(risqueLabel, DesignSystem.BODY);
        formPanel.add(risqueLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        risqueCombo = new JComboBox<>(NiveauDeRisque.values());
        risqueCombo.setFont(DesignSystem.BODY);
        formPanel.add(risqueCombo, gbc);

        add(formPanel, BorderLayout.CENTER);

        if (antecedent != null && antecedent.getId_Antecedent() != null) {
            nomField.setText(antecedent.getNom());
            categorieField.setText(antecedent.getCategorie());
            if (antecedent.getNiveauDeRisque() != null)
                risqueCombo.setSelectedItem(antecedent.getNiveauDeRisque());
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saveBtn = new RoundedButton("Enregistrer");
        cancelBtn = new RoundedButton("Annuler");
        cancelBtn.setBackground(DesignSystem.TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                this.antecedent.setNom(nomField.getText());
                this.antecedent.setCategorie(categorieField.getText());
                this.antecedent.setNiveauDeRisque((NiveauDeRisque) risqueCombo.getSelectedItem());
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

    public Antecedents getAntecedent() {
        return antecedent;
    }

    public void setReadOnly(boolean readOnly) {
        nomField.setEditable(!readOnly);
        categorieField.setEditable(!readOnly);
        risqueCombo.setEnabled(!readOnly);

        if (saveBtn != null)
            saveBtn.setVisible(!readOnly);
        setTitle(readOnly ? "Détails Antécédent"
                : (antecedent.getId_Antecedent() == null ? "Ajouter Antécédent" : "Modifier Antécédent"));
    }
}
