package ma.whitecare.mvc.ui.medecin;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import ma.whitecare.mvc.ui.secretaire.*;
import javax.swing.*;
import java.awt.*;

public class MedecinDashboardView extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;

    private MedecinHomePanel homePanel;
    // Placeholders for other panels to be implemented later
    private MedecinPatientPanel patientPanel;
    private ConsultationPanel consultationPanel;
    private MedecinActePanel actePanel;
    private RDVManagementPanel rdvPanel;
    private AntecedentManagementPanel antecedentPanel;
    private AgendaConfigPanel agendaPanel;
    private FinancePanel financePanel;
    private MedicamentManagementPanel medicamentPanel;

    public MedecinDashboardView(String userName) {
        setTitle("WhiteCare - Espace Médecin");
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
        JLabel userLabel = new JLabel("Dr. " + (userName != null ? userName : "Utilisateur"));
        userLabel.setFont(DesignSystem.BODY);
        userLabel.setForeground(new Color(220, 220, 220));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        addSidebarButton("Tableau de Bord", "DASHBOARD");
        addSidebarButton("Dossiers Patients", "PATIENTS");
        addSidebarButton("Rendez-vous", "RDV");
        addSidebarButton("Agenda", "AGENDA");
        addSidebarButton("Antécédents", "ANTECEDENTS");
        addSidebarButton("Consultation En Cours", "CONSULTATION");
        addSidebarButton("Catalogue des Actes", "ACTES");
        addSidebarButton("Gestion Médicaments", "MEDICAMENTS");
        addSidebarButton("Statistiques & Finances", "FINANCE");

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

        homePanel = new MedecinHomePanel();
        patientPanel = new MedecinPatientPanel();
        consultationPanel = new ConsultationPanel();
        actePanel = new MedecinActePanel();
        rdvPanel = new RDVManagementPanel();
        agendaPanel = new AgendaConfigPanel();
        antecedentPanel = new AntecedentManagementPanel();
        financePanel = new FinancePanel();
        medicamentPanel = new MedicamentManagementPanel();

        contentPanel.add(homePanel, "DASHBOARD");
        contentPanel.add(patientPanel, "PATIENTS");
        contentPanel.add(rdvPanel, "RDV");
        contentPanel.add(agendaPanel, "AGENDA");
        contentPanel.add(antecedentPanel, "ANTECEDENTS");
        contentPanel.add(consultationPanel, "CONSULTATION");
        contentPanel.add(actePanel, "ACTES");
        contentPanel.add(medicamentPanel, "MEDICAMENTS");
        contentPanel.add(financePanel, "FINANCE");

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

    public ConsultationPanel getConsultationPanel() {
        return consultationPanel;
    }

    public MedecinHomePanel getHomePanel() {
        return homePanel;
    }

    public MedecinPatientPanel getPatientPanel() {
        return patientPanel;
    }

    public MedecinActePanel getActePanel() {
        return actePanel;
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

    public MedicamentManagementPanel getMedicamentPanel() {
        return medicamentPanel;
    }

    public JButton getLogoutBtn() {
        return (JButton) findComponentByName(this, "logoutBtn");
    }

    public JButton getProfileBtn() {
        return (JButton) findComponentByName(this, "profileBtn");
    }

    public Container getContentPanel() {
        return contentPanel;
    }

    private Component findComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name != null && name.equals(c.getName()))
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
