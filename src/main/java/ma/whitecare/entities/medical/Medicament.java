package ma.whitecare.entities.medical;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.FormeMedicament;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Medicament extends BaseEntity {


    private Long idMct  ;
    private String nom;
    private String laboratoire;
    private String type;
    private FormeMedicament forme;
    private Boolean remboursable;
    private  Double prixUnitaire;
    private String Description ;
    //private List<Prescription> prescriptionList;
}
