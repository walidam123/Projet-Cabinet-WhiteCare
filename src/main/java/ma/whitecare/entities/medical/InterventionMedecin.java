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
public class InterventionMedecin extends BaseEntity {

    private Long idIM;
    private Double prixDePatient;
    private Integer numDent;
     private Acte acte;
    private Long acteId;
     private Consultation consultation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterventionMedecin)) return false;
        InterventionMedecin that = (InterventionMedecin) o;
        return idIM != null && idIM.equals(that.idIM);
    }

    @Override
    public int hashCode() {
        return idIM != null ? idIM.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            InterventionMedecin {
                id = %d,
                prixPatient = %.2f,
                numDent = %d,
                acteId = %d
            }
            """.formatted(idIM, prixDePatient != null ? prixDePatient : 0.0,
                numDent != null ? numDent : 0, acteId);
    }
}
