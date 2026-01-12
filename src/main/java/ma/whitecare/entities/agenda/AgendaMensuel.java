package ma.whitecare.entities.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.entities.user.Medecin;

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
    private Medecin medecin;

    private List<Long> joursID;

    @Override
    public String toString() {
        return String.format(
                "AgendaMensuel{id=%d, mois=%s, annee=%d,Nom:%s,Prenom:%s}",
                id != null ? id : 0,
                mois != null ? mois.name() : "N/A",
                annee != 0 ? annee : 0,
                medecin.getNom(),
                medecin.getPrenom()

        );
    }

}