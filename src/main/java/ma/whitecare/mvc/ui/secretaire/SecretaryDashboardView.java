package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import java.awt.*;

public class SecretaryDashboardView extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;

    private DashboardHomePanel homePanel;
    private PatientManagementPanel patientPanel;
    private RDVManagementPanel rdvPanel;
    private AntecedentManagementPanel antecedentPanel;
    private AgendaConfigPanel agendaPanel; // New
    private FinancePanel financePanel;

    public SecretaryDashboardView(String userName) {
        setTitle("WhiteCare - Espace Secrétaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Sidebar
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(DesignSystem.PRIMARY);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel("WHITE CARE");
        logo.setFont(DesignSystem.TITLE);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        // User name label
        JLabel userLabel = new JLabel(userName != null ? userName : "Utilisateur");
        userLabel.setFont(DesignSystem.BODY);
        userLabel.setForeground(new Color(220, 220, 220));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        addSidebarButton("Dashboard", "DASHBOARD");
        addSidebarButton("Patients", "PATIENTS");
        addSidebarButton("Patients", "PATIENTS");
        addSidebarButton("Rendez-vous", "RDV");
        addSidebarButton("Agenda", "AGENDA"); // New Button
        addSidebarButton("Antécédents", "ANTECEDENTS");
        addSidebarButton("Finances", "FINANCES");

        sidebar.add(Box.createVerticalGlue());

        RoundedButton profileBtn = new RoundedButton("Mon Profil");
        profileBtn.setBackground(DesignSystem.PRIMARY.darker());
        profileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileBtn.setName("profileBtn");
        sidebar.add(profileBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        RoundedButton logoutBtn = new RoundedButton("Déconnexion");
        logoutBtn.setBackground(DesignSystem.BTN_LOGIN);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setName("logoutBtn");
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        DesignSystem.stylePanel(contentPanel);

        homePanel = new DashboardHomePanel();
        patientPanel = new PatientManagementPanel();
        rdvPanel = new RDVManagementPanel();
        agendaPanel = new AgendaConfigPanel(); // New Panel
        antecedentPanel = new AntecedentManagementPanel();
        financePanel = new FinancePanel();

        contentPanel.add(homePanel, "DASHBOARD");
        contentPanel.add(patientPanel, "PATIENTS");
        contentPanel.add(rdvPanel, "RDV");
        contentPanel.add(agendaPanel, "AGENDA"); // New Card
        contentPanel.add(antecedentPanel, "ANTECEDENTS");
        contentPanel.add(financePanel, "FINANCES");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void addSidebarButton(String text, String cardName) {
        RoundedButton btn = new RoundedButton(text);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(DesignSystem.PRIMARY.darker());
        btn.setName(cardName + "_BTN");
        btn.addActionListener(e -> showCard(cardName));
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }

    public DashboardHomePanel getHomePanel() {
        return homePanel;
    }

    public PatientManagementPanel getPatientPanel() {
        return patientPanel;
    }

    public RDVManagementPanel getRdvPanel() {
        return rdvPanel;
    }

    public AntecedentManagementPanel getAntecedentPanel() {
        return antecedentPanel;
    }

    public AgendaConfigPanel getAgendaPanel() {
        return agendaPanel;
    }

    public FinancePanel getFinancePanel() {
        return financePanel;
    }

    public JButton getLogoutBtn() {
        return (JButton) findComponentByName(this, "logoutBtn");
    }

    public JButton getProfileBtn() {
        return (JButton) findComponentByName(this, "profileBtn");
    }

    private Component findComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name.equals(c.getName()))
                return c;
            if (c instanceof Container) {
                Component found = findComponentByName((Container) c, name);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
}
