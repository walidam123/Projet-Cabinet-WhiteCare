package ma.whitecare.mvc.ui.admin;

import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class UserFormDialog extends JDialog {
    private JComboBox<String> typeCombo;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JTextField loginField;
    private JPasswordField passField;
    private JTextField cinField;
    private JComboBox<Sexe> sexeCombo;
    private JTextField telField;
    private JTextField adresseField;

    // Staff fields
    private JTextField salaireField;
    private JTextField primeField;
    private JTextField soldeCongeField;
    private JComboBox<ma.whitecare.entities.cabinet.CabinetMedicale> cabinetCombo;

    // Specific fields
    private JTextField specialiteField; // Medecin
    private JTextField cnssField; // Secretaire
    private JTextField commissionField; // Secretaire

    private JButton saveBtn;
    private JButton cancelBtn;

    private boolean confirmed = false;
    private Utilisateur user;

    public UserFormDialog(Frame parent, Utilisateur user,
            java.util.List<ma.whitecare.entities.cabinet.CabinetMedicale> cabinets) {
        super(parent, user == null ? "Ajouter Utilisateur" : "Modifier Utilisateur", true);
        this.user = user;

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(550, 650);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(mainPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Type Selection
        JPanel typePanel = createFieldPanel("Type d'utilisateur:",
                typeCombo = new JComboBox<>(new String[] { "Administrateur", "Médecin", "Secrétaire" }));
        mainPanel.add(typePanel);

        // General Info
        mainPanel.add(createSectionHeader("Informations Générales"));
        mainPanel.add(createFieldPanel("Nom:", nomField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Prénom:", prenomField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Email:", emailField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Login:", loginField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Mot de passe:", passField = new JPasswordField(20)));
        mainPanel.add(createFieldPanel("CIN:", cinField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Sexe:", sexeCombo = new JComboBox<>(Sexe.values())));
        mainPanel.add(createFieldPanel("Téléphone:", telField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Adresse:", adresseField = new JTextField(20)));

        // Staff Info
        JPanel staffSection = new JPanel();
        staffSection.setLayout(new BoxLayout(staffSection, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(staffSection);
        staffSection.add(createSectionHeader("Informations Professionnelles"));
        staffSection.add(createFieldPanel("Salaire:", salaireField = new JTextField("0")));
        staffSection.add(createFieldPanel("Prime:", primeField = new JTextField("0")));
        staffSection.add(createFieldPanel("Solde Congé (Jours):", soldeCongeField = new JTextField("30")));

        cabinetCombo = new JComboBox<>(new java.util.Vector<>(cabinets));
        cabinetCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ma.whitecare.entities.cabinet.CabinetMedicale) {
                    setText(((ma.whitecare.entities.cabinet.CabinetMedicale) value).getNom());
                }
                return this;
            }
        });
        staffSection.add(createFieldPanel("Cabinet *:", cabinetCombo));

        mainPanel.add(staffSection);

        // Specific Info
        JPanel medecinSection = new JPanel();
        medecinSection.setLayout(new BoxLayout(medecinSection, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(medecinSection);
        medecinSection.add(createSectionHeader("Spécifique Médecin"));
        medecinSection.add(createFieldPanel("Spécialité:", specialiteField = new JTextField(20)));
        mainPanel.add(medecinSection);

        JPanel secretaireSection = new JPanel();
        secretaireSection.setLayout(new BoxLayout(secretaireSection, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(secretaireSection);
        secretaireSection.add(createSectionHeader("Spécifique Secrétaire"));
        secretaireSection.add(createFieldPanel("N° CNSS:", cnssField = new JTextField(20)));
        secretaireSection.add(createFieldPanel("Commission (%):", commissionField = new JTextField("0")));
        mainPanel.add(secretaireSection);

        // Listen for type changes
        typeCombo.addActionListener(e -> {
            String selected = (String) typeCombo.getSelectedItem();
            staffSection.setVisible(!selected.equals("Administrateur"));
            medecinSection.setVisible(selected.equals("Médecin"));
            secretaireSection.setVisible(selected.equals("Secrétaire"));
            revalidate();
            repaint();
        });

        // Initialize with user data if editing
        if (user != null) {
            typeCombo.setEnabled(false);
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            loginField.setText(user.getLogin());
            cinField.setText(user.getCin());
            sexeCombo.setSelectedItem(user.getSexe());
            telField.setText(user.getTel());
            adresseField.setText(user.getAdresse());

            if (user instanceof Medecin) {
                typeCombo.setSelectedItem("Médecin");
                Medecin m = (Medecin) user;
                salaireField.setText(String.valueOf(m.getSalaire()));
                primeField.setText(String.valueOf(m.getPrime()));
                soldeCongeField.setText(String.valueOf(m.getSoldeConge() != null ? m.getSoldeConge() : 30));
                if (m.getCabinetMedicaleId() != null) {
                    for (int i = 0; i < cabinetCombo.getItemCount(); i++) {
                        if (cabinetCombo.getItemAt(i).getId().equals(m.getCabinetMedicaleId())) {
                            cabinetCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                specialiteField.setText(m.getSpecialite());
            } else if (user instanceof Secretaire) {
                typeCombo.setSelectedItem("Secrétaire");
                Secretaire s = (Secretaire) user;
                salaireField.setText(String.valueOf(s.getSalaire()));
                primeField.setText(String.valueOf(s.getPrime()));
                soldeCongeField.setText(String.valueOf(s.getSoldeConge() != null ? s.getSoldeConge() : 30));
                if (s.getCabinetMedicaleId() != null) {
                    for (int i = 0; i < cabinetCombo.getItemCount(); i++) {
                        if (cabinetCombo.getItemAt(i).getId().equals(s.getCabinetMedicaleId())) {
                            cabinetCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                cnssField.setText(s.getNumCNSS());
                commissionField.setText(String.valueOf(s.getCommission()));
            } else {
                typeCombo.setSelectedItem("Administrateur");
            }
        } else {
            typeCombo.setSelectedItem("Médecin");
        }

        // Initial visibility
        typeCombo.getActionListeners()[0].actionPerformed(null);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saveBtn = new RoundedButton("Enregistrer");
        cancelBtn = new RoundedButton("Annuler");
        cancelBtn.setBackground(DesignSystem.TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                if (onSave != null) {
                    onSave.accept(this);
                } else {
                    confirmed = true;
                    dispose();
                }
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
        p.setMaximumSize(new Dimension(550, 45));
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
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || loginField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Les champs Nom, Prénom et Login sont obligatoires.");
            return false;
        }
        if (user == null && new String(passField.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le mot de passe est obligatoire pour un nouvel utilisateur.");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getUserType() {
        return (String) typeCombo.getSelectedItem();
    }

    public String getNom() {
        return nomField.getText();
    }

    public String getPrenom() {
        return prenomField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getLogin() {
        return loginField.getText();
    }

    public String getPassword() {
        return new String(passField.getPassword());
    }

    public String getCin() {
        return cinField.getText();
    }

    public Sexe getSexe() {
        return (Sexe) sexeCombo.getSelectedItem();
    }

    public String getTel() {
        return telField.getText();
    }

    public String getAdresse() {
        return adresseField.getText();
    }

    public Double getSalaire() {
        try {
            return Double.parseDouble(salaireField.getText());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Double getPrime() {
        try {
            return Double.parseDouble(primeField.getText());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Integer getSoldeConge() {
        try {
            return Integer.parseInt(soldeCongeField.getText());
        } catch (Exception e) {
            return 30; // Default
        }
    }

    public Long getCabinetId() {
        ma.whitecare.entities.cabinet.CabinetMedicale selected = (ma.whitecare.entities.cabinet.CabinetMedicale) cabinetCombo
                .getSelectedItem();
        return selected != null ? selected.getId() : null;
    }

    public String getSpecialite() {
        return specialiteField.getText();
    }

    public String getCnss() {
        return cnssField.getText();
    }

    public Double getCommission() {
        try {
            return Double.parseDouble(commissionField.getText());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void setReadOnly(boolean readOnly) {
        typeCombo.setEnabled(!readOnly);
        nomField.setEditable(!readOnly);
        prenomField.setEditable(!readOnly);
        emailField.setEditable(!readOnly);
        loginField.setEditable(!readOnly);
        passField.setEditable(!readOnly);
        cinField.setEditable(!readOnly);
        sexeCombo.setEnabled(!readOnly);
        telField.setEditable(!readOnly);
        adresseField.setEditable(!readOnly);
        salaireField.setEditable(!readOnly);
        primeField.setEditable(!readOnly);
        cabinetCombo.setEnabled(!readOnly);
        specialiteField.setEditable(!readOnly);
        cnssField.setEditable(!readOnly);
        commissionField.setEditable(!readOnly);

        if (saveBtn != null)
            saveBtn.setVisible(!readOnly);
        setTitle(readOnly ? "Détails Utilisateur" : (user == null ? "Ajouter Utilisateur" : "Modifier Utilisateur"));
    }

    // Callback for save action
    private java.util.function.Consumer<UserFormDialog> onSave;

    public void setOnSave(java.util.function.Consumer<UserFormDialog> onSave) {
        this.onSave = onSave;
    }

    public boolean validateAndConfirm() {
        return validateForm();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void closeDialog() {
        super.dispose();
    }
}
