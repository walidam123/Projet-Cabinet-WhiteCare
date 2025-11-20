package ma.whitecare.entities.appointment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.medical.InterventionMedecin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RDV extends BaseEntity {
    private Long idRDV;
    private LocalDate Date;
    private LocalTime heure;
    private String motif;
    private StatutRendezVous statut;
    private String noteMedecin;


    private DossierMedicale dossierMedicale;
    private Consultation consultation;

}
