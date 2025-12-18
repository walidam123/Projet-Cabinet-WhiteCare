package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Prescription extends BaseEntity {




private Long idPr;
private int quantite ;
private String frequence ;
private int dureeEnJours ;


private Medicament medicament;
private Ordonnance ordonnance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prescription)) return false;
        Prescription that = (Prescription) o;
        return idPr != null && idPr.equals(that.idPr);
    }

    @Override
    public int hashCode() {
        return idPr != null ? idPr.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Prescription {
                id = %d,
                medicament = %s
                quantite = %d,
                frequence = '%s',
                dureeEnJours = %d
            }
            """.formatted(idPr, medicament.getNom(), quantite, frequence, dureeEnJours);
    }
}
