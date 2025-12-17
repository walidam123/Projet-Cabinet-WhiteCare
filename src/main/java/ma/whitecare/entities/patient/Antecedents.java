package ma.whitecare.entities.patient;

import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.NiveauDeRisque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data @AllArgsConstructor @NoArgsConstructor @SuperBuilder
public class Antecedents extends BaseEntity {
    private Long id_Antecedent;
    private String nom;
    private  String categorie;
    private NiveauDeRisque niveauDeRisque;
    private List<Long> patientsid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Antecedents that = (Antecedents) o;
        return id_Antecedent != null && id_Antecedent.equals(that.id_Antecedent);
    }

    @Override
    public int hashCode() {
        return id_Antecedent != null ? id_Antecedent.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Antecedent {
          id = %d,
          nom = '%s',
          categorie = %s,
          niveauRisque = %s,
          patientsCount = %d
        }
        """.formatted(
                id_Antecedent,
                nom,
                categorie,
                niveauDeRisque,
                patientsid == null ? 0 : patientsid.size()
        );
    }
}
