package ma.whitecare.entities.medical;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterventionMedecin extends BaseEntity {

    private Long idIM;
    private Double prixDePatient;
    private Integer numDent;
     private Acte acte;
     private Consultation consultation;

    @Override
    public int hashCode() {
        return idIM != null ? idIM.hashCode() : 0;
    }
}
