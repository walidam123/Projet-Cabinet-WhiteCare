package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Acte extends BaseEntity {
    private Long idActe;

    private String libelle;

    private String categorie;

    public String getCategory() {
        return categorie;
    }

    public void setCategory(String category) {
        this.categorie = category;
    }

    private Double prixDeBase;
    List<InterventionMedecin> interventions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Acte))
            return false;
        Acte that = (Acte) o;
        return idActe != null && idActe.equals(that.idActe);
    }

    @Override
    public int hashCode() {
        return idActe != null ? idActe.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
                Acte {
                    id = %d,
                    libelle = '%s',
                    categorie = '%s',
                    prixBase = %.2f,
                    interventionsCount = %d
                }
                """.formatted(idActe, libelle, categorie, prixDeBase != null ? prixDeBase : 0.0,
                interventions == null ? 0 : interventions.size());
    }
}
