package ma.whitecare.mvc.ui.secretaire;

import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.mvc.ui.palette.DesignSystem;
import ma.whitecare.mvc.ui.palette.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssignAntecedentDialog extends JDialog {
    private List<Antecedents> allAntecedents;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private boolean confirmed = false;

    public AssignAntecedentDialog(Frame parent, Patient patient, List<Antecedents> allAntecedents) {
        super(parent, "Affecter des antécédents - " + patient.getNom() + " " + patient.getPrenom(), true);
        this.allAntecedents = allAntecedents;

        setLayout(new BorderLayout());
        getContentPane().setBackground(DesignSystem.BACKGROUND);
        setSize(400, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        DesignSystem.stylePanel(mainPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Choisir les antécédents"));
        add(scrollPane, BorderLayout.CENTER);

        List<Long> currentIds = patient.getAntecedents() != null
                ? patient.getAntecedents().stream().map(Antecedents::getId_Antecedent).collect(Collectors.toList())
                : new ArrayList<>();

        for (Antecedents ant : allAntecedents) {
            JCheckBox cb = new JCheckBox(ant.getNom() + " (" + ant.getCategorie() + ")");
            cb.setFont(DesignSystem.BODY);
            cb.setBackground(DesignSystem.BACKGROUND);
            if (currentIds.contains(ant.getId_Antecedent())) {
                cb.setSelected(true);
            }
            checkBoxes.add(cb);
            mainPanel.add(cb);
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        DesignSystem.stylePanel(buttonPanel);

        RoundedButton saveBtn = new RoundedButton("Enregistrer");
        RoundedButton cancelBtn = new RoundedButton("Annuler");
        cancelBtn.setBackground(DesignSystem.TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
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

    public List<Antecedents> getSelectedAntecedents() {
        List<Antecedents> selected = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selected.add(allAntecedents.get(i));
            }
        }
        return selected;
    }
}
