package ma.whitecare.mvc.controllers;

import ma.whitecare.mvc.ui.auth.LoginView;
import ma.whitecare.mvc.ui.admin.AdminDashboardView;
import ma.whitecare.mvc.ui.secretaire.SecretaryDashboardView;
import ma.whitecare.service.modules.auth.AuthenticationService;
import ma.whitecare.mvc.dto.AuthDto.LoginRequestDto;
import ma.whitecare.mvc.dto.AuthDto.LoginResponseDto;
import ma.whitecare.service.modules.UserManager.api.UserService;
import ma.whitecare.conf.ApplicationContext;

import javax.swing.*;
import java.awt.Frame;
import ma.whitecare.mvc.ui.auth.PasswordChangeDialog;

public class LoginController {
        private final LoginView view;
        private final AuthenticationService authService;
        private final UserService userService;

        public LoginController(LoginView view, AuthenticationService authService, UserService userService) {
                this.view = view;
                this.authService = authService;
                this.userService = userService;
                initController();
        }

        private void initController() {
                view.getLoginButton().addActionListener(e -> handleLogin());
        }

        public static void showLogin() {
                SwingUtilities.invokeLater(() -> {
                        ApplicationContext context = ApplicationContext.getInstance();

                        // Fix: Manually instantiate UserService as it's not a bean in the context
                        ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository userRepo = context
                                        .getBean("utilisateurRepo");
                        ma.whitecare.repository.modules.UserManager.api.RoleRepository roleRepo = context
                                        .getBean("roleRepo");

                        UserService userService = new ma.whitecare.service.modules.UserManager.impl.UserServiceImpl(
                                        userRepo, roleRepo);
                        AuthenticationService authService = new AuthenticationService(userService);

                        LoginView loginView = new LoginView();
                        new LoginController(loginView, authService, userService);
                        loginView.setVisible(true);
                });
        }

        private void handleLogin() {
                String username = view.getUsername();
                String password = view.getPassword();

                if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Veuillez remplir tous les champs", "Champs requis",
                                        JOptionPane.WARNING_MESSAGE);
                        return;
                }

                try {
                        LoginResponseDto response = authService.login(new LoginRequestDto(username, password));
                        JOptionPane.showMessageDialog(view,
                                        "Bienvenue " + response.getUser().getNom() + " "
                                                        + response.getUser().getPrenom());

                        view.dispose();
                        SwingUtilities.invokeLater(() -> {
                                ApplicationContext context = ApplicationContext.getInstance();
                                if (response.getUser().isFirstLogin()) {
                                        if (!handleFirstLoginChange(response.getUser())) {
                                                return; // User cancelled or failed
                                        }
                                }

                                String role = "";
                                if (response.getUser().getRoles() != null && !response.getUser().getRoles().isEmpty()) {
                                        role = response.getUser().getRoles().get(0).name();
                                }

                                if ("ADMIN".equalsIgnoreCase(role) || "Administrateur".equalsIgnoreCase(role)) {
                                        String userName = (response.getUser().getNom() != null
                                                        ? response.getUser().getNom()
                                                        : "") +
                                                        " "
                                                        + (response.getUser().getPrenom() != null
                                                                        ? response.getUser().getPrenom()
                                                                        : "");
                                        AdminDashboardView adminView = new AdminDashboardView(userName.trim());
                                        // Services for Admin
                                        ma.whitecare.repository.modules.UserManager.api.RoleRepository roleRepo = context
                                                        .getBean("roleRepo");
                                        ma.whitecare.service.modules.medicament.api.MedicamentService medService = new ma.whitecare.service.modules.medicament.impl.MedicamentServiceImpl(
                                                        context.getBean("medicamentRepo"));
                                        ma.whitecare.service.modules.actes.api.ActeService acteService = new ma.whitecare.service.modules.actes.impl.ActeServiceImpl(
                                                        context.getBean("acteRepo"));
                                        ma.whitecare.service.modules.patient.api.AntecedentService antService = new ma.whitecare.service.modules.patient.impl.AntecedentServiceImpl(
                                                        context.getBean("antecedentRepo"));
                                        ma.whitecare.service.modules.cabinet.api.CabinetService cabinetService = new ma.whitecare.service.modules.cabinet.impl.CabinetServiceImpl(
                                                        context.getBean("cabinetRepo"));
                                        ma.whitecare.service.modules.UserManager.api.MedecinService medecinService = new ma.whitecare.service.modules.UserManager.impl.MedecinServiceImpl(
                                                        context.getBean("medecinRepo"), userService,
                                                        context.getBean("cabinetRepo"),
                                                        context.getBean("staffRepo"));
                                        ma.whitecare.service.modules.UserManager.api.SecretaireService secretaireService = new ma.whitecare.service.modules.UserManager.impl.SecretaireServiceImpl(
                                                        context.getBean("secretaireRepo"), userService,
                                                        context.getBean("cabinetRepo"));
                                        ma.whitecare.service.modules.UserManager.api.StaffService staffService = new ma.whitecare.service.modules.UserManager.impl.StaffServiceImpl(
                                                        context.getBean("staffRepo"), userService,
                                                        context.getBean("cabinetRepo"));

                                        new AdminController(adminView, userService, roleRepo, medService, acteService,
                                                        antService, cabinetService, medecinService, secretaireService,
                                                        staffService, response.getUser());
                                        adminView.setVisible(true);
                                } else if ("MEDECIN".equalsIgnoreCase(role)) {
                                        String userName = (response.getUser().getNom() != null
                                                        ? response.getUser().getNom()
                                                        : "") +
                                                        " "
                                                        + (response.getUser().getPrenom() != null
                                                                        ? response.getUser().getPrenom()
                                                                        : "");
                                        ma.whitecare.mvc.ui.medecin.MedecinDashboardView medView = new ma.whitecare.mvc.ui.medecin.MedecinDashboardView(
                                                        userName.trim());

                                        // Instantiate Services needed for MedecinController
                                        ma.whitecare.service.modules.rdv.api.RDVService rdvService = new ma.whitecare.service.modules.rdv.impl.RDVServiceImpl(
                                                        context.getBean("rdvRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"));

                                        ma.whitecare.service.modules.dossierMedical.api.DossierMedicalService dmService = new ma.whitecare.service.modules.dossierMedical.impl.DossierMedicalServiceImpl(
                                                        context.getBean("dossierRepo"), context.getBean("patientRepo"),
                                                        context.getBean("medecinRepo"),
                                                        context.getBean("consultationRepo"));

                                        ma.whitecare.service.modules.dossierMedical.api.ConsultationService consultationService = new ma.whitecare.service.modules.dossierMedical.impl.ConsultationServiceImpl(
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("interventionRepo"),
                                                        context.getBean("ordonnanceRepo"),
                                                        context.getBean("prescriptionRepo"));

                                        ma.whitecare.service.modules.patient.api.PatientService patientService = new ma.whitecare.service.modules.patient.impl.PatientServiceImpl(
                                                        context.getBean("patientRepo"));

                                        ma.whitecare.service.modules.UserManager.api.MedecinService medecinService = new ma.whitecare.service.modules.UserManager.impl.MedecinServiceImpl(
                                                        context.getBean("medecinRepo"), userService,
                                                        context.getBean("cabinetRepo"),
                                                        context.getBean("staffRepo"));

                                        ma.whitecare.entities.user.Medecin currentMedecin = medecinService
                                                        .getMedecinById(response.getUser().getId());

                                        ma.whitecare.service.modules.actes.api.ActeService acteService = new ma.whitecare.service.modules.actes.impl.ActeServiceImpl(
                                                        context.getBean("acteRepo"));

                                        ma.whitecare.service.modules.facture.api.FactureService factureService = new ma.whitecare.service.modules.facture.impl.FactureServiceImpl(
                                                        context.getBean("factureRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("patientRepo"),
                                                        context.getBean("interventionRepo"));

                                        ma.whitecare.service.modules.dossierMedical.api.PrescriptionService prescriptionService = new ma.whitecare.service.modules.dossierMedical.impl.PrescriptionServiceImpl(
                                                        context.getBean("prescriptionRepo"),
                                                        context.getBean("ordonnanceRepo"),
                                                        context.getBean("medicamentRepo"),
                                                        context.getBean("dossierRepo"));

                                        ma.whitecare.service.modules.ordonnance.api.OrdonnanceService ordonnanceService = new ma.whitecare.service.modules.ordonnance.impl.OrdonnanceServiceImpl(
                                                        context.getBean("ordonnanceRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("prescriptionRepo"),
                                                        context.getBean("patientRepo"),
                                                        context.getBean("medecinRepo"),
                                                        context.getBean("medicamentRepo"));

                                        ma.whitecare.service.modules.medicament.api.MedicamentService medicamentService = new ma.whitecare.service.modules.medicament.impl.MedicamentServiceImpl(
                                                        context.getBean("medicamentRepo"));

                                        ma.whitecare.service.modules.certificat.api.CertificatService certificatService = new ma.whitecare.service.modules.certificat.impl.CertificatServiceImpl(
                                                        context.getBean("certificatRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("patientRepo"),
                                                        context.getBean("medecinRepo"));

                                        ma.whitecare.service.modules.patient.api.AntecedentService antecedentService = new ma.whitecare.service.modules.patient.impl.AntecedentServiceImpl(
                                                        context.getBean("antecedentRepo"));

                                        ma.whitecare.service.modules.caisse.impl.CaisseServiceImpl caisseService = new ma.whitecare.service.modules.caisse.impl.CaisseServiceImpl(
                                                        context.getBean("factureRepo"),
                                                        context.getBean("chargesRepo"),
                                                        context.getBean("revenuesRepo"));

                                        ma.whitecare.service.modules.situationFinanciere.api.SituationFinanciereService sfService = new ma.whitecare.service.modules.situationFinanciere.impl.SituationFinanciereServiceImpl(
                                                        context.getBean("situationFinanciereRepo"));

                                        new MedecinController(medView, rdvService, dmService, consultationService,
                                                        patientService, acteService, factureService,
                                                        medicamentService, ordonnanceService, prescriptionService,
                                                        certificatService,
                                                        antecedentService,
                                                        (ma.whitecare.service.modules.caisse.api.CaisseService) caisseService,
                                                        sfService,
                                                        medecinService,
                                                        currentMedecin,
                                                        userService,
                                                        response.getUser());
                                        medView.setVisible(true);
                                } else if ("SECRETAIRE".equalsIgnoreCase(role) || "Secrétaire".equalsIgnoreCase(role)) {
                                        String userName = (response.getUser().getNom() != null
                                                        ? response.getUser().getNom()
                                                        : "") +
                                                        " "
                                                        + (response.getUser().getPrenom() != null
                                                                        ? response.getUser().getPrenom()
                                                                        : "");
                                        SecretaryDashboardView secView = new SecretaryDashboardView(userName.trim());

                                        // Services for Secretary
                                        ma.whitecare.service.modules.patient.api.PatientService patientService = new ma.whitecare.service.modules.patient.impl.PatientServiceImpl(
                                                        context.getBean("patientRepo"));
                                        ma.whitecare.service.modules.rdv.api.RDVService rdvService = new ma.whitecare.service.modules.rdv.impl.RDVServiceImpl(
                                                        context.getBean("rdvRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"));
                                        ma.whitecare.service.modules.facture.impl.FactureServiceImpl factureService = new ma.whitecare.service.modules.facture.impl.FactureServiceImpl(
                                                        context.getBean("factureRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("patientRepo"),
                                                        context.getBean("interventionRepo"));
                                        ma.whitecare.service.modules.patient.api.AntecedentService antecedentService = new ma.whitecare.service.modules.patient.impl.AntecedentServiceImpl(
                                                        context.getBean("antecedentRepo"));
                                        ma.whitecare.service.modules.caisse.impl.CaisseServiceImpl caisseService = new ma.whitecare.service.modules.caisse.impl.CaisseServiceImpl(
                                                        context.getBean("factureRepo"),
                                                        context.getBean("chargesRepo"),
                                                        context.getBean("revenuesRepo"));
                                        ma.whitecare.service.modules.dossierMedical.api.DossierMedicalService dmService = new ma.whitecare.service.modules.dossierMedical.impl.DossierMedicalServiceImpl(
                                                        context.getBean("dossierRepo"), context.getBean("patientRepo"),
                                                        context.getBean("medecinRepo"),
                                                        context.getBean("consultationRepo"));
                                        ma.whitecare.service.modules.UserManager.api.MedecinService medecinService = new ma.whitecare.service.modules.UserManager.impl.MedecinServiceImpl(
                                                        context.getBean("medecinRepo"), userService,
                                                        context.getBean("cabinetRepo"),
                                                        context.getBean("staffRepo"));

                                        ma.whitecare.service.modules.UserManager.api.SecretaireService secretaireService = new ma.whitecare.service.modules.UserManager.impl.SecretaireServiceImpl(
                                                        context.getBean("secretaireRepo"), userService,
                                                        context.getBean("cabinetRepo"));
                                        ma.whitecare.entities.user.Secretaire currentSecretary = secretaireService
                                                        .getSecretaireById(response.getUser().getId());

                                        ma.whitecare.service.modules.dossierMedical.api.ConsultationService consultationService = new ma.whitecare.service.modules.dossierMedical.impl.ConsultationServiceImpl(
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("interventionRepo"),
                                                        context.getBean("ordonnanceRepo"),
                                                        context.getBean("prescriptionRepo"));

                                        ma.whitecare.service.modules.situationFinanciere.api.SituationFinanciereService sfService = new ma.whitecare.service.modules.situationFinanciere.impl.SituationFinanciereServiceImpl(
                                                        context.getBean("situationFinanciereRepo"));

                                        ma.whitecare.service.modules.ordonnance.api.OrdonnanceService ordonnanceService = new ma.whitecare.service.modules.ordonnance.impl.OrdonnanceServiceImpl(
                                                        context.getBean("ordonnanceRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("prescriptionRepo"),
                                                        context.getBean("patientRepo"),
                                                        context.getBean("medecinRepo"),
                                                        context.getBean("medicamentRepo"));

                                        ma.whitecare.service.modules.certificat.api.CertificatService certificatService = new ma.whitecare.service.modules.certificat.impl.CertificatServiceImpl(
                                                        context.getBean("certificatRepo"),
                                                        context.getBean("consultationRepo"),
                                                        context.getBean("dossierRepo"),
                                                        context.getBean("patientRepo"),
                                                        context.getBean("medecinRepo"));

                                        new SecretaryController(secView, patientService, rdvService,
                                                        (ma.whitecare.service.modules.facture.api.FactureService) factureService,
                                                        antecedentService,
                                                        (ma.whitecare.service.modules.caisse.api.CaisseService) caisseService,
                                                        dmService,
                                                        sfService,
                                                        medecinService,
                                                        currentSecretary,
                                                        consultationService,
                                                        ordonnanceService,
                                                        certificatService,
                                                        userService,
                                                        response.getUser());
                                        secView.setVisible(true);
                                } else {
                                        JOptionPane.showMessageDialog(null, "Accès non autorisé pour ce rôle: " + role,
                                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                                }
                        });

                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(view, "Erreur d'authentification : " + ex.getMessage(), "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        private boolean handleFirstLoginChange(ma.whitecare.mvc.dto.UserDto.UserDTO user) {
                PasswordChangeDialog dialog = new PasswordChangeDialog((Frame) SwingUtilities.getWindowAncestor(view));
                final boolean[] success = { false };

                dialog.getSubmitButton().addActionListener(e -> {
                        String newPass = dialog.getNewPassword();
                        String confirm = dialog.getConfirmPassword();

                        if (newPass.isEmpty() || confirm.isEmpty()) {
                                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs");
                                return;
                        }

                        if (!newPass.equals(confirm)) {
                                JOptionPane.showMessageDialog(dialog, "Les mots de passe ne correspondent pas");
                                return;
                        }

                        try {
                                userService.updatePassword(user.getId(), newPass);
                                userService.updateFirstLoginStatus(user.getId(), false);
                                success[0] = true;
                                dialog.dispose();
                        } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage());
                        }
                });

                dialog.setVisible(true);
                return success[0];
        }
}
