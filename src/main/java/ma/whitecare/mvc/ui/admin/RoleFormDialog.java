package ma.whitecare.mvc.ui.admin;

import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Role;

import javax.swing.*;
import java.awt.*;

public class RoleFormDialog extends JDialog {
    private JComboBox<LibelleRole> libelleCombo;
    private boolean confirmed = false;
    private Role role;

    public RoleFormDialog(Frame parent, Role role) {
        super(parent, role == null ? "Ajouter Rôle" : "Modifier Rôle", true);
        this.role = role != null ? role : new Role();

        setLayout(new BorderLayout());
        setSize(350, 200);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Libellé du Rôle:"));
        libelleCombo = new JComboBox<>(LibelleRole.values());
        if (role != null && role.getLibelle() != null) {
            libelleCombo.setSelectedItem(role.getLibelle());
        }
        mainPanel.add(libelleCombo);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Enregistrer");
        JButton cancelBtn = new JButton("Annuler");

        saveBtn.addActionListener(e -> {
            this.role.setLibelle((LibelleRole) libelleCombo.getSelectedItem());
            confirmed = true;
            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Role getRole() {
        return role;
    }
}
