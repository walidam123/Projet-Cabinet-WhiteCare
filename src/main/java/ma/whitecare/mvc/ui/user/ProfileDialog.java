package ma.whitecare.mvc.ui.user;

import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.mvc.dto.UserDto.UserProfileDTO;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfileDialog extends JDialog {
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField telField;
    private JTextField adresseField;
    private JTextField dateNaissanceField;
    private JComboBox<Sexe> sexeCombo;
    private JButton saveButton;
    private JButton closeButton;

    public ProfileDialog(Frame owner, UserProfileDTO profile) {
        super(owner, "Mon Profil", true);
        initComponents();
        fillFields(profile);
        setupLayout();
    }

    private void initComponents() {
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        telField = new JTextField(20);
        adresseField = new JTextField(20);
        dateNaissanceField = new JTextField(20);
        sexeCombo = new JComboBox<>(Sexe.values());

        saveButton = new JButton("Enregistrer");
        saveButton.setBackground(DesignSystem.PRIMARY);
        saveButton.setForeground(Color.WHITE);

        closeButton = new JButton("Fermer");
    }

    private void fillFields(UserProfileDTO profile) {
        nomField.setText(profile.getNom());
        prenomField.setText(profile.getPrenom());
        telField.setText(profile.getTelephone());
        adresseField.setText(profile.getAdresse());
        if (profile.getDateNaissance() != null) {
            dateNaissanceField.setText(profile.getDateNaissance().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        sexeCombo.setSelectedItem(profile.getSexe());
    }

    private void setupLayout() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(new EmptyBorder(25, 25, 25, 25));
        content.setBackground(DesignSystem.BACKGROUND);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 15));
        form.setBackground(DesignSystem.BACKGROUND);

        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Prénom:"));
        form.add(prenomField);
        form.add(new JLabel("Téléphone:"));
        form.add(telField);
        form.add(new JLabel("Adresse:"));
        form.add(adresseField);
        form.add(new JLabel("Date de Naissance (AAAA-MM-JJ):"));
        form.add(dateNaissanceField);
        form.add(new JLabel("Sexe:"));
        form.add(sexeCombo);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(DesignSystem.BACKGROUND);
        buttons.add(closeButton);
        buttons.add(saveButton);

        content.add(new JLabel("Mise à jour de vos informations personnelles", JLabel.CENTER), BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);

        add(content);
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String getNom() {
        return nomField.getText();
    }

    public String getPrenom() {
        return prenomField.getText();
    }

    public String getTel() {
        return telField.getText();
    }

    public String getAdresse() {
        return adresseField.getText();
    }

    public LocalDate getDateNaissance() {
        try {
            return LocalDate.parse(dateNaissanceField.getText());
        } catch (Exception e) {
            return null;
        }
    }

    public Sexe getSexe() {
        return (Sexe) sexeCombo.getSelectedItem();
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCloseButton() {
        return closeButton;
    }
}
