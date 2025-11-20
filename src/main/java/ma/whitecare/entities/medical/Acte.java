package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Acte extends BaseEntity {
    private Long idActe;

    private String libelle;

    private String categorie;

    private Double prixDeBase;
    /* private List<InterventionMedecin> interventionMedecinlist; */
}
