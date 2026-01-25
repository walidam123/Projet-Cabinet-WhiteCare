package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;
import javax.swing.*;
import java.awt.*;

public class AgendaConfigPanel extends JPanel {
    private JCheckBox[] dayChecks;
    private JSpinner[] startSpinners;
    private JSpinner[] endSpinners;
    private RoundedButton saveBtn;

    private static final String[] DAYS = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" };

    public AgendaConfigPanel() {
        DesignSystem.stylePanel(this);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Configuration de l'Agenda Médecin");
        header.setFont(DesignSystem.TITLE);
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(DAYS.length + 1, 4, 10, 10));
        DesignSystem.stylePanel(grid);

        grid.add(new JLabel("Jour"));
        grid.add(new JLabel("Travaillé ?"));
        grid.add(new JLabel("Début"));
        grid.add(new JLabel("Fin"));

        dayChecks = new JCheckBox[DAYS.length];
        startSpinners = new JSpinner[DAYS.length];
        endSpinners = new JSpinner[DAYS.length];

        for (int i = 0; i < DAYS.length; i++) {
            grid.add(new JLabel(DAYS[i]));

            dayChecks[i] = new JCheckBox();
            dayChecks[i].setSelected(i < 5); // Default Mon-Fri
            grid.add(dayChecks[i]);

            startSpinners[i] = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor de1 = new JSpinner.DateEditor(startSpinners[i], "HH:mm");
            startSpinners[i].setEditor(de1);
            startSpinners[i].setValue(java.sql.Time.valueOf("09:00:00"));
            grid.add(startSpinners[i]);

            endSpinners[i] = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor de2 = new JSpinner.DateEditor(endSpinners[i], "HH:mm");
            endSpinners[i].setEditor(de2);
            endSpinners[i].setValue(java.sql.Time.valueOf("17:00:00"));
            grid.add(endSpinners[i]);
        }

        add(new JScrollPane(grid), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(south);
        saveBtn = new RoundedButton("Enregistrer la Configuration");
        south.add(saveBtn);
        add(south, BorderLayout.SOUTH);
    }

    public RoundedButton getSaveBtn() {
        return saveBtn;
    }

    // Getters for configuration data would be here
    public boolean isDayWorked(int index) {
        return dayChecks[index].isSelected();
    }

    public String getStartTime(int index) {
        return new java.text.SimpleDateFormat("HH:mm").format(startSpinners[index].getValue());
    }

    public String getEndTime(int index) {
        return new java.text.SimpleDateFormat("HH:mm").format(endSpinners[index].getValue());
    }
}
