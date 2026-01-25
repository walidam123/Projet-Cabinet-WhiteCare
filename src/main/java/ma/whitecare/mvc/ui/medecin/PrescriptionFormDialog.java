package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class PrescriptionFormDialog extends JDialog {
    private JTextField quantiteField;
    private JTextField frequenceField;
    private JTextField dureeField;
    private boolean succeeded;

    private int quantite;
    private String frequence;
    private int duree;

    public PrescriptionFormDialog(Frame parent, Medicament medicament) {
        super(parent, "Détails de la Prescription", true);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        DesignSystem.stylePanel(panel);

        JLabel lMed = new JLabel("Médicament:");
        JLabel lName = new JLabel(medicament.getNom());
        lName.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lQty = new JLabel("Quantité (Boites):");
        quantiteField = new JTextField("1");

        JLabel lFreq = new JLabel("Posologie (Ex: 1 mat/1 soir):");
        frequenceField = new JTextField();

        JLabel lDur = new JLabel("Durée (Jours):");
        dureeField = new JTextField("7");

        RoundedButton btnAdd = new RoundedButton("Ajouter");
        btnAdd.setBackground(DesignSystem.PRIMARY);
        btnAdd.addActionListener(e -> {
            if (validateInput()) {
                succeeded = true;
                dispose();
            }
        });

        RoundedButton btnCancel = new RoundedButton("Annuler");
        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        panel.add(lMed);
        panel.add(lName);
        panel.add(lQty);
        panel.add(quantiteField);
        panel.add(lFreq);
        panel.add(frequenceField);
        panel.add(lDur);
        panel.add(dureeField);
        panel.add(btnCancel);
        panel.add(btnAdd);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    private boolean validateInput() {
        try {
            quantite = Integer.parseInt(quantiteField.getText().trim());
            duree = Integer.parseInt(dureeField.getText().trim());
            frequence = frequenceField.getText().trim();

            if (frequence.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une posologie.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantité et Durée doivent être des nombres entiers.");
            return false;
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getFrequence() {
        return frequence;
    }

    public int getDuree() {
        return duree;
    }
}
