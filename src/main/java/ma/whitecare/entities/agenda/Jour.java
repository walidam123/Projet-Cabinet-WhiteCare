package ma.whitecare.entities.agenda;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.JourSemaine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Jour {
    private Long id;
    private LocalDate date;
    private JourSemaine jourSemaine;
    private boolean estDisponible;
    private String raisonIndisponibilite;

    @Builder.Default
    private List<Creneau> creneaux = new ArrayList<>();

    // Méthodes utilitaires
    public boolean estWeekend() {
        return jourSemaine == JourSemaine.SAMEDI || jourSemaine == JourSemaine.DIMANCHE;
    }

    public boolean estFerie() {
        // Logique pour déterminer si c'est un jour férié
        // À implémenter selon le calendrier marocain
        return false;
    }

    public void ajouterCreneau(Creneau creneau) {
        if (creneaux == null) {
            creneaux = new ArrayList<>();
        }
        creneaux.add(creneau);
    }

    public void supprimerCreneau(Creneau creneau) {
        if (creneaux != null) {
            creneaux.remove(creneau);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Jour{date=%s, semaine=%s, disponible=%s, creneaux=%d}",
                date != null ? date.toString() : "N/A",
                jourSemaine != null ? jourSemaine.name() : "N/A",
                estDisponible,
                creneaux != null ? creneaux.size() : 0
        );
    }
}