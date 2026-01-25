package ma.whitecare.mvc.controllers;

import ma.whitecare.mvc.ui.admin.AdminDashboardView;
import ma.whitecare.service.modules.UserManager.api.UserService;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.mvc.ui.admin.*;
import ma.whitecare.entities.user.Role;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.entities.patient.Antecedents;
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
    private final ma.whitecare.service.modules.cabinet.api.CabinetService cabinetService;
    private final ma.whitecare.service.modules.UserManager.api.MedecinService medecinService;
    private final ma.whitecare.service.modules.UserManager.api.SecretaireService secretaireService;
    private final ma.whitecare.service.modules.UserManager.api.StaffService staffService;
    private final ma.whitecare.mvc.dto.UserDto.UserDTO currentUser;

    public AdminController(AdminDashboardView view,
            UserService userService,
            ma.whitecare.repository.modules.UserManager.api.RoleRepository roleRepo,
            ma.whitecare.service.modules.medicament.api.MedicamentService medicamentService,
            ma.whitecare.service.modules.actes.api.ActeService acteService,
            ma.whitecare.service.modules.patient.api.AntecedentService antecedentService,
            ma.whitecare.service.modules.cabinet.api.CabinetService cabinetService,
            ma.whitecare.service.modules.UserManager.api.MedecinService medecinService,
            ma.whitecare.service.modules.UserManager.api.SecretaireService secretaireService,
            ma.whitecare.service.modules.UserManager.api.StaffService staffService,
            ma.whitecare.mvc.dto.UserDto.UserDTO currentUser) {
        this.view = view;
        this.userService = userService;
        this.roleRepo = roleRepo;
        this.medicamentService = medicamentService;
        this.acteService = acteService;
        this.antecedentService = antecedentService;
        this.cabinetService = cabinetService;
        this.medecinService = medecinService;
        this.secretaireService = secretaireService;
        this.staffService = staffService;
        this.currentUser = currentUser;

        initController();
        refreshUserTable();
        refreshRoleTable();
        refreshReferentialTables();
        refreshCabinetTable();
    }

    private void initController() {
        // User Actions
        view.getUserPanel().getAddUserButton().addActionListener(e -> handleAddUser());
        view.getUserPanel().getDeleteUserButton().addActionListener(e -> handleDeleteUser());
        view.getUserPanel().getResetPasswordButton().addActionListener(e -> handleResetPassword());
        view.getUserPanel().getActivateDeactivateButton().addActionListener(e -> handleToggleStatus());
        view.getUserPanel().getDetailsUserButton().addActionListener(e -> handleViewUserDetails());
        view.getUserPanel().getSearchButton().addActionListener(e -> handleUserSearch());
        view.getUserPanel().getSearchField().addActionListener(e -> handleUserSearch());

        // Role Actions
        view.getRolePanel().getAddRoleButton().addActionListener(e -> handleAddRole());
        view.getRolePanel().getEditRoleButton().addActionListener(e -> handleEditRole());
        view.getRolePanel().getDeleteRoleButton().addActionListener(e -> handleDeleteRole());

        // Referential Actions (index: 0: Médicaments, 1: Antécédents, 2: Actes)
        view.getReferentialPanel().getAddButtonAt(0).addActionListener(e -> handleAddMedicament());
        view.getReferentialPanel().getEditButtonAt(0).addActionListener(e -> handleEditMedicament());
        view.getReferentialPanel().getDeleteButtonAt(0).addActionListener(e -> handleDeleteMedicament());
        view.getReferentialPanel().getDetailsButtonAt(0).addActionListener(e -> handleViewMedicamentDetails());
        view.getReferentialPanel().getSearchButtonAt(0).addActionListener(e -> handleMedicamentSearch()); // Added
        view.getReferentialPanel().getSearchFieldAt(0).addActionListener(e -> handleMedicamentSearch()); // Added

        view.getReferentialPanel().getAddButtonAt(1).addActionListener(e -> handleAddAntecedent());
        view.getReferentialPanel().getEditButtonAt(1).addActionListener(e -> handleEditAntecedent());
        view.getReferentialPanel().getDeleteButtonAt(1).addActionListener(e -> handleDeleteAntecedent());
        view.getReferentialPanel().getDetailsButtonAt(1).addActionListener(e -> handleViewAntecedentDetails());
        view.getReferentialPanel().getSearchButtonAt(1).addActionListener(e -> handleAntecedentSearch());
        view.getReferentialPanel().getSearchFieldAt(1).addActionListener(e -> handleAntecedentSearch());

        view.getReferentialPanel().getAddButtonAt(2).addActionListener(e -> handleAddActe());
        view.getReferentialPanel().getEditButtonAt(2).addActionListener(e -> handleEditActe());
        view.getReferentialPanel().getDeleteButtonAt(2).addActionListener(e -> handleDeleteActe());
        view.getReferentialPanel().getDetailsButtonAt(2).addActionListener(e -> handleViewActeDetails());
        view.getReferentialPanel().getSearchButtonAt(2).addActionListener(e -> handleActeSearch());
        view.getReferentialPanel().getSearchFieldAt(2).addActionListener(e -> handleActeSearch());

        // Cabinet Actions
        view.getCabinetPanel().getAddButton().addActionListener(e -> handleAddCabinet());
        view.getCabinetPanel().getEditButton().addActionListener(e -> handleEditCabinet());
        view.getCabinetPanel().getDeleteButton().addActionListener(e -> handleDeleteCabinet());
        view.getCabinetPanel().getDetailsButton().addActionListener(e -> handleViewCabinetDetails());
        view.getCabinetPanel().getSearchButton().addActionListener(e -> handleCabinetSearch());
        view.getCabinetPanel().getSearchField().addActionListener(e -> handleCabinetSearch());

        // Logout
        view.getLogoutButton().addActionListener(e -> handleLogout());

        // User Edit
        view.getUserPanel().getEditUserButton().addActionListener(e -> handleEditUser());

        // Profile Action
        if (view.getProfileButton() != null) {
            view.getProfileButton().addActionListener(e -> handleShowProfile());
        }
    }

    private void handleShowProfile() {
        ma.whitecare.mvc.dto.UserDto.UserProfileDTO profile = userService.getUserProfile(currentUser.getId());
        ma.whitecare.mvc.ui.user.ProfileDialog dialog = new ma.whitecare.mvc.ui.user.ProfileDialog(view, profile);

        dialog.getSaveButton().addActionListener(e -> {
            ma.whitecare.mvc.dto.UserDto.UpdateProfileDTO updateDto = ma.whitecare.mvc.dto.UserDto.UpdateProfileDTO
                    .builder()
                    .nom(dialog.getNom())
                    .prenom(dialog.getPrenom())
                    .telephone(dialog.getTel())
                    .adresse(dialog.getAdresse())
                    .dateNaissance(dialog.getDateNaissance())
                    .sexe(dialog.getSexe())
                    .build();

            try {
                userService.updateUserProfile(currentUser.getId(), updateDto);
                JOptionPane.showMessageDialog(dialog, "Profil mis à jour avec succès !");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de la mise à jour: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void refreshUserTable() {
        DefaultTableModel model = (DefaultTableModel) view.getUserPanel().getUserTable().getModel();
        model.setRowCount(0);
        try {
            List<Utilisateur> users = userService.getAllUsers();
            for (Utilisateur user : users) {
                // EXPLICITLY FETCH ROLES because getAllUsers performs a shallow fetch
                List<ma.whitecare.entities.enums.LibelleRole> libelleRoles = userService.getUserRoles(user.getIdUser());
                List<Role> roles = new java.util.ArrayList<>();
                if (libelleRoles != null) {
                    for (ma.whitecare.entities.enums.LibelleRole libelle : libelleRoles) {
                        Role r = new Role();
                        r.setLibelle(libelle);
                        roles.add(r);
                    }
                }
                user.setRoles(roles);

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
        // Médicaments (index 0)
        DefaultTableModel medModel = (DefaultTableModel) view.getReferentialPanel().getTableAt(0).getModel();
        medModel.setRowCount(0);
        try {
            medicamentService.getAll()
                    .forEach(m -> medModel.addRow(new Object[] { m.getIdMct(), m.getNom(), m.getType() }));
        } catch (Exception ex) {
        }

        // Antécédents (index 1)
        DefaultTableModel antModel = (DefaultTableModel) view.getReferentialPanel().getTableAt(1).getModel();
        antModel.setRowCount(0);
        try {
            antecedentService.getAllAntecedents()
                    .forEach(a -> antModel.addRow(new Object[] { a.getId_Antecedent(), a.getNom(), a.getCategorie() }));
        } catch (Exception ex) {
        }

        // Actes (index 2)
        DefaultTableModel actModel = (DefaultTableModel) view.getReferentialPanel().getTableAt(2).getModel();
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

    private void handleAddRole() {
        RoleFormDialog dialog = new RoleFormDialog(view, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            roleRepo.create(dialog.getRole());
            refreshRoleTable();
        }
    }

    private void handleEditRole() {
        int row = view.getRolePanel().getRoleTable().getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getRolePanel().getRoleTable().getValueAt(row, 0);
            Role role = roleRepo.findById(id);
            RoleFormDialog dialog = new RoleFormDialog(view, role);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                roleRepo.update(dialog.getRole());
                refreshRoleTable();
            }
        }
    }

    private void handleDeleteRole() {
        int row = view.getRolePanel().getRoleTable().getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getRolePanel().getRoleTable().getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(view, "Supprimer ce rôle ?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                roleRepo.deleteById(id);
                refreshRoleTable();
            }
        }
    }

    private void handleAddMedicament() {
        MedicamentFormDialog dialog = new MedicamentFormDialog(view, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Medicament m = dialog.getMedicament();
            ma.whitecare.mvc.dto.MedicamentDto.CreateMedicamentDTO dto = ma.whitecare.mvc.dto.MedicamentDto.CreateMedicamentDTO
                    .builder()
                    .nom(m.getNom()).laboratoire(m.getLaboratoire()).type(m.getType())
                    .forme(m.getForme()).prixUnitaire(m.getPrixUnitaire()).description(m.getDescription())
                    .remboursable(m.getRemboursable()).build();
            medicamentService.createMedicament(dto);
            refreshReferentialTables();
        }
    }

    private void handleEditMedicament() {
        int row = view.getReferentialPanel().getTableAt(0).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(0).getValueAt(row, 0);
            Medicament m = medicamentService.getMedicamentById(id);
            MedicamentFormDialog dialog = new MedicamentFormDialog(view, m);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                ma.whitecare.mvc.dto.MedicamentDto.UpdateMedicamentDTO dto = ma.whitecare.mvc.dto.MedicamentDto.UpdateMedicamentDTO
                        .builder()
                        .nom(m.getNom()).laboratoire(m.getLaboratoire()).type(m.getType())
                        .forme(m.getForme()).prixUnitaire(m.getPrixUnitaire()).description(m.getDescription())
                        .remboursable(m.getRemboursable()).build();
                medicamentService.updateMedicament(id, dto);
                refreshReferentialTables();
            }
        }
    }

    private void handleDeleteMedicament() {
        int row = view.getReferentialPanel().getTableAt(0).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(0).getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(view, "Supprimer ce médicament ?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                medicamentService.deleteMedicament(id);
                refreshReferentialTables();
            }
        }
    }

    private void handleMedicamentSearch() {
        String query = view.getReferentialPanel().getSearchFieldAt(0).getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshReferentialTables();
            return;
        }
        DefaultTableModel model = (DefaultTableModel) view.getReferentialPanel().getTableAt(0).getModel();
        model.setRowCount(0);
        medicamentService.getAll().stream()
                .filter(m -> m.getNom().toLowerCase().contains(query))
                .forEach(m -> model.addRow(new Object[] { m.getIdMct(), m.getNom(), m.getType() }));
    }

    private void handleAddAntecedent() {
        AntecedentFormDialog dialog = new AntecedentFormDialog(view, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            antecedentService.createAntecedent(dialog.getAntecedent());
            refreshReferentialTables();
        }
    }

    private void handleEditAntecedent() {
        int row = view.getReferentialPanel().getTableAt(1).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(1).getValueAt(row, 0);
            Antecedents a = antecedentService.getAntecedentById(id);
            AntecedentFormDialog dialog = new AntecedentFormDialog(view, a);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                antecedentService.updateAntecedent(dialog.getAntecedent());
                refreshReferentialTables();
            }
        }
    }

    private void handleDeleteAntecedent() {
        int row = view.getReferentialPanel().getTableAt(1).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(1).getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(view, "Supprimer cet antécédent ?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                antecedentService.deleteAntecedentById(id);
                refreshReferentialTables();
            }
        }
    }

    private void handleAddActe() {
        ActeFormDialog dialog = new ActeFormDialog(view, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Acte a = dialog.getActe();
            ma.whitecare.mvc.dto.ActeDto.CreateActeDTO dto = ma.whitecare.mvc.dto.ActeDto.CreateActeDTO.builder()
                    .libelle(a.getLibelle()).categorie(a.getCategorie()).prixDeBase(a.getPrixDeBase()).build();
            acteService.createActe(dto);
            refreshReferentialTables();
        }
    }

    private void handleEditActe() {
        int row = view.getReferentialPanel().getTableAt(2).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(2).getValueAt(row, 0);
            Acte a = acteService.getActeById(id);
            ActeFormDialog dialog = new ActeFormDialog(view, a);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                ma.whitecare.mvc.dto.ActeDto.UpdateActeDTO dto = ma.whitecare.mvc.dto.ActeDto.UpdateActeDTO.builder()
                        .libelle(a.getLibelle()).categorie(a.getCategorie()).prixDeBase(a.getPrixDeBase()).build();
                acteService.updateActe(id, dto);
                refreshReferentialTables();
            }
        }
    }

    private void handleDeleteActe() {
        int row = view.getReferentialPanel().getTableAt(2).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(2).getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(view, "Supprimer cet acte ?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                acteService.deleteActe(id);
                refreshReferentialTables();
            }
        }
    }

    private void handleLogout() {
        if (JOptionPane.showConfirmDialog(view, "Voulez-vous vous déconnecter ?", "Déconnexion",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            view.dispose();
            LoginController.showLogin();
        }
    }

    private void refreshCabinetTable() {
        DefaultTableModel model = (DefaultTableModel) view.getCabinetPanel().getCabinetTable().getModel();
        model.setRowCount(0);
        try {
            cabinetService.getAllCabinets().forEach(c -> model
                    .addRow(new Object[] { c.getId(), c.getNom(), c.getEmail(), c.getTel1(), c.getAdresse() }));
        } catch (Exception ex) {
        }
    }

    private void handleAddCabinet() {
        CabinetFormDialog dialog = new CabinetFormDialog(view, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            cabinetService.createCabinet(dialog.getCabinet());
            refreshCabinetTable();
        }
    }

    private void handleEditCabinet() {
        int row = view.getCabinetPanel().getCabinetTable().getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getCabinetPanel().getCabinetTable().getValueAt(row, 0);
            ma.whitecare.entities.cabinet.CabinetMedicale cabinet = cabinetService.getCabinetById(id);
            CabinetFormDialog dialog = new CabinetFormDialog(view, cabinet);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                cabinetService.updateCabinet(dialog.getCabinet());
                refreshCabinetTable();
            }
        }
    }

    private void handleDeleteCabinet() {
        int row = view.getCabinetPanel().getCabinetTable().getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getCabinetPanel().getCabinetTable().getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(view, "Supprimer ce cabinet ?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                cabinetService.deleteCabinet(id);
                refreshCabinetTable();
            }
        }
    }

    private void handleAddUser() {
        List<ma.whitecare.entities.cabinet.CabinetMedicale> cabinets = cabinetService.getAllCabinets();
        UserFormDialog dialog = new UserFormDialog(view, null, cabinets);

        dialog.setOnSave(d -> {
            try {
                if (d.getUserType().equals("Médecin")) {
                    ma.whitecare.mvc.dto.UserDto.CreateMedecinDTO dto = ma.whitecare.mvc.dto.UserDto.CreateMedecinDTO
                            .builder()
                            .nom(d.getNom()).prenom(d.getPrenom()).email(d.getEmail())
                            .login(d.getLogin()).password(d.getPassword()).cin(d.getCin())
                            .sexe(d.getSexe()).telephone(d.getTel()).adresse(d.getAdresse())
                            .salaire(d.getSalaire()).prime(d.getPrime())
                            .soldeConge(d.getSoldeConge()) // Added Solde Conge
                            .cabinetMedicaleId(d.getCabinetId())
                            .specialite(d.getSpecialite()).actif(true).build();
                    medecinService.createMedecin(dto);
                } else if (d.getUserType().equals("Secrétaire")) {
                    ma.whitecare.mvc.dto.UserDto.CreateSecretaireDTO dto = ma.whitecare.mvc.dto.UserDto.CreateSecretaireDTO
                            .builder()
                            .nom(d.getNom()).prenom(d.getPrenom()).email(d.getEmail())
                            .login(d.getLogin()).password(d.getPassword()).cin(d.getCin())
                            .sexe(d.getSexe()).telephone(d.getTel()).adresse(d.getAdresse())
                            .salaire(d.getSalaire()).prime(d.getPrime())
                            .soldeConge(d.getSoldeConge()) // Added Solde Conge
                            .cabinetMedicaleId(d.getCabinetId())
                            .numCNSS(d.getCnss()).commission(d.getCommission()).actif(true).build();
                    secretaireService.createSecretaire(dto);
                } else {
                    // Admin or General
                    ma.whitecare.mvc.dto.UserDto.CreateUserDTO dto = ma.whitecare.mvc.dto.UserDto.CreateUserDTO
                            .builder()
                            .nom(d.getNom()).prenom(d.getPrenom()).email(d.getEmail())
                            .login(d.getLogin()).password(d.getPassword()).cin(d.getCin())
                            .sexe(d.getSexe()).telephone(d.getTel()).adresse(d.getAdresse())
                            .actif(true).build();
                    userService.createUser(dto);
                }

                JOptionPane.showMessageDialog(dialog, "Utilisateur ajouté avec succès !");
                refreshUserTable();
                dialog.closeDialog();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage());
                // Do not close logic
            }
        });

        dialog.setVisible(true);
    }

    private void handleEditUser() {
        int row = view.getUserPanel().getUserTable().getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getUserPanel().getUserTable().getValueAt(row, 0);
            Utilisateur user = userService.getUserById(id);
            List<ma.whitecare.entities.cabinet.CabinetMedicale> cabinets = cabinetService.getAllCabinets();
            UserFormDialog dialog = new UserFormDialog(view, user, cabinets);

            dialog.setOnSave(d -> {
                try {
                    if (user instanceof ma.whitecare.entities.user.Medecin) {
                        ma.whitecare.mvc.dto.UserDto.UpdateMedecinDTO dto = ma.whitecare.mvc.dto.UserDto.UpdateMedecinDTO
                                .builder()
                                .nom(d.getNom()).prenom(d.getPrenom()).email(d.getEmail())
                                .telephone(d.getTel()).adresse(d.getAdresse())
                                .sexe(d.getSexe()).salaire(d.getSalaire()).prime(d.getPrime())
                                .soldeConge(d.getSoldeConge()) // Added Solde Conge
                                .cabinetMedicaleId(d.getCabinetId()).specialite(d.getSpecialite())
                                .actif(user.getActif()).build();
                        medecinService.updateMedecin(id, dto);
                    } else if (user instanceof ma.whitecare.entities.user.Secretaire) {
                        ma.whitecare.mvc.dto.UserDto.UpdateSecretaireDTO dto = ma.whitecare.mvc.dto.UserDto.UpdateSecretaireDTO
                                .builder()
                                .nom(d.getNom()).prenom(d.getPrenom()).email(d.getEmail())
                                .telephone(d.getTel()).adresse(d.getAdresse())
                                .sexe(d.getSexe()).salaire(d.getSalaire()).prime(d.getPrime())
                                .soldeConge(d.getSoldeConge()) // Added Solde Conge
                                .cabinetMedicaleId(d.getCabinetId()).numCNSS(d.getCnss())
                                .commission(d.getCommission()).actif(user.getActif()).build();
                        secretaireService.updateSecretaire(id, dto);
                    } else {
                        ma.whitecare.mvc.dto.UserDto.UpdateUserDTO dto = ma.whitecare.mvc.dto.UserDto.UpdateUserDTO
                                .builder()
                                .nom(d.getNom()).prenom(d.getPrenom()).email(d.getEmail())
                                .telephone(d.getTel()).adresse(d.getAdresse())
                                .sexe(d.getSexe()).actif(user.getActif()).build();
                        userService.updateUser(id, dto);
                    }
                    JOptionPane.showMessageDialog(dialog, "Utilisateur modifié avec succès !");
                    refreshUserTable();
                    dialog.closeDialog();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage());
                }
            });

            dialog.setVisible(true);
        }
    }

    private void handleUserSearch() {
        String query = view.getUserPanel().getSearchField().getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshUserTable();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) view.getUserPanel().getUserTable().getModel();
        model.setRowCount(0);
        try {
            List<Utilisateur> users = userService.getAllUsers();
            for (Utilisateur user : users) {
                String fullName = (user.getNom() + " " + user.getPrenom()).toLowerCase();
                if (fullName.contains(query)) {
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
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur de recherche: " + ex.getMessage());
        }
    }

    private void handleCabinetSearch() {
        String query = view.getCabinetPanel().getSearchField().getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshCabinetTable();
            return;
        }
        DefaultTableModel model = (DefaultTableModel) view.getCabinetPanel().getCabinetTable().getModel();
        model.setRowCount(0);
        cabinetService.getAllCabinets().stream()
                .filter(c -> c.getNom().toLowerCase().contains(query))
                .forEach(c -> model
                        .addRow(new Object[] { c.getId(), c.getNom(), c.getEmail(), c.getTel1(), c.getAdresse() }));
    }

    private void handleAntecedentSearch() {
        String query = view.getReferentialPanel().getSearchFieldAt(1).getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshReferentialTables();
            return;
        }
        DefaultTableModel model = (DefaultTableModel) view.getReferentialPanel().getTableAt(1).getModel();
        model.setRowCount(0);
        antecedentService.getAllAntecedents().stream()
                .filter(a -> a.getNom().toLowerCase().contains(query))
                .forEach(a -> model.addRow(new Object[] { a.getId_Antecedent(), a.getNom(), a.getCategorie() }));
    }

    private void handleActeSearch() {
        String query = view.getReferentialPanel().getSearchFieldAt(2).getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshReferentialTables();
            return;
        }
        DefaultTableModel model = (DefaultTableModel) view.getReferentialPanel().getTableAt(2).getModel();
        model.setRowCount(0);
        acteService.getAll().stream()
                .filter(a -> a.getLibelle().toLowerCase().contains(query))
                .forEach(a -> model.addRow(new Object[] { a.getIdActe(), a.getLibelle(), a.getPrixDeBase() }));
    }

    private void handleViewUserDetails() {
        int row = view.getUserPanel().getUserTable().getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getUserPanel().getUserTable().getValueAt(row, 0);
            Utilisateur user = userService.getUserById(id);
            UserFormDialog dialog = new UserFormDialog(view, user, cabinetService.getAllCabinets());
            dialog.setReadOnly(true);
            dialog.setVisible(true);
        }
    }

    private void handleViewCabinetDetails() {
        int row = view.getCabinetPanel().getCabinetTable().getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getCabinetPanel().getCabinetTable().getValueAt(row, 0);
            ma.whitecare.entities.cabinet.CabinetMedicale cabinet = cabinetService.getCabinetById(id);
            CabinetFormDialog dialog = new CabinetFormDialog(view, cabinet);
            dialog.setReadOnly(true);
            dialog.setVisible(true);
        }
    }

    private void handleViewMedicamentDetails() {
        int row = view.getReferentialPanel().getTableAt(0).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(0).getValueAt(row, 0);
            Medicament m = medicamentService.getMedicamentById(id);
            MedicamentFormDialog dialog = new MedicamentFormDialog(view, m);
            dialog.setReadOnly(true);
            dialog.setVisible(true);
        }
    }

    private void handleViewAntecedentDetails() {
        int row = view.getReferentialPanel().getTableAt(1).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(1).getValueAt(row, 0);
            Antecedents a = antecedentService.getAntecedentById(id);
            AntecedentFormDialog dialog = new AntecedentFormDialog(view, a);
            dialog.setReadOnly(true);
            dialog.setVisible(true);
        }
    }

    private void handleViewActeDetails() {
        int row = view.getReferentialPanel().getTableAt(2).getSelectedRow();
        if (row >= 0) {
            Long id = (Long) view.getReferentialPanel().getTableAt(2).getValueAt(row, 0);
            Acte a = acteService.getActeById(id);
            ActeFormDialog dialog = new ActeFormDialog(view, a);
            dialog.setReadOnly(true);
            dialog.setVisible(true);
        }
    }
}
