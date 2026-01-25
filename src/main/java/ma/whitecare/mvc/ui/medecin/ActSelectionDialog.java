package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.entities.medical.Acte;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ActSelectionDialog extends JDialog {
    private JComboBox<Acte> actComboBox;
    private JTextField toothField;
    private JTextField priceField;
    private boolean succeeded;
    private Acte selectedActe;
    private int selectedTooth;
    private double finalPrice;

    public ActSelectionDialog(Frame parent, List<Acte> availableActs) {
        super(parent, "Ajouter une Intervention", true);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        DesignSystem.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Act Selection
        JLabel lAct = new JLabel("Acte:");
        DesignSystem.styleLabel(lAct, DesignSystem.BODY);
        actComboBox = new JComboBox<>(availableActs.toArray(new Acte[0]));
        // Custom renderer for Acte to show Libelle
        actComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Acte) {
                    setText(((Acte) value).getLibelle() + " (" + ((Acte) value).getPrixDeBase() + " Dhs)");
                }
                return this;
            }
        });
        actComboBox.addActionListener(e -> {
            Acte a = (Acte) actComboBox.getSelectedItem();
            if (a != null) {
                priceField.setText(String.valueOf(a.getPrixDeBase()));
            }
        });

        // Tooth Selection
        JLabel lTooth = new JLabel("Dent N° (Optionnel):");
        DesignSystem.styleLabel(lTooth, DesignSystem.BODY);
        toothField = new JTextField();
        toothField.setToolTipText("Ex: 11, 21, 36... Laisser vide si global");

        // Price Selection (for Discount/Adjustment)
        JLabel lPrice = new JLabel("Prix (Dhs):");
        DesignSystem.styleLabel(lPrice, DesignSystem.BODY);
        priceField = new JTextField();

        // Buttons
        RoundedButton btnOk = new RoundedButton("Ajouter");
        btnOk.setBackground(new Color(40, 167, 69)); // Success Green
        btnOk.addActionListener(e -> {
            if (validateInput()) {
                succeeded = true;
                dispose();
            }
        });

        RoundedButton btnCancel = new RoundedButton("Annuler");
        btnCancel.setBackground(new Color(108, 117, 125)); // Grey
        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        formPanel.add(lAct);
        formPanel.add(actComboBox);
        formPanel.add(lTooth);
        formPanel.add(toothField);
        formPanel.add(lPrice);
        formPanel.add(priceField);
        formPanel.add(btnCancel);
        formPanel.add(btnOk);

        // Assistant Image
        JPanel assistantPanel = new JPanel(new BorderLayout());
        assistantPanel.setOpaque(false);
        assistantPanel.setBorder(BorderFactory.createTitledBorder("Aide Numérotation"));
        try {
            String imgPath = "/static/images/numerotation-des-dents.jpg";
            java.net.URL imgUrl = getClass().getResource(imgPath);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
                assistantPanel.add(new JLabel(new ImageIcon(img)), BorderLayout.CENTER);
            }
        } catch (Exception ex) {
        }

        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.add(assistantPanel, BorderLayout.WEST);
        mainLayout.add(formPanel, BorderLayout.CENTER);

        getContentPane().add(mainLayout);
        pack();
        setLocationRelativeTo(parent);

        // Trigger initial price set
        if (actComboBox.getItemCount() > 0) {
            actComboBox.setSelectedIndex(0);
        }
    }

    private boolean validateInput() {
        selectedActe = (Acte) actComboBox.getSelectedItem();
        if (selectedActe == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un acte.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            finalPrice = Double.parseDouble(priceField.getText().trim());
            if (finalPrice < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prix invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String toothTxt = toothField.getText().trim();
        if (toothTxt.isEmpty()) {
            selectedTooth = 0; // Convention for "General" or no specific tooth
        }

        // Parse tooth if present
        if (!toothTxt.isEmpty()) {
            try {
                selectedTooth = Integer.parseInt(toothTxt);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Numéro de dent invalide (doit être un entier).", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public Acte getSelectedActe() {
        return selectedActe;
    }

    public int getSelectedTooth() {
        return selectedTooth;
    }

    public double getFinalPrice() {
        return finalPrice;
    }
}
