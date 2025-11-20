package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DossierMedicale extends BaseEntity {

    private Long idDM;
    private LocalDate dateDeCreation;

    private Patient patient;
    private SituationFinanciere situationFinanciere;
     private List<Ordonnance> ordonnanceList;
     private List<Certificat> certificatLi;
     private Medecin medecin;
     private List<RDV> rdvList;
     private List<Consultation> consultationList;
    @Override
    public int hashCode() {
        return idDM != null ? idDM.hashCode() : 0;
    }
}
