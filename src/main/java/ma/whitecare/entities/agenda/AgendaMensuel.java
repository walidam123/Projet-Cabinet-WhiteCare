package ma.whitecare.entities.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Mois;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AgendaMensuel extends BaseEntity {
    private Long id;
    private Mois mois;
    private int annee;

    private Long medecinId;


    private List<Jour> jours = new ArrayList<>();

    @Override
    public String toString() {
        return String.format(
                "AgendaMensuel{id=%d, mois=%s, annee=%d, jours=%d, disponibles=%d, nonDisponibles=%d}",
                id != null ? id : 0,
                mois != null ? mois.name() : "N/A",
                annee,
                jours != null ? jours.size() : 0,
                getNombreJoursDisponibles(),
                getNombreJoursNonDisponibles()
        );
    }

    // Méthodes utilitaires
    public YearMonth getYearMonth() {
        return YearMonth.of(annee, mois.ordinal() + 1);
    }

    public List<Jour> getJoursNonDisponibles() {
        return jours.stream()
                .filter(jour -> !jour.isEstDisponible())
                .collect(Collectors.toList());
    }

    public List<Jour> getJoursDisponibles() {
        return jours.stream()
                .filter(Jour::isEstDisponible)
                .collect(Collectors.toList());
    }

    public Jour getJourParDate(java.time.LocalDate date) {
        return jours.stream()
                .filter(jour -> date.equals(jour.getDate()))
                .findFirst()
                .orElse(null);
    }

    public void ajouterJour(Jour jour) {
        if (jours == null) {
            jours = new ArrayList<>();
        }
        jours.add(jour);
    }

    public void supprimerJour(Jour jour) {
        if (jours != null) {
            jours.remove(jour);
        }
    }

    public int getNombreJoursDisponibles() {
        return getJoursDisponibles().size();
    }

    public int getNombreJoursNonDisponibles() {
        return getJoursNonDisponibles().size();
    }


}