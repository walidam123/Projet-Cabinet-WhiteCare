package ma.whitecare.mvc.ui.admin;

import ma.whitecare.entities.medical.Acte;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class ActeFormDialog extends JDialog {
    private JTextField libelleField;
    private JTextField categorieField;
    private JTextField prixField;
    private JButton saveBtn;
    private JButton cancelBtn;
    private boolean confirmed = false;
    private Acte acte;

    public ActeFormDialog(Frame parent, Acte acte) {
        super(parent, acte == null ? "Ajouter Acte" : "Modifier Acte", true);
        this.acte = acte != null ? acte : new Acte();

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
        addLabelAndField(formPanel, "Libellé *:", libelleField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Catégorie:", categorieField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Prix de Base *:", prixField = new JTextField(20), gbc, row++);

        add(formPanel, BorderLayout.CENTER);

        if (acte != null && acte.getIdActe() != null) {
            libelleField.setText(acte.getLibelle());
            categorieField.setText(acte.getCategorie());
            prixField.setText(acte.getPrixDeBase() != null ? String.valueOf(acte.getPrixDeBase()) : "0");
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saveBtn = new RoundedButton("Enregistrer");
        cancelBtn = new RoundedButton("Annuler");
        cancelBtn.setBackground(DesignSystem.TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                this.acte.setLibelle(libelleField.getText());
                this.acte.setCategorie(categorieField.getText());
                try {
                    this.acte.setPrixDeBase(Double.parseDouble(prixField.getText()));
                } catch (Exception ex) {
                    this.acte.setPrixDeBase(0.0);
                }
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
        if (libelleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le libellé est obligatoire.");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Acte getActe() {
        return acte;
    }

    public void setReadOnly(boolean readOnly) {
        libelleField.setEditable(!readOnly);
        categorieField.setEditable(!readOnly);
        prixField.setEditable(!readOnly);

        if (saveBtn != null)
            saveBtn.setVisible(!readOnly);
        setTitle(readOnly ? "Détails Acte" : (acte.getIdActe() == null ? "Ajouter Acte" : "Modifier Acte"));
    }
}
