package ma.whitecare.mvc.ui.admin;

import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class CabinetFormDialog extends JDialog {
    private JTextField nomField;
    private JTextField emailField;
    private JTextField logoField;
    private JTextField adresseField;
    private JTextField cinField;
    private JTextField tel1Field;
    private JTextField tel2Field;
    private JTextField siteWebField;
    private JTextField instagramField;
    private JTextField facebookField;
    private JTextArea descriptionArea;
    private JButton saveBtn;
    private JButton cancelBtn;
    private boolean confirmed = false;
    private CabinetMedicale cabinet;

    public CabinetFormDialog(Frame parent, CabinetMedicale cabinet) {
        super(parent, cabinet == null ? "Ajouter Cabinet" : "Modifier Cabinet", true);
        this.cabinet = cabinet != null ? cabinet : new CabinetMedicale();

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(550, 750);
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
        addLabelAndField(formPanel, "Email *:", emailField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Logo URL *:", logoField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Adresse *:", adresseField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "CIN *:", cinField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Téléphone 1 *:", tel1Field = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Téléphone 2 *:", tel2Field = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Site Web *:", siteWebField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Instagram *:", instagramField = new JTextField(20), gbc, row++);
        addLabelAndField(formPanel, "Facebook *:", facebookField = new JTextField(20), gbc, row++);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel descLabel = new JLabel("Description *:");
        DesignSystem.styleLabel(descLabel, DesignSystem.BODY);
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(DesignSystem.BODY);
        descriptionArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        mainPanel.add(formPanel);

        if (cabinet != null && cabinet.getId() != null) {
            nomField.setText(cabinet.getNom());
            emailField.setText(cabinet.getEmail());
            logoField.setText(cabinet.getLogo());
            adresseField.setText(cabinet.getAdresse());
            cinField.setText(cabinet.getCin());
            tel1Field.setText(cabinet.getTel1());
            tel2Field.setText(cabinet.getTel2());
            siteWebField.setText(cabinet.getSiteWeb());
            instagramField.setText(cabinet.getInstagram());
            facebookField.setText(cabinet.getFacebook());
            descriptionArea.setText(cabinet.getDescription());
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saveBtn = new RoundedButton("Enregistrer");
        cancelBtn = new RoundedButton("Annuler");
        cancelBtn.setBackground(DesignSystem.TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                this.cabinet.setNom(nomField.getText());
                this.cabinet.setEmail(emailField.getText());
                this.cabinet.setLogo(logoField.getText());
                this.cabinet.setAdresse(adresseField.getText());
                this.cabinet.setCin(cinField.getText());
                this.cabinet.setTel1(tel1Field.getText());
                this.cabinet.setTel2(tel2Field.getText());
                this.cabinet.setSiteWeb(siteWebField.getText());
                this.cabinet.setInstagram(instagramField.getText());
                this.cabinet.setFacebook(facebookField.getText());
                this.cabinet.setDescription(descriptionArea.getText());
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
        if (nomField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                logoField.getText().trim().isEmpty() ||
                adresseField.getText().trim().isEmpty() ||
                cinField.getText().trim().isEmpty() ||
                tel1Field.getText().trim().isEmpty() ||
                tel2Field.getText().trim().isEmpty() ||
                siteWebField.getText().trim().isEmpty() ||
                instagramField.getText().trim().isEmpty() ||
                facebookField.getText().trim().isEmpty() ||
                descriptionArea.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Tous les champs marqués d'une étoile (*) sont obligatoires.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public CabinetMedicale getCabinet() {
        return cabinet;
    }

    public void setReadOnly(boolean readOnly) {
        nomField.setEditable(!readOnly);
        emailField.setEditable(!readOnly);
        logoField.setEditable(!readOnly);
        adresseField.setEditable(!readOnly);
        cinField.setEditable(!readOnly);
        tel1Field.setEditable(!readOnly);
        tel2Field.setEditable(!readOnly);
        siteWebField.setEditable(!readOnly);
        instagramField.setEditable(!readOnly);
        facebookField.setEditable(!readOnly);
        descriptionArea.setEditable(!readOnly);

        if (saveBtn != null)
            saveBtn.setVisible(!readOnly);
        setTitle(readOnly ? "Détails Cabinet" : (cabinet.getId() == null ? "Ajouter Cabinet" : "Modifier Cabinet"));
    }
}
