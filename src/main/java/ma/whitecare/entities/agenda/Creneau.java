package ma.whitecare.entities.agenda;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Creneau extends BaseEntity {
    private Long id;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean estDisponible;
    private String motifIndisponibilite;
    private Long rendezVousId; // Référence à un éventuel rendez-vous

    // Méthodes utilitaires
    public long getDureeMinutes() {
        if (heureDebut != null && heureFin != null) {
            return java.time.Duration.between(heureDebut, heureFin).toMinutes();
        }
        return 0;
    }

    public boolean chevauche(Creneau autre) {
        if (heureDebut == null || heureFin == null || autre.heureDebut == null || autre.heureFin == null) {
            return false;
        }
        return heureDebut.isBefore(autre.heureFin) && heureFin.isAfter(autre.heureDebut);
    }

    @Override
    public String toString() {
        return String.format(
                "Creneau{%s-%s, disponible=%s}",
                heureDebut != null ? heureDebut.toString() : "N/A",
                heureFin != null ? heureFin.toString() : "N/A",
                estDisponible
        );
    }
}