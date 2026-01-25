package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.entities.medical.Acte;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class ActeFormDialog extends JDialog {
    private JTextField libelleField, categorieField, prixField;
    private boolean succeeded;
    private Acte acte;

    public ActeFormDialog(Frame parent, Acte acte) {
        super(parent, acte == null ? "Nouvel Acte" : "Modifier Acte", true);
        this.acte = acte;
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        DesignSystem.stylePanel(formPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        libelleField = new JTextField(15);
        categorieField = new JTextField(15);
        prixField = new JTextField(15);

        if (acte != null) {
            libelleField.setText(acte.getLibelle());
            categorieField.setText(acte.getCategory());
            prixField.setText(String.valueOf(acte.getPrixDeBase()));
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Libellé :"), gbc);
        gbc.gridx = 1;
        formPanel.add(libelleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Catégorie :"), gbc);
        gbc.gridx = 1;
        formPanel.add(categorieField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Prix de Base :"), gbc);
        gbc.gridx = 1;
        formPanel.add(prixField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton saveBtn = new RoundedButton("Enregistrer");
        saveBtn.setBackground(DesignSystem.PRIMARY);
        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                succeeded = true;
                dispose();
            }
        });

        RoundedButton cancelBtn = new RoundedButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private boolean validateForm() {
        if (libelleField.getText().trim().isEmpty())
            return false;
        try {
            Double.parseDouble(prixField.getText().trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public Acte getActe() {
        if (acte == null)
            acte = new Acte();
        acte.setLibelle(libelleField.getText().trim());
        acte.setCategory(categorieField.getText().trim());
        acte.setPrixDeBase(Double.parseDouble(prixField.getText().trim()));
        return acte;
    }
}
