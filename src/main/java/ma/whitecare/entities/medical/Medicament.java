package ma.whitecare.entities.medical;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.FormeMedicament;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class Medicament extends BaseEntity {


    private Long idMct  ;
    private String nom;
    private String laboratoire;
    private String type;
    private FormeMedicament forme;
    private Boolean remboursable;
    private  Double prixUnitaire;
    private String description ;
    private List<Prescription> prescriptionList= null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicament)) return false;
        Medicament that = (Medicament) o;
        return idMct != null && idMct.equals(that.idMct);
    }

    @Override
    public int hashCode() {
        return idMct != null ? idMct.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Medicament {
                id = %d,
                nom = '%s',
                laboratoire = '%s',
                forme = %s,
                prixUnitaire = %.2f, 
                prescriptionCount = '%d',
            }
            """.formatted(idMct, nom, laboratoire, forme,
                prixUnitaire != null ? prixUnitaire : 0.0, prescriptionList == null ? 0 : prescriptionList.size());
    }
}
