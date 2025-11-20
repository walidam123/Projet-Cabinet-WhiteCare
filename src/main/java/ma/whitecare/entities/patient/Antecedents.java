package ma.whitecare.entities.patient;

import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.NiveauDeRisque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Antecedents extends BaseEntity {
    private Long id_Antecedent;
    private String nom;
    private  String categorie;
    private NiveauDeRisque niveauDeRisque;
    private List<Patient> patients;
}
