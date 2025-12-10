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
    public String toString() {
        return "Medicament{" +
                "idMct=" + idMct +
                ", nom='" + nom + '\'' +
                ", laboratoire='" + laboratoire + '\'' +
                ", type='" + type + '\'' +
                ", forme=" + forme +
                ", remboursable=" + remboursable +
                ", prixUnitaire=" + prixUnitaire +
                ", description='" + description + '\''
                ;
    }
}
