package ma.whitecare.entities.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.entities.user.Medecin;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgendaMensuel extends BaseEntity {
    private Long id;
    private Mois mois;
    private List<LocalDate> joursNonDisponible;

    private Medecin medecin;

    @Override
    public String toString() {
        return "AgendaMensuel{" +
                "id=" + id +
                ", mois=" + mois +
                ", joursNonDisponible=" + (joursNonDisponible != null ? joursNonDisponible.size() + " jours" : "null") +
                ", medecin=" + (medecin != null ? medecin.getNom() : "null") +
                ", dateCreation=" + getDateCreation() +
                '}';
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
