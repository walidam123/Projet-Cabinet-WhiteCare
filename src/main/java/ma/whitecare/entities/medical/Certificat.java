package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Certificat extends BaseEntity {

    private Long idCertif;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer duree;
    private String noteMedecin;


    private DossierMedicale dossierMedicale;
    private Consultation consultation;
}
