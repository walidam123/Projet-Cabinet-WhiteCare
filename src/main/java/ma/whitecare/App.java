package ma.whitecare;

import ma.whitecare.conf.ApplicationContext;
import ma.whitecare.mvc.controllers.LoginController;
import ma.whitecare.mvc.ui.auth.LoginView;
import ma.whitecare.service.modules.UserManager.api.UserService;
import ma.whitecare.service.modules.auth.AuthenticationService;

import javax.swing.*;

import ma.whitecare.mvc.ui.palette.DesignSystem;

public class App {
    public static void main(String[] args) {
        // Temporary seeding call
        ma.whitecare.utils.DbSeeder.main(args);

        // Initialize Look and Feel and Global Styles
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Set global defaults
            UIManager.put("Panel.background", DesignSystem.BACKGROUND);
            UIManager.put("Label.foreground", DesignSystem.TEXT_PRIMARY);
            UIManager.put("Label.font", DesignSystem.BODY);
            UIManager.put("TextField.font", DesignSystem.BODY);
            UIManager.put("PasswordField.font", DesignSystem.BODY);
            UIManager.put("Button.font", DesignSystem.BUTTON_FONT);
            UIManager.put("Table.font", DesignSystem.BODY);
            UIManager.put("TableHeader.font", DesignSystem.BUTTON_FONT);
            UIManager.put("TabbedPane.font", DesignSystem.BUTTON_FONT);
            UIManager.put("OptionPane.messageFont", DesignSystem.BODY);
            UIManager.put("OptionPane.buttonFont", DesignSystem.BUTTON_FONT);
            UIManager.put("Dialog.background", DesignSystem.BACKGROUND);

        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Initialize Context
                ApplicationContext context = ApplicationContext.getInstance();

                // 2. Get Repositories and Services
                ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository userRepo = context
                        .getBean("utilisateurRepo");
                ma.whitecare.repository.modules.UserManager.api.RoleRepository roleRepo = context.getBean("roleRepo");

                if (userRepo == null || roleRepo == null) {
                    throw new RuntimeException("Erreur : Impossible de charger les repositories utilisateur ou rôle.");
                }

                UserService userService = new ma.whitecare.service.modules.UserManager.impl.UserServiceImpl(userRepo,
                        roleRepo);
                AuthenticationService authService = new AuthenticationService(userService);

                // 3. Initialize UI
                LoginView loginView = new LoginView();
                new LoginController(loginView, authService, userService);

                loginView.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erreur au démarrage : " + e.getMessage(), "Erreur Fatale",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
