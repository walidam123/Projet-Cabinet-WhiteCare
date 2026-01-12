package ma.whitecare.mvc.controllers;

import ma.whitecare.mvc.ui.admin.AdminDashboardView;
import ma.whitecare.service.modules.UserManager.api.UserService;
import ma.whitecare.entities.user.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class AdminController {
    private final AdminDashboardView view;
    private final UserService userService;
    private final ma.whitecare.repository.modules.UserManager.api.RoleRepository roleRepo;
    private final ma.whitecare.service.modules.medicament.api.MedicamentService medicamentService;
    private final ma.whitecare.service.modules.actes.api.ActeService acteService;
    private final ma.whitecare.service.modules.patient.api.AntecedentService antecedentService;

    public AdminController(AdminDashboardView view,
            UserService userService,
            ma.whitecare.repository.modules.UserManager.api.RoleRepository roleRepo,
            ma.whitecare.service.modules.medicament.api.MedicamentService medicamentService,
            ma.whitecare.service.modules.actes.api.ActeService acteService,
            ma.whitecare.service.modules.patient.api.AntecedentService antecedentService) {
        this.view = view;
        this.userService = userService;
        this.roleRepo = roleRepo;
        this.medicamentService = medicamentService;
        this.acteService = acteService;
        this.antecedentService = antecedentService;

        initController();
        refreshUserTable();
        refreshRoleTable();
        refreshReferentialTables();
    }

    private void initController() {
        // User Actions
        view.getUserPanel().getAddUserButton().addActionListener(e -> handleAddUser());
        view.getUserPanel().getDeleteUserButton().addActionListener(e -> handleDeleteUser());
        view.getUserPanel().getResetPasswordButton().addActionListener(e -> handleResetPassword());
        view.getUserPanel().getActivateDeactivateButton().addActionListener(e -> handleToggleStatus());

        // Role Actions
        view.getRolePanel().getAddRoleButton().addActionListener(e -> handleAddRole());
        view.getRolePanel().getDeleteRoleButton().addActionListener(e -> handleDeleteRole());

        // Referential Actions (index: 1: Médicaments, 2: Antécédents, 3: Actes)
        view.getReferentialPanel().getAddButtonAt(1).addActionListener(e -> handleAddMedicament());
        view.getReferentialPanel().getAddButtonAt(2).addActionListener(e -> handleAddAntecedent());
        view.getReferentialPanel().getAddButtonAt(3).addActionListener(e -> handleAddActe());
    }

    private void refreshUserTable() {
        DefaultTableModel model = (DefaultTableModel) view.getUserPanel().getUserTable().getModel();
        model.setRowCount(0);
        try {
            List<Utilisateur> users = userService.getAllUsers();
            for (Utilisateur user : users) {
                String rolesStr = "N/A";
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < user.getRoles().size(); i++) {
                        sb.append(user.getRoles().get(i).getLibelle());
                        if (i < user.getRoles().size() - 1)
                            sb.append(", ");
                    }
                    rolesStr = sb.toString();
                }

                model.addRow(new Object[] {
                        user.getIdUser(),
                        user.getNom() + " " + user.getPrenom(),
                        user.getEmail(),
                        rolesStr,
                        user.getActif() != null && user.getActif() ? "Actif" : "Inactif"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement des utilisateurs: " + ex.getMessage());
        }
    }

    private void refreshRoleTable() {
        DefaultTableModel model = (DefaultTableModel) view.getRolePanel().getRoleTable().getModel();
        model.setRowCount(0);
        try {
            roleRepo.findAll().forEach(r -> model.addRow(new Object[] { r.getIdRole(), r.getLibelle().name() }));
        } catch (Exception ex) {
        }
    }

    private void refreshReferentialTables() {
        // Médicaments (index 1)
        DefaultTableModel medModel = (DefaultTableModel) view.getReferentialPanel().getTableAt(1).getModel();
        medModel.setRowCount(0);
        try {
            medicamentService.getAll()
                    .forEach(m -> medModel.addRow(new Object[] { m.getIdMct(), m.getNom(), m.getType() }));
        } catch (Exception ex) {
        }

        // Antécédents (index 2)
        DefaultTableModel antModel = (DefaultTableModel) view.getReferentialPanel().getTableAt(2).getModel();
        antModel.setRowCount(0);
        try {
            antecedentService.getAllAntecedents()
                    .forEach(a -> antModel.addRow(new Object[] { a.getId_Antecedent(), a.getNom(), a.getCategorie() }));
        } catch (Exception ex) {
        }

        // Actes (index 3)
        DefaultTableModel actModel = (DefaultTableModel) view.getReferentialPanel().getTableAt(3).getModel();
        actModel.setRowCount(0);
        try {
            acteService.getAll()
                    .forEach(a -> actModel.addRow(new Object[] { a.getIdActe(), a.getLibelle(), a.getPrixDeBase() }));
        } catch (Exception ex) {
        }
    }

    private void handleResetPassword() {
        int row = view.getUserPanel().getUserTable().getSelectedRow();
        if (row >= 0) {
            Long userId = (Long) view.getUserPanel().getUserTable().getValueAt(row, 0);
            String newPass = JOptionPane.showInputDialog(view, "Nouveau mot de passe :");
            if (newPass != null && !newPass.isEmpty()) {
                userService.updatePassword(userId, newPass);
                JOptionPane.showMessageDialog(view, "Mot de passe réinitialisé.");
            }
        }
    }

    private void handleToggleStatus() {
        int row = view.getUserPanel().getUserTable().getSelectedRow();
        if (row >= 0) {
            Long userId = (Long) view.getUserPanel().getUserTable().getValueAt(row, 0);
            Utilisateur user = userService.getUserById(userId);
            if (user.getActif() != null && user.getActif())
                userService.deactivateUser(userId);
            else
                userService.activateUser(userId);
            refreshUserTable();
        }
    }

    private void handleDeleteUser() {
        int row = view.getUserPanel().getUserTable().getSelectedRow();
        if (row >= 0) {
            Long userId = (Long) view.getUserPanel().getUserTable().getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(view, "Supprimer l'utilisateur ID: " + userId + " ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    userService.deleteUser(userId);
                    refreshUserTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
                }
            }
        }
    }

    private void handleAddUser() {
        JOptionPane.showMessageDialog(view, "Ajout utilisateur...");
    }

    private void handleAddRole() {
        JOptionPane.showMessageDialog(view, "Ajout rôle...");
    }

    private void handleDeleteRole() {
        JOptionPane.showMessageDialog(view, "Suppression rôle...");
    }

    private void handleAddMedicament() {
        JOptionPane.showMessageDialog(view, "Ajout médicament...");
    }

    private void handleAddAntecedent() {
        JOptionPane.showMessageDialog(view, "Ajout antécédent...");
    }

    private void handleAddActe() {
        JOptionPane.showMessageDialog(view, "Ajout acte...");
    }
}
