package ma.whitecare.mvc.controllers;

import ma.whitecare.mvc.ui.auth.LoginView;
import ma.whitecare.mvc.ui.admin.AdminDashboardView;
import ma.whitecare.service.modules.auth.AuthenticationService;
import ma.whitecare.mvc.dto.AuthDto.LoginRequestDto;
import ma.whitecare.mvc.dto.AuthDto.LoginResponseDto;
import ma.whitecare.service.modules.UserManager.api.UserService;
import ma.whitecare.conf.ApplicationContext;

import javax.swing.*;

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
                    "Bienvenue " + response.getUser().getNom() + " " + response.getUser().getPrenom());

            // Proceed to Admin Dashboard
            view.dispose();
            SwingUtilities.invokeLater(() -> {
                // Launch Admin Dashboard
                AdminDashboardView adminView = new AdminDashboardView();
                ApplicationContext context = ApplicationContext.getInstance();

                // Retrieve all necessary services/repos
                ma.whitecare.repository.modules.UserManager.api.RoleRepository roleRepo = context.getBean("roleRepo");
                ma.whitecare.service.modules.medicament.api.MedicamentService medService = new ma.whitecare.service.modules.medicament.impl.MedicamentServiceImpl(
                        context.getBean("medicamentRepo"));
                ma.whitecare.service.modules.actes.api.ActeService acteService = new ma.whitecare.service.modules.actes.impl.ActeServiceImpl(
                        context.getBean("acteRepo"));
                ma.whitecare.service.modules.patient.api.AntecedentService antService = new ma.whitecare.service.modules.patient.impl.AntecedentServiceImpl(
                        context.getBean("antecedentRepo"));

                new AdminController(adminView, userService, roleRepo, medService, acteService, antService);
                adminView.setVisible(true);
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur d'authentification : " + ex.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
