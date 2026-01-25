package ma.whitecare.mvc.ui.auth;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PasswordChangeDialog extends JDialog {
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton submitButton;
    private boolean successfullyChanged = false;

    public PasswordChangeDialog(Frame owner) {
        super(owner, "Changement de mot de passe obligatoire", true);
        initComponents();
        setupLayout();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void initComponents() {
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        submitButton = new JButton("Changer le mot de passe");

        submitButton.setBackground(DesignSystem.PRIMARY);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
    }

    private void setupLayout() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));
        content.setBackground(DesignSystem.BACKGROUND);

        JPanel header = new JPanel(new GridLayout(2, 1, 5, 5));
        header.setBackground(DesignSystem.BACKGROUND);
        JLabel title = new JLabel("Première Connexion");
        title.setFont(DesignSystem.TITLE);
        JLabel subtitle = new JLabel("Pour des raisons de sécurité, veuillez changer votre mot de passe.");
        subtitle.setFont(DesignSystem.BODY);
        header.add(title);
        header.add(subtitle);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setBackground(DesignSystem.BACKGROUND);

        form.add(new JLabel("Mot de passe actuel:"));
        form.add(currentPasswordField);
        form.add(new JLabel("Nouveau mot de passe:"));
        form.add(newPasswordField);
        form.add(new JLabel("Confirmer le mot de passe:"));
        form.add(confirmPasswordField);

        content.add(header, BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);
        content.add(submitButton, BorderLayout.SOUTH);

        add(content);
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String getCurrentPassword() {
        return new String(currentPasswordField.getPassword());
    }

    public String getNewPassword() {
        return new String(newPasswordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    public JButton getSubmitButton() {
        return submitButton;
    }

    public void setSuccessfullyChanged(boolean value) {
        this.successfullyChanged = value;
    }

    public boolean isSuccessfullyChanged() {
        return successfullyChanged;
    }
}
