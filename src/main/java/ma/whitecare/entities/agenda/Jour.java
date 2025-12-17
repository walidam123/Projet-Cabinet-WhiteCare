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


    private List<Long> creneaux ;




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