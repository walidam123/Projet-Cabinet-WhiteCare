package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.entities.financial.Facture;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consultation extends BaseEntity {

    private Long idConsultation;
    private LocalDate Date;
    private StatutConsultation statut;
    private String observationMedecin;

    private DossierMedicale dossierMedicale;
    private List<RDV> rdvList;
    private Certificat certificat;
    private List<InterventionMedecin> interventionMedecinList;
    private List<Ordonnance> ordonnanceList;
    private List<Facture> factureList;


    @Override
    public int hashCode() {
        return idConsultation!= null ? idConsultation.hashCode() : 0;
    }
}
